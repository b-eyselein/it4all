package model.lti

object LTILisPersonData {

  def fromRequestData(data: Map[String, Seq[String]]): LTILisPersonData = new LTILisPersonData(
    sourcedId = data.getOrElse("lis_person_sourcedid", Seq.empty).mkString,
    givenName = data.getOrElse("lis_person_name_given", Seq.empty).mkString,
    familyName = data.getOrElse("lis_person_name_family", Seq.empty).mkString,
    fullName = data.getOrElse("lis_person_name_full", Seq.empty).mkString,
    primaryContactEmail = data.getOrElse("lis_person_contact_email_primary", Seq.empty).mkString
  )

}

class LTILisPersonData(val sourcedId: String, val primaryContactEmail: String,
                       val givenName: String, val familyName: String, val fullName: String)
