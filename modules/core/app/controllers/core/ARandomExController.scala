package controllers.core

import javax.inject.Inject
import play.data.FormFactory
import model.tools.RandomExToolObject
import play.twirl.api.Html
import play.mvc.Results
import model.user.User

abstract class ARandomExController @Inject() (f: FormFactory, toolObject: RandomExToolObject) extends BaseController(f) {

  def index() = Results.ok(renderIndex(BaseController.getUser))

  def renderIndex(user: User): Html

}