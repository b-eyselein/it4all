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

function cellOnPointerClick(cellView, evt, x, y) {
  var cellInGraph = graph.getCell(cellView.model.id);
  
  if(document.getElementById(5).value == "on") {
    // Klassen loeschen
    cellInGraph.remove();
    
    idList = [];
    
  } else if (document.getElementById(6).value == "on") {
    // Methoden hinzufügen
    
    if(cellInGraph.attributes.methods.length >= MAX_ENTRIES_PER_CLASS) {
      // Klasse hat bereits maximale Anzahl (3) an Methoden
      alert("Entfernen Sie zuerst einen Eintrag, bevor Sie weitere hinzufügen!");
      return;
    }
    
    var method = window.prompt("Bitte geben sie die Methode an, welche hinzugefügt werden soll");
    
    if (method != null) {
      cellInGraph.attributes.methods.push(method);
      cellInGraph.attr('.uml-class-methods-text/text', cellInGraph.attributes.methods.join("\n"));
    }
    
    idList = [];
    
  } else if(document.getElementById(7).value == "on") {
    // Attribute hinzuf&uuml;gen
    
    if(cellInGraph.attributes.attributes.length >= MAX_ENTRIES_PER_CLASS){
      // Klasse hat bereits maximale Anzahl (3) an Methoden
      alert("Entfernen Sie zuerst einen Eintrag, bevor Sie weitere hinzufügen!");
      return;
    }
    
    var attribute = window.prompt("Bitte geben sie das Attribut an, welche hinzugfügt werden soll");
    
    if (attribute != null) {
      cellInGraph.attributes.attributes.push(attribute);
      cellInGraph.attr('.uml-class-attrs-text/text', cellInGraph.attributes.attributes.join("\n"));
    }
    idList = [];
    
  } else if (document.getElementById(8).value == "on") {
    // Methoden entfernen
    var methodToRemove = window.prompt("Bitte geben Sie die STELLE der Methode an, welche entfernt werden soll");
    
    methodToRemove -= 1;
    cellInGraph.attributes.methods.splice(methodToRemove, 1);
    
    cellInGraph.attr('.uml-class-methods-text/text', cellInGraph.attributes.methods.join("\n"));
    idList = [];
    
  } else if (document.getElementById(9).value == "on") {
    // Attribute entfernen
    var attributeToRemove = window.prompt("Bitte geben Sie die STELLE des Attributes an, welches entfernt werden soll");
    
    attributeToRemove -= 1;
    cellInGraph.attributes.attributes.splice(attributeToRemove, 1);
    
    cellInGraph.attr('.uml-class-attrs-text/text', cellInGraph.attributes.attributes.join("\n"));
    idList = [];
    
  } else {
    if (idList.length < 2) {
      idList.push(cellView.model.id);
    }
    if (idList.length == 2) {
      if (idList[0] != idList[1]) {
        link();
      } else {
        console.log("gleich");
        idList = [];
      }
    }
  }
  // FIXME: aktualisiere div...
  if(idList && idList.size > 0) {
    var selectedNodes = graph.getCell(idList).attributes.name;
    document.getElementById("idList").innerHTML = "<p>sel = " + sel + "</p><p>idList = [" + selectedNodes + "]</p>";
  }
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
  for (let i = 1; i < 11; i++) {
    // Deactivate all buttons
    var button = document.getElementById(i);
    button.value = "off";
    button.className = "btn btn-default";
  }
  elem.value = "on";
  elem.className = "btn btn-primary";
  sel = elem.id;
}

function askMulitplicity(source, dest) {
  var multiplicity = window.prompt("Bitte geben Sie die Multiplizität von " + source + " nach " + dest + " an.");
  
  if(multiplicity)
    return multiplicity;
  
  return "";
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

function createClass() {
  var className = window.prompt("Wie soll die neue Klasse heißen?");
  
  if(className) {
    addClass(className);
  } else {
    alert("Der Name einer Klasse darf nicht leer sein!");
  }
}

function selectDeleteClass() {
  selectButton(document.getElementById(5));
}

function addMeth() {
  selectButton(document.getElementById(6));
}

function addAttr() {
  selectButton(document.getElementById(7));
}

function delMeth() {
  selectButton(document.getElementById(8));
}

function delAttr() {
  selectButton(document.getElementById(9));
}

function addClass(className) {
	if(className.includes(" ")) {
		window.alert("Bitte einzelne Wörter zum Erstellen einer Klasse verwenden");
		return;
	}
	
	var newClass = new joint.shapes.uml.Class({
		position: {
			x: Math.random() * 250,
			y: Math.random() * 250
		},
		size: {
			width: STD_CLASS_SIZE,
			height: STD_CLASS_SIZE
		},
		name: className,
		attributes: ["", ""],
		methods: ["", ""],
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
     		'y-alignment': 'middle'
     	},
     	'.uml-class-methods-text': {
     		ref: '.uml-class-methods-rect',
     		'ref-y': 0.5,
     		'y-alignment': 'middle'
     	}
    }
	});
  newClass.attributes.attributes = [];
  newClass.attributes.methods = [];
  graph.addCell(newClass);
}

//Begin Drag-And-Drop-Functionality

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
