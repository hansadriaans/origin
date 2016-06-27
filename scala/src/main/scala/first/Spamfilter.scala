//Main object controlling the application

package first 


object Spamfilter {
  
  //Control menu with options
  def printMenu = {
    
      println("What do you want to do?")
      println("1) Index emails")    
      println("2) Search emails")   
      println("9) Exit")           
      getInput(readLine("Choose...> "))
      
      def getInput(input: String):Unit = input match{
        // Call the indexer function to execute indexing 
        case "1" =>  Indexer.startIndexer
        // Start search menu
        case "2" =>  {
          // The word search is initiated with calling the current index, stripping the empty results 
           val hits = index.findWord(readLine("Word...> "),index.meta_mail).filter{ x => x.nonEmpty }
           println ("Number of hits=>"+hits.size)         
           hits.foreach{ x => println(x.head+" == " + x(1) + x(2)) }
           //Call main function
           main(Array("",""))
        }
        // Close the application
        case "9" => System.exit(1)
        // When the input is anything but the previous options
        case _ =>  println("unknow input");main(Array("",""))
    }
    
  }
  
  def index = new SearchIndex("index.txt")
  //main function for executing object 
  def main(args: Array[String]): Unit = {
    //Start with calling the printMenu for controlling the application
    printMenu
  }

}