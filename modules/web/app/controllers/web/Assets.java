package controllers.web;

import play.api.mvc.Action;
import play.api.mvc.AnyContent;

import com.google.inject.Inject;

import controllers.Assets.Asset;

public class Assets {
  
  @Inject
  Assets asset;
  
  public Action<AnyContent> versioned(String path, Asset file) {
    return asset.versioned(path, file);
  }
}