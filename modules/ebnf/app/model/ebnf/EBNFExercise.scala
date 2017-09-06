package model.ebnf

import javax.persistence.Entity

import com.fasterxml.jackson.annotation.JsonIgnore

import io.ebean.Finder
import model.exercise.Exercise
import com.fasterxml.jackson.annotation.JsonProperty

import scala.collection.JavaConverters._
import play.twirl.api.Html

@Entity
class EBNFExercise(i: Int) extends Exercise(i) {

  @JsonProperty(required = true)
  var terminals = ""

  def getTerminals = terminals.split(",")

  @JsonIgnore
  def getTerminalsForForm() = getTerminals.map(t => s"'$t'").mkString(" ")

  override def renderRest() = new Html(s"<td>$terminals</td>");

}

object EBNFExercise {

  val StartSymbol = "S"

  val finder = new Finder[Integer, EBNFExercise](classOf[EBNFExercise])

}