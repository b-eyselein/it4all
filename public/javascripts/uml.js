function allowDrop(ev) {
  ev.preventDefault();
}

function drag(ev) {
  document.getElementById(5).value = "off";
  if(ev.target.getAttribute('data-baseform') != null) {
    ev.dataTransfer.setData("text", ev.target.getAttribute('data-baseform'));
  } else {
    ev.dataTransfer.setData("text", ev.target.innerHTML);
  }
}

function drop(ev) {
  ev.preventDefault();
  var data = ev.dataTransfer.getData("text");
  addClass(data);
}

function selectButton(elem) {
  for(i = 1; i < 10; i++) {
    document.getElementById(i).value = "off";
  }
  document.getElementById(elem.id).value = "on";
  sel = elem.id;
}

 function link() {
  if(document.getElementById(sel).value = "on") {
    switch (sel){
    case '1':
      graph.addCell(new uml.Composition({
        source: {
          id: idList[0]
        },
        target: {
          id: idList[1]
        }
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
        }
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
        }
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
        }
      }));
      idList = [];
      break;
    default:
      idList = [];
      break;
    }
  }
}

function addClass(data) {
  var flug = new uml.Class({
    position: {
      x: Math.random() * 250,
      y: Math.random() * 250
    },
    
    size: {
      width: 280,
      height: 120
    },
    name: data,
    // TODO: attributes and methods empty!
    attributes: [ /* 'dob: date' */],
    methods: [ /* "setDateOfBirth(dob: Date): Void", "getAgeAsDays(): Numeric" */],
    attrs: {
      // TODO: set fill to white
      '.uml-class-name-rect': {
        fill: '#ffffff',
      /* stroke: '#fff', 'stroke-width': 1 */
      },
      '.uml-class-attrs-rect, .uml-class-methods-rect': {
        fill: '#ffffff',
      /* stroke: '#fff', 'stroke-width': 1 */
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
  graph.addCell(flug);
}

function loeschen() {
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
