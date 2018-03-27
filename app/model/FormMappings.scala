package model

import model.Enums.Role
import model.core.CoreConsts._
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.{Form, FormError}

object FormMappings {

  // Forms

  case class UpdateRoleForm(username: String, newRole: Role)

  case class UserCredForm(username: String, password: String)

  // Formatters

  implicit object RoleFormatter extends Formatter[Role] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Role] = data.get(key) match {
      case None        => Left(Seq(FormError(key, s"No value for $key was provided!")))
      case Some(value) => Role.byString(value) match {
        case None       => Left(Seq(FormError(key, s"The value $value is no legal value for a role!")))
        case Some(role) => Right(role)
      }
    }

    override def unbind(key: String, value: Role): Map[String, String] = Map(key -> value.name)

  }

  // Mappings

  val saveOptionsForm: Form[String] = Form("posTests" -> nonEmptyText)

  val updateRoleForm: Form[UpdateRoleForm] = Form(
    mapping(
      nameName -> nonEmptyText,
      roleName -> of[Role]
    )(UpdateRoleForm.apply)(UpdateRoleForm.unapply)
  )

  val pwChangeForm: Form[(String, String, String)] = Form(tuple("oldpw" -> nonEmptyText, "newpw1" -> nonEmptyText, "newpw2" -> nonEmptyText))

  val userCredForm: Form[UserCredForm] = Form(mapping(
    nameName -> nonEmptyText,
    pwName -> nonEmptyText
  )(UserCredForm.apply)(UserCredForm.unapply))

  def singleStrForm(str: String): Form[String] = Form(str -> nonEmptyText)

}

