//The Searchindex class is used to read the index file and return search words 

package first
import scala.io.Source
import java.io.File

class SearchIndex(indexFile : String) {
  //Open index file containing the meta data from the processed mails
  
  def meta_mail : Stream[Stream[String]] ={
    try {
      val index = Source.fromFile(new File(System.getProperty("user.dir")+"/"+indexFile),"UTF-8")
      (index.getLines().map{_.split(";").toStream}).toStream
    }
    catch {
        //Catch error in loading the filetree or initializing the parent actor
        case e : Exception => println ("Index file does not exist, please index emails first") 
        Stream.Empty
      }
  }
    //Load the index file into a stream of streams for every mail
   // val meta_mail = 
    
  // Using the created index every meta tag from the email is searched for containing the word
  // In this case full text search over the entire meta tag is used, so if the word is in the meta it will be returned
  def findWord(word:String,index:Stream[Stream[String]]): Stream[Stream[String]]= {
    //scrolling through the index is done line by line using head and add the same function to look in the tail
     if (!index.isEmpty) (  
         //checkWord makes sure that if the word is found the entire metaline is used to create the response stream 
         checkWord(index.head.filter{ x => x.toLowerCase().contains(word)},index.head)) #:: (findWord(word,index.tail))
    //when last indexed mail is found an empty stream is added
     else Stream.empty
  }
   
  // function checks if there is a metaline read with the search word and returns
  // subject, classification and the place where the searchword is found
  def checkWord(meta:Stream[String],line:Stream[String]):Stream[String] ={
    if (meta.isEmpty) Stream.Empty      
    else line(0) #:: line(1) #:: (Stream("\nLocated in --> "+meta.head))
      
    }
  
  
}