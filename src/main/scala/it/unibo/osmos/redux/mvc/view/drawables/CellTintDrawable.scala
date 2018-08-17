package it.unibo.osmos.redux.mvc.view.drawables

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.image.Image
import scalafx.scene.paint.Color

class CellTintDrawable(private var _image: Image, override val graphicsContext: GraphicsContext) extends ImageDrawable(_image, graphicsContext) {

  /**
    * Draws the cell on the canvas
    * @param dw the drawable wrapper containing the drawable info
    * @param color the color of the cell
    */
  override def draw(dw: DrawableWrapper, color: Color): Unit = {
    /* Line */
    graphicsContext.stroke = color
    //graphicsContext.strokeArc(dw.center.x, dw.center.y,20, 20, -45, 240, ArcType.Open)
    graphicsContext.lineWidth = 2
    //TODO: find a better way
    graphicsContext.strokeLine(dw.center.x, dw.center.y, dw.center.x + dw.speed._1 * dw.radius, dw.center.y + dw.speed._2 * dw.radius)

    graphicsContext.fill = color
    /* Calling the super */
    super.draw(dw, color)
  }

}
