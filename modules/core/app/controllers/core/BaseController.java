package controllers.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import io.ebean.Finder;
import model.StringConsts;
import model.WithId;
import model.user.User;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;

public abstract class BaseController extends Controller {

  protected static final String BASE_DATA_PATH = "/data"; // NOSONAR

  protected static final String SAMPLE_SUB_DIRECTORY = "samples";
  protected static final String SOLUTIONS_SUB_DIRECTORY = "solutions";

  protected FormFactory factory;

  public BaseController(FormFactory theFactory) {
    factory = theFactory;
  }

  public static User getUser() {
    return User.finder.byId(getUsername());
  }

  protected static <T extends WithId> int findMinimalNotUsedId(Finder<Integer, T> finder) {
    // FIXME: this is probably a ugly hack...
    List<T> questions = finder.all();

    Collections.sort(questions);

    if(questions.isEmpty())
      return 1;

    for(int i = 0; i < questions.size() - 1; i++)
      if(questions.get(i).getId() < questions.get(i + 1).getId() - 1)
        return questions.get(i).getId() + 1;

    return questions.get(questions.size() - 1).getId() + 1;
  }

  protected static String getUsername() {
    Http.Session session = Http.Context.current().session();

    if(session == null || session.get(StringConsts.SESSION_ID_FIELD) == null
        || session.get(StringConsts.SESSION_ID_FIELD).isEmpty())
      throw new IllegalArgumentException("No user name was given!");

    return session.get(StringConsts.SESSION_ID_FIELD);
  }

  public Path getSolDirForUser() {
    return getSolDirForUser(getUsername());
  }

  public Path getSolDirForUser(String username) {
    return Paths.get(BASE_DATA_PATH, SOLUTIONS_SUB_DIRECTORY, username);
  }

  protected boolean wantsJsonResponse() {
    return "application/json".equals(request().acceptedTypes().get(0).toString());
  }

}
