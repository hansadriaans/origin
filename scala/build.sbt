lazy val commonSettings = Seq(
  organization := "hansadriaans",
  version := "0.1.0",
  scalaVersion := "2.11.8"
)


mainClass in (MailProfiler, first) := Some("MailProfiler")

