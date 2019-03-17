package models

trait Combinable[T] {
  def combine(that: T): T
}
