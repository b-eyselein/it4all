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
    el: document.getElementById('paper'),
    width: WIDTH_PERCENTAGE * window.screen.availWidth,
    height:  HEIGHT_PERCENTAGE * window.screen.availHeight,
    gridSize: 1,
    model: graph
  });
  
  // Set callback for click
  paper.on('cell:pointerclick', cellOnPointerClick);
  
  // Draw all classes
  for(var clazz of defaultClasses) { // NOSONAR
    addClass(clazz);
  }
});
  
function cellOnPointerClick(cellView, evt, x, y) { // NOSONAR
  if(sel == "POINTER") {
    console.log(evt.toElement.getAttribute("class"));
    switch(evt.toElement.getAttribute("class")) {
    case "v-line":
      // Auf Klassennamen geklickt
    case "uml-class-name-rect":
      var newName = prompt("Wie wollen Sie die Klasse benennen?");
      if(newName)
        alert("TODO: Nenne Klasse in \"" + newName + "\" um.");
      break;
    case "uml-class-attrs-rect":
      console.log("Adding attribute...");
      break;
    case "uml-class-methods-rect":
      console.log("Adding method...");
      break;
    default:
      console.log("Doing nothing...");
      break;
    }
    return;
  }
  var selected = cellView.model.id;
  
  
  idList.push(selected);
  updateIdList();
  
  if(idList.length == 2)
    link();
}

function updateIdList() {
  var idSpan = document.getElementById("idList");
  var classNames = [];
  for(var id of idList) {
    classNames.push(graph.getCell(id).attr('.uml-class-name-text/text'));
  }
  idSpan.innerHTML = classNames.join(", ");
}

function selectButton(elem) {
  for(var buttonGroup of document.getElementById("buttonsDiv").children) {
    buttonGroup.children[0].className = "btn btn-default";
  }
  elem.className = "btn btn-primary";
  sel = elem.dataset.conntype;
}

function askMulitplicity(source, dest) {
  var multiplicity = window.prompt("Bitte geben Sie die Multiplizität von " + source + " nach " + dest + " auf der Seite " + source + " an.");
  
  if(multiplicity)
    return multiplicity;
  
  return "";
}

function prepareFormForSubmitting() {
  var toSend = extractParameters();
  document.getElementById("learnerSolution").value = toSend;
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

function link() {
  var sourceId = idList[0];
  var targetId = idList[1];
  
  idList = [];
  updateIdList();
  
  var source_name = graph.getCell(sourceId).attr('.uml-class-name-text/text');
  var destin_name = graph.getCell(targetId).attr('.uml-class-name-text/text');
  
  var source_mult = askMulitplicity(source_name, destin_name);
  var destin_mult = askMulitplicity(destin_name, source_name);
  
  var members = {
    source: {
      id: sourceId
    },
    target: {
      id: targetId
    },
    labels: [{
      position: 25,
      attrs: {
        text: {
          text: source_mult
        }
      }
    }, {
      position: -25,
      attrs: {
        text: {
          text: destin_mult
        }
      }
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
  case 'IMPLEMENTATION':
    cellToAdd = new joint.shapes.uml.Implementation(members);
    break;
  case 'ASSOCIATION':
    cellToAdd = new joint.dia.Link(members);
    break;
  default:
    return;
  }
  
  graph.addCell(cellToAdd);
}

function addClass(clazz) {
  graph.addCell(new joint.shapes.uml.Class({
    name: clazz.name,
    attributes: clazz.attributes,
    method: clazz.methods,
    position: {
      x: clazz.posX,
      y: clazz.posY
    },
    size: {
      width: STD_CLASS_SIZE,
      height: STD_CLASS_SIZE
    },
    attrs: {
      '.uml-class-name-rect': {
        fill: COLOR_WHITE,
      },
      '.uml-class-attrs-rect, .uml-class-methods-rect': {
        fill: COLOR_WHITE,
      },
      '.uml-class-attrs-text': {
        ref: '.uml-class-attrs-rect',
        'ref-y': 0.5,
        'y-alignment': 'bottom'
      },
      '.uml-class-methods-text': {
        ref: '.uml-class-methods-rect',
        'ref-y': 0.5,
        'y-alignment': 'bottom'
      }
    }
  }));
}
