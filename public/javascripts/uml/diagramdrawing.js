const STD_CLASS_SIZE = 140;

const WIDTH_PERCENTAGE = 0.45;
const HEIGHT_PERCENTAGE = 0.7;

const COLOR_WHITE = '#ffffff';

const MAX_ENTRIES_PER_CLASS = 3;

var idList = []; // Linkverbindungen

var graph;
var paper;

var sel = "POINTER";

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
  paper.on('cell:pointerclick', cellOnLeftClick);
  paper.on('cell:contextmenu', cellOnRightClick);
  paper.on('blank:pointerdown', blankOnPointerDown);
  
  // Draw all classes, empty if diagramdrawing - help
  for(var clazz of defaultClasses) { // NOSONAR
    addClass(clazz);
  }
});

function blankOnPointerDown(evt, x, y) {
  switch(sel) {
  case "INTERFACE":
  case "ABSTRACTCLASS":
  case "CLASS":
    newClass(x, y);
    break;
  default:
    // Do nothing
    break;
  }
}

function updateIdList() {
  var idSpan = $("#idList");
  
  if(idList.length == 0) {
    idSpan.html("--");
  } else {
    idSpan.html(idList.map(getClassNameFromCellId).join(", "));
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
    var newId = cellView.model.id;
    
    if(idList.length == 0 || idList[0] != newId) {
      idList.push(cellView.model.id);
    }
    
    if(idList.length == 2) {
      link(idList[0], idList[1]);
      idList = [];
    }
    break;
    
  case "INTERFACE":
  case "ABSTRACTCLASS":
  case "CLASS":
    // Do nothing
    break;
    
  default:
    console.error("TODO: " + sel);
    break;
  }
  
  updateIdList();
}

function cellOnRightClick(cellView, evt, x, y) {
  var cellInGraph = graph.getCell(cellView.model.id);
  if(canDelete && confirm("Wollen Sie die Klasse / das Interface " + cellInGraph.attributes.name + " wirklich löschen?")) {
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
  unMarkButtons();
  
  var classButton = document.getElementById("classButton")
  classButton.className = "btn btn-primary";
  classButton.dataset.conntype = button.dataset.conntype;
  
  sel = button.dataset.conntype;
  document.getElementById("selType").innerHTML = sel;
  
  document.getElementById("classType").textContent = button.textContent;
}

function selectAssocType(button) {
  unMarkButtons();
  
  var assocButton = document.getElementById("assocButton");
  assocButton.className = "btn btn-primary";
  assocButton.dataset.conntype = button.dataset.conntype;
  
  sel = button.dataset.conntype;
  document.getElementById("selType").innerHTML = sel;
  
  document.getElementById("assocType").textContent = button.textContent;
}

function selectButton(button) {
  unMarkButtons();
  button.className = "btn btn-primary";
  
  sel = button.dataset.conntype;
  document.getElementById("selType").innerHTML = sel;
}

function unMarkButtons() {
  for (var otherButton of document.getElementById("buttonsDiv").getElementsByTagName("button")) {
    otherButton.className = "btn btn-default";
  }
}

function exportDiagram() {
  var text = extractParametersAsJson();
  var a = document.getElementById("a");
  var file = new Blob([text], {type: 'text/json'});
  a.href = URL.createObjectURL(file);
  a.download = 'export.json';
}

function importDiagram() {
  console.error("NOT IMPLEMENTED YET!");
}

function askMulitplicity(source, dest) {
  var multiplicity = prompt("Bitte geben Sie die Multiplizität von " + source + " nach " + dest + " auf der Seite " + source + " an.");
  return (multiplicity && multiplicity == "1") ? multiplicity : "*";
}

function extractParametersAsJson() {
  var learnerSolution = {
    classes: graph.getCells()
    .filter(function(cell) {
      return cell.attributes.name != undefined;
    })
    .map(function(cell) {
      console.log(cell)
      return {
        name: cell.attributes.name,
        classType: cell.attributes.type,
        methods: cell.attributes.methods,
        attributes: cell.attributes.attributes
      };
    }),
    
    associations: graph.getLinks()
    .filter(function(conn) {
      return conn.attributes.type != "uml.Implementation";
    }) 
    .map(function(conn) {
      return {
        assocType: getTypeName(conn.attributes.type),
        ends: [
          {
            endName: getClassNameFromCellId(conn.attributes.source.id),
            multiplicity: getMultiplicity(conn.attributes.labels[0])
          },
          {
            endName: getClassNameFromCellId(conn.attributes.target.id),
            multiplicity: getMultiplicity(conn.attributes.labels[1])
          }
        ]
      };
    }),
    
    implementations: graph.getLinks()
    .filter(function(conn) {
      return conn.attributes.type == "uml.Implementation";
    }) 
    .map(function(conn) {
      return {
        subClass: getClassNameFromCellId(conn.attributes.source.id),
        superClass: getClassNameFromCellId(conn.attributes.target.id),
      };
    })
  };
  
  return JSON.stringify(learnerSolution, null, 2);
}

function getClassNameFromCellId(id) {
  return graph.getCell(id).attributes.name;
}

function getMultiplicity(label) {
  return label.attrs.text.text == "1" ? "SINGLE" : "UNBOUND";
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
  $('#learnerSolution').val(extractParametersAsJson());
}

function link(sourceId, targetId) {
  var source_name = getClassNameFromCellId(sourceId);
  var destin_name = getClassNameFromCellId(targetId);
  
  
  if(sel != "IMPLEMENTATION") {
    var source_mult = askMulitplicity(source_name, destin_name);
    var destin_mult = askMulitplicity(destin_name, source_name);
  }
  
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
  
  switch(clazz.classType) {
  case "INTERFACE":
    graph.addCell(new joint.shapes.uml.Interface(content));
    break;
  case "ABSTRACTCLASS":
    graph.addCell(new joint.shapes.uml.Abstract(content));
    break;
  case "CLASS":
    graph.addCell(new joint.shapes.uml.Class(content));
    break;
  default:
    return;
  }
}

function newClass(posX, posY) {
  var className = prompt("Wie soll die (abstrakte) Klasse / das Interface heißen?");
  
  if(!className || className.length == 0) {
    return;
  }
  
  addClass({
    // Replace all " " with "_"
    name: className.replace(/ /g, "_"),
    classType: sel,
    attributes: [], methods: [],
    position: {x: posX, y: posY},
  });
}

// FIXME: Begin Drag-And-Drop-Functionality

function allowDrop(ev) {
  ev.preventDefault();
}

function drag(ev) {
  var className = ev.target.innerHTML;
  if (ev.target.getAttribute('data-baseform') != null) {
    className = ev.target.getAttribute('data-baseform');
  }
  ev.dataTransfer.setData("text", className);
}

function drop(ev) {
  ev.preventDefault();
  var data = ev.dataTransfer.getData("text");
  addClass({
    // Replace all " " with "_"
    name: data,
    classType: "CLASS",
    attributes: [], methods: [],
    // TODO: Position!
    position: {x: 250, y: 250},
  });
}

// End D&D-Functionality
