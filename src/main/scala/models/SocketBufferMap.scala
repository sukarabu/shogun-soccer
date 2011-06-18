package com.sukarabu.sgsoccer.models

import java.io._
import java.nio.channels.SocketChannel
import scala.collection.mutable._

class SocketBufferMap(){
  val idMap = HashMap[String, SocketBuffer]()
  val socketMap = HashMap[SocketChannel, SocketBuffer]()

  def getBufferById(id:String):ByteArrayOutputStream = {
    return idMap(id).buffer
  }

  def getSocketById(id:String):SocketChannel = {
    return idMap(id).socket
  }

  def getBufferBySocket(soc:SocketChannel):ByteArrayOutputStream = {
    return socketMap(soc).buffer
  }

  def clearBufferBySocket(soc:SocketChannel):Unit = {
    return socketMap(soc).buffer.reset
  }

  def add(id:String,soc:SocketChannel):Unit = {
    val socketBuffer:SocketBuffer = new SocketBuffer(soc)
    idMap.put(id,socketBuffer)
    socketMap.put(soc,socketBuffer)
  }

  def writeBuffer(id:String,bytes:Array[Byte]):Unit = {
    idMap(id).buffer.write(bytes)
  }

}

class SocketBuffer(s:SocketChannel){
  val socket:SocketChannel = s
  val buffer:ByteArrayOutputStream = new ByteArrayOutputStream()
}
