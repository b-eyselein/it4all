package model.html.result;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import model.html.task.TagTask;

public class TagResult extends ElementResult<TagTask> {
  
  public TagResult(TagTask task, Success success, List<AttributeResult> attributeResults) {
    super(task, success, attributeResults);
  }

  @Override
  protected List<String> getAttributesAsJson() {
    if(attributeResults.isEmpty())
      return Collections.emptyList();
    else
      return attributeResults.parallelStream().map(attrRes -> attrRes.toJSON()).collect(Collectors.toList());
  }

  @Override
  protected List<String> getMessagesAsJson() {
    if(success == Success.NONE)
      return Arrays.asList("{\"suc\": \"-\", \"mes\": \"Element wurde nicht gefunden!\"}");
    else
      return Arrays.asList("{\"suc\": \"+\", \"mes\": \"Element wurde gefunden.\"}");
  }
}
