//Object to be used in creating a directory listener
//Currently not finished 

/*package first

import java.nio.file._
import scala.collection.JavaConversions._
import scala.sys.process._

object WatcherApp{
val file = Paths.get(System.getProperty("user.dir")+"/mails")

val watcher = FileSystems.getDefault.newWatchService

file.register(
  watcher, 
  StandardWatchEventKinds.ENTRY_CREATE
 // StandardWatchEventKinds.ENTRY_MODIFY,
 // StandardWatchEventKinds.ENTRY_DELETE
)

def watch(proc: Process): Unit = {
  val key = watcher.take
  val events = key.pollEvents

  val newProc = 
    if (!events.isEmpty) {
      Spamfilter.startIndexer
      proc.destroy()
      "cmd /c echo wachten..." run true
    } else proc

  if (key.reset) watch(newProc)
  else println("aborted")
}


}*/