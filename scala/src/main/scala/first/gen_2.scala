/*package first

import scala.concurrent.duration._
import scala.util.Random
import scala.concurrent.{Await, Future}
import scala.collection.mutable.ArrayBuffer
import akka.actor.{ActorSystem, Actor, ActorRef, Props, ActorLogging}
import akka.util.Timeout
import akka.pattern.after
import akka.actor.SupervisorStrategy.Stop
import java.util.UUID
import java.io._
import Reaper.WatchMe

object gen_2 {
  val rs = Random
  def Unique = UUID.randomUUID()
  
  case class Log[-T](self: List[Any])
  case class Person (val name: String, val age: Int)
  case class Webvisit (val person: Person, val site: String)
  case class Start(val persons:List[Person])

  def goto_otravo(webvisit :Webvisit):String = webvisit match {
    case Webvisit(_,null) => throw new Error ("no new site entered")
    case Webvisit(_,_) => {
     // println ("person " + webvisit.person.name + " is going to site " +webvisit.site)
      " otravo bezocht"
    }
  }

  def goto_website(person : Person):String = person match {
  case Person(null,_) => throw new Error("no user name defined")
  case Person(_,_) => {
  //    println ("person " + person.name + " is going to a website and he is " + person.age + " Years old")
        (rs.nextInt(100)) match{
          case y if y > 50  =>person.name+" heeft website bezocht " + person.age + goto_otravo(new Webvisit(person,"www.otravo.com"))
          case _ => person.name + " " +person.age + " geen verdere actie"
      } 
    }
  }

  sealed trait bezoek
 // case object nieuw_bezoek extends bezoek
  case class persoonlijk(person:Person)
  case class makeLog(entry:String)
  case object ClosingTime
  
  object Customer{
    case object nieuw_bezoek extends bezoek 
  }
  
  class Customer(ref: ActorRef) extends Actor with ActorLogging {
    def receive = {
      case persoonlijk(person) => {
          ref ! print(goto_website(person))
      }
    }
  }

  object Reisplanner {  
  }
  
  class Reisplanner extends Actor{
    var people = ArrayBuffer[String]()
    val writer = new PrintWriter(new File("test.txt" ))
    def receive = {
      case makeLog(person) =>  people += person
      case ClosingTime => {
          writer.write(people.mkString("\r\n")+" ----------- Done")
          println("klaar")
      }
      //  case nieuw_bezoek => println ("Site bezocht")
    }
  }
  
  class Parent(reaper: ActorRef, probe: ActorRef) extends Actor {
    def receive = {
      case Start(persons) =>
        for(x <- 0 to persons.length){
        //  val person = context.system.actorOf(Props(new Customer,this), Unique.toString())
      //    reaper ! WatchMe(person)
       //   person ! persoonlijk(persons(rs.nextInt(persons.length)))
            print ("test")
          
         // worker ! Customer("worker"+x)
         // probe ! "test"
      } 
    }
  }
  
  class TestReaper extends Reaper {
  
    def allSoulsReaped(): Unit = context.system.shutdown()
  }
  
  //val control : ActorRef = system.actorOf(Props[Parent], "all")
  
  
  //val reisplanner: ActorRef = system.actorOf(Props[Reisplanner], "bezoeker")
  
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("spamfiler")
    import system.dispatcher
  
    lazy val firstnames = List("Hans","Henk","Herman","Anton","Tim","Anie","Chantal","Peter")
   // val persons = List.tabulate(firstnames.length)(x => new Person(firstnames(x),rs.nextInt(100)))
    val persons = (0 to 1000).map(x => new first.gen_2.Person(firstnames(rs.nextInt(firstnames.length)),x))
    val dummyProbe = system.actorOf(Props(new Actor{
      def receive = {
        case _ => println("Stopped a worker")
      }
     }))
    
    val reaper = system.actorOf(Props[TestReaper])
    
    dummyProbe ! "test"
    
    //val parent = system.actorOf(Props(new Parent(reaper, dummyProbe)))
  //  parent ! Start(persons.toList)
          // system.scheduler.scheduleOnce(rs.nextInt(5) seconds){
          
     // }         
 
    }
   //  persons.map { x => printf ("person " + x.Name + " age " + x.Age + "\n" ) }
  }
*/