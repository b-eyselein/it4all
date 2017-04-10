const
stdClassSize = 140;
const
colorWhite = '#ffffff';

var divWidth = document.getElementById("sizepaper").clientWidth;
var divTextWidth = document.getElementById("text").parentNode.offsetWidth;
var divHeight = document.getElementById("sizepaper").parentNode.offsetHeight;

var idList = []; // Linkverbindungen

var graph = new joint.dia.Graph();
var sel;

// Festlegen der maximalen Anzahl von Methoden bzw. Attributen
var max_entries_class = 3;

var paper = new joint.dia.Paper({
  el: document.getElementById('paper'),
  width: 0.625 * window.screen.availWidth,
  height: 0.7 * window.screen.availHeight,
  gridSize: 1,
  model: graph
});

paper.on('cell:pointerclick', cellOnPointerClick());

function cellOnPointerClick()(cellView, evt, x, y) {
  var cellInGraph = graph.getCell(cellView.model.id);
  
  if(document.getElementById(5).value == "on") {
    // Klassen loeschen
    cellInGraph.remove();
    
    idList = [];
    
  } else if (document.getElementById(6).value == "on") {
    // Methoden hinzufügen
    
    if(cellInGraph.attributes.methods.length >= max_entries_class) {
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
    
    if(cellInGraph.attributes.attributes.length >= max_entries_class){
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
    document.getElementById("idListDiv").innerHTML = "<p>sel = " + sel + "</p><p>idList = [" + selectedNodes + "]</p>";
  }
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
  if (document.getElementById(sel).value == "off") {
    return;
  }
  var source_name = graph.getCell(idList[0]).attr('.uml-class-name-text/text');
  var destin_name = graph.getCell(idList[1]).attr('.uml-class-name-text/text');
  
  var source_mult = askMulitplicity(source_name, destin_name);
  var destin_mult = askMulitplicity(destin_name, source_name);
  
  switch (sel) {
  case '1':
      graph.addCell(new joint.shapes.uml.Composition({
        source: {
          id: idList[0]
        },
        target: {
          id: idList[1]
        },
        labels: [{
            position: 25,
            attrs: {
              text: {
                text: source_mult
              }
            }
          },
          {
            position: -25,
            attrs: {
              text: {
                text: destin_mult
              }
            }
          }
        ]
      }));
      idList = [];
      break;
      
    case '2':
      graph.addCell(new joint.shapes.uml.Aggregation({
        source: {
          id: idList[0]
        },
        target: {
          id: idList[1]
        },
        labels: [{
            position: 25,
            attrs: {
              text: {
                text: source_mult
              }
            }
          },
          {
            position: -25,
            attrs: {
              text: {
                text: destin_mult
              }
            }
          }
        ]
      }));
      idList = [];
      break;
      
    case '3':
      graph.addCell(new joint.shapes.uml.Implementation({
        source: {
          id: idList[0]
        },
        target: {
          id: idList[1]
        },
        labels: [{
            position: 25,
            attrs: {
              text: {
                text: source_mult
              }
            }
          },
          {
            position: -25,
            attrs: {
              text: {
                text: destin_mult
              }
            }
          }
        ]
      }));
      idList = [];
      break;
      
    case '4':
      graph.addCell(new joint.shapes.uml.Generalization({
        source: {
          id: idList[0]
        },
        target: {
          id: idList[1]
        },
        labels: [{
            position: 25,
            attrs: {
              text: {
                text: source_mult
              }
            }
          },
          {
            position: -25,
            attrs: {
              text: {
                text: destin_mult
              }
            }
          }
        ]
      }));
      idList = [];
      break;

    case '10':
      var obj = {
          source: {
            id: idList[0]
          },
          target: {
            id: idList[1]
          },
          labels: [{
              position: 25,
              attrs: {
                text: {
                  text: source_mult
                }
              }
            },
            {
              position: -25,
              attrs: {
                text: {
                  text: destin_mult
                }
              }
            }
          ]
        };
      graph.addCell(new joint.dia.Link(obj));
      idList = [];
      break;
    default:
      idList = [];
      break;
  }
} // End function link()

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
			width: stdClassSize,
			height: stdClassSize
		},
		name: className,
		attributes: ["", ""],
		methods: ["", ""],
		attrs: {
			'.uml-class-name-rect': {
				fill: colorWhite,
			},
     	'.uml-class-attrs-rect, .uml-class-methods-rect': {
     		fill: colorWhite,
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

// Namen aller Klassen
function getClasses() {
  var classes = [];
  for (i = 0; i < graph.getCells().length; i++) {
    if (graph.getCells()[i]._previousAttributes.name != undefined) {
      classes.push(graph.getCells()[i]._previousAttributes.name);
    }
  }
  return classes;
  // console.log("getClasses: "+ classes);
}
// Alle ids aller Zellen
function getIds() {
  var ids = [];
  for (i = 0; i < graph.getCells().length; i++) {
    ids.push(graph.getCells()[i].id);
  }
  return ids;
}

function getAttributes(id) {
  var cell = graph.getCell(id);
  if (cell.attributes.name != undefined) {
    var text = cell.attributes.name;
    for (i = 0; i < cell.attributes.attributes.length; i++) {
      if (graph.getCells()[i]._previousAttributes.name != undefined) {
        text += "_" + cell.attributes.attributes[i];
      }
    }
    // console.log("getAttributes: "+text);
  }
  return text;
}

function getMethodes(id) {
  var text = graph.getCell(id).attributes.name;
  for (i = 0; i < graph.getCell(id).attributes.methods.length; i++) {
    if (graph.getCells()[i]._previousAttributes.name != undefined) {
      text += "_" + graph.getCell(id).attributes.methods[i];
    }
  }
  return text;
}