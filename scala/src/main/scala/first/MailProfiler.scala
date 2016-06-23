package first
import java.io._
import scala.io.Source
import scala.collection.immutable.ListMap
import org.jsoup.Jsoup
import scala.util.Random


object mailProfiler {
  //mail class for saving meta tags
   class Mail(val sender:String, val recipient: List[String], val subject: String, val body:Stream[String], val top : List[(String,Int)], val clasification : String, val moved : Boolean, val fileName: String){
     override def toString(): String = "sender =>"+sender+ ", recipient=>"+recipient+", subject=>" + subject+", top word="+top  
  }
  
   // regex for finding the email adress
   lazy val email_pat = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9-]+.[A-Za-z]{2,3}+".r 
 
   // function for reading the email and searching for sender, recipient and subject 
   // for analysis the top most common words are being added
  def profile(source : File) :Mail = {
     val fileName = source.getName
     val r = Random
     try {
       
       def checkFolder(f:Int,s:Int):String ={
          if (!new File(System.getProperty("user.dir")+"/processed/"+f+"/"+s).exists())
          {
            if (new File(System.getProperty("user.dir")+"/processed/"+f).exists())
            {
              new File(System.getProperty("user.dir")+"/processed/"+f+"/"+s).mkdir()
            } else {
              new File(System.getProperty("user.dir")+"/processed/"+f).mkdir()
              new File(System.getProperty("user.dir")+"/processed/"+f+"/"+s).mkdir()
            }  
          } 
          f+"/"+s+"/"    
        }
    
        lazy val folder = System.getProperty("user.dir")+"/processed/"+(checkFolder (r.nextInt(10),r.nextInt(20)))
      
       def moved = source.renameTo(new File(folder+fileName))
       
       try {moved}
       catch{case e : Exception => println (e)}
     
        
       def email = Source.fromFile(new File(folder+fileName),"ISO-8859-7") 
    
       //meta searches the email for the lines From, To and Subject
       def meta = email.getLines().filter(x => x.contains("From")||x.contains("To")||x.contains("Subject"))
       
       //sender of the email is extracted by filtering on From and using a regex to find just the adres
       val sender = email_pat.findFirstMatchIn(meta.filter(x => x.contains("From")).mkString).mkString
       
       //subject is extracted by filtering on Subject
       val subject = meta.filter(x => x.contains("Subject")).mkString("").split(" ",2).toStream
       
       //recipient of the email is extracted by filtering on From and using a regex to find just the adres
       val recipient = email_pat.findAllMatchIn(meta.filter(x => x.contains("To")).mkString).toList.map(x => x.toString)
      
       //parse text from html to text
       val clean = email.getLines().map { x => Jsoup.parse(x.toString()).text()}.map { x => x.replaceAll("[%\"=()#$.<>&-',;\\d]+: *", "").toLowerCase() }
       
       // the occurrence of each word is counted 
       val topwords = clean.flatMap{_.split("\\s+")}
                               .foldLeft(Map.empty[String,Int]){
                               (number,word) => number + (word ->(number.getOrElse(word, 0)+1))
                       }
       
       //only take words length > 1
       val sortword = (ListMap(topwords.toSeq.sortWith(_._2>_._2):_*)).filter(x => x._1.length()>1)
        
       // create mail object
       new Mail(sender,
                 recipient,
                 subject.tail.mkString,
                 email.getLines().toStream,
                 sortword.take(10).toList,
                 "SPAM",
                 moved,
                 System.getProperty("user.dir")+"/processed/"+folder+fileName)
       
     }
     catch{   case e : Exception =>
           new Mail("Error",List("Error"),e.getLocalizedMessage,Stream(e.getLocalizedMessage),List.empty,"None",source.renameTo(new File(System.getProperty("user.dir")+"/failed/"+fileName)),fileName )
       }
     finally{
         new Mail("Error",List("Error"),"Error reading files",Stream("Error"),List.empty,"None",source.renameTo(new File(System.getProperty("user.dir")+"/failed/"+fileName)),fileName)
      }
         
   }

}

