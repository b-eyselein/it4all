package model.ebnf

import java.util.Arrays
import java.util.List
import java.util.stream.Collectors

import javax.persistence.Entity

import com.fasterxml.jackson.annotation.JsonIgnore

import io.ebean.Finder
import model.exercise.Exercise

@Entity
class EBNFExercise(i: Int) extends Exercise(i) {

  val terminals = "1,0"

  def getTerminals() = terminals.split(",")

  @JsonIgnore
  def getTerminalsForForm() = getTerminals().map("'" + _ + "'").mkString(" ")

}

object EBNFExercise {

  val StartSymbol = "S"
  
  val finder = new Finder[Integer, EBNFExercise](classOf[EBNFExercise])

}