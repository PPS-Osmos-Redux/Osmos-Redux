package it.unibo.osmos.redux.ecs.systems.victoryconditions

import it.unibo.osmos.redux.ecs.entities.EntityType
import it.unibo.osmos.redux.ecs.entities.properties.composed.DeathProperty

import scala.collection.mutable.ListBuffer

/** class implementing absorb cells that share a common type victory condition
  *
  * victory is fulfilled when there are no more cells with the specified type
  */
case class AbsorbCellsWithTypeVictoryCondition(entityType: EntityType.Value) extends VictoryCondition {

  override def check(playerCellEntity: DeathProperty, entityList: ListBuffer[DeathProperty]): Boolean = {
    !entityList.exists(entity => entity.getTypeComponent.typeEntity == entityType)
  }
}
