var divWidth = document.getElementById("sizepaper").parentNode.offsetWidth;
var divTextWidth = document.getElementById("text").parentNode.offsetWidth;
var divHeight = document.getElementById("sizepaper").parentNode.offsetHeight;
var idList = new Array(); // Linkverbindungen
var graph = new joint.dia.Graph();
var uml = joint.shapes.uml;
var erd = joint.shapes.erd;

var paper = new joint.dia.Paper({
  el: $('#paper'),
  width: 0.7 * window.screen.availWidth,
  height: 0.7 * window.screen.availHeight,
  gridSize: 1,
  model: graph
});

paper.on('cell:pointerclick',
  function(cellView, evt, x, y) {
    if (idList.length < 2) {
      idList.push(cellView.model.id);
    }
    if (idList.length == 2) {
      // console.log("0: "+idList[0]+" 1:
      // "+idList[1]);
      if (idList[0] != idList[1]) {
        link();
      } else {
        // console.log("gleich");
        idList = [];
      }
    }
  }
);

var sel;

function selectButton(elem) {
  for (var i = 1; i < 6; i++) {
    document.getElementById(i).value = "off";
  }
  document.getElementById(elem.id).value = "on";
  sel = elem.id;
}

function link() {
  if (document.getElementById(sel).value == "on") {
    var source_name = graph.getCell(idList[0]).attr('.uml-class-name-text/text');
    var destin_name = graph.getCell(idList[1]).attr('.uml-class-name-text/text');
    var source_mult = window.prompt("Bitte geben Sie die Multiplizität von " + source_name + " nach " + destin_name +
      " an.");
    if(source_mult==null){
    	source_mult="";
    }
    var destin_mult = window.prompt("Bitte geben Sie die Multiplizität von " + destin_name + " nach " + source_name +
      " an.");
    if(destin_mult==null){
    	destin_mult="";
    }
    switch (sel) {
      case '1':
        graph.addCell(new uml.Composition({
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
        graph.addCell(new uml.Aggregation({
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
        graph.addCell(new uml.Implementation({
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
        graph.addCell(new uml.Generalization({
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

      case '5':
        graph.addCell(new joint.dia.Link({
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
      default:
        idList = [];
        break;
    }
  }
}

function addClass(name, posx, posy, a1, a2, m1, m2) {
  flugzeug: var flug = new uml.Class({
    position: {
      x: posx,
      y: posy
    },
    size: {
      width: 140,
      height: 140
    },
    name: name,
    attributes: [a1, a2],
    methods: [m1, m2],
    attrs: {
      '.uml-class-name-rect': {
        fill: '#ffffff',
      },
      '.uml-class-attrs-rect, .uml-class-methods-rect': {
        fill: '#ffffff',
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
  });
  graph.addCell(flug);
}


console.log(  "width: "+ 0.7 * window.screen.availWidth);
console.log("height: "+0.7 * window.screen.availHeight);

var x = 485;
var y = 10;
addClass("Person", x -300, y, "id:String", "name:String", "", "");
addClass("Doktor", x -300, y +180, "", "", "verschreiben(Patient):void", "");
addClass("Rezept", x, y+360, "id:String", "", "", "");
addClass("Patient", x, y + 180, "", "", "entlassen(Station):void", "aufnehmen(Station):void");
addClass("Krankenschwester", x+300, y, "", "", "", "");
addClass("Station", x +300, y+180, "nummer:int", "", "", "");
addClass("Medikament", x+300, y+360, "id:String", "name:String", "", "");
addClass("Krankenhaus", x, y, "", "", "", "");
/*
var x = 485;
var y = 10;
addClass("Telekonverter", x, y, "Verlängerungsfaktor:Zahl", "", "", "");
addClass("Kameragehäuse", x, y + 180, "", "", "", "");
addClass("Profigehäuse", x - 300, y + 180, "", "", "", "");
addClass("Profiblitz", x - 300, y + 360, "", "", "", "");
addClass("Amateurgehäuse", x + 300, y + 180, "", "", "", "");
addClass("Amateurblitz", x + 300, y + 360, "", "", "", "");
addClass("Objektiv", x, y + 360, "Gewindedurchmesser:Zahl", "", "", "");
addClass("Sonnenblende", x - 300, y + 540, "Gewindedurchmesser:Zahl", "", "", "");
addClass("Festbrennweitenobjektiv", x + 300, y + 540, "Brennweite:Zahl", "", "", "");
addClass("Zoomobjektiv", x, y + 540, "Brennweite_maximal:Zahl", "Brennweite_minimal:Zahl", "", "");
*/

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
  var text = "";
  if (graph.getCell(id).attributes.name != undefined) {
    var text = graph.getCell(id).attributes.name;
    for (i = 0; i < graph.getCell(id).attributes.attributes.length; i++) {
      if (graph.getCells()[i]._previousAttributes.name != undefined) {
        text += "_" + graph.getCell(id).attributes.attributes[i];
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