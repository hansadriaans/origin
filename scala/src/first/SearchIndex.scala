package first
import scala.io.Source
import java.io.File

class SearchIndex(indexFile : String) {
  val index = Source.fromFile(new File(System.getProperty("user.dir")+"/"+indexFile),"ISO-8859-7")
   
  val meta_mail = (index.getLines().map{_.split(";").toStream}).toStream
 
  def findWord(word:String): Unit = {
     meta_mail.foreach{ x =>(if (x.head.contains(word))(println(x.mkString(","))))}
  }
  
//  def findWord(word:String): S = {
//     meta_mail.foreach{ x =>(if (x.head.contains(word))(println(x.mkString(","))))}
//  }
}