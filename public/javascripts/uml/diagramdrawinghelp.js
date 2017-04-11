const
stdClassSize = 140;
const
colorWhite = '#ffffff';

var divWidth = document.getElementById("sizepaper").parentNode.offsetWidth;
var divTextWidth = document.getElementById("text").parentNode.offsetWidth;
var divHeight = document.getElementById("sizepaper").parentNode.offsetHeight;

var idList = []; // Linkverbindungen


var graph = new joint.dia.Graph();

var sel;

var paper = new joint.dia.Paper({
  el: document.getElementById('paper'),
  width: 0.7 * window.screen.availWidth,
  height: 0.7 * window.screen.availHeight,
  gridSize: 1,
  model: graph
});
paper.on('cell:pointerclick', cellOnPointerClick);

function cellOnPointerClick(cellView, evt, x, y) {
  if(idList.length < 2) {
    idList.push(cellView.model.id);
    return;
  }
  if(idList[0] == idList[1]) {
    console.error("You selected the same cell twice!");
    idList = [];
    return;
  }
  link();
}

function selectButton(elem) {
  for(var i = 1; i < 6; i++) {
    document.getElementById(i).value = "off";
  }
  document.getElementById(elem.id).value = "on";
  sel = elem.id;
}

function askMulitplicity(source, dest) {
  var multiplicity = window.prompt("Bitte geben Sie die Multiplizität von " + source + " nach " + dest + " an.");
  
  if(multiplicity)
    return multiplicity;
  
  return "";
}

var learnerSolution = {
    classes: [],
    otherMethods: [/* remains empty in diagDrawingHelp */],
    otherAttributes: [/* remains empty in diagDrawingHelp */],
    connections: [/* remains empty in classSelectin */]
  };

  function prepareFormForSubmitting() {
    document.getElementById("learnerSolution").value = extractParameters();
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
        type: conn.attributes.type,
        source: graph.getCell(conn.attributes.source.id).attr('.uml-class-name-text/text'),
        target: graph.getCell(conn.attributes.target.id).attr('.uml-class-name-text/text'),
        mulStart: conn.attributes.labels[0].attrs.text.text,
        mulTarget: conn.attributes.labels[1].attrs.text.text
      }
      learnerSolution.connections.push(connection);
    }
    
    return JSON.stringify(learnerSolution, null, 2);
  }


function link() {
  if(document.getElementById(sel).value == "off") {
    return;
  }
  
  var sourceId = idList[0];
  var targetId = idList[1];
  idList = [];
  
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
  switch (sel){
  case '1':
    cellToAdd = new joint.shapes.uml.Composition(members);
    break;
  case '2':
    cellToAdd = new joint.shapes.uml.Aggregation(members);
    break;
  case '3':
    cellToAdd = new joint.shapes.uml.Implementation(members);
    break;
  case '4':
    cellToAdd = new joint.shapes.uml.Generalization(members);
    break;
  case '5':
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
      width: stdClassSize,
      height: stdClassSize
    },
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
