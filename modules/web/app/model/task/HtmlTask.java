package model.task;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.google.common.base.Splitter;

import io.ebean.Finder;
import model.Attribute;

@Entity
public class HtmlTask extends WebTask {

  public static final String ATTRS_JOIN_STR = ";";
  
  private static final Splitter ATTR_SPLITTER = Splitter.on(ATTRS_JOIN_STR).omitEmptyStrings();

  public static final Finder<WebTaskKey, HtmlTask> finder = new Finder<>(HtmlTask.class);

  @Column
  public String attributes;

  @Column
  public String textContent;

  public HtmlTask(WebTaskKey theKey) {
    super(theKey);
  }
  
  public List<Attribute> getAttributes() {
    return ATTR_SPLITTER.splitToList(attributes).stream().map(Attribute::fromString).collect(Collectors.toList());
  }

}
