package model;

import org.junit.Test;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

public class NotNullTest {
  
  @Test
  public void test() throws JSQLParserException {
    String sql = "SELECT * FROM table WHERE column IS NOT NULL;";
    
    Select statement = (Select) CCJSqlParserUtil.parse(sql);
    
    PlainSelect plain = (PlainSelect) statement.getSelectBody();
    
    Expression where = plain.getWhere();
    
    System.out.println(where.getClass());
  }
  
}
