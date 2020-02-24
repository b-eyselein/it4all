package model.lti

object LTILisPersonData {

  def fromRequestData(data: Map[String, Seq[String]]): LTILisPersonData = new LTILisPersonData(
    sourcedId = data.getOrElse("lis_person_sourcedid", Seq[String]()).mkString,
    givenName = data.getOrElse("lis_person_name_given", Seq[String]()).mkString,
    familyName = data.getOrElse("lis_person_name_family", Seq[String]()).mkString,
    fullName = data.getOrElse("lis_person_name_full", Seq[String]()).mkString,
    primaryContactEmail = data.getOrElse("lis_person_contact_email_primary", Seq[String]()).mkString
  )

}

class LTILisPersonData(
  val sourcedId: String,
  val primaryContactEmail: String,
  val givenName: String,
  val familyName: String,
  val fullName: String
)
