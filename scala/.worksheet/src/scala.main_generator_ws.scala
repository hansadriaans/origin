package scala

import akka.util.Timeout
import scala.util.Random
import akka.actor.{ActorSystem,Actor, ActorRef, Props}

object main_generator_ws {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(166); 

  val rs = Random
  case class Person (val name: String, val age: Int)
  case class Webvisit (val person: Person, val site: String);System.out.println("""rs  : scala.util.Random.type = """ + $show(rs ));$skip(400); 
 
  def goto_otravo(webvisit :Webvisit):String = webvisit match {
    case Webvisit(_,null) => throw new Error ("no new site entered")
    case Webvisit(_,_) => {
     // println ("person " + webvisit.person.name + " is going to site " +webvisit.site)
      " otravo bezocht"
    }
  };System.out.println("""goto_otravo: (webvisit: scala.main_generator_ws.Webvisit)String""");$skip(488); 

  def goto_website(person : Person):String = person match {

  case Person(null,_) => throw new Error("no user name defined")
  case Person(_,_) => {
  //    println ("person " + person.name + " is going to a website and he is " + person.age + " Years old")
        (rs.nextInt(100)) match{
          case y if y > 50  =>person.name+" heeft website bezocht " + goto_otravo(new Webvisit(person,"www.otravo.com"))
          case _ => person.name +  " geen verdere actie"
      }
    }
  };System.out.println("""goto_website: (person: scala.main_generator_ws.Person)String""");$skip(67); 
  lazy val firstnames = List("Hans","Henk","Herman","Anton","Tim");System.out.println("""firstnames: => List[String]""");$skip(97); 
  val persons = List.tabulate(firstnames.length)(x => new Person(firstnames(x),rs.nextInt(100)))

  //reisplanner ! nieuw_bezoek
   sealed trait bezoek
 
   case object nieuw_bezoek extends bezoek
   case class persoonlijk(person:Person);System.out.println("""persons  : List[scala.main_generator_ws.Person] = """ + $show(persons ));$skip(181); 

   val system = ActorSystem("webhsop")
	 class Reisplanner extends Actor{
	 	def receive = {
			 	case persoonlijk(person) => println(goto_website(person))
		  	case nieuw_bezoek => println ("Site bezocht")
  	}
  };System.out.println("""system  : akka.actor.ActorSystem = """ + $show(system ));$skip(254); 
  val reisplanner: ActorRef = system.actorOf(Props[Reisplanner], "bezoeker");System.out.println("""reisplanner  : akka.actor.ActorRef = """ + $show(reisplanner ));$skip(52); val res$0 = 

  persons.map(p =>  reisplanner !  persoonlijk(p));System.out.println("""res0: List[Unit] = """ + $show(res$0))}

    //  persons.map { x => printf ("person " + x.Name + " age " + x.Age + "\n" ) }
}
