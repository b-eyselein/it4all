package model.lti

import enumeratum.{EnumEntry, PlayEnum}
import scala.collection.immutable.IndexedSeq


sealed abstract class Role(val urn: String) extends EnumEntry


sealed abstract class SystemRole(urn: String) extends Role(urn)

case object SystemRoles extends PlayEnum[SystemRole] {

  val values: IndexedSeq[SystemRole] = findValues


  case object SysAdmin extends SystemRole("urn:lti:sysrole:ims/lis/SysAdmin")

  case object SysSupport extends SystemRole("urn:lti:sysrole:ims/lis/SysSupport")

  case object Creator extends SystemRole("urn:lti:sysrole:ims/lis/Creator")

  case object AccountAdmin extends SystemRole("urn:lti:sysrole:ims/lis/AccountAdmin")

  case object User extends SystemRole("urn:lti:sysrole:ims/lis/User")

  case object Administrator extends SystemRole("urn:lti:sysrole:ims/lis/Administrator")

  case object None extends SystemRole("urn:lti:sysrole:ims/lis/None")

}


sealed abstract class InstitutionRole(urn: String) extends Role(urn)

case object InstitutionRoles extends PlayEnum[InstitutionRole] {

  val values: IndexedSeq[InstitutionRole] = findValues


  case object Student extends InstitutionRole("urn:lti:instrole:ims/lis/Student")

  case object Faculty extends InstitutionRole("urn:lti:instrole:ims/lis/Faculty")

  case object Member extends InstitutionRole("urn:lti:instrole:ims/lis/Member")

  case object Learner extends InstitutionRole("urn:lti:instrole:ims/lis/Learner")

  case object Instructor extends InstitutionRole("urn:lti:instrole:ims/lis/Instructor")

  case object Mentor extends InstitutionRole("urn:lti:instrole:ims/lis/Mentor")

  case object Staff extends InstitutionRole("urn:lti:instrole:ims/lis/Staff")

  case object Alumni extends InstitutionRole("urn:lti:instrole:ims/lis/Alumni")

  case object ProspectiveStudent extends InstitutionRole("urn:lti:instrole:ims/lis/ProspectiveStudent")

  case object Guest extends InstitutionRole("urn:lti:instrole:ims/lis/Guest")

  case object Other extends InstitutionRole("urn:lti:instrole:ims/lis/Other")

  case object Administrator extends InstitutionRole("urn:lti:instrole:ims/lis/Administrator")

  case object Observer extends InstitutionRole("urn:lti:instrole:ims/lis/Observer")

  case object None extends InstitutionRole("urn:lti:instrole:ims/lis/None")

}