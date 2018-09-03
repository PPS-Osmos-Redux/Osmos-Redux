package it.unibo.osmos.redux

import it.unibo.osmos.redux.mvc.model.SinglePlayerLevels
import it.unibo.osmos.redux.mvc.view.events.{GameLost, GameWon}
import org.scalatest.{BeforeAndAfter, FunSuite}

class TestLevelsProgression extends FunSuite with BeforeAndAfter {

  after(SinglePlayerLevels.reset())

  test("Test complete levels"){
    assert(SinglePlayerLevels.toDoLevel(SinglePlayerLevels.getCampaignLevels).equals(SinglePlayerLevels.getLevelsInfo.head.name))

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo.head.name)
    assert(SinglePlayerLevels.toDoLevel(SinglePlayerLevels.getCampaignLevels).equals(SinglePlayerLevels.getLevelsInfo(1).name))

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo(1).name)
    assert(SinglePlayerLevels.toDoLevel(SinglePlayerLevels.getCampaignLevels).equals(SinglePlayerLevels.getLevelsInfo(2).name))

    SinglePlayerLevels.newEndGameEvent(GameLost, SinglePlayerLevels.getLevelsInfo(2).name)
    assert(SinglePlayerLevels.toDoLevel(SinglePlayerLevels.getCampaignLevels).equals(SinglePlayerLevels.getLevelsInfo(2).name))

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo(2).name)
    assert(SinglePlayerLevels.toDoLevel(SinglePlayerLevels.getCampaignLevels).equals(SinglePlayerLevels.getLevelsInfo(3).name))

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo(3).name)
    assert(SinglePlayerLevels.toDoLevel(SinglePlayerLevels.getCampaignLevels).equals(SinglePlayerLevels.getLevelsInfo(4).name))

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo(4).name)
    assert(SinglePlayerLevels.toDoLevel(SinglePlayerLevels.getCampaignLevels).equals(SinglePlayerLevels.getLevelsInfo(4).name))
  }

  test("Test user statistics") {
    assert(SinglePlayerLevels.toDoLevel(SinglePlayerLevels.getCampaignLevels).equals(SinglePlayerLevels.getLevelsInfo.head.name))

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo.head.name)
    assert(SinglePlayerLevels.getCampaignLevels.head.levelStat.victories == 1)
    assert(SinglePlayerLevels.getCampaignLevels.head.levelStat.defeats == 0)

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo(1).name)
    assert(SinglePlayerLevels.getCampaignLevels(1).levelStat.victories == 1)
    assert(SinglePlayerLevels.getCampaignLevels(1).levelStat.defeats == 0)

    SinglePlayerLevels.newEndGameEvent(GameLost, SinglePlayerLevels.getLevelsInfo(2).name)
    assert(SinglePlayerLevels.getCampaignLevels(2).levelStat.victories == 0)
    assert(SinglePlayerLevels.getCampaignLevels(2).levelStat.defeats == 1)

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo(2).name)
    assert(SinglePlayerLevels.getCampaignLevels(2).levelStat.victories == 1)
    assert(SinglePlayerLevels.getCampaignLevels(2).levelStat.defeats == 1)

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo(3).name)
    assert(SinglePlayerLevels.getCampaignLevels(3).levelStat.victories == 1)
    assert(SinglePlayerLevels.getCampaignLevels(3).levelStat.defeats == 0)

    SinglePlayerLevels.newEndGameEvent(GameWon, SinglePlayerLevels.getLevelsInfo(4).name)
    assert(SinglePlayerLevels.getCampaignLevels(4).levelStat.victories == 1)
    assert(SinglePlayerLevels.getCampaignLevels(4).levelStat.defeats == 0)
  }
}
