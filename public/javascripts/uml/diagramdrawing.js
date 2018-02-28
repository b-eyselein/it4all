const STD_CLASS_SIZE = 140;
const HEIGHT_PERCENTAGE = 0.7;
const COLOR_WHITE = '#ffffff';

let chosenCellView = null;

const graph = new joint.dia.Graph();
let paper;

let sel = 'POINTER';

let classEditModal;

const UmlTypes = ['String', 'Int', 'Double', 'Char', 'Boolean'];

const CLASS_TYPES = ['CLASS', 'ABSTRACT', 'INTERFACE'];
const ASSOC_TYPES = ['ASSOCIATION', 'AGGREGATION', 'COMPOSITION', 'IMPLEMENTATION'];

function addClass(clazz) {
    let content = {
        position: clazz.position,
        size: {width: STD_CLASS_SIZE, height: STD_CLASS_SIZE},
        name: clazz.name.replace(/ /g, '_'),
        attributes: clazz.attributes,
        methods: clazz.methods,
        attrs: {
            '.uml-class-name-rect': {fill: COLOR_WHITE},
            '.uml-class-attrs-rect, .uml-class-methods-rect': {fill: COLOR_WHITE},
            '.uml-class-attrs-text': {ref: '.uml-class-attrs-rect', 'ref-y': 0.5, 'y-alignment': 'middle'},
            '.uml-class-methods-text': {ref: '.uml-class-methods-rect', 'ref-y': 0.5, 'y-alignment': 'middle'}
        }
    };

    switch (clazz.classType) {
        case 'INTERFACE':
            graph.addCell(new joint.shapes.uml.Interface(content));
            return;
        case 'ABSTRACT':
            graph.addCell(new joint.shapes.uml.Abstract(content));
            return;
        case 'CLASS':
            graph.addCell(new joint.shapes.uml.Class(content));
            return;
        default:
            return;
    }
}

function newClass(posX, posY) {
    let className = prompt('Wie soll die (abstrakte) Klasse / das Interface heißen?');

    if (!className || className.length === 0) {
        return;
    }

    addClass({
        name: className,
        classType: sel,
        attributes: [], methods: [],
        position: {x: posX, y: posY}
    });
}

function blankOnPointerDown(evt, x, y) {
    if (CLASS_TYPES.includes(sel)) {
        newClass(x, y);
    }
}

function discardEdits() {
    classEditModal.find('.modal-body').html('');
    classEditModal.modal('hide');
}

function addMember(button, isAttr) {
    $(button).before(memberInputs('', '', isAttr));
}

function deleteMember(button) {
    $(button).parent().parent().remove();
}

// noinspection JSUnusedGlobalSymbols
function updateClass(elementId) {
    console.log('Updating class...');
    let element = graph.getCell(elementId);

    // FIXME: update class (--> element...)
    console.warn('TODO: change type ' + element.attributes.type);
    element.attr('.uml-class-name-text/text', $('#editClassName').val());

    let attrs = $('#editAttrsDiv').find('.input-group').map((index, attrGroup) => {
        return {
            name: $(attrGroup).find('input').val(),
            type: $(attrGroup).find('select').val()
        };
    }).get();
    element.attributes.attributes = attrs;
    element.attr('.uml-class-attrs-text/text', attrs.map((a) => a.name + ': ' + a.type).join('\n'));

    // FIXME: method parameters?
    let mets = $('#editMethodsDiv').find('.input-group').map((index, metGroup) => {
        return {
            name: $(metGroup).find('input').val(),
            type: $(metGroup).find('select').val()
        }
    }).get();

    element.attributes.methods = mets;
    element.attr('.uml-class-methods-text/text', mets.map(m => m.name + ': ' + m.type).join('\n'));

    discardEdits();
}

function memberInputs(memberName, memberType = '', isAttr = true) {
    return `
<div class="form-group">
  <div class="input-group">
    <input class="form-control" placeholder="${isAttr ? 'Attribut' : 'Methode'} für diese Klasse" value="${memberName}" required>
    <span class="input-group-addon">:</span>
    <select class="form-control">
      ${UmlTypes.map(umlType => `<option ${umlType === memberType ? 'selected' : ''}>${umlType}</option>`).join("")}
    </select>
    <span class="input-group-addon"></span>
    <button class="form-control btn-warning" title="L&ouml;schen" onclick="deleteMember(this)"><span class="glyphicon glyphicon-remove"></span></button>
  </div>
</div>`.trim();
}

function htmlForClassEdit(cellView) {
    let classType = cellView.model.attributes.type;
    let className = cellView.model.attributes.name;
    let currentAttrs = cellView.model.attributes.attributes;
    let currentMethods = cellView.model.attributes.methods;

    return `
<form action="javascript:updateClass('${cellView.model.id}')">
   <div class="panel panel-default">
     <div class="panel-heading">
       <div class="form-group">
         <select class="form-control text-center">
           <option value="${'CLASS'}" ${classType === 'uml.Class' ? 'selected' : ''}>&lt;&lt;class&gt;&gt;</option>
           <option value="${'ABSTRACT'}" ${classType === 'uml.Abstract' ? 'selected' : ''}>&lt;&lt;abstract&gt;&gt;</option>
           <option value="${'INTERFACE'}" ${classType === 'uml.Interface' ? 'selected' : ''}>&lt;&lt;interface&gt;&gt;</option>
         </select>
       </div>
       <div class="form-group">
         <input value="${className}" id="editClassName" class="form-control text-center" placeholder="Name der Klasse">
       </div>
     </div>
     <div class="panel-body">
       <div id="editAttrsDiv">
         ${currentAttrs.map(attr => memberInputs(attr.name, attr.type)).join('\n')}
         <button class="btn btn-primary" id="editAttrsPlusBtn" onclick="addMember(this, true)">
           <span class="glyphicon glyphicon-plus"></span> Attribut hinzufügen
         </button>
       </div>
       <hr>
       <div id="editMethodsDiv">
         ${currentMethods.map(met => memberInputs(met.name, met.type)).join('\n')}
         <button class="btn btn-primary" id="editMethodsPlusBtn" onclick="addMember(this, false)">
           <span class="glyphicon glyphicon-plus"></span> Methode hinzufügen
         </button>
       </div>
     </div>
   </div>
   <hr>
   <div class="btn-group btn-group-justified">
     <div class="btn-group">
       <button type="reset" class="btn btn-default" onclick="discardEdits()">Änderungen verwerfen</button>
     </div>
     <div class="btn-group">
       <button class="btn btn-primary">Klasse aktualisieren</button>
     </div>
   </div>
 </form>`.trim();
}

function cellOnLeftClick(cellView, evt, x, y) {

    console.log(evt);

    if (sel === 'POINTER') {
        if (getsHelp) return;

        // TODO: Changing class type, name, attributes or methods!?!
        let bodyEditModalJQ = classEditModal.find('.modal-body');

        bodyEditModalJQ.html(htmlForClassEdit(cellView));

        classEditModal.modal('show');

    } else if (ASSOC_TYPES.includes(sel)) {
        // FIXME: do not select arrows or other things, only classes!
        cellView.highlight();

        if (chosenCellView === null) {
            chosenCellView = cellView;

        } else if (chosenCellView.model.id === cellView.model.id) {
            cellView.unhighlight();
            chosenCellView = null;

        } else {
            link(chosenCellView.model.id, cellView.model.id);

            chosenCellView.unhighlight();
            cellView.unhighlight();

            chosenCellView = null;
        }
    } else {
        console.error('TODO: ' + sel);
    }

}

function cellOnRightClick(cellView, evt) {
    let cellInGraph = graph.getCell(cellView.model.id);
    if (getsHelp) {
        alert('Sie können keine Klassen löschen!');
    } else if (cellInGraph !== null && confirm('Wollen Sie die Klasse / das Interface ' + cellInGraph.attributes.name + ' wirklich löschen?')) {
        cellInGraph.remove();
    }
}

function backwards() {
    console.error('TODO: not yet implemented!');
}

function forwards() {
    console.error('TODO: not yet implemented!');
}

function unMarkButtons() {
    $('#buttonsDiv').find('button').removeClass('btn-primary').addClass('btn-default');
}

function selectClassType(button) {
    unMarkButtons();

    let classButton = document.getElementById('classButton');
    classButton.className = 'btn btn-primary';
    classButton.dataset.conntype = button.dataset.conntype;

    sel = button.dataset.conntype.trim();

    $('#classType').text(button.textContent);
}

function selectAssocType(button) {
    unMarkButtons();

    let assocButton = document.getElementById('assocButton');
    assocButton.className = 'btn btn-primary';
    assocButton.dataset.conntype = button.dataset.conntype;

    sel = button.dataset.conntype.trim();
    $('#assocType').text(button.textContent);
}

function selectButton(button) {
    unMarkButtons();

    button.className = 'btn btn-primary';

    sel = button.dataset.conntype.trim();
}

function exportDiagram() {
    let text = JSON.stringify(extractParametersAsJson());
    let a = document.getElementById('a');
    let file = new Blob([text], {type: 'text/json'});
    a.href = URL.createObjectURL(file);
    a.download = 'export.json';
}

function importDiagram() {
    console.error('TODO: not yet implemented!');
}

function askMulitplicity(source, dest) {
    let multiplicity = prompt('Bitte geben Sie die Multiplizität von ' + source + ' nach ' + dest + ' auf der Seite ' + source + ' an.');
    return (multiplicity && multiplicity === '1') ? multiplicity : '*';
}

function getClassNameFromCellId(id) {
    return graph.getCell(id).attributes.name;
}

function getMultiplicity(label) {
    return label.attrs.text.text === '1' ? 'SINGLE' : 'UNBOUND';
}

/**
 * @return {{classes: object[], associations: object[], implementations: object[]}}
 */
function extractParametersAsJson() {
    return {
        classes: graph.getCells().filter(cell => cell.attributes.name !== undefined)
            .map(function (cell) {
                return {
                    name: cell.attributes.name,
                    classType: cell.attributes.type,
                    methods: cell.attributes.methods,
                    attributes: cell.attributes.attributes
                };
            }),

        associations: graph.getLinks().filter(conn => conn.attributes.type !== 'uml.Implementation')
            .map(function (conn) {
                return {
                    assocType: getTypeName(conn.attributes.type),
                    assocName: '',

                    firstEnd: getClassNameFromCellId(conn.attributes.source.id),
                    firstMult: getMultiplicity(conn.attributes.labels[0]),

                    secondEnd: getClassNameFromCellId(conn.attributes.target.id),
                    secondMult: getMultiplicity(conn.attributes.labels[1])
                };
            }),

        implementations: graph.getLinks().filter(conn => conn.attributes.type === 'uml.Implementation')
            .map(function (conn) {
                return {
                    subClass: getClassNameFromCellId(conn.attributes.source.id),
                    superClass: getClassNameFromCellId(conn.attributes.target.id)
                };
            })
    };
}

function getTypeName(type) {
    switch (type) {
        case 'link':
            return 'ASSOCIATION';
        case 'uml.Aggregation':
            return 'AGGREGATION';
        case 'uml.Composition':
            return 'COMPOSITION';
        case 'uml.Implementation':
            return 'IMPLEMENTATION';
        default:
            return 'ERROR!';
    }
}

function prepareFormForSubmitting() {
    let solution = extractParametersAsJson();

    $('#learnerSolution').val(JSON.stringify(solution));
}

function link(sourceId, targetId) {
    let sourceName = getClassNameFromCellId(sourceId);
    let targetName = getClassNameFromCellId(targetId);

    let sourceMultiplicity = '';
    let targetMultiplicity = '';

    if (sel !== 'IMPLEMENTATION') {
        sourceMultiplicity = askMulitplicity(sourceName, targetName);
        targetMultiplicity = askMulitplicity(targetName, sourceName);
    }

    let members = {
        source: {id: sourceId},
        target: {id: targetId},
        labels: [{
            position: 25,
            attrs: {text: {text: sourceMultiplicity}}
        }, {
            position: -25,
            attrs: {text: {text: targetMultiplicity}}
        }]
    };

    let cellToAdd;
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

$(document).ready(function () {
    let paperJQ = $('#paper');

    // Init Graph and Paper
    paper = new joint.dia.Paper({
        el: paperJQ,
        width: paperJQ.parent().width(),
        height: HEIGHT_PERCENTAGE * window.screen.availHeight,
        gridSize: 1,
        model: graph
    });

    // Set callback for click on cells and blank paper
    paper.on('cell:pointerclick', cellOnLeftClick);
    paper.on('cell:contextmenu', cellOnRightClick);
    paper.on('blank:pointerdown', blankOnPointerDown);

    // Draw all classes, empty if diagramdrawing - help
    for (let clazz of defaultClasses) {
        addClass(clazz);
    }

    classEditModal = $('#classEditModal');
    classEditModal.modal({show: false});
});

// TODO: Begin Drag-And-Drop-Functionality

function allowDrop(ev) {
    ev.preventDefault();
}

function drag(ev) {
    let className = ev.target.innerHTML;
    if (ev.target.getAttribute('data-baseform') !== null) {
        className = ev.target.getAttribute('data-baseform');
    }
    ev.dataTransfer.setData('text', className);
}

function drop(ev) {
    ev.preventDefault();
    addClass({
        name: ev.dataTransfer.getData('text'),
        classType: 'CLASS',
        attributes: [], methods: [],
        position: {x: ev.x, y: ev.y}
    });
}

// TODO: End Drag-And-Drop-Functionality
