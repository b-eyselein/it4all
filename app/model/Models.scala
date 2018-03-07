package model

import model.Enums.{Role, ShowHideAggregate}

import scala.util.Random

object Models {

}


case class User(username: String, pwHash: String, stdRole: Role = Role.RoleUser, todo: ShowHideAggregate = ShowHideAggregate.SHOW) {

  val isAdmin: Boolean = stdRole ne Role.RoleUser

}

case class Course(id: String, courseName: String)

case class UserInCourse(username: String, courseId: String, role: Role = Role.RoleUser)


object TippHelper {

  val ran = new Random

  val StdTipp = "Hier werden in Zukunft Tipps & Tricks zur Benutzung von it4all präsentiert."

  def getRandom: Tipp = Tipp(-1, StdTipp)

}

case class Tipp(id: Int, str: String)
