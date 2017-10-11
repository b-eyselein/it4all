package model

import javax.persistence.{Column, Entity, EnumType, Enumerated}

import com.fasterxml.jackson.annotation.{JsonIgnore, JsonProperty}
import io.ebean.Finder
import model.exercise.{Exercise, Tag}
import play.twirl.api.Html

import scala.collection.JavaConverters._

@Entity
class XmlExercise(id: Int) extends Exercise(id) {

  @JsonProperty(value = "exerciseType", required = true)
  var exerciseTypeStr: String = XML_DTD.name

  @Column
  @JsonProperty(required = true)
  var rootNode: String = "root"

  @JsonIgnore
  def exerciseType = XmlExType.byName(exerciseTypeStr).getOrElse(XML_DTD)


  def fixedStart: String = if (exerciseType != XML_DTD) ""
  else
    s"""<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE $rootNode SYSTEM "$rootNode.dtd">"""


  @JsonIgnore
  override def getTags: java.util.List[Tag] = java.util.Arrays.asList(exerciseType)

  @JsonIgnore
  override def renderRest: Html = new Html("<td>" + exerciseType + "</td>\n<td>" + rootNode + "</td>")

}

object XmlExercise {
  val finder: Finder[Integer, XmlExercise] = new Finder(classOf[XmlExercise])

  def byType(exType: XmlExType): List[XmlExercise] = finder.all.asScala.filter(_.exerciseType == exType).toList

}