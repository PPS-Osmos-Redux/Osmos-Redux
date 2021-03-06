package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.ecs.entities.{CellBuilder, CellEntity, EntityType}
import it.unibo.osmos.redux.mvc.view.components.custom.TitledDoubleField
import scalafx.beans.property.{DoubleProperty, ObjectProperty}
import scalafx.scene.control.{Label, TextField}
import scalafx.scene.layout.{HBox, VBox}

/** A panel showing input nodes which is also capable of providing the requested CellEntity */
class CellEntityCreator extends BaseEditorCreator[CellEntity] with EditorCellBuilderConfigurator {

  /** Position node */
  val x: ObjectProperty[Double] = ObjectProperty(300)
  val y: ObjectProperty[Double] = ObjectProperty(300)
  val positionNode = new VBox(2.0, new Label("Position"), new HBox(new Label("x: "), new TextField() {
    editable = false
    text <== x.asString()
  }), new HBox(new Label("y: "), new TextField() {
    editable = false
    text <== y.asString()
  }))
  /** Radius node */
  val radius: ObjectProperty[Double] = ObjectProperty(50)
  /** Speed node */
  val xSpeed: DoubleProperty = DoubleProperty(0.0)
  val ySpeed: DoubleProperty = DoubleProperty(0.0)
  val speedNode = new VBox(2.0, new Label("Speed"),
    new TitledDoubleField("x: ", xSpeed).innerNode,
    new TitledDoubleField("y: ", ySpeed).innerNode
  )
  /** Acceleration node */
  val xAcceleration: DoubleProperty = DoubleProperty(0.0)
  val yAcceleration: DoubleProperty = DoubleProperty(0.0)
  val accelerationNode = new VBox(2.0, new Label("Acceleration"),
    new TitledDoubleField("x: ", xAcceleration).innerNode,
    new TitledDoubleField("y: ", yAcceleration).innerNode
  )
  private val radiusNode = new HBox(new Label("Radius: "), new TextField() {
    editable = false
    text <== radius.asString()
  })
  private[this] var _entityType: EntityType.Value = EntityType.Matter

  def entityType_=(value: EntityType.Value): Unit = {
    _entityType = value
  }

  children = Seq(positionNode, radiusNode, speedNode, accelerationNode)

  override def create(): CellEntity = {
    val builder = CellBuilder()
    configureBuilder(builder)
    builder.buildCellEntity()
  }

  /** Method that configures the basic cell builder. It may be overridden by other cell entity creators
    *
    * @param builder the cell builder
    */
  override def configureBuilder(builder: CellBuilder, withEntityType: Boolean = true): Unit = {
    builder
      .visible(true)
      .collidable(true)
      .withPosition(x.value, y.value)
      .withDimension(radius.value)
      .withSpeed(xSpeed.value, ySpeed.value)
      .withAcceleration(xAcceleration.value, yAcceleration.value)
    if (withEntityType) builder.withEntityType(_entityType)
  }

}
