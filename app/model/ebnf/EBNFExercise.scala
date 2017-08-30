package model.ebnf

import javax.persistence.Entity

import com.fasterxml.jackson.annotation.JsonIgnore

import io.ebean.Finder
import model.exercise.Exercise

@Entity
class EBNFExercise(i: Int) extends Exercise(i) {

  var terminals = "1,0"

  def terminals_=(theTerminals: List[String]): Unit = terminals = theTerminals.mkString(",")

  def getTerminals() = terminals.split(",")

  @JsonIgnore
  def getTerminalsForForm() = getTerminals().map("'" + _ + "'").mkString(" ")

}

object EBNFExercise {

  val StartSymbol = "S"

  val finder = new Finder[Integer, EBNFExercise](classOf[EBNFExercise])

}