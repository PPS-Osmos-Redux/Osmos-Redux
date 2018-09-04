package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.ecs.entities.builders._
import it.unibo.osmos.redux.mvc.controller.LevelInfo
import it.unibo.osmos.redux.mvc.model.MapShape.{Circle, Rectangle}
import it.unibo.osmos.redux.mvc.model._
import it.unibo.osmos.redux.utils.Point
import org.scalatest.{BeforeAndAfter, FunSuite}

class TestMapBoundariesCheck extends FunSuite with BeforeAndAfter {

  val d = DimensionComponent(2)
  val p = Point(-3, -3)
  val p1 = Point(3, -3)
  val p2 = Point(-3, 3)
  val p3 = Point(3, 3)
  var ce:CellEntity = _
  var pce:CellEntity = _
  var gc:CellEntity = _
  var sc:CellEntity = _

  before{
    ce = new CellBuilder().withPosition(p).withDimension(d).build
    pce = PlayerCellBuilder().withPosition(p1).withDimension(d).build
    gc = GravityCellBuilder().withPosition(p2).withDimension(d).build
    sc = SentientCellBuilder().withPosition(p3).withDimension(d).build
  }

  val levelId: String = 1.toString

  test("Rectangular map boundaries") {

    val listCells = List(ce, pce, gc, sc)
    //LevelMap
    val rectangle: MapShape = Rectangle((0, 0), 10, 10)
    val levelMap: LevelMap = LevelMap(rectangle, CollisionRules.bouncing)
    //Level
    var level: Level = Level(LevelInfo(levelId, VictoryRules.becomeTheBiggest),
      levelMap,
      listCells)
    level.checkCellPosition()
    assert(listCells.size.equals(level.entities.size))

    ce.getPositionComponent.point_(Point(-4, -4))
    pce.getPositionComponent.point_(Point(5, -5))
    gc.getPositionComponent.point_(Point(0, 12))
    sc.getPositionComponent.point_(Point(12, 0))
    level = Level(LevelInfo(levelId, VictoryRules.becomeTheBiggest), levelMap, List(ce, pce, gc, sc))
    level.checkCellPosition()
    assert(level.entities.isEmpty)
  }

  test("Circular map boundaries") {
    val listCells = List(ce, pce, gc, sc)
    //LevelMap
    val circle: MapShape = Circle((0, 0), 5)
    val levelMap: LevelMap = LevelMap(circle, CollisionRules.bouncing)
    //Level
    var level: Level = Level(LevelInfo(levelId, VictoryRules.becomeTheBiggest),
      levelMap,
      listCells)
    level.checkCellPosition()
    assert(level.entities.isEmpty)

    ce.getPositionComponent.point_(Point(-2, 2))
    pce.getPositionComponent.point_(Point(2, 2))
    gc.getPositionComponent.point_(Point(2, -2))
    sc.getPositionComponent.point_(Point(-2, -2))
    level = Level(LevelInfo(levelId, VictoryRules.becomeTheBiggest), levelMap, List(ce, pce, gc, sc))
    level.checkCellPosition()
    assert(level.entities.size.equals(listCells.size))
  }
}
