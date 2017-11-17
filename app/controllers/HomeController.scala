package controllers

import javax.inject._

import models.{Passenger, Prediction}
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  implicit val passengerReads: Reads[Passenger] = (
    (JsPath \ "pclass").read[Double] and
      (JsPath \ "name").read[String] and
      (JsPath \ "sex").read[String] and
      (JsPath \ "age").readNullable[Double] and
      (JsPath \ "sibsp").read[Int] and
      (JsPath \ "parch").read[Int] and
      (JsPath \ "fare").readNullable[Double] and
      (JsPath \ "embarked").read[String]
    ) (Passenger.apply _)

  implicit val predictionWrites: Writes[Prediction] = (
    (JsPath \ "name").write[String] and
      (JsPath \ "probability").write[Double] and
      (JsPath \ "survives").write[Boolean]
    ) (unlift(Prediction.unapply))

  def titanic() = Action { implicit request: Request[AnyContent] =>
    val inputData: JsValue = request.body.asJson.get
    val passengers: Seq[Passenger] = inputData.validate[Seq[Passenger]].get
    val predictions: Seq[Prediction] = ModelScorer.predict(passengers)
    val outputData: JsValue = Json.toJson(predictions)

    Ok(outputData)
  }
}