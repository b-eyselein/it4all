package controllers.core;

import java.util.List;

import com.avaje.ebean.Model.Finder;

import model.StringConsts;
import model.Util;
import model.exercise.Exercise;
import model.user.User;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;

public abstract class AbstractController extends Controller {

  protected Util util;
  protected FormFactory factory;

  public AbstractController(Util theUtil, FormFactory theFactory) {
    util = theUtil;
    factory = theFactory;
  }

  public static User getUser() {
    return User.finder.byId(getUsername());
  }

  protected static <T extends Exercise> int findMinimalNotUsedId(Finder<Integer, T> finder) {
    // FIXME: this is probably a ugly hack...
    List<T> questions = finder.order().asc("id").findList();

    if(questions.isEmpty())
      return 1;

    for(int i = 0; i < questions.size() - 1; i++)
      if(questions.get(i).id < questions.get(i + 1).id - 1)
        return questions.get(i).id + 1;

    return questions.get(questions.size() - 1).id + 1;
  }

  protected static String getUsername() {
    Http.Session session = Http.Context.current().session();

    if(session == null || session.get(StringConsts.SESSION_ID_FIELD) == null
        || session.get(StringConsts.SESSION_ID_FIELD).isEmpty())
      throw new IllegalArgumentException("No user name was given!");

    return session.get(StringConsts.SESSION_ID_FIELD);
  }

}
