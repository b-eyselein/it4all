package model.html.result;

import java.util.List;

import model.html.task.TagTask;

public class TagResult extends ElementResult<TagTask> {

  public TagResult(TagTask task, Success success, List<AttributeResult> attributeResults) {
    super(task, success, attributeResults);
  }

}
