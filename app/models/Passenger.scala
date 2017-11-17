package models

case class Passenger(
  pclass: Double,
  name: String,
  sex: String,
  age: Option[Double],
  sibsp: Int,
  parch: Int,
  fare: Option[Double],
  embarked: String
)