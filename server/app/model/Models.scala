package model

import enumeratum.{EnumEntry, PlayEnum}


sealed trait Role extends EnumEntry

object Role extends PlayEnum[Role] {

  override val values: IndexedSeq[Role] = findValues

  case object RoleUser extends Role

  case object RoleAdmin extends Role

  case object RoleSuperAdmin extends Role

}

// Users

sealed trait User {

  val username: String
  val stdRole : Role

  def isAdmin: Boolean = stdRole ne Role.RoleUser

}

final case class LtiUser(username: String, stdRole: Role = Role.RoleUser) extends User

final case class RegisteredUser(username: String, stdRole: Role = Role.RoleUser) extends User

final case class PwHash(username: String, pwHash: String)
