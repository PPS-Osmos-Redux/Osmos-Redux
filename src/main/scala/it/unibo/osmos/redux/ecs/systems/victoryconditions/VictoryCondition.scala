package it.unibo.osmos.redux.ecs.systems.victoryconditions

import it.unibo.osmos.redux.ecs.entities.properties.composed.DeathProperty

import scala.collection.mutable.ListBuffer

/** trait for victory condition strategy */
trait VictoryCondition {

  /** Checks if the victory condition is fulfilled
    *
    * @param playerCellEntity player's entity
    * @param entityList       entities present in this game instant
    * @return the evaluation result
    */
  def check(playerCellEntity: DeathProperty, entityList: ListBuffer[DeathProperty]): Boolean
}
