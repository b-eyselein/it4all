package model.toolMains

import model.CompleteExWrapper
import model.core.ExPart
import play.api.mvc.Call

case class ExAndRoute(ex: CompleteExWrapper, routes: Seq[(ExPart, Call)])
