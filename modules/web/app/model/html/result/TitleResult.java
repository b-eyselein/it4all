package model.html.result;

import java.util.Collections;

import model.html.task.TitleTask;

public class TitleResult extends ElementResult<TitleTask> {

  public TitleResult(TitleTask task, Success success) {
    super(task, success, Collections.emptyList());
  }

}
