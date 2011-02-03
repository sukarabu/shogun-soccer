package com.sukarabu.sgsoccer

import java.nio.channels.{ SelectionKey,Selector, ServerSocketChannel, SocketChannel }
import com.sukarabu.sgsoccer.models._

object Client {
  def main(args:Array[String]) {
    Game.start
    val player = new Player
    println("Game started!")
    Game ! Join(player)
    println("Player joined!")
  }
}


// vim: set ts=2 sw=2 et:
