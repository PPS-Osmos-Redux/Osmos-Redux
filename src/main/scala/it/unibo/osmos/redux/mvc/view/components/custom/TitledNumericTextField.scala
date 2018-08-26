package it.unibo.osmos.redux.mvc.view.components.custom

import scalafx.beans.property.StringProperty
import scalafx.scene.control.TextFormatter.Change
import scalafx.scene.control.{TextField, TextFormatter}
import scalafx.util.converter.IntStringConverter

class TitledNumericTextField(override val title: StringProperty) extends TitledNode[TextField](title, vertical = false) {

  def this(title: String) {
    this(StringProperty(title))
  }

  /**
    * The maximum value
    */
  var maxValue: Int = Int.MaxValue
  /**
    * The minimum value
    */
  var minValue: Int = Int.MinValue

  /**
    * The node that will be shown after the text
    *
    * @return a node of type N <: Node
    */
  override def node: TextField = new TextField(){
    editable = true
    prefWidth <== maxWidth
    textFormatter = new TextFormatter[Int](new IntStringConverter, 0, { c: Change => {
      val input = c.getText
      val isNumber = input.matches("\\d+")
      if (!isNumber) c.setText("")
      if (isNumber && (maxValue < c.getControlNewText.toInt || minValue > c.getControlNewText.toInt)) c.setText("")
      c
    }})
  }
}

