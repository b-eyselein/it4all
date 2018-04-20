package model

import model.Enums.{Role, ShowHideAggregate}

import scala.util.Random

sealed trait User {

  val username: String
  val stdRole: Role
  val showHideAggregate: ShowHideAggregate

  def isAdmin: Boolean = stdRole ne Role.RoleUser

}

case class LtiUser(username: String, stdRole: Role = Role.RoleUser, showHideAggregate: ShowHideAggregate = ShowHideAggregate.SHOW) extends User

case class RegisteredUser(username: String, stdRole: Role = Role.RoleUser, showHideAggregate: ShowHideAggregate = ShowHideAggregate.SHOW) extends User

case class PwHash(username: String, pwHash: String)


case class Course(id: String, courseName: String)

case class UserInCourse(username: String, courseId: String, role: Role = Role.RoleUser)


object TippHelper {

  val ran = new Random

  val StdTipp = "Hier werden in Zukunft Tipps & Tricks zur Benutzung von it4all präsentiert."

  def getRandom: Tipp = Tipp(-1, StdTipp)

}

case class Tipp(id: Int, str: String)
