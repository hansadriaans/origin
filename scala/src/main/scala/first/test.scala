package first
import scala.util.Random
import akka.actor.{ActorSystem, Actor, ActorRef, Props, ActorLogging}
import java.util.UUID
import java.io._

object test {
  case class Start(val persons:List[String])
  case class Work(val text: String)
  val system = ActorSystem("spamfiler")
  
  sealed trait bezoek
  def Unique = UUID.randomUUID()
  
  
  class RunController extends Controller {
    def allActors(): Unit = context.system.shutdown()
  }
  
  object Customer{
    case object nieuw_bezoek extends bezoek 
  }
   
  class Customer extends Actor with ActorLogging {
    def receive = {
      //case persoonlijk(person) => {
         // ref ! print(goto_website(person))
        case Work(x) => {
                  print (x + "\n")
                  context.stop(self)
                }
    }
  }
  
  //Parent actor for collecting the results and launching working actors
  class Parent(reaper: ActorRef, probe: ActorRef) extends Actor {
    import Controller._
    def receive = {
      case Start(persons) =>
        //reaper ! WatchMe(self)
        persons.map{x =>
          val person = system.actorOf(Props[Customer], Unique.toString())
            reaper ! WatchMe(person)
            person ! Work(x)
        }
      case _ => print ("Dit is een test stop")
        //person./
    }
  }
  
  val rs = Random
  def main(args: Array[String]): Unit = {
  
    import system.dispatcher
  
   // lazy val firstnames = List("Hans","Henk","Herman","Anton","Tim","Anie","Chantal","Peter")
   
    def getFileTree(f: File): Stream[File] =
        f #:: (if (f.isDirectory) f.listFiles().toStream.flatMap(getFileTree) 
               else Stream.empty)
        
    val files = getFileTree(new File("C:\\Users\\hadriaans.PF0A49T4\\workspace\\scala\\mails")).map(x => x.toString())

            
//    // val persons = List.tabulate(firstnames.length)(x => new Person(firstnames(x),rs.nextInt(100)))
//    val persons = (0 to 10).map(x => firstnames(rs.nextInt(firstnames.length)))
    val dummyProbe = system.actorOf(Props(new Actor{
      def receive = {
        case _ => println("Stopped a worker")
      }
     }))
     
    val controller = system.actorOf(Props[RunController])
    
    val parent = system.actorOf(Props(new Parent(controller, dummyProbe)))
    
    parent ! Start(files.toList)
    system.awaitTermination()
  }

}