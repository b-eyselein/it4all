package model.exercise;

import play.twirl.api.Html;

public interface Tag {

  public default String cssClass() {
    return "label label-primary";
  }

  public default String getButtonContent() {
    return toString();
  }

  public default String getTitle() {
    return toString();
  }

  public default Html render() {
    return new Html(
        String.format("<span class=\"%s\" title=\"%s\">%s</span>", cssClass(), getTitle(), getButtonContent()));
  }

}