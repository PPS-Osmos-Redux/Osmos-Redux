package it.unibo.osmos.redux.mvc.model

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, GravityCellEntity, PlayerCellEntity, SentientCellEntity, _}
import it.unibo.osmos.redux.mvc.controller.levels.structure._
import it.unibo.osmos.redux.mvc.controller.{Setting, SettingsTypes, Volume}
import it.unibo.osmos.redux.mvc.view.drawables.DrawableWrapper
import it.unibo.osmos.redux.utils.{Logger, Point}
import org.apache.commons.lang3.SerializationException
import spray.json.DefaultJsonProtocol._
import spray.json._

/** Json implicit stategies for: convert json to Level or convert Level to json */
object JsonProtocols {
  implicit val who: String = "JsonProtocols"

  /** Convert acceleration component to/from json */
  implicit object AccelerationFormatter extends RootJsonFormat[AccelerationComponent] {
    def write(acceleration: AccelerationComponent) = JsObject(
      "accelerationX" -> JsNumber(acceleration.vector.x),
      "accelerationY" -> JsNumber(acceleration.vector.y)
    )

    def read(value: JsValue): AccelerationComponent = {
      value.asJsObject.getFields("accelerationX", "accelerationY") match {
        case Seq(JsNumber(accelerationX), JsNumber(accelerationY)) =>
          AccelerationComponent(accelerationX.toDouble, accelerationY.toDouble)
        case _ => throw DeserializationException("Acceleration component expected")
      }
    }
  }

  /** Convert collidable component to/from json */
  implicit object CollidableFormatter extends RootJsonFormat[CollidableComponent] {
    def write(collidable: CollidableComponent) =
      JsObject("collidable" -> JsBoolean(collidable.isCollidable))

    def read(value: JsValue): CollidableComponent = {
      value.asJsObject.getFields("collidable") match {
        case Seq(JsBoolean(collidable)) => CollidableComponent(collidable)
        case _ => throw DeserializationException("Collidable component expected")
      }
    }
  }

  /** Convert visible component to/from json */
  implicit object VisibleFormatter extends RootJsonFormat[VisibleComponent] {
    def write(visible: VisibleComponent) = JsObject("visible" -> JsBoolean(visible.isVisible))

    def read(value: JsValue): VisibleComponent = {
      value.asJsObject.getFields("visible") match {
        case Seq(JsBoolean(visible)) => VisibleComponent(visible)
        case _ => throw DeserializationException("Collidable component expected")
      }
    }
  }

  /** Convert dimension component to/from json */
  implicit object DimensionFormatter extends RootJsonFormat[DimensionComponent] {
    def write(dimension: DimensionComponent) = JsObject("radius" -> JsNumber(dimension.radius))

    def read(value: JsValue): DimensionComponent = {
      value.asJsObject.getFields("radius") match {
        case Seq(JsNumber(radius)) => DimensionComponent(radius.toDouble)
        case _ => throw DeserializationException("Dimension component expected")
      }
    }
  }

  /** Convert point to/from json */
  implicit object PointFormatter extends RootJsonFormat[Point] {
    def write(point: Point) = JsObject("x" -> JsNumber(point.x), "y" -> JsNumber(point.y))

    def read(value: JsValue): Point = {
      value.asJsObject.getFields("x", "y") match {
        case Seq(JsNumber(x), JsNumber(y)) => Point(x.toDouble, y.toDouble)
        case _ => throw DeserializationException("Point expected")
      }
    }
  }

  /** Convert position component to/from json */
  implicit object PositionFormatter extends RootJsonFormat[PositionComponent] {
    def write(position: PositionComponent) = JsObject("point" -> position.point.toJson)

    def read(value: JsValue): PositionComponent = {
      value.asJsObject.getFields("point") match {
        case Seq(point) => PositionComponent(point.convertTo[Point])
        case _ => throw DeserializationException("Position component expected")
      }
    }
  }

  /** Convert speed component to/from json */
  implicit object SpeedFormatter extends RootJsonFormat[SpeedComponent] {
    def write(speed: SpeedComponent) =
      JsObject("speedX" -> JsNumber(speed.vector.x), "speedY" -> JsNumber(speed.vector.y))

    def read(value: JsValue): SpeedComponent = {
      value.asJsObject.getFields("speedX", "speedY") match {
        case Seq(JsNumber(speedX), JsNumber(speedY)) =>
          SpeedComponent(speedX.toDouble, speedY.toDouble)
        case _ => throw DeserializationException("Speed component expected")
      }
    }
  }

  /** Convert entity type to/from json */
  implicit object EntityTypeFormatter extends RootJsonFormat[EntityType.Value] {
    def write(entityType: EntityType.Value) =
      JsObject("entityType" -> JsString(entityType.toString))

    def read(value: JsValue): EntityType.Value = {
      value.asJsObject.getFields("entityType") match {
        case Seq(JsString(entityType)) => EntityType.withName(entityType)
        case _ => throw DeserializationException("EntityType component expected")
      }
    }
  }

  /** Convert type component to/from json */
  implicit object ComponentTypeFormatter extends RootJsonFormat[TypeComponent] {
    def write(entityType: TypeComponent) =
      JsObject("componentType" -> JsString(entityType.typeEntity.toString))

    def read(value: JsValue): TypeComponent = {
      value.asJsObject.getFields("componentType") match {
        case Seq(JsString(componentType)) => TypeComponent(EntityType.withName(componentType))
        case _ => throw DeserializationException("Type component component expected")
      }
    }
  }

  /** Convert spawner component to/from json */
  implicit object SpawnerFormatter extends RootJsonFormat[SpawnerComponent] {
    def write(spawner: SpawnerComponent) = JsObject("canSpawn" -> JsBoolean(spawner.canSpawn))

    def read(value: JsValue): SpawnerComponent = {
      value.asJsObject.getFields("canSpawn") match {
        case Seq(JsBoolean(canSpawn)) => SpawnerComponent(canSpawn)
        case _ => throw DeserializationException("Spawner expected")
      }
    }
  }

  /** Convert specific weight component to/from json */
  implicit object SpecificWeightFormatter extends RootJsonFormat[SpecificWeightComponent] {
    def write(specificWeight: SpecificWeightComponent) = JsObject("specificWeight" -> JsNumber(specificWeight.specificWeight))

    def read(value: JsValue): SpecificWeightComponent = {
      value.asJsObject.getFields("specificWeight") match {
        case Seq(JsNumber(specificWeight)) => SpecificWeightComponent(specificWeight.toDouble)
        case _ => throw DeserializationException("Specific weight component expected")
      }
    }
  }

  /** Convert gravity cell to/from json */
  implicit object GravityCellEntityFormatter extends RootJsonFormat[GravityCellEntity] {
    def write(gravityCell: GravityCellEntity) = JsObject(
      "cellType" -> JsString(CellType.gravityCell),
      "acceleration" -> gravityCell.getAccelerationComponent.toJson,
      "collidable" -> gravityCell.getCollidableComponent.toJson,
      "dimension" -> gravityCell.getDimensionComponent.toJson,
      "position" -> gravityCell.getPositionComponent.toJson,
      "speed" -> gravityCell.getSpeedComponent.toJson,
      "visible" -> gravityCell.getVisibleComponent.toJson,
      "typeEntity" -> gravityCell.getTypeComponent.toJson,
      "specificWeight" -> gravityCell.getSpecificWeightComponent.toJson)

    def read(value: JsValue): GravityCellEntity = {
      value.asJsObject.getFields("acceleration",
        "collidable",
        "dimension",
        "position",
        "speed",
        "visible",
        "typeEntity",
        "specificWeight") match {
        case Seq(acceleration, collidable, dimension, position, speed, visible, typeEntity, specificWeight) =>
          GravityCellEntity(acceleration.convertTo[AccelerationComponent],
            collidable.convertTo[CollidableComponent],
            dimension.convertTo[DimensionComponent],
            position.convertTo[PositionComponent],
            speed.convertTo[SpeedComponent],
            visible.convertTo[VisibleComponent],
            typeEntity.convertTo[TypeComponent],
            specificWeight.convertTo[SpecificWeightComponent])
        case _ => throw DeserializationException("Gravity cell entity expected")
      }
    }
  }

  /** Convert player cell to/from json */
  implicit object PlayerCellEntityFormatter extends RootJsonFormat[PlayerCellEntity] {
    def write(playerCell: PlayerCellEntity) = JsObject(
      "cellType" -> JsString(CellType.playerCell),
      "acceleration" -> playerCell.getAccelerationComponent.toJson,
      "collidable" -> playerCell.getCollidableComponent.toJson,
      "dimension" -> playerCell.getDimensionComponent.toJson,
      "position" -> playerCell.getPositionComponent.toJson,
      "speed" -> playerCell.getSpeedComponent.toJson,
      "visible" -> playerCell.getVisibleComponent.toJson,
      "spawner" -> playerCell.getSpawnerComponent.toJson)

    def read(value: JsValue): PlayerCellEntity = {
      value.asJsObject.getFields("acceleration",
        "collidable",
        "dimension",
        "position",
        "speed",
        "visible",
        "spawner") match {
        case Seq(acceleration, collidable, dimension, position, speed, visible, spawner) =>
          PlayerCellEntity(acceleration.convertTo[AccelerationComponent],
            collidable.convertTo[CollidableComponent],
            dimension.convertTo[DimensionComponent],
            position.convertTo[PositionComponent],
            speed.convertTo[SpeedComponent],
            visible.convertTo[VisibleComponent],
            spawner.convertTo[SpawnerComponent])
        case _ => throw DeserializationException("Player cell entity expected")
      }
    }
  }

  /** Convert sentient cell to/from json */
  implicit object SentientCellEntityFormatter extends RootJsonFormat[SentientCellEntity] {
    def write(sentientCell: SentientCellEntity) = JsObject(
      "cellType" -> JsString(CellType.sentientCell),
      "acceleration" -> sentientCell.getAccelerationComponent.toJson,
      "collidable" -> sentientCell.getCollidableComponent.toJson,
      "dimension" -> sentientCell.getDimensionComponent.toJson,
      "position" -> sentientCell.getPositionComponent.toJson,
      "speed" -> sentientCell.getSpeedComponent.toJson,
      "visible" -> sentientCell.getVisibleComponent.toJson,
      "spawner" -> sentientCell.getSpawnerComponent.toJson)

    def read(value: JsValue): SentientCellEntity = {
      value.asJsObject.getFields("acceleration",
        "collidable",
        "dimension",
        "position",
        "speed",
        "visible",
        "spawner") match {
        case Seq(acceleration, collidable, dimension, position, speed, visible, spawner) =>
          SentientCellEntity(acceleration.convertTo[AccelerationComponent],
            collidable.convertTo[CollidableComponent],
            dimension.convertTo[DimensionComponent],
            position.convertTo[PositionComponent],
            speed.convertTo[SpeedComponent],
            visible.convertTo[VisibleComponent],
            spawner.convertTo[SpawnerComponent])
        case _ => throw DeserializationException("Sentient cell entity expected")
      }
    }
  }

  /** Convert basic cell to/from json */
  implicit object CellEntityFormatter extends RootJsonFormat[CellEntity] {
    def write(cellEntity: CellEntity): JsValue = cellEntity match {
      case sc: SentientCellEntity => sc.toJson
      case gc: GravityCellEntity => gc.toJson
      case pce: PlayerCellEntity => pce.toJson
      case _: CellEntity => JsObject("cellType" -> JsString(CellType.basicCell),
        "acceleration" -> cellEntity.getAccelerationComponent.toJson,
        "collidable" -> cellEntity.getCollidableComponent.toJson,
        "dimension" -> cellEntity.getDimensionComponent.toJson,
        "position" -> cellEntity.getPositionComponent.toJson,
        "speed" -> cellEntity.getSpeedComponent.toJson,
        "visible" -> cellEntity.getVisibleComponent.toJson,
        "typeEntity" -> cellEntity.getTypeComponent.toJson)
      case _ => Logger.log("Cell " + cellEntity + " currently is not managed!"); JsObject()
    }

    def read(value: JsValue): CellEntity = {
      value.asJsObject.getFields("cellType") match {
        case Seq(JsString(CellType.basicCell)) =>
          value.asJsObject.getFields("acceleration",
            "collidable",
            "dimension",
            "position",
            "speed",
            "visible",
            "typeEntity") match {
            case Seq(acceleration, collidable, dimension, position, speed, visible, typeEntity) =>
              CellEntity(acceleration.convertTo[AccelerationComponent],
                collidable.convertTo[CollidableComponent],
                dimension.convertTo[DimensionComponent],
                position.convertTo[PositionComponent],
                speed.convertTo[SpeedComponent],
                visible.convertTo[VisibleComponent],
                typeEntity.convertTo[TypeComponent])
            case _ => throw DeserializationException("Cell entity expected")
          }
        case Seq(JsString(CellType.sentientCell)) =>
          value.convertTo[SentientCellEntity]
        case Seq(JsString(CellType.gravityCell)) =>
          value.convertTo[GravityCellEntity]
        case Seq(JsString(CellType.playerCell)) =>
          value.convertTo[PlayerCellEntity]
        case _ => throw DeserializationException("Cell entity expected")
      }
    }
  }

  /** Convert map shape to/from json */
  implicit object MapShapeFormatter extends RootJsonFormat[MapShape] {
    def write(mapShape: MapShape): JsObject = mapShape match {
      case mapShape: MapShape.Rectangle => JsObject("center" -> mapShape.center.toJson,
        "mapShape" -> JsString(mapShape.mapShape.toString),
        "height" -> JsNumber(mapShape.height),
        "base" -> JsNumber(mapShape.base))
      case mapShape: MapShape.Circle => JsObject("center" -> mapShape.center.toJson,
        "mapShape" -> JsString(mapShape.mapShape.toString),
        "radius" -> JsNumber(mapShape.radius))
      case _ => throw new SerializationException("Shape " + mapShape.mapShape + " not managed!")
    }

    def read(value: JsValue): MapShape = {
      val rectangle = MapShapeType.Rectangle.toString
      val circle = MapShapeType.Circle.toString

      value.asJsObject.getFields("center", "mapShape") match {
        case Seq(center, JsString(`rectangle`)) =>
          value.asJsObject.getFields("height", "base") match {
            case Seq(JsNumber(height), JsNumber(base)) =>
              MapShape.Rectangle(center.convertTo[Point], height.toDouble, base.toDouble)
            case _ => throw DeserializationException("Rectangular map expected")
          }
        case Seq(center, JsString(`circle`)) =>
          value.asJsObject.getFields("radius") match {
            case Seq(JsNumber(radius)) =>
              MapShape.Circle(center.convertTo[Point], radius.toDouble)
            case _ => throw DeserializationException("Circular map expected")
          }
        case _ => throw DeserializationException("Map shape expected")
      }
    }
  }

  /** Convert level map to/from json */
  implicit object LevelMapFormatter extends RootJsonFormat[LevelMap] {
    def write(levelMap: LevelMap) = JsObject(
      "mapShape" -> levelMap.mapShape.toJson,
      "collisionRule" -> JsString(levelMap.collisionRule.toString))

    def read(value: JsValue): LevelMap = {
      value.asJsObject.getFields("mapShape", "collisionRule") match {
        case Seq(mapShape, JsString(collisionRule)) =>
          LevelMap(mapShape.convertTo[MapShape], CollisionRules.withName(collisionRule))
        case _ => throw DeserializationException("Level map expected")
      }
    }
  }

  /** Convert victory rule to/from json */
  implicit object VictoryRuleFormatter extends RootJsonFormat[VictoryRules.Value] {
    def write(vicRule: VictoryRules.Value) = JsObject("victoryRule" -> JsString(vicRule.toString))

    def read(value: JsValue): VictoryRules.Value = {
      value.asJsObject.getFields("victoryRule") match {
        case Seq(JsString(victoryRule)) => VictoryRules.withName(victoryRule)
        case _ => throw DeserializationException("Victory rule expected expected")
      }
    }
  }

  /** Convert LevelInfo to/from json
    *
    * Takes from levels only name, victoryRule and if present isAvailable
    */
  implicit object LevelInfoFormatter extends RootJsonFormat[LevelInfo] {
    def write(levelInfo: LevelInfo) = JsObject(
      "name" -> JsString(levelInfo.name),
      "victoryRule" -> levelInfo.victoryRule.toJson,
      "isAvailable" -> JsBoolean(levelInfo.isAvailable))

    def read(value: JsValue): LevelInfo = {
      value.asJsObject.getFields("name", "victoryRule", "isAvailable") match {
        case Seq(JsString(name), victoryRule, JsBoolean(isAvailable)) =>
          LevelInfo(name, victoryRule.convertTo[VictoryRules.Value], isAvailable)
        case Seq(JsString(name), victoryRule) =>
          LevelInfo(name, victoryRule.convertTo[VictoryRules.Value])
        case _ => throw DeserializationException("Level info expected")
      }
    }
  }

  /** Convert Level to/from json */
  implicit object levelFormatter extends RootJsonFormat[Level] {
    def write(level: Level): JsObject = JsObject(
      "name" -> JsString(level.levelInfo.name),
      "victoryRule" -> level.levelInfo.victoryRule.toJson,
      "levelMap" -> level.levelMap.toJson,
      "entities" -> level.entities.toJson)

    def read(value: JsValue): Level = {
      value.asJsObject.getFields("name", "victoryRule", "levelMap", "entities") match {
        case Seq(JsString(levelId), victoryRule, levelMap, entities) =>
          Level(LevelInfo(levelId, victoryRule.convertTo[VictoryRules.Value]), levelMap.convertTo[LevelMap], entities.convertTo[List[CellEntity]])
        case _ => throw DeserializationException("Level expected")
      }
    }
  }

  /** Convert drawable wrapper to/from json */
  implicit val drawableWrapperFormatter: RootJsonFormat[DrawableWrapper] = jsonFormat4(DrawableWrapper)

  /** Convert campaign level stat to/from json */
  implicit val campaignLevelStatFormatter: RootJsonFormat[CampaignLevelStat] = jsonFormat2(CampaignLevelStat)

  /** Convert campaign level to/from json */
  implicit val campaignLevelsFormatter: RootJsonFormat[CampaignLevel] = jsonFormat2(CampaignLevel)

  /** Convert volume setting to/from json */
  implicit object volumeSettingFormatter extends RootJsonFormat[Volume] {
    def write(vol: Volume): JsObject = JsObject("settingType" -> JsString(SettingsTypes.Volume.toString),
      "vValue" -> JsNumber(vol.value))

    def read(value: JsValue): Volume = {
      value.asJsObject.getFields("vValue") match {
        case Seq(JsNumber(vValue)) => Volume(vValue.toDouble)
        case _ => throw DeserializationException("Volume expected")
      }
    }
  }

  /** Convert setting to/from json */
  implicit object settingFormatter extends RootJsonFormat[Setting] {
    def write(setting: Setting): JsValue = setting match {
      case vol: Volume => vol.toJson
      case _ => Logger.log("Error i can't convert to json " + setting.settingType)
        JsObject()
    }

    def read(value: JsValue): Setting = {
      val volumeSetting = SettingsTypes.Volume.toString
      value.asJsObject.getFields("settingType") match {
        case Seq(JsString(`volumeSetting`)) =>
          value.convertTo[Volume]
        case Seq(e) => throw DeserializationException("Setting expected " + e)
      }
    }
  }

}
