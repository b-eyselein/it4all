package controllers

import model.tools.RandomExToolObject
import model.tools.ToolState

object NAryToolObject extends RandomExToolObject("Zahlensyteme", ToolState.LIVE) {

  override def indexCall = controllers.routes.NaryController.index
  
}