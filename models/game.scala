import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.mutable._
import models._

case class Join(player:Player)
case class Leave(player:Player)
case class Command(bytes:Array[Byte])

object Game extends Actor{
  var players:ArrayBuffer[Player] = new ArrayBuffer
  val fields:Array[Field] = Array.fill(144){ new Field }
  //val ball:Ball = new Ball()

  private def move(player:Player,dx:Int,dy:Int):Unit = {
    val field = player.field
    val newFieldIndex:Int = field.position + (12 * dy) + dx
    if(newFieldIndex > 0 && newFieldIndex < 144){
      val newField = fields(newFieldIndex)
      newField.player = player
      field.player = null
      player.field = newField
    }
  }

  private def notifyAll():Unit = {
    for (player <- players){
      player ! WriteBuffer(currentPosition.getBytes)
    }
  }

  private def currentPosition():String = {
    var buff:String = ""
    for (player <- players){
      buff += player.toStr
    }
    return buff
  }

  def act() = {
    loop {
      react {
        case Join(player) => {
          players += player
        }
        case Leave(player) => {
          players -= player
        }
        case Command(bytes) => {
          val str:String = new String(bytes);
          str match{
            case "h" => {
              //left
            }
            case "l" => {
              //right
            }
            case "k" => {
              //up
            }
            case "j" => {
              //down
            }
          }
        }
      }
    }
  }

}
