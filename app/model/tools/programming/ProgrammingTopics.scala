package model.tools.programming

import model.Topic

object ProgrammingTopics {

  val ForLoops   = Topic("FL", "programming", "For-Schleifen")
  val WhileLoops = Topic("WL", "programming", "While-Schleifen")
  val Conditions = Topic("C", "programming", "Bedingungen")
  val Lists      = Topic("L", "programming", "Listen")
  val Tuples     = Topic("T", "programming", "Tuples")
  val Dicts      = Topic("D", "programming", "Dictionaries")
  val Classes    = Topic("CL", "programming", "Klassen")
  val Exceptions = Topic("E", "programming", "Exceptions")
  val Maths      = Topic("M", "programming", "Mathematik")
  val Strings    = Topic("S", "programming", "Strings")
  val Slicing    = Topic("SL", "programming", "Slicing")
  val Recursion  = Topic("R", "programming", "Rekursion")

  val values: Seq[Topic] = Seq(
    ForLoops,
    WhileLoops,
    Conditions,
    Lists,
    Tuples,
    Dicts,
    Classes,
    Exceptions,
    Maths,
    Strings,
    Slicing,
    Recursion
  )
}
