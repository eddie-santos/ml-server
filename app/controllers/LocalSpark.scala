package controllers

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

trait LocalSparkConnection {

  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

  val spark = SparkSession.builder
    .master("local")
    .appName("ml-server")
    .getOrCreate()

  val sc = spark.sparkContext

}
