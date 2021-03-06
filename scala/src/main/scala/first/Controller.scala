//The controller manages the different actors 
// To prevent premature closing

package first

import akka.actor.{Actor, ActorRef, Terminated}
import scala.collection.mutable.ArrayBuffer

object Controller {
  // Used by others to register an Actor for watching
  case class WatchMe(ref: ActorRef)
}

abstract class Controller extends Actor {
  import Controller._
  
  // Keep track of what we're watching
  val watched = ArrayBuffer.empty[ActorRef]

  // The hook that's called when everything's dead
  def allActors(): Unit

  // Register the actors to the watch queue and check for termination
  final def receive = {
    case WatchMe(ref) =>
      context.watch(ref)
      watched += ref
    case Terminated(ref) =>
      watched -= ref
      if (watched.isEmpty) allActors()
  }
}
