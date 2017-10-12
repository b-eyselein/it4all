package model.ebnf

import javax.persistence.Entity

import com.fasterxml.jackson.annotation.{JsonIgnore, JsonProperty}
import io.ebean.Finder
import model.exercise.Exercise
import play.twirl.api.Html

@Entity
class EBNFExercise(i: Int) extends Exercise(i) {

  @JsonProperty(required = true)
  var terminals: String = ""

  def getTerminals: Array[String] = terminals.split(",")

  @JsonIgnore
  def getTerminalsForForm: String = getTerminals.map(t => s"'$t'").mkString(" ")

  override def renderRest: Html = new Html(s"<td>$terminals</td>")

}

object EBNFExercise {

  val StartSymbol = "S"

  val finder = new Finder[Integer, EBNFExercise](classOf[EBNFExercise])

}