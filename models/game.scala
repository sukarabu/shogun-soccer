package com.sukarabu.sgsoccer.models

import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.mutable._
import com.sukarabu.sgsoccer.models._
import com.sukarabu.sgsoccer.EchoServerNIO
import com.sukarabu.sgsoccer.EchoActor

case class Join(player:Player)
case class Leave(player:Player)
case class Command(player:Player,bytes:Array[Byte])

object Game extends Actor{
  var players:ArrayBuffer[Player] = new ArrayBuffer
  val fields:Array[Field] = (0 to 143).toArray.map{i => new Field(i%12,i/12) }
  //val ball:Ball = new Ball()

  private def move(player:Player,dx:Int,dy:Int):Unit = {
    val field = player.field
    val newFieldIndex:Int = field.position + (11 * dy) + dx
    if(newFieldIndex > 0 && newFieldIndex < 144){
      val newField = fields(newFieldIndex)
      field.player = null
      newField.player = player
      player.field = newField
    }
  }

  private def notifyAllPlayer():Unit = {
    for (player <- players){
      player ! WriteBuffer(currentPosition.getBytes,EchoActor.selector)
    }
  }

  private def currentPosition():String = {
    var buff:String = ""
    for (i <- 0 to 143){
      buff += fields(i).toStr
      if(i != 0 && i % 11 == 0) buff += "\n"
    }
    return buff
  }

  def act() = {
    loop {
      react {
        case Join(player) => {
          fields.find{f => f.player == null} match {
            case Some(f) => {
              f.player = player
              player.field = f
              players += player
            }
            case None => 
          }
        }
        case Leave(player) => {
          players -= player
        }
        case Command(player,bytes) => {
          val str:String = new String(bytes);
          str.trim match{
            case "h" => { //left
              move(player,-1,0)
            }
            case "l" => { //right
              move(player,1,0)
            }
            case "k" => { //up
              move(player,0,-1)
            }
            case "j" => { //down
              move(player,0,1)
            }
            case _ => {
              println(str)
            }
          }
          notifyAllPlayer
        }
      }
    }
  }
}
