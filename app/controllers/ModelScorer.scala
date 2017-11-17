package controllers

import com.google.common.cache.{CacheBuilder, CacheLoader}
import models.{Passenger, Prediction}
import org.apache.spark.ml.tuning.CrossValidatorModel
import org.apache.spark.sql.{DataFrame, Row}

object ModelScorer extends LocalSparkConnection {

  import spark.implicits._

  val modelCache = CacheBuilder.newBuilder()
    .build(
      new CacheLoader[String, CrossValidatorModel] {
        def load(path: String): CrossValidatorModel = {
          CrossValidatorModel.load(path)
        }
      }
    )

  val model: CrossValidatorModel = modelCache.get("trained-models/cv-pipeline-2017-08-21")

  def predict(passengers: Seq[Passenger]): Seq[Prediction] = {

    val passengerTuple = passengers.map{
      (p: Passenger) => (p.pclass,p.name,p.sex,p.age,p.sibsp,p.parch,p.fare,p.embarked)
    }

    val passengerRDD = sc.parallelize(passengerTuple)
    val columns: Seq[String] = Seq("pclass","name","sex","age","sibsp","parch","fare","embarked")
    val df: DataFrame = passengerRDD.toDF(columns: _*)

    def probabilityStrip(prob: String): Double = {
      prob.split(",")(1).dropRight(1).take(5).toDouble
    }

    val predictionsDF: DataFrame = model.transform(df)
      .select("name","probability","prediction")
      .withColumnRenamed("prediction","survives")

    val predictions: Seq[Prediction] = predictionsDF.collect
      .map{(row: Row) =>
        Prediction(row(0).toString, probabilityStrip(row(1).toString), (row(2) == 1))
      }

    predictionsDF.show()

    predictions
  }

}
