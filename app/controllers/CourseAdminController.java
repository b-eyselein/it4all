package controllers;

import javax.inject.Inject;

import controllers.core.BaseController;
import model.AdminSecured;
import model.StringConsts;
import model.exercisereading.ExerciseReader;
import model.user.Course;
import model.user.CourseRole;
import model.user.CourseRoleKey;
import model.user.Role;
import model.user.User;
import play.data.FormFactory;
import play.mvc.Result;
import play.mvc.Security.Authenticated;

@Authenticated(AdminSecured.class)
public class CourseAdminController extends BaseController {
  
  @Inject
  public CourseAdminController(FormFactory theFactory) {
    super(theFactory);
  }
  
  public Result addAdmin(int courseId) {
    // FIXME: AJAX!
    String userName = factory.form().bindFromRequest().get(StringConsts.NAME_NAME);
    
    User user = User.finder.byId(userName);
    if(user == null)
      return badRequest("Der Nutzer mit Name >>" + userName + "<< existiert nicht!");
    
    CourseRoleKey key = new CourseRoleKey(user.name, courseId);
    CourseRole courseRole = CourseRole.finder.byId(key);
    if(courseRole == null)
      courseRole = new CourseRole(key);
    
    courseRole.role = Role.ADMIN;
    courseRole.save();
    
    return redirect(routes.CourseAdminController.course(courseId));
  }
  
  public Result course(int id) {
    return ok(views.html.admin.course.render(getUser(), Course.finder.byId(id)));
  }
  
  public Result newCourse() {
    // FIXME: AJAX!
    String courseName = factory.form().bindFromRequest().get(StringConsts.NAME_NAME);
    
    Course course = Course.finder.all().stream().filter(c -> c.name.equals(courseName)).findAny().orElse(null);
    if(course != null)
      return badRequest("Kurs mit Namen >>" + courseName + "<< existiert bereits!");
    
    course = new Course(ExerciseReader.findMinimalNotUsedId(Course.finder));
    course.name = courseName;
    
    course.save();
    
    // Create course admin with current user
    CourseRole firstAdmin = new CourseRole(new CourseRoleKey(getUser().name, course.id));
    firstAdmin.role = Role.ADMIN;
    firstAdmin.save();
    
    return redirect(routes.AdminController.index());
  }
  
  public Result newCourseForm() {
    return ok(views.html.admin.newCourseForm.render(getUser()));
  }
}
