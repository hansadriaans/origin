package first
import java.io.File

import akka.actor.{ActorSystem, Actor, ActorRef, Props, ActorLogging,PoisonPill}

object Worker {
       case class Work(val file: File, indexer: ActorRef)
}


class Worker extends Actor with ActorLogging {
     import mailProfiler._
     import Indexer._
     import Worker._
     
     // When the Worker receives work it start processing the file and registering its result at the indexer
    def receive = {
        case Work(file,indexer) => {
          try{
              // first check on directory, if true then print the directory and no work required
               if (file.isDirectory()) println("Home dir =>"+file.getAbsolutePath)
               else {
                 // file is processed into a mail object and send to the indexer
                 //This part could be extended with a smarter mail profiler
                  indexer ! Index(mailProfiler.profile(file))
               }
          }
          catch{ 
            // on error with the opening the file print a stacktrace
            case e : Exception => e.printStackTrace()    
          }
          finally{
            // at the end of the process sent self destruction message 
            // this part will also trigger the controller Terminated message 1
            
            self ! PoisonPill
          }   
        }
        // when the message is not of type work kill self
        case _ =>  {
          println("Uknown message self-distruct")
          self ! PoisonPill
        }
     }
  }