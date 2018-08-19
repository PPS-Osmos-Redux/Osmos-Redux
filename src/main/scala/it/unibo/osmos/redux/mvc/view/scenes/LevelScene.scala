package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.ecs.components.EntityType
import it.unibo.osmos.redux.mvc.model.MapShape
import it.unibo.osmos.redux.mvc.view.ViewConstants.Entities._
import it.unibo.osmos.redux.mvc.view.components.{LevelScreen, LevelStateBox, LevelStateBoxListener}
import it.unibo.osmos.redux.mvc.view.drawables._
import it.unibo.osmos.redux.mvc.view.events.MouseEventWrapper
import it.unibo.osmos.redux.mvc.view.levels.{LevelContext, LevelContextListener}
import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import it.unibo.osmos.redux.utils.MathUtils._
import it.unibo.osmos.redux.utils.Point
import scalafx.animation.FadeTransition
import scalafx.application.Platform
import scalafx.beans.property.BooleanProperty
import scalafx.scene.canvas.Canvas
import scalafx.scene.image.Image
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle, Shape}
import scalafx.stage.Stage
import scalafx.util.Duration

/**
  * This scene holds and manages a single level
  */
class LevelScene(override val parentStage: Stage, val listener: LevelSceneListener) extends BaseScene(parentStage)
  with LevelContextListener with LevelStateBoxListener {

  /**
    * The current game pending state: true if the game is paused
    */
  private var paused: BooleanProperty = BooleanProperty(false)

  /**
    * The canvas which will draw the elements on the screen
    */
  private val canvas: Canvas = new Canvas(parentStage.getWidth, parentStage.getHeight) {
    width <== parentStage.width
    height <== parentStage.height
    cache = true
    opacity = 0.0
  }

  /**
    * The screen showed when the game is paused (with a bound property)
    */
  private val pauseScreen = LevelScreen.Builder(this)
    .withText("Game paused", 30, Color.White)
    .build()
  pauseScreen.visible <== paused

  /**
    * The splash screen showed when the game is paused
    */
  private val splashScreen = LevelScreen.Builder(this)
    .withText("Become huge", 50, Color.White)
    .build()

  /* We start the level */
  private def startLevel(): Unit = {
    /* Splash screen animation, starting with a FadeIn */
    new FadeTransition(Duration.apply(2000), splashScreen) {
      fromValue = 0.0
      toValue = 1.0
      autoReverse = true
      /* FadeOut */
      onFinished = _ => new FadeTransition(Duration.apply(1000), splashScreen) {
        fromValue = 1.0
        toValue = 0.0
        autoReverse = true
        /* Showing the canvas */
        onFinished = _ => new FadeTransition(Duration.apply(3000), canvas) {
          fromValue = 0.0
          toValue = 1.0
          /* Removing the splash screen to reduce the load. Then the level is starte */
          onFinished = _ => content.remove(splashScreen); listener.onStartLevel()
        }.play()
      }.play()
    }.play()
  }

  /**
    * The images used to draw cells, background and level
    */
  private val cellDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage("/textures/cell.png"), canvas.graphicsContext2D)
  private val playerCellDrawable: CellDrawable = new CellWithSpeedDrawable(ImageLoader.getImage("/textures/cell.png"), canvas.graphicsContext2D)
  private val backgroundImage: Image = ImageLoader.getImage("/textures/background.png")
  private var mapBorder: Option[Shape] = Option.empty

  /**
    * The content of the whole scene
    */
  content = Seq(canvas, pauseScreen, new LevelStateBox(this,4.0), splashScreen)

  /**
    * The level context, created with the LevelScene. It still needs to be properly setup
    */
  private var _levelContext: Option[LevelContext] = Option.empty
  def levelContext: Option[LevelContext] = _levelContext
  def levelContext_= (levelContext: LevelContext): Unit = _levelContext = Option(levelContext)

  override def onPause(): Unit = {
    paused.value = true
    canvas.opacity = 0.5

    listener.onPauseLevel()
  }

  override def onResume(): Unit = {
    paused.value = false
    canvas.opacity = 1

    listener.onResumeLevel()
  }

  override def onExit(): Unit = {
    listener.onStopLevel()
  }

  /**
    * OnMouseClicked handler, reacting only if the game is not paused
    */
  onMouseClicked = mouseEvent => {
    /* Creating a circle representing the player click */
    val clickCircle = Circle(mouseEvent.getX, mouseEvent.getY, 2.0, defaultPlayerColor)
    content.add(clickCircle)
    val fadeOutTransition = new FadeTransition(Duration.apply(2000), clickCircle) {
      fromValue = 1.0
      toValue = 0.0
      onFinished = _ => content.remove(clickCircle)
    }
    fadeOutTransition.play()

    levelContext match {
      case Some(lc) => if (!paused.value) lc notifyMouseEvent MouseEventWrapper(Point(mouseEvent.getX, mouseEvent.getY))
      case _ =>
    }
  }

  override def onLevelSetup(mapShape: MapShape): Unit = mapBorder match {
    case Some(e) => throw new IllegalStateException("Map has already been set")
    case _ =>
      val center = Point(mapShape.center._1, mapShape.center._2)
      mapShape match {
        case c: MapShape.Circle => mapBorder = Option(new Circle {
          centerX = center.x
          centerY = center.y
          radius = c.radius
        })
        case r: MapShape.Rectangle => mapBorder = Option(new Rectangle {
          x = center.x - r.base / 2
          y = center.y - r.height / 2
          width = r.base
          height = r.height
        })
      }

      /* Configuring the mapBorder */
      mapBorder.get.fill = Color.Transparent
      mapBorder.get.stroke = Color.White
      mapBorder.get.strokeWidth = 2.0
      mapBorder.get.opacity <== canvas.opacity

      /* Adding the mapBorder */
      content.add(mapBorder.get)

      /* Starting the level */
      startLevel()
  }

  override def onDrawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper]): Unit = {

    var entitiesWrappers : Seq[(DrawableWrapper, Color)] = Seq()
    var specialWrappers : Seq[(DrawableWrapper, Color)] = entities filter(e => e.entityType.equals(EntityType.Attractive) || e.entityType.equals(EntityType.Repulse)) map(e => e.entityType match {
      case EntityType.Attractive => (e, attractiveCellColor)
      case EntityType.Repulse => (e, repulsiveCellColor)
    })

    playerEntity match {
      /* The player is present */
      case Some(pe) => entitiesWrappers = calculateColors(defaultEntityMinColor, defaultEntityMaxColor, defaultPlayerColor, pe, entities)
      /* The player is not present */
      case _ => entitiesWrappers = calculateColors(defaultEntityMinColor, defaultEntityMaxColor, entities)
    }

    /* We must draw to the screen the entire collection */
    Platform.runLater({
      /* Clear the screen */
      canvas.graphicsContext2D.clearRect(0, 0, width.value, height.value)
      /* Draw the background */
      canvas.graphicsContext2D.drawImage(backgroundImage, 0, 0, width.value, height.value)
      /* Draw the entities */
      playerEntity match  {
        case Some(pe) => (entitiesWrappers ++ specialWrappers) foreach(e => e._1 match {
          case `pe` => playerCellDrawable.draw(e._1, e._2)
          case _ => cellDrawable.draw(e._1, e._2)
        })
        case _ => (entitiesWrappers ++ specialWrappers) foreach(e => cellDrawable.draw(e._1, e._2))
      }
    })
  }

  override def onLevelEnd(levelResult: Boolean): Unit = {
    /* Creating an end screen with a button */
    val endScreen = LevelScreen.Builder(this)
      .withText(if (!levelResult) "You won!" else "You lost.", 50, Color.White)
      .withButton("Return to Level Selection", _ => onExit())
      .build()
    endScreen.opacity = 0.0

    /* Fade in/fade out transition */
    new FadeTransition(Duration.apply(3000), canvas) {
      fromValue = 1.0
      toValue = 0.0
      onFinished = _ => {
        /* Remove all the contents and add the end screen */
        content.clear()
        content.add(endScreen)
        new FadeTransition(Duration.apply(3000), endScreen) {
          fromValue = 0.0
          toValue = 1.0
        }.play()
      }
    }.play()
  }

  /**
    * This method calculates the color of the input entities, interpolating and normalizing it according to the entities size
    * @param minColor the base lower Color
    * @param maxColor the base upper Color
    * @param entities the input entities
    * @return the sequence of pair where the first field is the entity and the second is the color
    */
  private def calculateColors(minColor: Color, maxColor: Color, entities: Seq[DrawableWrapper]): Seq[(DrawableWrapper, Color)] = {
    entities match {
      case Nil => Seq()
      case _ =>
      /* Calculate the min and max radius among the entities */
      val endRadius = getEntitiesExtremeRadiusValues(entities)

      entities map( e => {
        /* Normalize the entity radius */
        val normalizedRadius = normalize(e.radius, endRadius._1, endRadius._2)
        /* Create a pair where the second value is the interpolated color between the two base colors */
        (e, minColor.interpolate(maxColor, normalizedRadius))
      }) seq
    }
  }

  /**
    * This method calculates the color of the input entities, interpolating and normalizing it according to the entities size
    *
    * @param minColor the base lower Color
    * @param maxColor the base upper Color
    * @param playerColor the base player Color
    * @param playerEntity the player entity
    * @param entities the input entities
    * @return the sequence of pair where the first field is the entity and the second is the color
    */
  private def calculateColors(minColor: Color, maxColor: Color, playerColor: Color, playerEntity: DrawableWrapper, entities: Seq[DrawableWrapper]): Seq[(DrawableWrapper, Color)] = {
    entities match {
      case Nil => Seq()
      case _ =>
        /* Calculate the min and max radius among the entities, considering the player */
        val endRadius = getEntitiesExtremeRadiusValues(entities)

        entities map {
          /* The entity has the same radius of the player so it will have the same color */
          case e if e.radius == playerEntity.radius => (e, playerColor)
          case e if e.radius < playerEntity.radius =>
            /* The entity is smaller than the player so it's color hue will approach the min one */
            val normalizedRadius = normalize(e.radius, endRadius._1, playerEntity.radius)
            (e, minColor.interpolate(playerColor, normalizedRadius))
          case e =>
            /* The entity is larger than the player so it's color hue will approach the max one */
            val normalizedRadius = normalize(e.radius, playerEntity.radius, endRadius._2)
            (e, playerColor.interpolate(maxColor, normalizedRadius))
        } seq
    }
  }

  /**
    * This method returns a pair consisting of the min and the max radius found in the entities sequence
    * @param entities a DrawableWrapper sequence
    * @return a pair consisting of the min and the max radius found; an IllegalArgumentException on empty sequence
    */
  private def getEntitiesExtremeRadiusValues(entities: Seq[DrawableWrapper]): (Double, Double) = {
    /* Sorting the entities */
    val sorted = entities.sortWith(_.radius < _.radius)
    /* Retrieving the min and the max radius values */
    sorted match {
      case head +: _ :+ tail => (head.radius, tail.radius)
      case head +: _ => (head.radius, head.radius)
      case _ => throw new IllegalArgumentException("Could not determine the min and max radius from an empty sequence of entities")
    }
  }

}

/**
  * Trait which gets notified when a LevelScene event occurs
  */
trait LevelSceneListener {

  /**
    * Called when the level gets started
    */
  def onStartLevel()

  /**
    * Called when the level gets paused
    */
  def onPauseLevel()

  /**
    * Called when the level gets resumed
    */
  def onResumeLevel()

  /**
    * Called when the level gets stopped
    */
  def onStopLevel()

}
