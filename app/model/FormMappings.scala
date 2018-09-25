package model

import model.core.CoreConsts._
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.{Form, FormError}

object FormMappings {

  // Forms

  final case class UpdateRoleForm(username: String, newRole: Role)

  final case class UserCredentials(username: String, password: String)

  // Formatters

  implicit object RoleFormatter extends Formatter[Role] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Role] = data.get(key) match {
      case None        => Left[Seq[FormError], Role](Seq(FormError(key, s"No value for $key was provided!")))
      case Some(value) => Role.withNameInsensitiveOption(value) match {
        case None       => Left[Seq[FormError], Role](Seq(FormError(key, s"The value $value is no legal value for a role!")))
        case Some(role) => Right[Seq[FormError], Role](role)
      }
    }

    override def unbind(key: String, value: Role): Map[String, String] = Map(key -> value.entryName)

  }

  // Mappings

  val saveOptionsForm: Form[String] = Form("showHideAgg" -> nonEmptyText)

  val updateRoleForm: Form[UpdateRoleForm] = Form(
    mapping(
      nameName -> nonEmptyText,
      roleName -> of[Role]
    )(UpdateRoleForm.apply)(UpdateRoleForm.unapply)
  )

  val pwChangeForm: Form[(String, String, String)] = Form(tuple("oldpw" -> nonEmptyText, "newpw1" -> nonEmptyText, "newpw2" -> nonEmptyText))

  val userCredForm: Form[UserCredentials] = Form(mapping(
    nameName -> nonEmptyText,
    pwName -> nonEmptyText
  )(UserCredentials.apply)(UserCredentials.unapply))

  def singleStrForm(str: String): Form[String] = Form(str -> nonEmptyText)

}

