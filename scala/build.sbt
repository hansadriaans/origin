lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0",
  scalaVersion := "2.10.6"
)

mainClass in (Test, packageBin) := Some("WordCount")

