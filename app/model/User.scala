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

final case class UserCredentials(username: String, password: String)

final case class User(username: String, pwHash: Option[String], stdRole: Role = Role.RoleUser) {

  def isAdmin: Boolean = stdRole ne Role.RoleUser

}
