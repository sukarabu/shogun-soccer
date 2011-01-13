package com.sukarabu.sgsoccer

import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.JavaConversions._
import scala.collection.mutable._

import java.io._
import java.nio._
import java.net._
import java.nio.charset._
import java.nio.channels.{ SelectionKey,Selector, ServerSocketChannel, SocketChannel }

import com.sukarabu.sgsoccer.models._

object EchoServerNIO{

  def main( args:Array[String] ){
    EchoActor.start()
    EchoActor ! Start( args.head.toInt )
  }
}

case class Select()
case class Start( port:Int )
case class Accept( key:SelectionKey )
case class Read( key:SelectionKey )
case class Write( key:SelectionKey )

object EchoActor extends Actor {
  val BUF_SIZE = 1024
  lazy val selector = Selector.open
  lazy val serverChannel = ServerSocketChannel.open
  val buffs = new ArrayBuffer[ByteBuffer]()
  val playerList = ListBuffer[Player]()
  val playerMap = collection.mutable.Map[SocketChannel, Player]()

  def act() = {
    loop {
      react {
        case Start( port ) => {
          serverChannel.configureBlocking(false)
          serverChannel.socket.bind( new InetSocketAddress( port ) )
          serverChannel.register(selector, SelectionKey.OP_ACCEPT )

          println( "EchoServer is running on port=%d".format( port ) )
          EchoActor ! Select
          Game.start()
        }
        case Select => {
          selector.select
          selector.selectedKeys.foreach{ key =>
            if( key.isAcceptable ){
              EchoActor ! Accept( key )
            } else if( key.isReadable ){
              EchoActor ! Read( key )
            } else if( key.isWritable ){
              EchoActor ! Write( key )
            }
          }
          EchoActor ! Select
        }
        case Accept( key ) => {
          val socket = key.channel.asInstanceOf[ServerSocketChannel]
          socket.accept match {
            case null =>
            case channel => {
              val remoteAddress = channel.socket.getRemoteSocketAddress.toString();
              println(remoteAddress + ":[connected]" )

              channel.configureBlocking(false)
              channel.register(selector, SelectionKey.OP_READ);
              val player = new Player(channel)
              player.start
              playerList += player
              playerMap.put(channel,player)
              Game ! Join(player)
            }
          }
        }
        case Read( key ) => {
          val channel = key.channel.asInstanceOf[SocketChannel]
          val buf = ByteBuffer.allocate(BUF_SIZE)
          val charset = Charset.forName("UTF-8")

          val remoteAddress = channel.socket.getRemoteSocketAddress.toString();
          def close = {
            channel.close()
            println( remoteAddress + ":[disconnected]" )
          }

          channel.read( buf ) match {
            case -1 => close
            case 0 =>
            case x => {
              buf.flip
              val bytes = new Array[Byte](buf.limit)
              buf.get(bytes,0,bytes.length)
              Game ! Command(playerMap(channel),bytes)
            }
          }
        }
        case Write( key ) => {
          val channel = key.channel.asInstanceOf[SocketChannel]
          if(playerMap.contains(channel)){
            val player = playerMap(channel)
            player ! WriteSocket(selector)
          }
        }
      }
    }
  }
}
