package model

import model.Enums.{Role, ShowHideAggregate}

import scala.util.Random

// Users

sealed trait User {

  val username         : String
  val stdRole          : Role
  val showHideAggregate: ShowHideAggregate

  def isAdmin: Boolean = stdRole ne Role.RoleUser

}

case class LtiUser(username: String, stdRole: Role = Role.RoleUser, showHideAggregate: ShowHideAggregate = ShowHideAggregate.SHOW) extends User

case class RegisteredUser(username: String, stdRole: Role = Role.RoleUser, showHideAggregate: ShowHideAggregate = ShowHideAggregate.SHOW) extends User

case class PwHash(username: String, pwHash: String)

// Courses

case class Course(id: String, courseName: String)

case class UserInCourse(username: String, courseId: String, role: Role = Role.RoleUser)

// Learning paths

object LearningPathSection {

  def tupled(values: (Int, Int, Boolean, String, String)): LearningPathSection = if (values._3) {
    ExerciseSection(values._1, values._2, values._4, values._5)
  } else {
    TextSection(values._1, values._2, values._4, values._5)
  }

  def unapply(arg: LearningPathSection): Option[(Int, Int, Boolean, String, String)] = arg match {
    case TextSection(id, pathId, title, content)     => Some(id, pathId, false, title, content)
    case ExerciseSection(id, pathId, title, content) => Some(id, pathId, true, title, content)
  }

}

trait LearningPathSection {

  val id     : Int
  val pathId : Int
  val title  : String
  val content: String

}

case class TextSection(id: Int, pathId: Int, title: String, content: String) extends LearningPathSection

case class ExerciseSection(id: Int, pathId: Int, title: String, content: String) extends LearningPathSection


case class LearningPathBase(id: Int, title: String)


case class CompleteLearningPath(learningPath: LearningPathBase, sections: Seq[LearningPathSection])

// Tipps

object TippHelper {

  val ran = new Random

  val StdTipp = "Hier werden in Zukunft Tipps & Tricks zur Benutzung von it4all präsentiert."

  def getRandom: Tipp = Tipp(-1, StdTipp)

}

case class Tipp(id: Int, str: String)
