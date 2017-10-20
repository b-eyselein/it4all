package controllers

import javax.inject.Inject

import controllers.core.BaseController
import model.StringConsts._
import model.user._
import play.api.mvc.ControllerComponents
import play.mvc.Security

import scala.collection.JavaConverters._

@Security.Authenticated(classOf[model.AdminSecured])
class CourseAdminController @Inject()(cc: ControllerComponents) extends BaseController(cc) {

  // FIXME: AJAX!
  def addAdmin(courseId: Int) = Action { implicit request =>
    val userName = singleStrForm(NAME_NAME).bindFromRequest.get.str
    Option(User.finder.byId(userName)) match {
      case None => BadRequest(s"Der Nutzer mit dem Namen $userName existiert nicht!")
      case Some(user) =>
        val key = new CourseRoleKey(user.name, courseId)
        val courseRole = Option(CourseRole.finder.byId(key)).getOrElse(new CourseRole(key))

        courseRole.role = Role.ADMIN
        courseRole.save()

        Redirect(routes.CourseAdminController.course(courseId))
    }
  }

  def course(id: Int) = Action { implicit request =>
    Ok(views.html.admin.course.render(getUser, Course.finder.byId(id)))
  }

  // FIXME: AJAX!
  def newCourse() = Action { implicit request =>
    val courseName = singleStrForm(NAME_NAME).bindFromRequest.get.str
    Course.finder.all.asScala.find(_.name == courseName) match {
      case Some(course) => BadRequest(s"Kurs mit Namen $courseName existiert bereits!")
      case None =>
        val course = new Course()
        course.name = courseName

        course.save()

        // Create course admin with current user
        val firstAdmin = new CourseRole(new CourseRoleKey(getUser.name, course.id))
        firstAdmin.role = Role.ADMIN
        firstAdmin.save()

        Redirect(routes.AdminController.index())
    }
  }

  def newCourseForm() = Action { implicit request =>
    Ok(views.html.admin.newCourseForm.render(getUser))
  }
}
