package controllers

import com.google.common.cache.{CacheBuilder, CacheLoader}
import models.{Passenger, Prediction}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.tuning.CrossValidatorModel
import org.apache.spark.sql.{Dataset, SparkSession}

object ModelScorer {

  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

  val spark = SparkSession.builder
    .master("local")
    .appName("ml-server")
    .getOrCreate()

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

    val ds: Dataset[Passenger] = passengers.toDS
    val predictions: Seq[Prediction] = model.transform(ds)
      .select("name","probability","prediction")
      .withColumnRenamed("prediction","survives")
      .as[Prediction]
      .collect
      .toSeq
    predictions
  }

}
