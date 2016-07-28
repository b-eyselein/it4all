package model;

import model.queryCorrectors.CreateCorrector;
import model.queryCorrectors.QueryCorrector;
import model.queryCorrectors.SelectCorrector;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.Select;

public enum Correctors {
  
  // @formatter:off
  SELECT(Select.class.getSimpleName(), new SelectCorrector()),
  CREATE(CreateTable.class.getSimpleName(), new CreateCorrector());
  // @formatter:on

  private String className;
  private QueryCorrector<? extends Statement> corrector;
  
  private Correctors(String theClassName, QueryCorrector<? extends Statement> theCorrector) {
    className = theClassName;
    corrector = theCorrector;
  }
  
  public QueryCorrector<? extends Statement> getCorrectorForClass(String classSimpleName) {
    for(Correctors corr: values())
      if(corr.className.equals(classSimpleName))
        return corr.corrector;
    return null;
  }
  
}
