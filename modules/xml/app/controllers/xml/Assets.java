package controllers.web;

import play.api.mvc.Action;
import play.api.mvc.AnyContent;

import com.google.inject.Inject;

public class Assets {
  
  @Inject
  controllers.Assets assets;
  
  public Action<AnyContent> versioned(String path, controllers.Assets.Asset file) {
    return assets.versioned(path, file);
  }
}