package models

import play.api.libs.json.{Json, Reads}

case class Person(name: String, age: Int) extends Combinable[Person] {
  override def combine(that: Person): Person = copy(name = s"$name ${that.name}", age = age + that.age)
}

object Person {
  implicit val reads: Reads[Person] = Json.reads[Person]
}
