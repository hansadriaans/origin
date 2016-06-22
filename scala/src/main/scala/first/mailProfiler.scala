package first
import java.io._
import scala.io.Source
import scala.collection.immutable.ListMap


object mailProfiler {
   class Mail(val sender:String, val recipient: List[String], val subject: String, val body:String, val count:Int, val top : (String,Int)){
     override def toString(): String = "sender =>"+sender+ ", recipient=>"+recipient+", subject=>" + subject+", body size=>"+body.length()+" characters, number of words=>"+count+" , top word="+top  
     
     
   }
  
   lazy val email_pat = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9-]+.[A-Za-z]{2,3}+".r 
   //print (source)
  def profile( source : String) :Mail = {
     def email = Source.fromFile(source,"ISO-8859-1")
     val sender = email_pat.findFirstMatchIn(email.getLines().filter(x => x.contains("From")).mkString).mkString
     val subject = email.getLines().filter(x => x.contains("Subject")).mkString("").split(" ",2) 
     val recipient = email_pat.findAllMatchIn(email.getLines().filter(x => x.contains("To")).mkString("")).toList.map(x => x.toString)
     val count = email.getLines().flatMap{_.split("\\W")}.map(_ => 1).length
     val topwords = email.getLines()
                     .flatMap{_.split("\\W+")}
                     .foldLeft(Map.empty[String,Int]){
                             (number,word) => number + (word ->(number.getOrElse(word, 0)+1))
                     }
   //  println ("topwords=>"+topwords)
    // ListMap(topwords.toSeq.sortBy(_._2):_*).head
     
     val body = email.getLines().mkString("\n" )

     new Mail(sender,recipient,subject(subject.length-1),body,count,(ListMap(topwords.toSeq.sortWith(_._2>_._2):_*)).head)
     }
  
   def saveFile(mail : Mail): Unit = {
     val file = new File (mail.sender+".txt")
     val bw = new BufferedWriter(new FileWriter(file))
     bw.write(mail.body)
     bw.close()
   }

}

