package controllers;

import java.nio.file.Files;

import controllers.core.Util;
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

    // TODO: Assert folder for solutions exists
    if(!Files.exists(Util.getRootSolDir()))
      theLogger.error("Folder for solutions does not exits!");
    
    // StartupCheck for Subproject Web
    WebStartUpChecker.performStartUpCheck();
  }

}
