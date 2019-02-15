package controllers

import javax.inject._

import model.core.Repository
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

class CourseAdminController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repository: Repository)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  override protected val adminRightsRequired: Boolean = true

  // FIXME: AJAX!
  def addAdmin(courseId: Int): EssentialAction = withUser { _ =>
    implicit request =>
      //      val userName = singleStrForm(NAME_NAME).bindFromRequest.get.str
      //      Option(User.finder.byId(userName)) match {
      //        case None => BadRequest(s"Der Nutzer mit dem Namen $userName existiert nicht!")
      //        case Some(userToChange) =>
      //          val key = new CourseRoleKey(userToChange.name, courseId)
      //          val courseRole = Option(CourseRole.finder.byId(key)).getOrElse(new CourseRole(key))
      //
      //          courseRole.role = Role.ADMIN
      //          courseRole.save()
      //
      //          Redirect(routes.CourseAdminController.course(courseId))
      //      }
      Ok("TODO!")
  }

  def course(id: Int): EssentialAction = withUser { _ =>
    implicit request =>
      //      Ok(views.html.admin.course.render(user, Course.finder.byId(id)))

      Ok("TODO!")
  }

  // FIXME: AJAX!
  def newCourse(): EssentialAction = withUser { _ =>
    implicit request =>
      //      val courseName = singleStrForm(NAME_NAME).bindFromRequest.get.str
      //      Course.finder.all.asScala.find(_.name == courseName) match {
      //        case Some(_) => BadRequest(s"Kurs mit Namen $courseName existiert bereits!")
      //        case None =>
      //          val course = new Course(courseName)
      //
      //          course.save()
      //
      //          // Create course admin with current user
      //          val firstAdmin = new CourseRole(new CourseRoleKey(user.name, course.id))
      //          firstAdmin.role = Role.ADMIN
      //          firstAdmin.save()
      //
      //          Redirect(routes.AdminController.activityDrawingIndex())
      //      }
      Ok("TODO!")
  }

  def newCourseForm(): EssentialAction = withUser { user => implicit request => Ok(views.html.admin.newCourseForm.render(user)) }
}
