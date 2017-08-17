package controllers

import com.google.common.cache.{CacheBuilder, CacheLoader}
import models.{Passenger, Prediction}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.tuning.CrossValidatorModel
import org.apache.spark.sql.{Dataset, SparkSession}

object ModelScorer {

  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

  val conf = new org.apache.spark.SparkConf()
    .setSparkHome("/Users/ykhodorkovsky/spark-2.2.0-bin-hadoop2.7")
    .setMaster("spark://ip-192-168-56-190.ec2.internal:7077")
    .setJars(Seq(System.getProperty("user.dir") + "/target/scala-2.11/ml-server-assembly-1.0.jar"))
    .setAppName("ml-server")

  val spark = SparkSession.builder
    .config(conf)
    .getOrCreate()
  implicit val sc = spark.sparkContext

  import spark.implicits._

  val modelCache = CacheBuilder.newBuilder()
    .build(
      new CacheLoader[String, CrossValidatorModel] {
        def load(path: String): CrossValidatorModel = {
          CrossValidatorModel.load(path)
        }
      }
    )

  val model: CrossValidatorModel = modelCache.get("trained-cv-pipeline")

  def predict(passengers: Seq[Passenger]): Seq[Prediction] = {

    lazy val ds: Dataset[Passenger] = passengers.toDS //sc.parallelize(passengers).toDS
    val predictions: Seq[Prediction] = model.transform(ds)
      .select("name", "probability", "prediction")
      .withColumnRenamed("prediction", "survives")
      .as[Prediction]
      .collect
      .toSeq
    predictions
  }

}
