package first 
import akka.actor.{ActorSystem, Actor, ActorRef, Props, ActorLogging,PoisonPill}
import java.io._
import scala.io.Source
import scala.sys.process._
import Indexer._
import scala.collection.JavaConversions._

object Spamfilter {
  //case class for starting the parent with a list of files 
  case class Start(val persons:List[File])
 
  // case class for worker class with a 1selected file and a indexed for the result
  case class Work(val file: File, indexer: ActorRef)
  
  // the class is used to kill the context system when all the work is done
  class RunController extends Controller {
    // when allActors are killed the context system is finally terminated
    def allActors(): Unit = context.system.terminate()
    
     override def postStop() {
      println("Index completed")  
      printMenu
      }
  }
  
  // Worker class is 
  class Worker extends Actor with ActorLogging {
     import mailProfiler._
     import Indexer._
    
     // When the Worker receives work it start processing the file and registering its result at the indexer
    def receive = {
        case Work(file,indexer) => {
          try{
              // first check on directory, if true then print the directory and no work required
               if (file.isDirectory()) println("Home dir =>"+file.getAbsolutePath)
               else {
                 // file is processed into a mail object
                 // val mail = 
                  // sender and subject are used for the index to the mailfile
                  //file.renameTo(new File(mail.fileName))
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
  
  //Parent actor used for collecting the results and launching working actors
  class Parent(controller: ActorRef, indexer: ActorRef) extends Actor with ActorLogging {
    // imports for controller and indexer objects
    import Controller._
    import Indexer._

    // Action when receiving a start or unexpected message 
    def receive = {
      // on receiving start message with a list of files to processed
      case Start(mails) => {
        // the parent itself is registered as active actor in the controller to prevent the shutdown of the system when there are no active Workers but still work to be done 
        controller ! WatchMe(self)
        // loop through all the files in mails and start an actor for each 
        mails.map{x =>
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

  // Indexer is self contained and runs an ActorSystem 
  def startIndexer:Unit ={
    import Indexer._
    // Creating main actor system for registering akka actors
    val system = ActorSystem("spamfiler")
    
    // Function for reading filenames and directory names into a stream
    // Loops through directory and calls itself when encountering a directory appending all files into a stream of files 
    def getFileTree(f: File): Stream[File] =
        f #:: (if (f.isDirectory) f.listFiles().toStream.flatMap(getFileTree).filter { x => !x.isDirectory() }
               else Stream.empty)
    
    // create the stream of all files in root directory/mails           
    val files = getFileTree(new File(System.getProperty("user.dir")+"/mails"))
   
    // register the Runcontoller object as actor to monitor the activity of all the other actors 
    val controller = system.actorOf(Props[RunController])
    
    // register the indexer used for collecting all output from Worker actors
    val indexer = system.actorOf(Props[Indexer])
    
    // register Parent of all Worker actors with reference to the controller and indexer
    val parent = system.actorOf(Props(new Parent(controller,indexer)))
    
    // Call Start method in the parent actor with the generated file stream
    parent ! Start(files.toList)
    
    println("Index started on "+(files.size-1)+" files")
  }
  
  //Control menu with options
  def printMenu = {
    
      println("What do you want to do?")
      println("1) Index emails")
      println("2) Search emails")
      println("9) Exit")
      getInput(scala.io.StdIn.readLine("Choose...> "))
      //call the indexer function to execute indexing 
      
      def getInput(input: String):Unit = input match{
        case "1" =>  startIndexer
        case "2" =>  {
           index.findWord(scala.io.StdIn.readLine("Word...> "))
           main(Array("",""))
        }
        case "9" => System.exit(1)
        case _ =>  println("unknow input");main(Array("",""))
    }
    
  }
  
  def index = new SearchIndex("index.txt");
  //main function for executing object 
  def main(args: Array[String]): Unit = {
    
    printMenu
  }

}