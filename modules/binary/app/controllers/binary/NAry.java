package controllers.binary;

import model.Secured;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import java.util.Random;

import javax.inject.Inject;

import views.html.NAryV;
import views.html.NArySolution;
import model.NAryNumbers.*;

@Security.Authenticated(Secured.class)
public class NAry extends Controller {
  
  @Inject
  FormFactory factory;
  
  int number;
  String formula;
  String numberType;
  
  public Result addFormula() {
    DynamicForm dynFormula = factory.form().bindFromRequest();
    formula = dynFormula.get("formula");
    return redirect(routes.NAry.index2());
  }
  
  public Result index() {
    Random generator = new Random();
    number = generator.nextInt(256);
    String n = "" + number;
    int nType = generator.nextInt(3);
    if(nType == 0) {
      numberType = "Binärzahl";
    } else if(nType == 1) {
      numberType = "Oktalzahl";
    } else if(nType == 2) {
      numberType = "Hexadezimalzahl";
    }
    return ok(NAryV.render(n, numberType));
  }
  
  public Result index2() {
    if(numberType.equals("Oktalzahl")) {
      OctalNumber nr = new OctalNumber(number);
      if(nr.toString().equals(formula)) {
        return ok(NArySolution.render(formula, "richtig"));
      } else {
        return ok(NArySolution.render(formula, "falsch"));
      }
    } else if(numberType.equals("Binärzahl")) {
      BinaryNumber nr = new BinaryNumber(number);
      if(nr.toString().equals(formula)) {
        return ok(NArySolution.render(formula, "richtig"));
      } else {
        return ok(NArySolution.render(formula, "falsch"));
      }
    } else if(numberType.equals("Hexadezimalzahl")) {
      HexadecimalNumber nr = new HexadecimalNumber(number);
      if(nr.toString().equals(formula)) {
        return ok(NArySolution.render(formula, "richtig"));
      } else {
        return ok(NArySolution.render(formula, "falsch"));
      }
    } else {
      return ok("fail");
    }
  }
}
