package main.scala.first

import akka.actor.{Actor, PoisonPill}
import scala.collection.mutable.ArrayBuffer
import java.io._
import akka.actor.actorRef2Scala

object Indexer {
  // Used by others to register an Actor for watching
  case class Index(meta: List[String])
  case object End
}

abstract class Indexer extends Actor {
  import Indexer._
  
  // Keep track of what we're watching
  val indexList = ArrayBuffer.empty[String]
  // Derivations need to implement this method.  It's the
  // hook that's called when everything's dea

  // Watch and check for termination
  final def receive = {
    case Index(meta) => 
      this.indexList += meta.toString()

    case End => self ! PoisonPill
      //indexList append meta
  }
   override def postStop() {
     val file = new File ("index.txt")
     val bw = new BufferedWriter(new FileWriter(file))
     bw.write(indexList.mkString("\n"))
     bw.close()  
   }
}
