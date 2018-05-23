import * as $ from 'jquery';
import * as joint from 'jointjs';

import {DEF_GRID, GRID_SIZE, PAPER_HEIGHT} from "../umlConsts";
import {
    UmlAssociation,
    UmlClass,
    UmlClassAttribute,
    UmlClassMethod,
    UmlImplementation,
    UmlSolution
} from "../umlInterfaces";

import {MyJointClass, MyJointClassView, STD_CLASS_HEIGHT, STD_CLASS_WIDTH} from "./classDiagElements";
import {editLink} from "./classDiagEdit";
import {testSol} from "./classDiagCorrection";

export {classDiagGraph, classDiagPaper, classDiagTestBtn};

const PADDING = 40;

let chosenCellView = null;

let classDiagTestBtn;

const classDiagGraph = new joint.dia.Graph();
let classDiagPaper;

let sel = 'POINTER';

const SIMPLE_CLASS_PREFIX = 'Klasse_';

function addUmlClass(umlClass: UmlClass): void {
    classDiagGraph.addCell(new MyJointClass({
        position: umlClass.position,

        size: {width: STD_CLASS_WIDTH, height: STD_CLASS_HEIGHT},

        className: umlClass.name.replace(/ /g, '_'),

        classType: umlClass.classType,

        attributes: <UmlClassAttribute[]> umlClass.attributes,

        methods: <UmlClassMethod[]> umlClass.methods,
    }));
}

function newClass(posX: number, posY: number): void {
    let simpleNameInts: number[] = classDiagGraph.getCells()
        .filter((c) => c instanceof MyJointClass)
        .map((cell: MyJointClass) => cell.getClassName())
        .filter((cn: string) => cn.startsWith(SIMPLE_CLASS_PREFIX))
        .map((str) => parseInt(str.substring(SIMPLE_CLASS_PREFIX.length)));

    addUmlClass({
        name: SIMPLE_CLASS_PREFIX + (Math.max(...simpleNameInts, 0) + 1),
        classType: sel.toUpperCase(),
        attributes: [], methods: [],
        position: {x: posX, y: posY}
    });
}

function blankOnPointerDown(evt: Event, x: number, y: number) {
    switch (sel) {
        case 'CLASS':
        case 'ABSTRACT':
        case 'INTERFACE':
            newClass(x, y);
            break;
        case 'POINTER':
            // Ignore...
            break;
        default :
            console.info(sel);
    }
}


function cellOnLeftClick(cellView: joint.dia.CellView, evt) {

    console.warn('pointer click...');

    let model = cellView.model;

    switch (model.attributes.type) {
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
        case 'MyJointClass':
            // FIXME: use? model.onLeftClick();
            break;
        default:
            console.warn(model.attributes.type);
    }

    // if (sel === 'POINTER') {
    // Class editing -> MyJointClassView::pointerdblclick
    // }

    if ('IMPLEMENTATION' === sel) {
        // FIXME: do not select arrows or other things, only classes!
        cellView.highlight();

        if (chosenCellView === null) {
            chosenCellView = cellView;

        } else if (chosenCellView.model.id === model.id) {
            cellView.unhighlight();
            chosenCellView = null;

        } else {
            addImplementation(chosenCellView.model.id, model.id as string);

            chosenCellView.unhighlight();
            cellView.unhighlight();

            chosenCellView = null;
        }
    } else if (sel === 'ASSOCIATION' || sel === 'AGGREGATION' || sel === 'COMPOSITION') {
        // FIXME: do not select arrows or other things, only classes!
        cellView.highlight();

        if (chosenCellView === null) {
            chosenCellView = cellView;

        } else if (chosenCellView.model.id === model.id) {
            cellView.unhighlight();
            chosenCellView = null;

        } else {
            let newLink: joint.dia.LinkView = addAssociation(chosenCellView.model.id, model.id as string, sel, '*', '*');

            console.warn(newLink);

            editLink(newLink);

            // htmlForCardinalityEdit(newId, '', source, sourceMult, target, targetMult);

            chosenCellView.unhighlight();
            cellView.unhighlight();

            chosenCellView = null;
        }
    } else if (sel === 'CLASS' || sel === 'POINTER') {
        // alert('Sie können keine Klassen innerhalb von Klassen erstellen!');
    } else {
        console.error('TODO: ' + sel);
    }
}

function unMarkButtons(): void {
    $('#buttonsDiv').find('button').removeClass('btn-primary').addClass('btn-default');
}

function selectAssocType(button: HTMLAnchorElement): void {
    unMarkButtons();

    let assocButton = document.getElementById('assocButton');
    assocButton.className = 'btn btn-primary';
    assocButton.dataset.conntype = button.dataset.conntype;

    sel = button.dataset.conntype.trim();
    $('#assocType').text(button.textContent);
}

function selectButton(button: HTMLElement): void {
    const buttonType = $(button).data('conntype').trim();

    unMarkButtons();

    if (buttonType === sel) {
        sel = 'POINTER';
    } else {
        sel = buttonType;
        button.className = 'btn btn-primary';
    }
}

function addImplementation(sourceId: string, targetId: string) {
    classDiagGraph.addCell(new joint.shapes.uml.Implementation({source: {id: sourceId}, target: {id: targetId}}));
}

function addAssociation(sourceId: string, targetId: string, linkType: string, sourceMult: string, targetMult: string): joint.dia.LinkView {
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

    classDiagGraph.addCell(cellToAdd);
    return classDiagPaper.findViewByModel(cellToAdd.id);
}

function loadImplementation(implementationToLoad: UmlImplementation): void {
    let subClass = implementationToLoad.subClass, superClass = implementationToLoad.superClass;
    let subClassId = null, superClassId = null;

    for (let cell of classDiagGraph.getCells()) {
        if (cell instanceof MyJointClass) {
            let cellClassName = cell.getClassName();
            if (cellClassName === subClass) {
                subClassId = cell.id;
            } else if (cellClassName === superClass) {
                superClassId = cell.id;
            }
        }
    }

    if (subClassId !== null && superClassId !== null) {
        addImplementation(subClassId, superClassId);
    } else {
        console.error('Could not find subClass or superClass for implenentation!')
    }
}

function loadAssociation(associationToLoad: UmlAssociation): void {
    let firstEnd = associationToLoad.firstEnd, secondEnd = associationToLoad.secondEnd;
    let firstEndId = null, secondEndId = null;

    for (let cell of classDiagGraph.getCells()) {
        if (cell instanceof MyJointClass) {
            let cellClassName = cell.getClassName();
            if (cellClassName === firstEnd) {
                firstEndId = cell.id;
            } else if (cellClassName === secondEnd) {
                secondEndId = cell.id;
            }
        }
    }

    let firstMult = associationToLoad.firstMult === 'SINGLE' ? '1' : '*';
    let secondMult = associationToLoad.secondMult === 'SINGLE' ? '1' : '*';

    if (firstEndId !== null && secondEndId !== null) {
        addAssociation(firstEndId, secondEndId, associationToLoad.assocType, firstMult, secondMult);
    } else {
        console.error('Could not find ends for association!');
    }
}

function loadClasses(classesToLoad: UmlClass[]): void {
    let sqrt = Math.ceil(Math.sqrt(classesToLoad.length));

    const size = STD_CLASS_WIDTH + PADDING;

    for (let i = 0; i < classesToLoad.length; i++) {
        const classToLoad = classesToLoad[i];


        if (!classToLoad.hasOwnProperty('position')) {
            classToLoad.position = {
                x: size * (i % sqrt) + PADDING,
                y: size * Math.floor(i / sqrt) + PADDING
            };
        }

        addUmlClass(classToLoad);
    }
}

function onSolutionLoadSuccess(response: UmlSolution): void {
    loadClasses(response.classes);

    for (let assoc of response.associations) {
        loadAssociation(assoc);
    }

    for (let impl of response.implementations) {
        loadImplementation(impl);
    }
}

function onSolutionLoadError(jqXHR): void {
    console.error(jqXHR);
}

function loadSolution(url: string) {
    $.ajax({
        type: 'GET',
        dataType: 'json', // return type
        url,
        async: true,
        success: onSolutionLoadSuccess,
        error: onSolutionLoadError
    });
}

function addHandlersToButtons(): void {
    $('button[data-conntype]').each((index, element: HTMLElement) => {
        $(element).on('click', () => {
            selectButton(element);
        })
    });

    $('a[data-conntype]').each((index, element: HTMLAnchorElement) => {
        $(element).on('click', () => {
            selectAssocType(element);
        })
    })
}

function onDragStart(ev: DragEvent) {
    let target = ev.target as HTMLSpanElement;

    let className = (target as HTMLSpanElement).innerHTML;

    if (target.getAttribute('data-baseform') !== null) {
        className = target.getAttribute('data-baseform');
    }

    ev.dataTransfer.setData('text', className);
}


function drop(jqEvent) {
    jqEvent.preventDefault();

    let event = jqEvent.originalEvent;

    addUmlClass({
        name: event.dataTransfer.getData('text'),
        classType: 'CLASS',
        attributes: [],
        methods: [],
        position: {x: event.x, y: event.y}
    });
}


function addDrag(paperJQ: JQuery): void {
    let classSpans = $('.non-marked');

    classSpans.prop('draggable', true);
    classSpans.each((index, element: HTMLSpanElement) => {
        element.addEventListener('dragstart', onDragStart);
    });

    paperJQ.on('dragover', (event) => event.preventDefault());
    paperJQ.on('drop', drop);
}

$(() => {
    let paperJQ = $('#classDiagPaper');

    addHandlersToButtons();

    addDrag(paperJQ);

    // Init Graph and Paper
    classDiagPaper = new joint.dia.Paper({
        el: paperJQ, model: classDiagGraph,

        width: paperJQ.width(), height: PAPER_HEIGHT,

        gridSize: GRID_SIZE, drawGrid: {name: DEF_GRID},

        elementView: (element) => {
            if (element instanceof MyJointClass) {
                return MyJointClassView;
            } else {
                return joint.dia.Paper.prototype.options.elementView.apply(this, element);
            }
        }
    });

    // Set callback for click on cells and blank paper
    classDiagPaper.on('cell:pointerclick', cellOnLeftClick);
    classDiagPaper.on('blank:pointerdown', blankOnPointerDown);


    // paper.on('cell:pointerdown', (cellView, evt) => {
    //     console.warn('pointer down...');
    //     evt.stopPropagation();
    // });

    // paper.on('cell:contextmenu', (elementView, evt) => {
    //     if (elementView instanceof MyJointClassView) {
    //  // Do nothing...
    // } else {
    //     evt.stopPropagation();
    //
    //  // FIXME:
    // if (getsHelp && elementView.model.attributes.type === 'uml.Class') {
    //     alert('Sie können keine Klassen löschen!');
    // } else if (confirm('Wollen Sie dieses Objekt wirklich löschen?')) {
    // elementView.model.remove();
    // }
    // }
    // });


    loadSolution(paperJQ.data('url'));

    classDiagTestBtn = $('#testBtn');
    classDiagTestBtn.on('click', testSol);
});
