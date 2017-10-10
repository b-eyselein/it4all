package controllers.core

import javax.inject.Inject

import model.tools.RandomExToolObject
import model.user.User
import play.api.Configuration
import play.data.FormFactory
import play.mvc.Results
import play.twirl.api.Html

abstract class ARandomExController @Inject()(c: Configuration, f: FormFactory, toolObject: RandomExToolObject)
  extends BaseController(c, f) {

  def index = Results.ok(renderIndex(getUser))

  def renderIndex(user: User): Html

}