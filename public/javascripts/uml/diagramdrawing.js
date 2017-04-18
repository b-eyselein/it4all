const STD_CLASS_SIZE = 140;

const WIDTH_PERCENTAGE = 0.45;
const HEIGHT_PERCENTAGE = 0.7;

const COLOR_WHITE = '#ffffff';

const MAX_ENTRIES_PER_CLASS = 3;

var idList = []; // Linkverbindungen

var graph;
var paper;

var sel = "POINTER";

var learnerSolution = {
  classes: [],
  otherMethods: [/* remains empty in diagDrawingHelp */],
  otherAttributes: [/* remains empty in diagDrawingHelp */],
  connections: [/* remains empty in classSelectin */]
};

$(document).ready(function() {
  // Init Graph and Paper
  graph  = new joint.dia.Graph(); // NOSONAR
  paper = new joint.dia.Paper({
    el: $('#paper'),
    width: WIDTH_PERCENTAGE * window.screen.availWidth,
    height:  HEIGHT_PERCENTAGE * window.screen.availHeight,
    gridSize: 1,
    model: graph
  });
  
  // Set callback for click on cells and blank paper
  paper.on('cell:pointerclick', cellOnPointerClick);
  paper.on('blank:pointerdown', blankOnPointerDown);
  
  // Draw all classes, empty if diagramdrawing - help
  for(var clazz of defaultClasses) { // NOSONAR
    addClass(clazz);
  }
});

function blankOnPointerDown(evt, x, y) {
  switch(sel) {
  case "INTERFACE":
  case "ABSTRACT":
  case "CLASS":
    newClass(x, y);
    break;
  default:
    // Do nothing
    break;
  }
}

function updateIdList() {
  var idSpan = document.getElementById("idList");
  
  if(idList.length == 0) {
    idSpan.innerHTML = "--";
    return;
  }
  
  var classNames = idList.map(function(id) {
    return graph.getCell(id).attr('.uml-class-name-text/text');
  });
  
  idSpan.innerHTML = classNames.join(", ");
}

function cellOnPointerClick(cellView, evt, x, y) {
  if(evt.which == 1) {
    // Left Click
    cellOnLeftClick(cellView, evt, x, y);
  } else if(evt.which == 2) {
    // Mouse Wheel
    cellOnMouseWheel();
  } else if(evt.which == 3) {
    // Right Click
    cellOnRightClick(graph.getCell(cellView.model.id));
  } else {
    console.error(evt);
  }
}

function cellOnLeftClick(cellView, evt, x, y) {
  switch(sel) {
  case "POINTER":
    // TODO: Changing class type, name, attributes or methods!?!
    console.log(evt.toElement);
    break;
    
  case "ASSOCIATION":
  case "AGGREGATION":
  case "COMPOSITION":
  case "IMPLEMENTATION":
    idList.push(cellView.model.id);
    if(idList.length == 2) {
      link(idList[0], idList[1]);
      idList = [];
    }
    break;
    
  case "INTERFACE":
  case "ABSTRACT":
  case "CLASS":
    // Do nothing
    break;
    
  default:
    console.error("TODO: " + sel);
    break;
  }
  
  updateIdList();
}

function cellOnMouseWheel() {
  // Do nothing for now...
}

function cellOnRightClick(cellInGraph) {
  if(confirm("Wollen Sie die Klasse / das Interface " + cellInGraph.attr('.uml-class-attrs-text/text') + "wirklich löschen?")) {
    cellInGraph.remove();
  }
}

function backwards() {
  console.error("TODO!");
}

function forwards() {
  console.error("TODO!");
}

function selectClassType(button) {
  selectButton(button);
  
  var classButton = document.getElementById("classButton")
  classButton.className = "btn btn-primary";
  classButton.dataset.conntype = button.dataset.conntype;
  
  document.getElementById("classType").textContent = button.textContent;
}

function selectAssocType(button) {
  selectButton(button);
  
  var assocButton = document.getElementById("assocButton");
  assocButton.className = "btn btn-primary";
  assocButton.dataset.conntype = button.dataset.conntype;
  
  document.getElementById("assocType").textContent = button.textContent;
}

function selectButton(button) {
  markButtons(button);
  sel = button.dataset.conntype;
  document.getElementById("selType").innerHTML = sel;
}

function markButtons(button) {
  for (var otherButton of document.getElementById("buttonsDiv").getElementsByTagName("button")) {
    otherButton.className = "btn btn-default";
  }
  button.className = "btn btn-primary";
}

function exportDiagram() {
  console.error("NOT IMPLEMENTED YET!");
}

function importDiagram() {
  console.error("NOT IMPLEMENTED YET!");
}

function askMulitplicity(source, dest) {
  var multiplicity = prompt("Bitte geben Sie die Multiplizität von " + source + " nach " + dest + " an.");
  return multiplicity ? multiplicity : "";
}

function extractParameters() {
  for (var cell of graph.getCells()) {
    var clazz = {
      name: cell.attributes.name,
      methods: cell.attributes.methods,
      attributes: cell.attributes.attributes
    };
    learnerSolution.classes.push(clazz);
  }
  
  for (var conn of graph.getLinks()) {
    var connection = {
      type: getTypeName(conn.attributes.type),
      source: graph.getCell(conn.attributes.source.id).attr('.uml-class-name-text/text'),
      target: graph.getCell(conn.attributes.target.id).attr('.uml-class-name-text/text'),
      mulstart: conn.attributes.labels[0].attrs.text.text,
      multarget: conn.attributes.labels[1].attrs.text.text
    }
    learnerSolution.connections.push(connection);
  }
  
  return JSON.stringify(learnerSolution, null, 2);
}

function getTypeName(type) {
  switch(type) {
  case "link":
    return "ASSOCIATION";
  case "uml.Aggregation":
    return "AGGREGATION";
  case "uml.Composition":
    return "COMPOSITION";
  case "uml.Implementation":
    return "IMPLEMENTATION";
  default:
    return "ERROR!";
  }
}

function prepareFormForSubmitting() {
  var toSend = extractParameters();
  document.getElementById("learnerSolution").value = toSend;
}

function link(sourceId, targetId) {
  var source_name = graph.getCell(sourceId).attr('.uml-class-name-text/text');
  var destin_name = graph.getCell(targetId).attr('.uml-class-name-text/text');
  
  var source_mult = askMulitplicity(source_name, destin_name);
  var destin_mult = askMulitplicity(destin_name, source_name);

  var members = {
    source: { id: sourceId },
    target: { id: targetId },
    labels: [{
      position: 25,
      attrs: { text: { text: source_mult } }
    }, {
      position: -25,
      attrs: { text: { text: destin_mult } }
    }]
  };
  
  var cellToAdd;
  switch (sel) {
  case 'COMPOSITION':
    cellToAdd = new joint.shapes.uml.Composition(members);
    break;
  case 'AGGREGATION':
    cellToAdd = new joint.shapes.uml.Aggregation(members);
    break;
  case 'ASSOCIATION':
    cellToAdd = new joint.dia.Link(members);
    break;
    
  case 'IMPLEMENTATION':
    cellToAdd = new joint.shapes.uml.Implementation(members);
    break;
    
  default:
    return;
  }
  
  graph.addCell(cellToAdd);
}

function addClass(clazz) {
  var content = {
    position: clazz.position,
    size: { width: STD_CLASS_SIZE, height: STD_CLASS_SIZE },
    name: clazz.name,
    attributes: clazz.attributes,
    methods: clazz.methods,
    attrs: {
      '.uml-class-name-rect': {
        fill: COLOR_WHITE,
      },
      '.uml-class-attrs-rect, .uml-class-methods-rect': {
        fill: COLOR_WHITE,
      },
      '.uml-class-attrs-text, .uml-class-methods-text': {
        ref: '.uml-class-attrs-rect',
        'ref-y': 0.5,
        'y-alignment': 'middle'
      }
    }
  };
  
  var classToAdd;
  switch(clazz.type) {
  case "INTERFACE":
    classToAdd = new joint.shapes.uml.Interface(content);
    break;
  case "ABSTRACT":
    classToAdd = new joint.shapes.uml.Abstract(content);
    break;
  case "CLASS":
    classToAdd = new joint.shapes.uml.Class(content);
    break;
  default:
    return;
  }
  graph.addCell(classToAdd);
}

function newClass(posX, posY) {
  var className = prompt("Wie soll die (abstrakte) Klasse / das Interface heißen?");
  
  if(!className) {
    return;
  }
  
  addClass({
    // Replace all " " with "_"
    name: className.replace(/ /g, "_"),
    attributes: [], methods: [],
    position: {x: posX, y: posY},
    type: sel
  });
}

// Begin Drag-And-Drop-Functionality

function allowDrop(ev) {
  ev.preventDefault();
}

function drag(ev) {
  document.getElementById(5).value = "off";
  
  var className = ev.target.innerHTML;
  if (ev.target.getAttribute('data-baseform') != null) {
    className = ev.target.getAttribute('data-baseform');
  }
  ev.dataTransfer.setData("text", className);
}

function drop(ev) {
  ev.preventDefault();
  var data = ev.dataTransfer.getData("text");
  addClass(data);
}

// End D&D-Functionality
