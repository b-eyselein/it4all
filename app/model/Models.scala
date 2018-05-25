package model

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable.IndexedSeq
import scala.util.Random

sealed trait Role extends EnumEntry

object Role extends Enum[Role] {

  override val values: IndexedSeq[Role] = findValues

  case object RoleUser extends Role

  case object RoleAdmin extends Role

  case object RoleSuperAdmin extends Role

}

sealed abstract class ShowHideAggregate(val german: String) extends EnumEntry

object ShowHideAggregate extends Enum[ShowHideAggregate] {

  override val values: IndexedSeq[ShowHideAggregate] = findValues

  case object SHOW extends ShowHideAggregate("Einblenden")

  case object HIDE extends ShowHideAggregate("Ausblenden")

  case object AGGREGATE extends ShowHideAggregate("Zusammenfassen")

}

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

// Tipps

object TippHelper {

  val ran = new Random

  val StdTipp = "Hier werden in Zukunft Tipps & Tricks zur Benutzung von it4all präsentiert."

  def getRandom: Tipp = Tipp(-1, StdTipp)

}

case class Tipp(id: Int, str: String)
