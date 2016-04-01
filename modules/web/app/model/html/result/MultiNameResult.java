package model.html.result;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import model.html.task.MultiNameTask;

public class MultiNameResult extends ElementResult<MultiNameTask> {

  private List<String> defining_Attributes = new LinkedList<String>();
  
  // private HashMap<String, WebElement> singleResults;

  public MultiNameResult(MultiNameTask task, Success success, String commonAttributes, String differentAttributes) {
    super(task, success, Collections.emptyList());

    for(String attribute: differentAttributes.split(";"))
      if(attribute.contains("="))
        defining_Attributes.add(attribute);

    // TODO: Defining Attributes must not be empty (or size >= 2?
    // [___MULTI___ElementResult])
    if(defining_Attributes.size() < 2)
      throw new IllegalArgumentException("Es müssen mindestens 2 definierende Attribute vorhanden sein!");

    // singleResults = new HashMap<String, WebElement>();
  }

  @Override
  protected List<String> getAttributesAsJson() {
    // TODO Auto-generated method stub
    List<String> retList = new LinkedList<String>();
    return retList;
  }

  @Override
  protected List<String> getMessagesAsJson() {
    // TODO Auto-generated method stub
    return Collections.emptyList();
  }

}
