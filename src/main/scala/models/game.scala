package com.sukarabu.sgsoccer.models

import java.nio.channels.SocketChannel
import java.util.Date
import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.mutable._
import com.sukarabu.sgsoccer.models._
import com.sukarabu.sgsoccer.EchoServerNIO
import com.sukarabu.sgsoccer.EchoActor
import com.sukarabu.sgsoccer.WriteToBuffer

case class Join(id:String,channel:SocketChannel)
case class Leave(player:Player)
case class Command(channel:SocketChannel,bytes:Array[Byte])

object Game extends Actor{
  var players:ArrayBuffer[Player] = new ArrayBuffer
  val fields:Array[Field] = (0 to 143).toArray.map{i => new Field(i%12,i/12) }
  val channelToPlayer:HashMap[SocketChannel,Player] = new HashMap[SocketChannel,Player]

  def getPlayerId(seed:String):String = {
    return seed + new Date().toString()
  }

  private def move(player:Player,dx:Int,dy:Int):Unit = {
    val field = player.field
    val newFieldIndex:Int = field.position + (12 * dy) + dx
    if(newFieldIndex >= 0 && newFieldIndex < 144){
      val newField = fields(newFieldIndex)
      field.player = null
      newField.player = player
      player.field = newField
    }
  }

  private def notifyAllPlayer():Unit = {
    for (player <- players){
      EchoActor ! WriteToBuffer(player.id,currentPosition.getBytes)
    }
  }

  private def currentPosition():String = {
    var buff:String = ""
    for (i <- 0 to 143){
      buff += fields(i).toStr
      if(i != 0 && (i+1) % 12 == 0) buff += "\n"
    }
    return buff
  }

  def act() = {
    loop {
      react {
        case Join(id,channel) => {
          val player = new Player(id)
          channelToPlayer.put(channel,player)
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
        case Command(channel,bytes) => {
          val player = channelToPlayer(channel)
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
              println("no commands matched:"+str)
            }
          }
          notifyAllPlayer
        }
      }
    }
  }
}
