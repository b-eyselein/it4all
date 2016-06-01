package controllers.binary;

import javax.inject.Inject;

import model.user.Secured;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.TableThree;

@Security.Authenticated(Secured.class)
public class Bool extends Controller {
  
  @Inject
  FormFactory factory;

  String[] solutions;

  public Result index() {
    return ok(TableThree.render("a", "b", "c", "a & b & c"));
  }

  public Result indexThree() {
    return ok(" " + solutions[0] + " " + solutions[7]);
  }

  public Result tableThreeAdd() {
    solutions = new String[8];
    DynamicForm dynFormula = factory.form().bindFromRequest();
    for(int i = 1; i < 9; i++) {
      solutions[(i - 1)] = dynFormula.get("sol" + i + "");
    }
    return redirect(routes.Bool.indexThree());
  }
}
