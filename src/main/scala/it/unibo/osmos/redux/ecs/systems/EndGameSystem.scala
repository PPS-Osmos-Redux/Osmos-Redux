package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.{DeathProperty, PlayerCellEntity, Property}
import it.unibo.osmos.redux.mvc.model.VictoryRules
import it.unibo.osmos.redux.mvc.view.events.{GameLost, GamePending, GameWon}
import it.unibo.osmos.redux.mvc.view.levels.GameStateHolder

/** System managing the level's ending rules
  *
  * @param levelContext object to notify the view of the end game result
  * @param victoryRules enumeration representing the level's victory rules
  */
case class EndGameSystem(levelContext: GameStateHolder, victoryRules: VictoryRules.Value) extends AbstractSystem[DeathProperty] {

  private val victoryCondition = victoryRules match {
    case VictoryRules.becomeTheBiggest => BecomeTheBiggestVictoryCondition()
    case _ => throw new NotImplementedError()
  }

  override def getGroupProperty: Class[DeathProperty] = classOf[DeathProperty]

  override def update(): Unit = {
    if (levelContext.gameCurrentState == GamePending) {
      //TODO: upgrade to support multiple players
      val optionalPlayer: Option[DeathProperty] = entities.find(entity => entity.isInstanceOf[PlayerCellEntity])

      optionalPlayer match {
        case Some(player) =>
          if (victoryCondition.check(player, entities)) {
            levelContext.notify(GameWon)
          }
        case None => levelContext.notify(GameLost)
      }
    }
  }
}
