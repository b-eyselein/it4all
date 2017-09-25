package controllers.core

import javax.inject.Inject
import play.data.FormFactory
import model.tools.RandomExToolObject

class ARandomExController @Inject() (f: FormFactory, toolObject: RandomExToolObject) extends BaseController(f) {

}