import * as $ from 'jquery';
import * as joint from 'jointjs';

import 'lodash';
import 'backbone';

import {DEF_GRID, GRID_SIZE, PAPER_HEIGHT} from "../umlConsts";
import {
    LinkType,
    UmlAssociation,
    UmlClass,
    UmlClassAttribute,
    UmlClassMethod,
    UmlImplementation,
    UmlSolution,
} from "../umlInterfaces";

import {MyJointClass, MyJointClassView, STD_CLASS_HEIGHT, STD_CLASS_WIDTH} from "./classDiagElements";
import {editLink} from "./classDiagEdit";
import {
    displayMatchingResultList,
    explainAssocResult,
    explainClassResult,
    explainImplResult,
    UmlClassDiagCorrectionResult,
} from "./classDiagCorrection";
import {domReady, testExerciseSolution} from "../../otherHelpers";

export {classDiagGraph, classDiagPaper, classDiagTestBtn};

const PADDING = 40;

let chosenCellView = null;

let classDiagTestBtn: HTMLButtonElement;

const classDiagGraph = new joint.dia.Graph();
let classDiagPaper;

let sel: 'POINTER' | 'CLASS' | 'ABSTRACT' | 'INTERFACE' | 'IMPLEMENTATION' | 'ASSOCIATION' | 'AGGREGATION' | 'COMPOSITION' = 'POINTER';

const SIMPLE_CLASS_PREFIX = 'Klasse_';

function addUmlClass(umlClass: UmlClass): void {
    classDiagGraph.addCell(new MyJointClass({
        position: umlClass.position,
        size: {width: STD_CLASS_WIDTH, height: STD_CLASS_HEIGHT},
        className: umlClass.name.replace(/ /g, '_'),
        classType: umlClass.classType,
        attributes: <UmlClassAttribute[]>umlClass.attributes,
        methods: <UmlClassMethod[]>umlClass.methods,
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


function cellOnLeftClick(cellView: joint.dia.CellView, evt): void {
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

    sel = button.dataset.conntype.trim() as ('POINTER' | 'CLASS' | 'ABSTRACT' | 'INTERFACE' | 'IMPLEMENTATION' | 'ASSOCIATION' | 'AGGREGATION' | 'COMPOSITION');
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

function addImplementation(sourceId: string, targetId: string): void {
    classDiagGraph.addCell(new joint.shapes.uml.Implementation({source: {id: sourceId}, target: {id: targetId}}));
}

function addAssociation(sourceId: string, targetId: string, linkType: LinkType, sourceMult: string, targetMult: string): joint.dia.LinkView {
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
    response.associations.forEach(loadAssociation);
    response.implementations.forEach(loadImplementation);
}

function onSolutionLoadError(jqXHR): void {
    alert(jqXHR);
}

function loadSolution(url: string): void {
    $.ajax({
        type: 'GET',
        dataType: 'json',
        url,
        async: true,
        beforeSend: (xhr) => {
            const token = $('input[name="csrfToken"]').val() as string;
            xhr.setRequestHeader("Csrf-Token", token);
        },
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

function onDragStart(ev: DragEvent): void {
    let jTarget = $(ev.target);

    let className = jTarget.text();
    const baseName: string = jTarget.data('baseform');

    if (baseName !== null && baseName.length > 0) {
        className = baseName;
    }

    ev.dataTransfer.setData('text', className);
}


function drop(jqEvent): void {
    jqEvent.preventDefault();

    let event = jqEvent.originalEvent as DragEvent;

    addUmlClass({
        name: event.dataTransfer.getData('text'),
        classType: 'CLASS',
        attributes: [],
        methods: [],
        position: {x: event.x, y: event.y}
    });
}


function addDrag(paperJQ: JQuery): void {
    let classSpans = $('.text-muted');

    classSpans.prop('draggable', true);
    classSpans.each((index, element: HTMLSpanElement) => {
        element.addEventListener('dragstart', onDragStart);
    });

    paperJQ.on('dragover', (event) => event.preventDefault());
    paperJQ.on('drop', drop);
}

function onUmlClassDiagCorrectionSuccess(response: UmlClassDiagCorrectionResult): void {
    console.warn(JSON.stringify(response, null, 2));

    document.querySelector<HTMLDivElement>('#resultDiv').hidden = false;

    let html: string = '';
    if (response.classResult != null) {
        html += displayMatchingResultList(response.classResult, "Klassen", explainClassResult);
    }

    if (response.assocAndImplResult != null) {
        html += displayMatchingResultList(response.assocAndImplResult.implResult, 'Vererbungsbeziehungen', explainImplResult);
        html += displayMatchingResultList(response.assocAndImplResult.assocResult, 'Assoziationen', explainAssocResult);
    }

    document.querySelector<HTMLDivElement>('#results').innerHTML = html;
}

function getClassNameFromCellId(id: string): string {
    return (classDiagGraph.getCell(id) as MyJointClass).getClassName();
}

function getTypeName(type: string): string {
    switch (type) {
        case 'uml.Association':
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

function getMultiplicity(label): "SINGLE" | "UNBOUND" {
    return label.attrs.text.text === '1' ? 'SINGLE' : 'UNBOUND';
}


function umlImplfromConnection(conn: joint.dia.Link): UmlImplementation {
    return {
        subClass: getClassNameFromCellId(conn.attributes.source.id),
        superClass: getClassNameFromCellId(conn.attributes.target.id)
    }
}

function umlAssocfromConnection(conn: joint.dia.Link): UmlAssociation {
    return {
        assocType: getTypeName(conn.attributes.type) as LinkType,
        assocName: '',        // TODO: name of association!?!
        firstEnd: getClassNameFromCellId(conn.attributes.source.id),
        firstMult: getMultiplicity(conn.attributes.labels[0]),
        secondEnd: getClassNameFromCellId(conn.attributes.target.id),
        secondMult: getMultiplicity(conn.attributes.labels[1])
    };
}


function testSol(): void {
    let solution: UmlSolution = {
        classes: classDiagGraph.getCells()
            .map((cell) => {
                if (cell instanceof MyJointClass) {
                    return cell.getAsUmlClass();
                } else {
                    return null;
                }
            }).filter(c => c != null),

        associations: classDiagGraph.getLinks()
            .filter((conn) => conn.get('type') !== 'uml.Implementation')
            .map((conn) => umlAssocfromConnection(conn)),

        implementations: classDiagGraph.getLinks()
            .filter((conn) => conn.get('type') === 'uml.Implementation')
            .map((conn) => umlImplfromConnection(conn))
    };

    testExerciseSolution<UmlSolution, UmlClassDiagCorrectionResult>(classDiagTestBtn, solution, onUmlClassDiagCorrectionSuccess);
}

domReady(() => {
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

    loadSolution(paperJQ.data('url'));

    classDiagTestBtn = document.querySelector<HTMLButtonElement>('#testBtn');
    classDiagTestBtn.onclick = testSol;
});
