package controllers.core

import model.tools.RandomExToolObject
import model.user.User
import play.data.FormFactory
import play.mvc.Security.Authenticated
import play.mvc.{Result, Results}
import play.twirl.api.Html

@Authenticated(classOf[model.Secured])
abstract class ARandomExController(f: FormFactory, toolObject: RandomExToolObject)
  extends BaseController(f) {

  def index: Result = Results.ok(renderIndex(getUser))

  def renderIndex(user: User): Html

}