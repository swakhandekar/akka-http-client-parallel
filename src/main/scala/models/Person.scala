package models

import play.api.libs.json.{Json, Reads}

case class Person(name: String, age: Int)

object Person {
  implicit val reads: Reads[Person] = Json.reads[Person]
}

case class PersonList(elements: List[Person]) extends Combinable[PersonList] {
  override def combine(that: PersonList): PersonList = copy(elements = elements ++ that.elements)

}

object PersonList {
  implicit val reads: Reads[PersonList] = Json.reads[PersonList]
}