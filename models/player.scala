import scala.actors.Actor
import scala.actors.Actor._
import java.io._
import java.nio._
import java.nio.channels.{ SelectionKey,Selector, ServerSocketChannel, SocketChannel }

package models {
  case class WriteBuffer(bytes:Array[Byte],selector:Selector)
  case class WriteSocket(selector:Selector)
  
  class Player(soc:SocketChannel) extends Actor{
    val socket = soc
    var buffOut:ByteArrayOutputStream = new ByteArrayOutputStream()
    var id:Int = 0
    var field:Field = new Field
  
    def act() = {
      loop{
        react {
          case WriteBuffer(bytes,selector) => {
            println("WriteBuffer")
            //TODO selectorの取得方法
            buffOut.write(bytes)
            socket.register(selector,SelectionKey.OP_WRITE)
          }
          case WriteSocket(selector) => {
            println("WriteSocket")
            val bbuf = ByteBuffer.wrap(buffOut.toByteArray())
            socket.write(bbuf)
            if(bbuf.hasRemaining()){
              val rest = new ByteArrayOutputStream();
              rest.write(bbuf.array,bbuf.position,bbuf.remaining)
              buffOut = rest 
              socket.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE)
            } else {
              buffOut = new ByteArrayOutputStream()
              socket.register(selector, SelectionKey.OP_READ)
            }
          }
        }
      }
    }

    def toStr():String = { "Player!!" }
  
    def loggedIn():Boolean = {
      return true
    }
    
  }
}
