package it.unibo.osmos.redux.ecs.systems.victoryconditions

import it.unibo.osmos.redux.ecs.entities.EntityType
import it.unibo.osmos.redux.ecs.entities.properties.composed.DeathProperty

import scala.collection.mutable.ListBuffer

/** class implementing become huge victory condition
  *
  * victory is fulfilled when the player's radius is greater
  * than the other matter cells radius by a certain percentage
  */
case class BecomeHugeVictoryCondition() extends VictoryCondition {

  private val radiusPercentageToBeHuge = 70

  override def check(playerCellEntity: DeathProperty, entityList: ListBuffer[DeathProperty]): Boolean = {
    val playerRadius = playerCellEntity.getDimensionComponent.radius
    var totalRadius: Double = 0
    entityList.filter(e => !e.getTypeComponent.typeEntity.equals(EntityType.AntiMatter))
      .foreach(entity => totalRadius += entity.getDimensionComponent.radius)
    playerRadius / totalRadius * 100 > radiusPercentageToBeHuge
  }
}
