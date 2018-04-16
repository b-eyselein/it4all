const STD_CLASS_SIZE = 150;
const PADDING = 50;

const COLOR_WHITE = '#ffffff';

let chosenCellView = null;

const graph = new joint.dia.Graph();

let sel = 'POINTER';

let classEditModal, cardinalityEditModal;

const UmlTypes = ['String', 'int', 'double', 'char', 'boolean', 'void'];

const CLASS_TYPES = ['CLASS', 'ABSTRACT', 'INTERFACE'];
const ASSOC_TYPES = ['ASSOCIATION', 'AGGREGATION', 'COMPOSITION', 'IMPLEMENTATION'];

function addUmlClass(clazz) {
    let content = {
        position: clazz.position,
        size: {width: STD_CLASS_SIZE, height: STD_CLASS_SIZE},

        name: clazz.name.replace(/ /g, '_'),

        attributes: clazz.attributes.map((a) => a.name + ": " + a.type).join('\n'),
        attributesObject: clazz.attributes,

        methods: clazz.methods.map((m) => m.name + ": " + m.type).join('\n'),
        methodsObject: clazz.methods,

        attrs: {
            '.uml-class-name-rect': {fill: COLOR_WHITE},
            '.uml-class-attrs-rect, .uml-class-methods-rect': {fill: COLOR_WHITE},
            '.uml-class-attrs-text': {ref: '.uml-class-attrs-rect', 'ref-y': 0.5, 'y-alignment': 'middle'},
            '.uml-class-methods-text': {ref: '.uml-class-methods-rect', 'ref-y': 0.5, 'y-alignment': 'middle'}
        }
    };

    let cellToAdd;

    switch (clazz.classType) {
        case 'INTERFACE':
            cellToAdd = new joint.shapes.uml.Interface(content);
            break;
        case 'ABSTRACT':
            cellToAdd = new joint.shapes.uml.Abstract(content);
            break;
        case 'CLASS':
            cellToAdd = new joint.shapes.uml.Class(content);
            break;
        default:
            break;
    }

    graph.addCell(cellToAdd);
}

function newClass(posX, posY) {
    let className = prompt('Wie soll die (abstrakte) Klasse / das Interface heißen?');

    if (!className || className.length === 0) {
        return;
    }

    addUmlClass({
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

function askCardinality(linkId, source, sourceMult, target, targetMult) {
    let bodyEditModalJQ = cardinalityEditModal.find('.modal-body');

    bodyEditModalJQ.html(htmlForCardinalityEdit(linkId, source, sourceMult, target, targetMult));

    cardinalityEditModal.modal('show');
}

/**
 * FIXME: eventually render popup!
 *
 * @param linkView
 */
function linkOnClick(linkView) {
    let sourceId = linkView.model.attributes.source, targetId = linkView.model.attributes.target;
    let source = graph.getCell(sourceId), target = graph.getCell(targetId);

    let sourceMult = linkView.model.attributes.labels[0].attrs.text.text;
    let targetMult = linkView.model.attributes.labels[1].attrs.text.text;

    askCardinality(linkView.model.id, source.attributes.name, sourceMult, target.attributes.name, targetMult);
}

function cellOnLeftClick(cellView, evt) {

    switch (cellView.model.attributes.type) {
        case 'uml.Implementation':
            evt.stopPropagation();
            return;
        case 'uml.Association':
        case 'uml.Aggregation':
        case 'uml.Composition':
            evt.stopPropagation();
            return linkOnClick(cellView);
        case 'uml.Class':
        case 'uml.Abstract':
        case 'uml.Interfact':
            break;
        default:
            console.warn(cellView.model.attributes.type);
    }

    if (sel === 'POINTER') {
        if (getsHelp) return;

        // TODO: Changing class type, name, attributes or methods!?!
        let bodyEditModalJQ = classEditModal.find('.modal-body');

        bodyEditModalJQ.html(htmlForClassEdit(cellView));

        classEditModal.modal('show');

    } else if ('IMPLEMENTATION' === sel) {
        // FIXME: do not select arrows or other things, only classes!
        cellView.highlight();

        if (chosenCellView === null) {
            chosenCellView = cellView;

        } else if (chosenCellView.model.id === cellView.model.id) {
            cellView.unhighlight();
            chosenCellView = null;

        } else {
            addImplementation(chosenCellView.model.id, cellView.model.id);

            chosenCellView.unhighlight();
            cellView.unhighlight();

            chosenCellView = null;
        }
    } else if (ASSOC_TYPES.includes(sel)) {
        // FIXME: do not select arrows or other things, only classes!
        cellView.highlight();

        if (chosenCellView === null) {
            chosenCellView = cellView;

        } else if (chosenCellView.model.id === cellView.model.id) {
            cellView.unhighlight();
            chosenCellView = null;

        } else {
            let source = cellView.model.attributes.name, target = chosenCellView.model.attributes.name;
            let sourceMult = '*', targetMult = '*';

            let newId = addAssociation(chosenCellView.model.id, cellView.model.id, sel, targetMult, sourceMult);

            askCardinality(newId, source, sourceMult, target, targetMult);


            chosenCellView.unhighlight();
            cellView.unhighlight();

            chosenCellView = null;
        }
    } else if (sel === 'CLASS') {
        alert('Sie können keine Klassen innerhalb von Klassen erstellen!');
    } else {
        console.error('TODO: ' + sel);
    }
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

function addImplementation(sourceId, targetId) {
    graph.addCell(new joint.shapes.uml.Implementation({source: {id: sourceId}, target: {id: targetId}}));
}

/**
 * @param {string | number} sourceId
 * @param {string | number} targetId
 * @param {string} linkType
 * @param {string} sourceMult
 * @param {string} targetMult
 *
 * @return {string | number}
 */
function addAssociation(sourceId, targetId, linkType, sourceMult, targetMult) {
    let members = {
        source: {id: sourceId},
        target: {id: targetId},
        labels: [{
            position: 25,
            attrs: {text: {text: sourceMult}}
        }, {
            position: -25,
            attrs: {text: {text: targetMult}}
        }]
    };

    let cellToAdd;
    switch (linkType) {
        case 'COMPOSITION':
            cellToAdd = new joint.shapes.uml.Composition(members);
            break;
        case 'AGGREGATION':
            cellToAdd = new joint.shapes.uml.Aggregation(members);
            break;
        case 'ASSOCIATION':
            cellToAdd = new joint.shapes.uml.Association(members);
            break;
        default:
            return;
    }

    graph.addCell(cellToAdd);
    return cellToAdd.id;
}


/**
 * @param {object} implementationToLoad
 * @param {string} implementationToLoad.subClass
 * @param {string} implementationToLoad.superClass
 */
function loadImplementation(implementationToLoad) {
    let subClass = implementationToLoad.subClass, superClass = implementationToLoad.superClass;
    let subClassId = null, superClassId = null;

    for (let cell of graph.getCells()) {
        let cellClassName = cell.attributes.name;
        if (cellClassName === subClass) {
            subClassId = cell.id;
        } else if (cellClassName === superClass) {
            superClassId = cell.id;
        }
    }

    if (subClassId !== null && superClassId !== null) {
        addImplementation(subClassId, superClassId);
    }
}

/**
 * @param {object} associationToLoad
 * @param {string} associationToLoad.assocType
 * @param {string} associationToLoad.assocName
 * @param {string} associationToLoad.firstEnd
 * @param {string} associationToLoad.firstMult
 * @param {string} associationToLoad.secondEnd
 * @param {string} associationToLoad.secondMult
 */
function loadAssociation(associationToLoad) {
    let firstEnd = associationToLoad.firstEnd, secondEnd = associationToLoad.secondEnd;
    let firstEndId = null, secondEndId = null;

    for (let cell of graph.getCells()) {
        let cellClassName = cell.attributes.name;
        if (cellClassName === firstEnd) {
            firstEndId = cell.id;
        } else if (cellClassName === secondEnd) {
            secondEndId = cell.id;
        }
    }

    let firstMult = associationToLoad.firstMult === 'SINGLE' ? '1' : '*';
    let secondMult = associationToLoad.secondMult === 'SINGLE' ? '1' : '*';

    if (firstEndId !== null && secondEndId !== null) {
        addAssociation(firstEndId, secondEndId, associationToLoad.assocType, firstMult, secondMult);
    }
}

/**
 * @param {object} solution
 * @param {object[]} solution.classes
 * @param {object[]} solution.associations
 * @param {object[]} solution.implementations
 * @param paperWidth
 * @param paperHeight
 */
function loadSolution(solution, paperWidth, paperHeight) {

    let sqrt = Math.ceil(Math.sqrt(solution.classes.length));
    for (let i = 0; i < solution.classes.length; i++) {
        let classToLoad = solution.classes[i];

        if (!classToLoad.hasOwnProperty('position')) {
            classToLoad.position = {
                x: 200 * (i % sqrt) + PADDING,
                y: 200 * Math.floor(i / sqrt) + PADDING
            };
        }

        addUmlClass(classToLoad);
    }

    for (let assoc of solution.associations) {
        loadAssociation(assoc);
    }

    for (let impl of solution.implementations) {
        loadImplementation(impl);
    }
}

$(document).ready(function () {
    let paperJQ = $('#paper');

    let paperWidth = paperJQ.width(), paperHeight = 700;

    // Init Graph and Paper
    let paper = new joint.dia.Paper({
        el: paperJQ,
        width: paperWidth,
        height: 700, // paperJQ.height(),
        gridSize: 10,
        model: graph
    });

    // Set callback for click on cells and blank paper
    paper.on('cell:pointerclick', cellOnLeftClick);

    paper.on('cell:contextmenu', function (elementView, evt) {
        evt.stopPropagation();

        if (getsHelp && elementView.model.attributes.type === 'uml.Class') {
            alert('Sie können keine Klassen löschen!');
        } else if (confirm('Wollen Sie dieses Objekt wirklich löschen?')) {
            elementView.model.remove();
        }
    });

    paper.on('blank:pointerdown', blankOnPointerDown);

    // noinspection JSUnresolvedVariable
    loadSolution(defaultSol, paperWidth, paperHeight);

    classEditModal = $('#classEditModal');
    classEditModal.modal({show: false});

    cardinalityEditModal = $('#cardinalityEditModal');
    cardinalityEditModal.modal({show: false});
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
    addUmlClass({
        name: ev.dataTransfer.getData('text'),
        classType: 'CLASS',
        attributes: [], methods: [],
        position: {x: ev.x, y: ev.y}
    });
}

// TODO: End Drag-And-Drop-Functionality
