package model.user

import javax.persistence._

import io.ebean.{Finder, Model}

import scala.collection.JavaConverters._

@Entity
class Course(@Id var id: Int) extends Model {

  //  @Id
  //  var id: Int = _

  @Column
  var name: String = _

  @OneToMany(cascade = Array(CascadeType.ALL), mappedBy = "course")
  var courseRoles: java.util.List[CourseRole] = _

  def getAdministratorNames: List[String] = {
    val admins = courseRoles.asScala.filter(_.isAdmin).map(_.user.name)

    if (admins.isEmpty)
      return List("-- Dieser Kurs besitzt noch keine Administratoren! --")

    admins.toList
  }

  def getUsers: List[User] = courseRoles.asScala.map(_.getUser).toList

}

object Course {
  val finder: Finder[Integer, Course] = new Finder(classOf[Course])
}
