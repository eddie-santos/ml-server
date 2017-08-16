name := """ml-server"""

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  guice
  , "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test
  , "org.apache.spark" %% "spark-core" % "2.2.0"
  , "org.apache.spark" %% "spark-sql" % "2.2.0"
  , "org.apache.spark" %% "spark-mllib" % "2.2.0"
  , "org.apache.hadoop" % "hadoop-client" % "2.7.2"
)

dependencyOverrides ++= Set(
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.5"
  , "com.google.guava" % "guava" % "19.0"
)