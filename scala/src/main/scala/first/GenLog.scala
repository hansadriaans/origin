package ebicus
import java.util.Random

class GenLog {
  case class Log[-T](self: List[Any])
  
  def goto_otravo(webvisit :Log[String], I: Int):String = webvisit match {
    case Log(null) => throw new Error ("no new site entered")
    case Log(_) => {
      ((new Random).nextInt(100)) match{
        case y if y > 75  => {
          goto_otravo(new Log(webvisit.self :+ I.toString+"x faal"),I+1)
          }
        case y if y > 50  => {
            goto_website(new Log(webvisit.self :+ I.toString+"x succes"))
        }
        case y if y > 25  => {
            goto_otravo(new Log(webvisit.self :+ I.toString+"x nog een keer"),I+1)
        }        
        case _ => webvisit.self.mkString(":") +  "-> geen verdere booking"
      }
    }
  }

  def goto_website(person : Log[String]):String = person match {
    case Log(null) => throw new Error("no user name defined")
    case Log(_) => {
      ((new Random).nextInt(100)) match{
        case y if y > 50  => {
            goto_otravo(new Log(person.self :+ "www.otravo.com"),1)
       
        }
        case _ => person.self.mkString(":") +  "-> geen verdere actie"
      }
    } 
  }
}