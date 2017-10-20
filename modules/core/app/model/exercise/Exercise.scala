package model.exercise

import java.nio.file.Path
import javax.persistence._

import com.fasterxml.jackson.annotation.{JsonGetter, JsonIgnore, JsonProperty}
import com.google.common.base.Splitter
import io.ebean.Model
import model.JsonReadable
import model.exercise.Exercise._
import play.twirl.api.Html
import scala.collection.JavaConverters._
import scala.util.Try

object Exercise {
  val SPLIT_CHAR = "#"

  val SPLITTER: Splitter = Splitter.fixedLength(100).omitEmptyStrings()

  val NEW_LINE_SPLITTER: Splitter = Splitter.on("\n")
}

@MappedSuperclass
abstract class Exercise extends Model with JsonReadable {

  @Id
  var id: Int

  @Column
  @JsonProperty(required = true)
  var title: String = _

  @Column
  @JsonProperty(required = true)
  var author: String = _

  @Column(columnDefinition = "text")
  @JsonProperty(required = true)
  var text: String = _

  @Column
  @Enumerated(EnumType.STRING)
  var state: ExerciseState = _


  override def getId: Int = id

  @JsonIgnore
  def getTags: java.util.List[Tag] = List.empty.asJava

  @JsonGetter("text")
  def getTextForJson: java.util.List[String] = SPLITTER.splitToList(text)

  def renderRest(fileResults: List[Try[Path]]) = new Html("")

  def renderEditRest(isCreation: Boolean) = new Html("")

  override def saveInDB(): Unit = save()

}
