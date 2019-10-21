package model

import model.core.CoreConsts._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{Format, Json}

final case class UpdateRoleForm(username: String, newRole: Role)

final case class UserCredentials(username: String, password: String)

object RequestBodyHelpers {

  //Json formats

  val userCredentialsFormat: Format[UserCredentials] = Json.format[UserCredentials]

  // Form Mappings

  val saveOptionsForm: Form[String] = Form("showHideAgg" -> nonEmptyText)

  val updateRoleForm: Form[UpdateRoleForm] = Form(
    mapping(
      nameName -> nonEmptyText,
      roleName -> Role.formField
    )(UpdateRoleForm.apply)(UpdateRoleForm.unapply)
  )

  val pwChangeForm: Form[(String, String, String)] = Form(tuple("oldpw" -> nonEmptyText, "newpw1" -> nonEmptyText, "newpw2" -> nonEmptyText))

  val userCredForm: Form[UserCredentials] = Form(mapping(
    nameName -> nonEmptyText,
    pwName -> nonEmptyText
  )(UserCredentials.apply)(UserCredentials.unapply))

  def singleStrForm(str: String): Form[String] = Form(str -> nonEmptyText)

}

