package controllers.core;

import javax.inject.Inject;

import play.data.FormFactory;

public class TestController extends BaseController {

  @Inject
  public TestController(FormFactory theFactory) {
    super(theFactory);
  }

}
