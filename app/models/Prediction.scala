package models

case class Prediction(
  name: String,
  probability: Double,
  survives: Boolean
)
