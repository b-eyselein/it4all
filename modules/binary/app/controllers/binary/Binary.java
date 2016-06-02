package controllers.binary;

import model.Secured;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.BinaryV;

@Security.Authenticated(Secured.class)
public class Binary extends Controller {

	
  public Result index() {
    return ok(BinaryV.render());
  }

}
