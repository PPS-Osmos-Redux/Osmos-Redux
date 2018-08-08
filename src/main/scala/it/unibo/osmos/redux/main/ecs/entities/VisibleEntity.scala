package it.unibo.osmos.redux.main.ecs.entities

import it.unibo.osmos.redux.main.ecs.components.VisibleComponent

trait VisibleEntity {

  /**
    * Gets the Visible Component
    *
    * @return the Visible Component
    */
  def getVisibleComponent: VisibleComponent
}