import models._

class Field{
  val x:Int = 0
  val y:Int = 0
  var player:Player = null

  def position():Int = {
    y * 12 + x
  }
}
