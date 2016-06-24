package main.scala.first
import scala.io.Source
import java.io.File

class SearchIndex(indexFile : String) {
  val index = Source.fromFile(new File(System.getProperty("user.dir")+"/"+indexFile),"UTF-8")
  
  val meta_mail = (index.getLines().map{_.split(";").toStream}).toStream
 
  def findWord(word:String,index:Stream[Stream[String]]): List[List[String]]= {
     if (!index.isEmpty) (  
         checkWord(index.head,word)) :: (findWord(word,index.tail))
     else List.empty
  }
    
  def checkWord(meta:Stream[String],word:String):List[String] ={
      if (meta.head.toLowerCase().contains(word.toLowerCase())) meta.toList
      else List.empty    
    }
  
  
}