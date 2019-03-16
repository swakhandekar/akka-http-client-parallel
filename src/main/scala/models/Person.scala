package models

import play.api.libs.json.{Json, Reads}

case class Person(name: String, age: Int)

object Person {
  implicit val reads: Reads[Person] = Json.reads[Person]
}
