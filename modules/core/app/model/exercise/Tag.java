package model.exercise;

import play.twirl.api.Html;

public interface Tag {

  public String getButtonContent();

  public String getTitle();

  public default Html render() {
    return new Html(
        "<span class=\"label label-default\" title=\"" + getTitle() + "\">" + getButtonContent() + "</span>");

  }

}