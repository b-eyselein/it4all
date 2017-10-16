package model

import model.user.User
import play.mvc.Http.Context
import play.mvc.{Result, Results, Security}

class AdminSecured extends Security.Authenticator {

  override def getUsername(ctx: Context): String = Option(ctx.session.get(StringConsts.SESSION_ID_FIELD)).map(userName => User.finder.byId(userName)) match {
    case None =>
      ctx.session.clear()
      null
    case Some(user) => if (user.isAdmin) user.name else null
  }

  override def onUnauthorized(ctx: Context): Result = Results.redirect(controllers.routes.LoginController.login)
}

class Secured extends Security.Authenticator {

  override def getUsername(ctx: Context): String = Option(ctx.session.get(StringConsts.SESSION_ID_FIELD)).map(userName => User.finder.byId(userName)) match {
    case None =>
      ctx.session.clear()
      null
    case Some(user) => user.name
  }

  override def onUnauthorized(ctx: Context): Result = Results.redirect(controllers.routes.LoginController.login)
}
