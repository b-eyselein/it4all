const STD_CLASS_HEIGHT = 160;
const STD_CLASS_WIDTH = 200;

const PADDING = 40;

let chosenCellView = null;

const graph = new joint.dia.Graph();

let paper;

let sel = 'POINTER';

const SIMPLE_CLASS_PREFIX = 'Klasse_';

interface MyPosition {
    x: number,
    y: number
}

interface MemberToLoad {
    visibility: string,
    name: string,
    type: string,
    isStatic: boolean,
    isAbstract: boolean
}

interface AttributeToLoad extends MemberToLoad {
    isDerived: boolean
}

interface MethodToLoad extends MemberToLoad {
    parameters: string
}

interface ClassToLoad {
    classType: string,
    name: string,
    attributes: AttributeToLoad[],
    methods: MethodToLoad[],
    position?: MyPosition
}

function addUmlClass(clazz: ClassToLoad) {

    let content = {
        position: clazz.position,

        size: {width: STD_CLASS_WIDTH, height: STD_CLASS_HEIGHT},

        className: clazz.name.replace(/ /g, '_'),

        classType: clazz.classType,

        attributes: <UmlClassAttribute[]> clazz.attributes.map(UmlClassAttribute.fromAttributeToLoad),

        methods: <UmlClassMethod[]> clazz.methods.map(UmlClassMethod.fromMethodToLoad),
    };

    graph.addCell(new joint.shapes.customUml.CustomClass(content));
}

function newClass(posX, posY) {
    let allClassNames: string[] = graph.getCells().filter((c) => c.get('type') === 'customUml.Class').map((cell) => cell.get('name'));
    let simpleNameInts: number[] = allClassNames.filter((cn) => cn.startsWith(SIMPLE_CLASS_PREFIX)).map((cn) => parseInt(cn.substring(SIMPLE_CLASS_PREFIX.length)));

    addUmlClass({
        name: SIMPLE_CLASS_PREFIX + (Math.max(...simpleNameInts, 0) + 1),
        classType: sel,
        attributes: [], methods: [],
        position: {x: posX, y: posY}
    });
}

function blankOnPointerDown(evt, x, y) {
    switch (sel) {
        case 'CLASS':
        case 'ABSTRACT':
        case 'INTERFACE':
            newClass(x, y);
            break;
        default :
            console.info(sel);
    }
}

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
        attributes: [],
        methods: [],
        position: {x: ev.x, y: ev.y}
    });
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
            return editLink(cellView);
        case 'uml.Class':
        case 'uml.Abstract':
        case 'uml.Interfact':
            break;
        case 'customUml.CustomClass':
            // FIXME: use? cellView.model.onLeftClick();
            break;
        default:
            console.warn(cellView.model.attributes.type);
    }

    if (sel === 'POINTER') {
        if (!getsHelp) {
            editClass(cellView);
        }
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
    } else if (sel === 'ASSOCIATION' || sel === 'AGGREGATION' || sel === 'COMPOSITION') {
        // FIXME: do not select arrows or other things, only classes!
        cellView.highlight();

        if (chosenCellView === null) {
            chosenCellView = cellView;

        } else if (chosenCellView.model.id === cellView.model.id) {
            cellView.unhighlight();
            chosenCellView = null;

        } else {
            let newLink = addAssociation(chosenCellView.model.id, cellView.model.id, sel, '*', '*');

            console.warn(newLink);

            editLink(newLink);

            // htmlForCardinalityEdit(newId, '', source, sourceMult, target, targetMult);

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

function addAssociation(sourceId: string, targetId: string, linkType: string, sourceMult: string, targetMult: string): joint.dia.Link {
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
    return paper.findViewByModel(cellToAdd.id);
}


/**
 * @param {object} implementationToLoad
 * @param {string} implementationToLoad.subClass
 * @param {string} implementationToLoad.superClass
 */
function loadImplementation(implementationToLoad) {
    let subClass = implementationToLoad.subClass, superClass = implementationToLoad.superClass;
    let subClassId = null, superClassId = null;

    for (let cell of graph.getCells().filter((cell) => cell.get('type') === 'customUml.CustomClass')) {
        let cellClassName = cell.get('className');
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

    for (let cell of graph.getCells().filter((cell) => cell.get('type') === 'customUml.CustomClass')) {
        let cellClassName = cell.get('className');
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

function loadClasses(classesToLoad: ClassToLoad[]) {
    let sqrt = Math.ceil(Math.sqrt(classesToLoad.length));


    const size = STD_CLASS_WIDTH + PADDING;

    for (let i = 0; i < classesToLoad.length; i++) {
        const classToLoad: ClassToLoad = classesToLoad[i];


        if (!classToLoad.hasOwnProperty('position')) {
            classToLoad.position = {
                x: size * (i % sqrt) + PADDING,
                y: size * Math.floor(i / sqrt) + PADDING
            };
        }

        addUmlClass(classToLoad);
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

    loadClasses(solution.classes);

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
    paper = new joint.dia.Paper({
        el: paperJQ,
        width: paperWidth,
        height: 700, // paperJQ.height(),
        gridSize: 20,
        drawGrid: 'dot',
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


    cardinalityEditModal = $('#cardinalityEditModal');
    cardinalityEditModal.modal({show: false});
});
