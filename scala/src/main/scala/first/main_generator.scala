package first
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import akka.actor

import akka.util.Timeout
import scala.util.Random
import akka.actor.{ActorSystem,Actor, ActorRef, Props}

object main_generator {

  val rs = Random
  case class Person (val name: String, val age: Int)
  case class Webvisit (val person: Person, val site: String)
 
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
          case y if y > 50  =>person.name+" heeft website bezocht " + goto_otravo(new Webvisit(person,"www.otravo.com"))
          case _ => person.name +  " geen verdere actie"
      }
    }
  }
  lazy val firstnames = List("Hans","Henk","Herman","Anton","Tim")
  val persons = List.tabulate(20)(x => new Person(firstnames(rs.nextInt(5)),rs.nextInt(100)))

  //reisplanner ! nieuw_bezoek
   sealed trait bezoek
 
   case object nieuw_bezoek extends bezoek
   case class persoonlijk(person:Person)

   val system = ActorSystem("webhsop")
	 class Reisplanner extends Actor{
	 	def receive = {
			 	case persoonlijk(person) => println(goto_website(person))
		  	case nieuw_bezoek => println ("Site bezocht")
  	}
  }
  
  def main(args: Array[String]): Unit = {
    val reisplanner: ActorRef = system.actorOf(Props[Reisplanner], "bezoeker")
    persons.map(p =>  reisplanner !  persoonlijk(p))
  }

    //  persons.map { x => printf ("person " + x.Name + " age " + x.Age + "\n" ) }
}