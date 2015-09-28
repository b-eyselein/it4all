package uniwue.html.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import uniwue.html.parser.document.ChildrenHtmlTag;
import uniwue.html.parser.document.HtmlTag;
import uniwue.html.parser.document.RootHtmlTag;
import uniwue.html.parser.handler.DefaultStateCharHandler;
import uniwue.html.parser.result.FailureParseResult;
import uniwue.html.parser.result.HtmlParsingError;
import uniwue.html.parser.result.ParseResult;
import uniwue.html.parser.result.SuccessParseResult;

public class Parser {
  
  private ParsingState parsingState;
  
  public Parser() {
    parsingState = ParsingState.DEFAULT;
  }
  
  public ParseResult parse(String html) {
    parsingState = ParsingState.DEFAULT;
    
    StringReader reader = new StringReader(html);
    
    List<HtmlParsingError> errors = new LinkedList<HtmlParsingError>();
    
    String doctypeString = "";
    int read;
    String currentRead = "";
    RootHtmlTag docRoot = null;
    HtmlTag currentOpenTag = null;
    
    String attributeName = "";
    
    int line = 1;
    try {
      while((read = reader.read()) != -1) {
        char c = (char) read;
        
        if(c == 10) // 10 == NEW LINE
          line++;
        
        switch(parsingState) {
        case DEFAULT:
          currentRead = DefaultStateCharHandler.handle(this, c, currentRead, currentOpenTag);
          break;
        
        case OPEN_TAG_SEARCH_NAME:
          if(c == '>') {
            // gelesen: "<   >" --> FEHLER!!!
            errors.add(new HtmlParsingError(parsingState, "Leeres Tag", line));
            currentRead = "";
            changeStateTo(ParsingState.DEFAULT);
          } else if(c == '/') {
            changeStateTo(ParsingState.CLOSE_TAG);
          } else if(c == '!') {
            // Ignore character, start reading doctype
            currentRead += c;
            changeStateTo(ParsingState.READING_DOCTYPE);
          } else if(c == '<') {
            // FEHLER: Neues Tag doppelt
            errors.add(new HtmlParsingError(parsingState, "Tag doppelt geoeffnet: <<", line));
          } else if(Character.isWhitespace(c)) {
            // Ignore Whitespaces
          } else {
            // Tagname found
            currentRead += c;
            changeStateTo(ParsingState.OPEN_TAG_READ_NAME);
          }
          break;
        
        case OPEN_TAG_READ_NAME:
          if(c == '>') {
            changeStateTo(ParsingState.DEFAULT);
            HtmlTag newParent = null;
            if(currentOpenTag != null) {
              newParent = new ChildrenHtmlTag(currentRead, currentOpenTag);
              currentOpenTag.addChildren((ChildrenHtmlTag) newParent);
            } else {
              newParent = new RootHtmlTag(currentRead);
              docRoot = (RootHtmlTag) newParent;
            }
            currentOpenTag = newParent;
            currentRead = "";
          } else if(c == '/') {
            changeStateTo(ParsingState.CLOSE_TAG);
          } else if(c == '!') {
            // Ignore character, start reading doctype
            changeStateTo(ParsingState.READING_DOCTYPE);
          } else if(c == '<') {
            // FEHLER: Neues Tag innerhalb Tag geoeffnet
            errors.add(new HtmlParsingError(parsingState, currentRead + "<", line));
          } else if(Character.isWhitespace(c)) {
            // TagName gelesen, attribute!
            // TODO: tag erstellen, um att hinzuzufuegen
            HtmlTag newParent = null;
            if(currentOpenTag != null) {
              newParent = new ChildrenHtmlTag(currentRead, currentOpenTag);
              currentOpenTag.addChildren((ChildrenHtmlTag) newParent);
            } else {
              newParent = new RootHtmlTag(currentRead);
              docRoot = (RootHtmlTag) newParent;
            }
            currentOpenTag = newParent;
            currentRead = "";
            changeStateTo(ParsingState.OPEN_TAG_READ_ATTRIBUTE_NAME);
          } else {
            // Tagname found
            currentRead += c;
          }
          break;
        
        case OPEN_TAG_READ_ATTRIBUTE_NAME:
          if(c == '>') {
            // TODO: attribut ohne value?
            currentOpenTag.addAttribute(currentRead, "");
            currentRead = "";
            changeStateTo(ParsingState.DEFAULT);
          } else if(c == '=') {
            attributeName = currentRead;
            currentRead = "";
            changeStateTo(ParsingState.OPEN_TAG_READ_ATTRIBUTE_VALUE);
          } else if(Character.isWhitespace(c)) {
            // TODO: Unterscheidung: kommt '=' oder neues Attribut?
          } else {
            currentRead += c;
          }
          break;
        
        case OPEN_TAG_READ_ATTRIBUTE_VALUE:
          if(c == '>') {
            currentOpenTag.addAttribute(attributeName, currentRead);
            currentRead = "";
            changeStateTo(ParsingState.DEFAULT);
          } else if(c == '=') {
            // TODO: Fehler!
            changeStateTo(ParsingState.OPEN_TAG_READ_ATTRIBUTE_NAME);
          } else if(c == '"') {
            // Ignore quotation marks
          } else if(Character.isWhitespace(c)) {
            // TODO: Unterscheidung: kommt value oder neues Attribut?
          } else {
            currentRead += c;
          }
          break;
        
        case CLOSE_TAG:
          if(c == '>') {
            // TODO: close tag!
            changeStateTo(ParsingState.DEFAULT);
            if(currentOpenTag instanceof ChildrenHtmlTag) {
              currentRead = "";
              currentOpenTag = ((ChildrenHtmlTag) currentOpenTag).getParent();
            } else {
              currentRead = "";
              currentOpenTag = null;
            }
          } else if(c == '<') {
            errors.add(new HtmlParsingError(parsingState, "Closing tag not properly closed: </" + currentRead + "<",
                line));
          } else {
            currentRead += c;
          }
          break;
        
        case READING_DOCTYPE:
          if(c == '>') {
            changeStateTo(ParsingState.DEFAULT);
            doctypeString = "<" + currentRead + ">";
            currentRead = "";
          } else if(c == '<') {
            errors.add(new HtmlParsingError(parsingState, "DOCTYPE was not properly closed: <" + currentRead + "<",
                line));
            currentRead = "";
            changeStateTo(ParsingState.OPEN_TAG_SEARCH_NAME);
          } else if(c == 10) {
            //Ignore Whitespaces
          }else
            currentRead += c;
          break;
        
        default:
          // throw new IllegalArgumentException("ILLEGAL PARSINGSTATE");
        }
        
      }
      
      if(errors.isEmpty())
        return new SuccessParseResult(doctypeString, docRoot);
      else {
        FailureParseResult failure = new FailureParseResult();
        failure.addAllErrors(errors);
        return failure;
      }
    } catch (IOException e) {
      FailureParseResult failure = new FailureParseResult();
      failure.addParsingError(new HtmlParsingError(parsingState, "An IO-Error occured...?", line));
      return failure;
    }
  }
  
  public void changeStateTo(ParsingState state) {
    this.parsingState = state;
  }
  
  public static enum ParsingState {
    OPEN_TAG_SEARCH_NAME, OPEN_TAG_READ_NAME, OPEN_TAG_READ_ATTRIBUTE_NAME, OPEN_TAG_READ_ATTRIBUTE_VALUE, CLOSE_TAG, READING_DOCTYPE, SEARCHING_TAG, DEFAULT;
  }
}
