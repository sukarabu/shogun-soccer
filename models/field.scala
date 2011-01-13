package com.sukarabu.sgsoccer.models

import com.sukarabu.sgsoccer.models._

class Field(x:Int,y:Int){
  var player:Player = null

  def toStr():String = {
    if(player == null) "." else player.toStr
  }

  def position():Int = {
    y * 12 + x
  }
}
