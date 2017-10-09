package model;

import model.exercise.Tag

class WebTag(part: String, hasExes: Boolean) extends Tag {

  override def cssClass = if (hasExes) "label label-primary" else "label label-default"

  override def getButtonContent = part

  override def getTitle = s"Diese Aufgabe besitzt ${if (!hasExes) "k" else ""}einen $part-Teil";

}