package model

import javax.persistence.{Column, Entity}

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
  var rootNode: String = _

  @JsonIgnore
  def exerciseType: XmlExType = XmlExType.byName(exerciseTypeStr).getOrElse(XML_DTD)

  val fixedStart: String = if (exerciseType != XML_DTD) ""
  else
    s"""<?xml version="1.0" encoding="UTF-8"?>
       |<!DOCTYPE $rootNode SYSTEM "$rootNode.dtd">""".stripMargin

  @JsonIgnore
  override def getTags: java.util.List[Tag] = java.util.Arrays.asList(exerciseType)

  @JsonIgnore
  override def renderRest: Html = new Html(
    s"""<td>$exerciseType</td>
       |<td>$rootNode</td>""".stripMargin)

  @JsonIgnore
  override def renderEditRest(isCreation: Boolean): Html = views.html.editXmlExRest(this, isCreation)

}

object XmlExercise {
  val finder: Finder[Integer, XmlExercise] = new Finder(classOf[XmlExercise])

  def byType(exType: XmlExType): List[XmlExercise] = finder.all.asScala.filter(_.exerciseType == exType).toList
}