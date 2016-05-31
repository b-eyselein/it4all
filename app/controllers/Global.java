package controllers;

import controllers.web.WebStartUpChecker;
import play.Application;
import play.GlobalSettings;
import play.Logger;

public class Global extends GlobalSettings {
  
  static Logger.ALogger theLogger = Logger.of("startup");

  @Override
  public void onStart(Application app) {
    super.onStart(app);

    theLogger.info("Starting app: Performing Startup-checks...");

    // FIXME: check, ob entsprechende Ordner vorhanden sind...
    WebStartUpChecker.performStartUpCheck();
  }

}
