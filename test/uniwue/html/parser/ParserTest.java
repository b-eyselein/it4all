package uniwue.html.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import uniwue.html.parser.Parser.ParsingState;
import uniwue.html.parser.document.ChildrenHtmlTag;
import uniwue.html.parser.document.RootHtmlTag;
import uniwue.html.parser.document.TextTag;
import uniwue.html.parser.result.FailureParseResult;
import uniwue.html.parser.result.HtmlParsingError;
import uniwue.html.parser.result.ParseResult;
import uniwue.html.parser.result.SuccessParseResult;

public class ParserTest {

  
  @Test
  public void testParse() {
    String html = "<!DOCTYPE html><html lang=\"en\"><head><title>Titel</title></head><body>Body...</body></html>";

    Parser parser = new Parser();
    ParseResult parseResult = parser.parse(html);
    
    assertThat(parseResult, instanceOf(SuccessParseResult.class));
    SuccessParseResult successResult = (SuccessParseResult) parseResult;
    
    assertThat(successResult.getDoctypeString(), equalTo("<!DOCTYPE html>"));
    
    RootHtmlTag rootTag = successResult.getRootTag();
    assertThat(rootTag.getTag(), equalTo("html"));
    assertThat(rootTag.getAttributeValue("lang"), equalTo("en"));
    
    List<ChildrenHtmlTag> children = rootTag.getChildren();
    ChildrenHtmlTag head = children.get(0);
    assertThat(head.getTag(), equalTo("head"));
    
    ChildrenHtmlTag title = head.getChildren().get(0);
    assertThat(title.getTag(), equalTo("title"));
    
    TextTag titleText = (TextTag) title.getChildren().get(0);
    assertThat(titleText.getContent(), equalTo("Titel"));
    
    ChildrenHtmlTag body = children.get(1);
    assertThat(body.getTag(), equalTo("body"));
    
    TextTag bodyContent = (TextTag) body.getChildren().get(0);
    assertThat(bodyContent.getContent(), equalTo("Body..."));
    
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testRootTagMissing() {
    String html = "<!DOCTYPE html><head>";
    Parser parser = new Parser();
    parser.parse(html);
  }
  
  @Test
  public void testDoctypeNotProperlyClosed() {
    String html = "<!DOCTYPE html<";
    
    Parser parser = new Parser();
    ParseResult parseResult = parser.parse(html);
    assertFalse(parseResult.parseWasSuccessful());
    
    HtmlParsingError error = ((FailureParseResult) parseResult).getErrors().get(0);
    assertThat(error.getState(), equalTo(ParsingState.READING_DOCTYPE));
    assertThat(error.getMessage(), equalTo("DOCTYPE was not properly closed: <!DOCTYPE html<"));
    assertThat(error.getLine(), equalTo(1));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testRootNotProperlyClosed() {
    String html = "<!DOCTYPE html><head<\n    <title>Titel</title>\n</head>\n<body>\n    Body...\n</body>\n</html>";
    
    Parser parser = new Parser();
    ParseResult parseResult = parser.parse(html);
    assertFalse(parseResult.parseWasSuccessful());
    
    HtmlParsingError error = ((FailureParseResult) parseResult).getErrors().get(0);
    String message = error.getMessage();
    assertThat(error.getState(), equalTo(ParsingState.OPEN_TAG_READ_NAME));
    assertTrue("Expected string matching '...head<...'. Got: " + message, message.contains("head<"));
    assertThat(error.getLine(), equalTo(1));
  }

  @Test
  public void testEmptyTag() {
    String html = "<!DOCTYPE html><   >";

    Parser parser = new Parser();
    ParseResult parseResult = parser.parse(html);
    assertFalse(parseResult.parseWasSuccessful());
    
    HtmlParsingError error = ((FailureParseResult) parseResult).getErrors().get(0);
    String message = error.getMessage();
    assertThat(error.getState(), equalTo(ParsingState.OPEN_TAG_SEARCH_NAME));
    assertTrue("Expected string matching '...Leeres Tag...'. Got: " + message, message.contains("Leeres Tag"));
    assertThat(error.getLine(), equalTo(1));
  }
  
  @Test
  public void testDoubleTagOpening() {
    String html = "<!DOCTYPE html><<html";

    Parser parser = new Parser();
    ParseResult parseResult = parser.parse(html);
    assertFalse(parseResult.parseWasSuccessful());
    
    HtmlParsingError error = ((FailureParseResult) parseResult).getErrors().get(0);
    String message = error.getMessage();
    assertThat(error.getState(), equalTo(ParsingState.OPEN_TAG_SEARCH_NAME));
    assertTrue("Expected string matching '...Tag doppelt geoeffnet: <<...'. Got: " + message,
        message.contains("Tag doppelt geoeffnet: <<"));
    assertThat(error.getLine(), equalTo(1));
  }
  
  @Test
  public void newTest() {
    String html = "<!DOCTYPE html><html><head>    <title>Titel</title></head><body>    Body...</body></html>  ";
    
    Parser parser = new Parser();
    ParseResult parseResult = parser.parse(html);
    assertTrue(parseResult.parseWasSuccessful());
  }
  
}
