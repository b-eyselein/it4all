package controllers.binary;

import model.Secured;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import java.util.Random;

import javax.inject.Inject;

import controllers.core.UserManagement;
import views.html.naryquestion;
import views.html.narysolution;
import model.NAryNumbers.*;

@Security.Authenticated(Secured.class)
public class NAry extends Controller {
  
  @Inject
  FormFactory factory;
  
  NAryNumber number;
  String learnerSolution;
  String numberType;
  
  public Result addLearnerSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();
    learnerSolution = dynFormula.get("learnerSolution");
    return redirect(routes.NAry.checkSolution());
  }
  
  public Result index() {
    Random generator = new Random();
    int nValue = generator.nextInt(256);
    int nType = generator.nextInt(3);
    if(nType == 0) {
      numberType = "Binärzahl";
      nType = 2;
    } else if(nType == 1) {
      numberType = "Oktalzahl";
      nType = 8;
    } else if(nType == 2) {
      numberType = "Hexadezimalzahl";
      nType = 16;
    }
    number = new NAryNumber(nValue, nType);
    return ok(naryquestion.render(UserManagement.getCurrentUser(), number.toDec(), numberType));
  }
  
  public Result checkSolution() {
	return ok(narysolution.render(UserManagement.getCurrentUser(), number.toDec(), numberType, learnerSolution, number.toString(), number.toString().equals(learnerSolution)));
  }
}
