//Indexer is collects the processed mail meta tags and puts them into  single file after indexing is finished

package first

import akka.actor.{Actor, PoisonPill,ActorSystem,Props}
import scala.collection.mutable.ArrayBuffer
import java.io._
import first.mailProfiler.Mail


  // the class is used to kill the context system when all the work is done
class RunController extends Controller {
    // when allActors are killed the context system is finally terminated
    def allActors(): Unit = context.system.terminate()
}


object Indexer {
// Case class for communicating with the indexer actor
  case class Index(meta: Mail)

 // Indexer is self contained and runs an ActorSystem 
  def startIndexer:Unit ={
    import Parent._
    // Creating main actor system for registering akka actors
    val system = ActorSystem("spamfiler")
    
    // Function for reading filenames and directory names into a stream
    // Loops through directory and calls itself when encountering a directory appending all files into a stream of files 
    def getFileTree(f: File): Stream[File] =
        f #:: (if (f.isDirectory) f.listFiles().toStream.flatMap(getFileTree)
               else Stream.empty)
     
    // register the Runcontoller object as actor to monitor the activity of all the other actors 
    val controller = system.actorOf(Props[RunController])
    
    // register the indexer used for collecting all output from Worker actors
    val indexer = system.actorOf(Props[Indexer])
    
    // register Parent of all Worker actors with reference to the controller and indexer
    val parent = system.actorOf(Props(new Parent(controller,indexer)))
    try {
      val filelist = getFileTree(new File(System.getProperty("user.dir")+"/mails"))
    
    // Call Start method in the parent actor with the generated file stream
      parent ! Start(filelist)
      println(filelist.size +" files and folders found")
    }
    catch {
      //Catch error in loading the filetree or initializing the parent actor
      case e : Exception => println ("Given directory: "+System.getProperty("user.dir")+"/mails could not be loaded") 
    }
    
    println("Indexing started")
  }

}

// Index actor for collecting the data from work actors
class Indexer extends Actor {
  import Indexer._
  
  // Keep track of what we're watching
  val indexList = ArrayBuffer.empty[Mail]
  // Derivations need to implement this method.  It's the
  // hook that's called when everything's finished

  // Watch and check for termination
  final def receive = {
    // build index one message at a time
    case Index(meta) => 
      this.indexList += meta
      //unexpected call means closing the actor
    case _ => self ! PoisonPill
   
  }
  //When the all actors are finished the indexer is killed by the controller 
  // at that point it will write all its data to a files
   override def postStop() {
     //Writing to system using append 
     val bw = new BufferedWriter(new FileWriter( new File ("index.txt"),true))
     bw.write(indexList.mkString("\n"))
     bw.close()  
     println("Index completed")  
     Spamfilter.printMenu
   }
}
