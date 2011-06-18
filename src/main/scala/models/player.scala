package com.sukarabu.sgsoccer.models

class Player(_id:String){
  var id:String = _id
  var field:Field = null

  def toStr():String = { "a" }

  def loggedIn():Boolean = {
    return true
  }
  
}
