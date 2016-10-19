package model.mindmap.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import model.mindmap.basics.Image;
import model.mindmap.basics.TreeNode;
import model.mindmap.evaluation.enums.DifferenceResult;
import model.mindmap.evaluation.enums.Modus;
import model.mindmap.parser.basics.UnZip;

public class MindManagerParser extends AbstractEvaluationParser {
  
  private int id;
  
  private int index = 0;
  private Document doc;
  
  private static void deleteDir(File folder) {
    if(folder.isDirectory()) {
      File[] list = folder.listFiles();
      if(list != null) {
        for(int i = 0; i < list.length; i++) {
          File tmpF = list[i];
          if(tmpF.isDirectory()) {
            deleteDir(tmpF);
          }
          tmpF.delete();
        }
      }
    }
    folder.delete();
  }
  
  /**
   * @see {@link AbstractParser#read(File)}}
   */
  @Override
  public List<TreeNode> read(File file) throws Exception {
    DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document docToRead = dBuilder.parse(file);
    docToRead.getDocumentElement().normalize();
    return buildTree(docToRead);
  }
  
  /**
   * @see {@link AbstractParser#write(String, LinkedList, String)}}
   *
   */
  @Override
  public void write(String filepath, List<TreeNode> rootList, String templatePath) throws Exception {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    doc = docBuilder.newDocument();
    Element rootElement = doc.createElement("ap:Map");
    setRootElementAttributes(rootElement);
    doc.appendChild(rootElement);
    createContent(rootList, rootElement);
    if(templatePath != null) {
      loadTemplateStyle(templatePath, rootElement);
    }
    // write the content into xml file
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    // ATTENTION: this must be done like this, otherwise path will be treated as
    // URL. Why o.o?
    FileOutputStream fso = new FileOutputStream(new File(filepath));
    StreamResult result = new StreamResult(fso);
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    DOMSource source = new DOMSource(doc);
    transformer.transform(source, result);
    fso.close();
  }
  
  private Element addNewTopic(Element parentElement) {
    Element topic = createAndAppend("ap:Topic", parentElement);
    topic.setAttribute("Dirty", "0000000000000001");
    topic.setAttribute("OId", createBase64Binary());
    topic.setAttribute("Gen", "0000000000000000");
    return topic;
  }
  
  private String buildOptAndRatingPart(TreeNode treeNode) {
    String text = treeNode.getText();
    if(getModus() == Modus.MAXIMAL && treeNode.isOptional())
      text += " [?]";
    if(treeNode.getMaxRating() != 0) {
      if(treeNode.isSolution()) {
        text += " [" + treeNode.getMaxRating() + "]";
      } else {
        text += " [" + treeNode.getRealRating() + "/" + treeNode.getMaxRating() + "]";
      }
    }
    return text;
  }
  
  private LinkedList<TreeNode> buildTree(Document doc) {
    LinkedList<TreeNode> listOfRoots = new LinkedList<>();
    // root element from DOM - NOT the root of the tree
    Element rootElement = doc.getDocumentElement();
    NodeList nodeListDOMRoot = rootElement.getChildNodes();
    for(int i = 0; i < nodeListDOMRoot.getLength(); i++) {
      Node childDOMRoot = nodeListDOMRoot.item(i);
      if(isSearchedElement(childDOMRoot, "ap:OneTopic")) {
        LinkedList<Node> topics = searchChildren(childDOMRoot, "ap:Topic");
        for(Node aTopic: topics) {
          buildTreeRecursive(aTopic, null, listOfRoots);
        }
      }
    }
    return listOfRoots;
  }
  
  private void buildTreeRecursive(Node aTopic, TreeNode parent, LinkedList<TreeNode> listOfRoots) {
    Element elTopic = (Element) aTopic;
    TreeNode treeNode = new TreeNode(elTopic.getAttribute("OId"));
    treeNode.setParent(parent);
    if(parent == null) {
      listOfRoots.add(treeNode);
    } else {
      parent.addChild(treeNode);
    }
    NodeList nl = elTopic.getChildNodes();
    for(int i = 0; i < nl.getLength(); i++) {
      if(isSearchedElement(nl.item(i), "ap:Text")) {
        handleText(nl.item(i), treeNode);
        isTaskDescription(treeNode);
      }
      // synonyms set with help of Tags
      if(isSearchedElement(nl.item(i), "ap:TextLabels")) {
        handleTagSynonyms(nl.item(i), treeNode);
      }
      // synonyms set with help of notice
      if(isSearchedElement(nl.item(i), "ap:NotesGroup")) {
        handleNoteSynonyms(nl.item(i), treeNode);
      }
      if(isSearchedElement(nl.item(i), "ap:Offset")) {
        handleOffset(nl.item(i), treeNode);
      }
      if(isSearchedElement(nl.item(i), "ap:OneImage")) {
        handleImage(nl.item(i), treeNode);
      }
      if(isSearchedElement(nl.item(i), "ap:SubTopics")) {
        LinkedList<Node> topicsOfSubTopic = searchChildren(nl.item(i), "ap:Topic");
        for(Node topic: topicsOfSubTopic) {
          buildTreeRecursive(topic, treeNode, listOfRoots);
        }
      }
      if(isSearchedElement(nl.item(i), "ap:FloatingTopics")) {
        LinkedList<Node> topicsOfFloating = searchChildren(nl.item(i), "ap:Topic");
        for(Node topic: topicsOfFloating) {
          buildTreeRecursive(topic, null, listOfRoots);
        }
      }
    }
  }
  
  private Element createAndAppend(String toCreate, Element parent) {
    Element element = doc.createElement(toCreate);
    parent.appendChild(element);
    return element;
  }
  
  private void createAttributesIndexHyperLink(Element hl, String path, boolean absolute) {
    createSharedAttributesHyperLink(hl, path, absolute);
    hl.setAttribute("HyperlinkId", createBase64Binary());
    hl.setAttribute("Index", String.valueOf(index));
    index++;
  }
  
  private void createBackGround(Element topic, TreeNode treeNode) {
    if(treeNode.isMetaNode()) {
      return;
    }
    if(treeNode.getDifferenceResult() != null) {
      Element backGroundColor = createAndAppend("ap:Color", topic);
      backGroundColor.setAttribute("Dirty", "0000000000000001");
      if(treeNode.getDifferenceResult() == DifferenceResult.WRONG
          || treeNode.getDifferenceResult() == DifferenceResult.MISSING) {
        backGroundColor.setAttribute("FillColor", "ffffc0a0");
      } else if(treeNode.getDifferenceResult() == DifferenceResult.PARTIALLY_CORRECT) {
        backGroundColor.setAttribute("FillColor", "ffffcc00");
      } else {
        backGroundColor.setAttribute("FillColor", "ff00ff00");
      }
    }
  }
  
  private String createBase64Binary() {
    // (21 - digits of id) times "0" + id + "A=="
    String sID = String.valueOf(id);
    id++;
    int length = sID.length();
    StringBuilder sb = new StringBuilder(24);
    for(int i = 0; i < 21 - length; i++) {
      sb.append("0");
    }
    sb.append(sID);
    sb.append("A==");
    return sb.toString();
  }
  
  // must be treated special because of the floatingTopics and OneTopic stuff
  private void createContent(List<TreeNode> rootList, Element rootElement) {
    // id = 0 is ap:Map
    id = 1;
    Element oneTopic = createAndAppend("ap:OneTopic", rootElement);
    // the main root of the mindMap. Only one such root is allowed.
    TreeNode mainRoot = rootList.get(0);
    Element topic = addNewTopic(oneTopic);
    // this is a recursive step
    createSubTopics(topic, mainRoot);
    // floatingTopics
    if(rootList.size() > 1) {
      Element floatingTopics = doc.createElement("ap:FloatingTopics");
      topic.appendChild(floatingTopics);
      for(int i = 1; i < rootList.size(); i++) {
        recursiveTreeToContent(rootList.get(i), floatingTopics);
      }
    }
    createImage(topic, mainRoot);
    createTextPart(topic, mainRoot);
    createBackGround(topic, mainRoot);
    createNote(topic, mainRoot);
    createOffset(topic, mainRoot);
    createLinks(topic, mainRoot);
    // createTag(topic, mainRoot);
  }
  
  private void createImage(Element topic, TreeNode treeNode) {
    if(treeNode.getImage() != null) {
      Element oneImage = createAndAppend("ap:OneImage", topic);
      Element image = createAndAppend("ap:Image", oneImage);
      image.setAttribute("Dirty", "0000000000000001");
      image.setAttribute("OId", createBase64Binary());
      image.setAttribute("Gen", "0000000000000000");
      Element imageData = createAndAppend("ap:ImageData", image);
      imageData.setAttribute("ImageType", treeNode.getImage().getType());
      imageData.setAttribute("Dirty", "0000000000000001");
      Element corBase = createAndAppend("cor:Base64", imageData);
      corBase.setAttribute("xsi:nil", "false");
      corBase.setTextContent(treeNode.getImage().getBase64());
      Element imageSize = createAndAppend("ap:ImageSize", image);
      imageSize.setAttribute("Width", String.valueOf(treeNode.getImage().getWidth()));
      imageSize.setAttribute("Height", String.valueOf(treeNode.getImage().getHeight()));
      imageSize.setAttribute("Dirty", "0000000000000001");
    }
  }
  
  private void createLinks(Element topic, TreeNode treeNode) {
    if(isMultipleOutput()) {
      index = 0;
      Element hlSolutionOrInput = createAndAppend("ap:Hyperlink", topic);
      // solution must be linking to input and vice versa
      if(treeNode.isSolution()) {
        createSharedAttributesHyperLink(hlSolutionOrInput, getAbsolutePath(getAlteredInput()), true);
      } else {
        createSharedAttributesHyperLink(hlSolutionOrInput, getAbsolutePath(getAlteredSolution()), true);
      }
      Element hyperlinkGroup = createAndAppend("ap:HyperlinkGroup", topic);
      Element hlResult = createAndAppend("ap:IndexedHyperlink", hyperlinkGroup);
      Element hlMetaData = createAndAppend("ap:IndexedHyperlink", hyperlinkGroup);
      createAttributesIndexHyperLink(hlResult, getAbsolutePath(getResult()), true);
      createAttributesIndexHyperLink(hlMetaData, getAbsolutePath(getMetaData()), true);
    }
  }
  
  private void createNote(Element topic, TreeNode treeNode) {
    if(getModus() == Modus.MAXIMAL || treeNode.isMetaNode() && treeNode.getSynonyms().size() > 1
        || treeNode.getDifferenceResult() == DifferenceResult.PARTIALLY_CORRECT
        || treeNode.getDifferenceResult() == DifferenceResult.WRONG || treeNode.isMetaNode()) {
      // show all synonyms OR show correct solutions when its wrong/partially
      // correct OR
      // show notice if it is not a normal node
      Element notesGroup = createAndAppend("ap:NotesGroup", topic);
      Element notesXHtmlData = createAndAppend("ap:NotesXhtmlData", notesGroup);
      notesXHtmlData.setAttribute("Dirty", "0000000000000001");
      Element html = createAndAppend("html", notesXHtmlData);
      html.setAttribute("xmlns", "http://www.w3.org/1999/xhtml");
      // for line breaks
      Element p = createAndAppend("p", html);
      Element p2 = createAndAppend("p", html);
      Element p3 = createAndAppend("p", html);
      String plainText = "";
      if(!treeNode.isSolution() && treeNode.getDifferenceResult() == DifferenceResult.PARTIALLY_CORRECT) {
        p.setTextContent((treeNode.getDistancePercent() * 100) + "% correct");
        p2.setTextContent("Correct solutions (with regex):");
        for(String correct: treeNode.getCorrectSolutions()) {
          plainText += correct + ";";
        }
      }
      if(!treeNode.isSolution() && treeNode.getDifferenceResult() == DifferenceResult.WRONG) {
        for(String correct: treeNode.getCorrectSolutions()) {
          plainText += correct + ";";
        }
      }
      for(String synonym: treeNode.getSynonyms()) {
        if(synonym.equals(treeNode.getText())) {
          continue;
        }
        plainText += synonym + ";";
      }
      p3.setTextContent(plainText);
      if(plainText.length() > 200) {
        notesXHtmlData.setAttribute("PreviewPlainText", plainText.substring(0, 199));
      } else {
        notesXHtmlData.setAttribute("PreviewPlainText", plainText);
      }
      if(plainText.isEmpty()) {
        topic.removeChild(notesGroup);
      }
    }
    
  }
  
  // private void addBackGroundImage(Node styleNode) throws DOMException,
  // IOException {
  // Element style = (Element)styleNode;
  // Element bgid = doc.createElement("ap:BackgroundImageData");
  // style.insertBefore(bgid,
  // style.getElementsByTagName("ap:NotesDefaultFont").item(0));
  // bgid.setAttribute("ImageTileOption", "urn:mindjet:Center");
  // bgid.setAttribute("ImageType", "urn:mindjet:PngImage");
  // bgid.setAttribute("Transparency", "90");
  // bgid.setAttribute("Dirty", "0000000000000001");
  // Element base64 = createAndAppend("cor:Base64", bgid);
  // base64.setAttribute("xsi:nil", "false");
  // base64.setTextContent(encodeToString("platzhalter"));
  // }
  
  private void createOffset(Element topic, TreeNode treeNode) {
    if(treeNode.getxOffset() != 0.0 || treeNode.getyOffset() != 0.0) {
      Element offset = createAndAppend("ap:Offset", topic);
      offset.setAttribute("CX", String.valueOf(treeNode.getxOffset()));
      offset.setAttribute("CY", String.valueOf(treeNode.getyOffset()));
    }
  }
  
  private void createSharedAttributesHyperLink(Element hl, String path, boolean absolute) {
    hl.setAttribute("Dirty", "0000000000000001");
    hl.setAttribute("Url", "\"" + path + "\"");
    hl.setAttribute("Absolute", String.valueOf(absolute));
  }
  
  private void createSubTopics(Element topic, TreeNode treeNode) {
    if(treeNode.getChildren().size() != 0) {
      Element subTopics = createAndAppend("ap:SubTopics", topic);
      for(TreeNode child: treeNode.getChildren()) {
        recursiveTreeToContent(child, subTopics);
      }
    }
  }
  
  private void createTextPart(Element topic, TreeNode treeNode) {
    // text
    Element text = createAndAppend("ap:Text", topic);
    text.setAttribute("Dirty", "0000000000000001");
    String textVal = buildOptAndRatingPart(treeNode);
    text.setAttribute("PlainText", textVal);
    text.setAttribute("ReadOnly", "false");
    // font
    createAndAppend("ap:Font", text);
  }
  
  private String extractMetaData(TreeNode treeNode, String text) {
    String regexOptional = "\\[(\\?)\\]";
    String regexRating = "\\[([0-9]*(\\.[0-9]*)?)\\]";
    Pattern optionalPattern = Pattern.compile(regexOptional);
    Pattern ratingPattern = Pattern.compile(regexRating);
    Matcher optionalMatcher = optionalPattern.matcher(text);
    Matcher ratingMatcher = ratingPattern.matcher(text);
    if(optionalMatcher.find() || (treeNode.getParent() != null && treeNode.getParent().isOptional())) {
      treeNode.setOptional(true);
    }
    if(ratingMatcher.find()) {
      treeNode.setMaxRating(Double.valueOf(ratingMatcher.group(1)));
    }
    text = text.replaceAll(regexOptional, "");
    text = text.replaceAll(regexRating, "");
    text = text.trim();
    return text;
  }
  
  private void handleImage(Node nodeDOM, TreeNode treeNode) {
    LinkedList<Node> images = searchChildren(nodeDOM, "ap:Image");
    // this is only correct for one image per node BUT there is only one image
    // per node allowed in MindManager as far as is know.
    for(Node n: images) {
      Image image = new Image();
      LinkedList<Node> imageData = searchChildren(n, "ap:ImageData");
      for(Node imgNode: imageData) {
        Element imgData = (Element) imgNode;
        image.setType(imgData.getAttribute("ImageType"));
        LinkedList<Node> base = searchChildren(imgNode, "cor:Base64");
        for(Node baseNode: base) {
          Element baseEl = (Element) baseNode;
          image.setBase64(baseEl.getTextContent());
        }
      }
      LinkedList<Node> imageSize = searchChildren(n, "ap:ImageSize");
      for(Node imgNode: imageSize) {
        Element imgSizeEl = (Element) imgNode;
        image.setWidth(Double.valueOf(imgSizeEl.getAttribute("Width")));
        image.setHeight(Double.valueOf(imgSizeEl.getAttribute("Height")));
      }
      treeNode.setImage(image);
    }
  }
  
  private void handleNoteSynonyms(Node nodeDOM, TreeNode treeNode) {
    LinkedList<Node> notesXHtmlData = searchChildren(nodeDOM, "ap:NotesXhtmlData");
    String allSynonymsChain = "";
    // usually all these for loops run only once except the last
    for(Node note: notesXHtmlData) {
      LinkedList<Node> htmlContainer = searchChildren(note, "html");
      for(Node html: htmlContainer) {
        LinkedList<Node> pTags = searchChildren(html, "p");
        for(Node synonymsChain: pTags) {
          String synWithComment = synonymsChain.getTextContent();
          String[] synonymsArr = synWithComment.split("\\\\");
          if(synonymsArr.length > 0) {
            allSynonymsChain += synonymsArr[0];
          }
        }
      }
    }
    if(!allSynonymsChain.isEmpty()) {
      for(String synonym: allSynonymsChain.split(";")) {
        synonym = synonym.trim();
        treeNode.addSynonym(synonym);
      }
    }
  }
  
  private void handleOffset(Node nodeDOM, TreeNode treeNode) {
    Element elOffset = (Element) nodeDOM;
    String xOffset = elOffset.getAttribute("CX");
    String yOffset = elOffset.getAttribute("CY");
    treeNode.setxOffset(Double.valueOf(xOffset));
    treeNode.setyOffset(Double.valueOf(yOffset));
  }
  
  private void handleTagSynonyms(Node nodeDOM, TreeNode treeNode) {
    LinkedList<Node> textLabelWithSynonyms = searchChildren(nodeDOM, "ap:TextLabel");
    for(Node textLabel: textLabelWithSynonyms) {
      Element elTextLabel = (Element) textLabel;
      String synonymsChain = elTextLabel.getAttribute("TextLabelName");
      String[] synonymsArr = synonymsChain.split("\\\\");
      for(String synonym: synonymsArr[0].split(";")) {
        synonym = synonym.trim();
        treeNode.addSynonym(synonym);
      }
    }
  }
  
  private void handleText(Node nodeDOM, TreeNode treeNode) {
    Element elText = (Element) nodeDOM;
    String plainText = elText.getAttribute("PlainText");
    String text = extractMetaData(treeNode, plainText);
    treeNode.setText(text);
    // the synonyms of a node text MUST contain the text itself
    treeNode.addSynonym(text);
  }
  
  // private void createTag(Element topic, TreeNode treeNode) {
  // if(getModus() == Modus.MAXIMAL) {
  // if(treeNode.getSynonyms().size() > 1) {
  // Element textLabels = doc.createElement("ap:TextLabels");
  // topic.appendChild(textLabels);
  // textLabels.setAttribute("Dirty", "0000000000000001");
  // Element textLabel = doc.createElement("ap:TextLabel");
  // textLabels.appendChild(textLabel);
  // String synonymChain = "";
  // for(String synonym : treeNode.getSynonyms()) {
  // if(synonym.equals(treeNode.getText())) {
  // continue;
  // }
  // synonymChain += synonym + ";";
  // }
  // textLabel.setAttribute("TextLabelName", synonymChain);
  // }
  // }
  // }
  
  private boolean isSearchedElement(Node nodeDOM, String toSearch) {
    if(nodeDOM != null && nodeDOM.getNodeType() == Node.ELEMENT_NODE) {
      Element el = (Element) nodeDOM;
      if(el.getTagName().equals(toSearch)) {
        return true;
      }
    }
    return false;
  }
  
  private void isTaskDescription(TreeNode treeNode) {
    if(treeNode.getText().startsWith("Arbeitsauftrag")) {
      treeNode.setMetaNode(true);
    }
  }
  
  private void loadTemplateStyle(String templatePath, Element rootElement)
      throws ParserConfigurationException, SAXException, IOException {
    // unzip design file
    UnZip unzip = new UnZip();
    String unzipedPath = unzip.unzip(templatePath);
    // read style from unzipped xml
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    Document docStyle = docBuilder.parse(new File(unzipedPath + "/Document.xml"));
    NodeList list = docStyle.getElementsByTagName("ap:StyleGroup");
    Element style = (Element) list.item(0);
    Node styleNode = doc.importNode(style, true);
    // doesn't work for now
    // addBackGroundImage(styleNode);
    // append to current file
    rootElement.appendChild(styleNode);
    // delete unziped file
    deleteDir(new File(unzipedPath));
  }
  
  private void recursiveTreeToContent(TreeNode treeNode, Element parentElement) {
    Element topic = addNewTopic(parentElement);
    // this is a recursive step
    createSubTopics(topic, treeNode);
    createImage(topic, treeNode);
    createTextPart(topic, treeNode);
    createBackGround(topic, treeNode);
    createNote(topic, treeNode);
    createOffset(topic, treeNode);
    // createTag(topic, treeNode);
  }
  
  private LinkedList<Node> searchChildren(Node node, String toSearch) {
    LinkedList<Node> list = new LinkedList<>();
    if(node.getNodeType() == Node.ELEMENT_NODE) {
      Element element = (Element) node;
      NodeList nl = element.getChildNodes();
      for(int i = 0; i < nl.getLength(); i++) {
        if(isSearchedElement(nl.item(i), toSearch)) {
          list.add(nl.item(i));
        }
      }
    }
    return list;
  }
  
  // private String encodeToString(String imagePath) throws IOException {
  // imagePath = "C:/Users/Magnus/Desktop/Test w img/test.jpg";
  // File imgFile = new File(imagePath);
  // BufferedImage image = ImageIO.read(imgFile);
  // WritableRaster raster = image.getRaster();
  // DataBufferByte data = (DataBufferByte) raster.getDataBuffer();
  // String encoded = Base64.getEncoder().encodeToString(data.getData());
  // return encoded;
  // }
  
  private void setRootElementAttributes(Element rootElement) {
    rootElement.setAttribute("Dirty", "0000000000000001");
    rootElement.setAttribute("OId", "000000000000000000000A==");
    rootElement.setAttribute("Gen", "0000000000000000");
    rootElement.setAttribute("xmlns:ap", "http://schemas.mindjet.com/MindManager/Application/2003");
    rootElement.setAttribute("xmlns:cor", "http://schemas.mindjet.com/MindManager/Core/2003");
    rootElement.setAttribute("xmlns:pri", "http://schemas.mindjet.com/MindManager/Primitive/2003");
    rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
    rootElement.setAttribute("xsi:schemaLocation", "http://schemas.mindjet.com/MindManager/Application/2003"
        + " http://schemas.mindjet.com/MindManager/Application/2003 "
        + "http://schemas.mindjet.com/MindManager/Core/2003 " + "http://schemas.mindjet.com/MindManager/Core/2003 "
        + "http://schemas.mindjet.com/MindManager/Delta/2003 " + "http://schemas.mindjet.com/MindManager/Delta/2003 "
        + "http://schemas.mindjet.com/MindManager/Primitive/2003 "
        + "http://schemas.mindjet.com/MindManager/Primitive/2003");
  }
}
