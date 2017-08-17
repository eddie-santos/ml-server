name := """ml-server"""

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  guice
  , "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test
  , ("org.apache.spark" %% "spark-core" % "2.2.0")
  , ("org.apache.spark" %% "spark-sql" % "2.2.0")
  , ("org.apache.spark" %% "spark-mllib" % "2.2.0")
  , ("org.apache.hadoop" % "hadoop-client" % "2.7.2")
)

dependencyOverrides ++= Set(
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.5",
  "com.google.guava" % "guava" % "19.0"
)

assemblyMergeStrategy in assembly := {
  case PathList("com", "sun", "research", xs@_*) => MergeStrategy.first
  case PathList("javax", xs@_*) => MergeStrategy.first
  case PathList("org","apache","spark","unused", xs@_*) => MergeStrategy.discard
  case PathList("org", "apache", "commons", xs@_*) => MergeStrategy.first
  case PathList("org", xs@_*) => MergeStrategy.first
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".conf" => MergeStrategy.concat
  case PathList(ps @ _*) if ps.last endsWith ".css" => MergeStrategy.concat
  case PathList(ps @ _*) if ps.last endsWith ".js" => MergeStrategy.discard

  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}