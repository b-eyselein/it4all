package model

final case class RegisterValues(username: String, firstPassword: String, secondPassword: String) {

  def isInvalid: Boolean = firstPassword != secondPassword

}

final case class UserCredentials(username: String, password: String)

final case class User(username: String, pwHash: Option[String] = None, isAdmin: Boolean = false)

final case class LoggedInUser(username: String, isAdmin: Boolean = false)

final case class LoggedInUserWithToken(
  loggedInUser: LoggedInUser,
  jwt: String
)
