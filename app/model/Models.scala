package model

import enumeratum.{EnumEntry, PlayEnum}

import scala.collection.immutable.IndexedSeq

sealed trait Role extends EnumEntry

object Role extends PlayEnum[Role] {

  override val values: IndexedSeq[Role] = findValues

  case object RoleUser extends Role

  case object RoleAdmin extends Role

  case object RoleSuperAdmin extends Role

}

sealed abstract class ShowHideAggregate(val german: String) extends EnumEntry

object ShowHideAggregate extends PlayEnum[ShowHideAggregate] {

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

final case class LtiUser(username: String, stdRole: Role = Role.RoleUser, showHideAggregate: ShowHideAggregate = ShowHideAggregate.SHOW) extends User

final case class RegisteredUser(username: String, stdRole: Role = Role.RoleUser, showHideAggregate: ShowHideAggregate = ShowHideAggregate.SHOW) extends User

final case class PwHash(username: String, pwHash: String)

// Courses

final case class Course(id: String, courseName: String)

final case class UserInCourse(username: String, courseId: String, role: Role = Role.RoleUser)
