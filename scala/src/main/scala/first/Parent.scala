package first
import java.io.File

import akka.actor.{ActorSystem, Actor, ActorRef, Props, ActorLogging,PoisonPill}

 
//The parent object is used to control the parent actor
object Parent{  
   case class Start(val persons:Stream[File])
}

//Parent actor used for controlling and launching working actors
class Parent(controller: ActorRef, indexer: ActorRef) extends Actor with ActorLogging {    
    // imports for controller and indexer objects
    import Controller.WatchMe
    import Worker.Work
    import Parent.Start

    // Action when receiving a start or unexpected message 
    def receive = {
      // on receiving start message with a list of files to processed
      case Start(mails) => {
        // the parent itself is registered as active actor in the controller to prevent the shutdown of the system when there are no active Workers but still work to be done 
        controller ! WatchMe(self)
        // loop through all the files in mails and start an actor for each 
        mails.foreach {x =>
          //start actor for the selected file
          val worker = context.system.actorOf(Props[Worker])
          
          //register the actor at the controller 
          controller ! WatchMe(worker)
          
          //commit the work to the actor with the select mail and the indexer for the result
          worker ! Work(x,indexer)
        }
        // Add a Kill command to the end of the queue for the parent actor so it can kill the actorSystem when finished  
        self ! PoisonPill
      }
      // catch unexpected messages to parent actor
      case _ => println("Unexpected call to parent Actor") 
    }
  }