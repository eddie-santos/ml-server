package controllers

import com.google.common.cache.{CacheBuilder, CacheLoader}
import models.{Passenger, Prediction}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.tuning.CrossValidatorModel
import org.apache.spark.sql.{Dataset, Row, SparkSession}

trait CacheProvider {

  val spark = SparkSession.builder
    .master("local")
    .appName("Titanic")
//    .config("spark.driver.host", "localhost")
//    .config("spark.driver.allowMultipleContexts", true)
    .getOrCreate

  import spark.implicits._

  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

  val modelCache = CacheBuilder.newBuilder()
    .maximumSize(2)
    .build(
      new CacheLoader[String, CrossValidatorModel] {
        def load(path: String): CrossValidatorModel = {
          CrossValidatorModel.load(path)
        }
      }
    )

  def predict(data: Seq[Passenger]): Seq[Prediction] = {
    val model: CrossValidatorModel = modelCache.get("/Conf/trained_cv_pipeline")
    val newPassengers: Dataset[Passenger] = data.toDS
    val predictions: Seq[Prediction] = model.transform(newPassengers)
      .select("name","probability","prediction")
      .withColumnRenamed("prediction","survives")
      .collect()
      .map{
        case Row(name: String, probability: Double, survives: Boolean) => Prediction(name,probability,survives)
      }.toSeq
    predictions
  }

}
