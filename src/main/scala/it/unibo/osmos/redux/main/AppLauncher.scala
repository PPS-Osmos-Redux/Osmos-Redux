package it.unibo.osmos.redux.main

import it.unibo.osmos.redux.main.mvc.controller.Controller
import it.unibo.osmos.redux.main.mvc.model.Model
import it.unibo.osmos.redux.main.mvc.view.View
import scalafx.application.JFXApp

/**
  * Application entry point.
  */
object AppLauncher extends JFXApp {

  //TODO: replace null with proper implementation
  val model: Model = null
  val controller: Controller = null
  val view = View(this)
  view.setController(controller)
  println(Thread.currentThread())
  view.setup()

}