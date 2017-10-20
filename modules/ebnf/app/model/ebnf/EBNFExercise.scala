package model.ebnf

import java.nio.file.Path
import javax.persistence.Entity

import com.fasterxml.jackson.annotation.{JsonIgnore, JsonProperty}
import io.ebean.Finder
import model.exercise.Exercise
import play.twirl.api.Html

import scala.util.Try

@Entity
class EBNFExercise extends Exercise {

  @JsonProperty(required = true)
  var terminals: String = ""

  def getTerminals: Array[String] = terminals.split(",")

  @JsonIgnore
  def getTerminalsForForm: String = getTerminals.map(t => s"'$t'").mkString(" ")

  override def renderRest(fileResults: List[Try[Path]]): Html = new Html(s"<td>$terminals</td>")

}

object EBNFExercise {

  val StartSymbol = "S"

  val finder = new Finder[Integer, EBNFExercise](classOf[EBNFExercise])

}