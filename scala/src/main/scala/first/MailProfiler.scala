//Mailprofiler extracts meta tags from mail files 

package first
import java.io._
import scala.io.Source
import scala.collection.immutable.ListMap
import org.jsoup.Jsoup
import scala.util.Random

object mailProfiler {
  //mail class for saving meta tags
   class Mail(val sender:String, val recipient: List[String], val subject: String, val top : List[(String,Int)], val clasification : String, val moved : Boolean, val fileName: String){
     override def toString(): String = "subject=>"+subject+ 
                                       ";clasification=>"+clasification+
                                       ";sender=>"+sender+ 
                                       ";recipient=>("+recipient.mkString(",")+
                                       ");top_words=>("+top.mkString(",")+
                                       ");file=>"+fileName
  }
  
   // regex for finding the email adress
   lazy val email_pat = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9-]+.[A-Za-z]{2,3}+".r 
 
   // function for reading the email and searching for sender, recipient and subject 
   // for analysis the top most common words are being added
  def profile(source : File) :Mail = {
     val fileName = source.getName
     val r = Random
     try {
       
       //Function for creating the desired folder
       //Function needs a re-model
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
       // the folder to copy the mail to is creating using an 16 x 16 directory
       // This structure is used to prevent problems with the OS on indexing a directory with a lot of small files
       val folder = System.getProperty("user.dir")+"/processed/"+(checkFolder (r.nextInt(16),r.nextInt(16)))
      
       //move the file from mails directory to the new processed directory,
       //this value is used to check if the file is moved correctly and more usefull when the indexing failed
       val moved = source.renameTo(new File(folder+fileName))

       def email = Source.fromFile(new File(folder+fileName),"UTF-8") 
    
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
       
       //only take words length > 11
       val sortword = (ListMap(topwords.toSeq.sortWith(_._2>_._2):_*)).filter(x => x._1.length()>1)
        
       
       // create mail object which is used to save the meta tags for this mail
       new Mail(sender,
                 recipient,
                 Jsoup.parse(subject.tail.mkString).text(),
                 sortword.take(10).toList,
                 if (r.nextInt(2)> 0) "SPAM" else "HAM",
                 moved,
                 folder+fileName) 
         
       
     }
     catch{   case e : Exception =>
           // When something goes wrong with the indexing of the mail the file wil be move to the failed directory
           new Mail("Error",List("Error"),e.getLocalizedMessage,List.empty,"None",source.renameTo(new File(System.getProperty("user.dir")+"/failed/"+fileName)),fileName )
       }
     finally{
           //
         new Mail("Error",List("Error"),"Error moving file",List.empty,"None",false,fileName)
      }
         
   }

}

