import * as  $ from 'jquery';
import * as joint from 'jointjs';
import * as _ from 'lodash';
import 'codemirror/mode/python/python';

import 'backbone';

import {DEF_GRID, GRID_SIZE, PAPER_HEIGHT, START_END_SIZE} from "../umlConsts";
import {
    ActionInput,
    ActionInputView,
    createDoWhile,
    createEdit,
    createEndState,
    createForLoop,
    createStartState,
    createWhileDo,
    Edit,
    EDIT_HEIGHT,
    EDIT_WIDTH,
    EditView,
    EndState,
    ForLoopEmbed,
    ForLoopEmbedView,
    ForLoopText,
    ForLoopTextView,
    IfElseText,
    IfElseTextView,
    MyElementView,
    StartState,
    WhileLoopText,
    WhileLoopView
} from "./umlActivityElements";
import {HtmlElement} from "./umlADHtmlElement";

export {
    umlActivityPaper, umlActivityGraph,
    mainStartNode, mainEndNode,
    selElement, createElements,
    parentChildNodes
}

const notSelectedElemButtonClass = 'btn-default';
const selectedElemButtonClass = 'btn-primary';

const umlActivityGraph = new joint.dia.Graph();
let umlActivityPaper: joint.dia.Paper;


let mainStartNode: StartState;
let mainEndNode: EndState;

let dragX: number, dragY: number;

// used for select element and click on paper to generate Element
let selElement;

//vars
let MousePosElementName;
let MousePosElementID;

// Buttons

// TODO:

interface ParentChildNodes {
    parentId: string,
    startId: string,
    endId: string,
    endName: string
}

let parentChildNodes: ParentChildNodes[]; // Array with all subgraphs (startid,endid,..)

interface ConnectProperties {
    sourceId: string,
    targetId: string,
    sourcePort: string,
    targetPort: string
}

interface PositionObject {
    position: { x: number, y: number }
}

document.addEventListener('dragover', (e: DragEvent) => {
// FIXME: --> classDiagDrawing!
    const offset = $('#umlActivityPaper').offset();

    dragX = e.pageX - offset.left;
    dragY = e.pageY - offset.top;
}, false);


function setSelElement(anchor: HTMLElement): void {
    let jAnchor = $(anchor);

    let elemToSelect = jAnchor.data('elemname');
    jAnchor.parent().find('a').removeClass(selectedElemButtonClass).addClass(notSelectedElemButtonClass);

    if (selElement === elemToSelect) {
        clearSelElement();
    } else {
        selElement = elemToSelect;
        jAnchor.removeClass(notSelectedElemButtonClass).addClass(selectedElemButtonClass);
    }
}


function clearSelElement(): void {
    $('#buttonsDiv').find('a').removeClass(selectedElemButtonClass).addClass(notSelectedElemButtonClass);
    selElement = '';
}

function createElementsAndConnections(elementName: string, position: PositionObject, embedInto?: joint.dia.Element): void {
    if (elementName === 'uml.Edit') {
        let editElement = new Edit(position);
        if (embedInto) {
            embedInto.embed(editElement);
        }
        umlActivityGraph.addCell(editElement);

        let editStartState: joint.dia.Cell = new StartState({
            position: {x: position.position.x, y: position.position.y}
        });
        let editEndState = new EndState({
            position: {
                x: position.position.x + EDIT_WIDTH - START_END_SIZE,
                y: position.position.y + EDIT_HEIGHT - START_END_SIZE
            }
        });

        editElement.embed(editStartState);
        editElement.embed(editEndState);

        umlActivityGraph.addCells([editStartState, editEndState]);
    } else {

        let elementsToAdd: joint.dia.Element[] = [];
        let connectionsToCreate: ConnectProperties[] = [];

        switch (elementName) {
            case 'elementActionInput':
                try {
                    elementsToAdd.push(new ActionInput(position));
                } catch (err) {
                    console.error(err);
                }
                break;

            // case 'elementActionSelect':
            //     elementsToAdd.push(createActionSelect(position.position.x, position.position.y));
            //     break;

            // case 'elementActionDeclare':
            //     elementsToAdd.push(createActionDeclare(position.position.x, position.position.y));
            //     break;

            case 'elementFor':
                let forElement = createForLoop(position.position.x, position.position.y);
                let forEditElement = createEdit(position.position.x + 350, position.position.y);

                elementsToAdd.push(forElement, forEditElement);

                connectionsToCreate.push({
                    sourceId: forElement.id as string, sourcePort: 'extern',
                    targetId: forEditElement.id as string, targetPort: 'extern'
                });
                break;

            case 'elementDoWhile':
                let doWhile = createDoWhile(position.position.x, position.position.y);
                let doWhileEditElement = createEdit(position.position.x + 350, position.position.y);

                elementsToAdd.push(doWhile, doWhileEditElement);

                connectionsToCreate.push({
                    sourceId: doWhile.id as string, sourcePort: 'extern',
                    targetId: doWhileEditElement.id as string, targetPort: 'extern'
                });
                break;

            case 'elementWhileDo':
                let whileDoElement = createWhileDo(position.position.x, position.position.y);
                let whileDoEditElement = createEdit(position.position.x + 350, position.position.y);

                elementsToAdd.push(whileDoElement, whileDoEditElement);

                connectionsToCreate.push({
                    sourceId: whileDoElement.id as string, sourcePort: 'extern',
                    targetId: whileDoEditElement.id as string, targetPort: 'extern'
                });
                break;

            case 'elementEdit':
                elementsToAdd.push(createEdit(position.position.x, position.position.y));
                break;

            case 'uml.ForLoopText':
                elementsToAdd.push(new ForLoopText(position));
                break;

            case 'uml.ForLoopEmbed':
                elementsToAdd.push(new ForLoopEmbed(position));
                break;

            case 'uml.WhileLoopText':
                elementsToAdd.push(new WhileLoopText(position));
                break;

            case 'uml.IfElseText':
                elementsToAdd.push(new IfElseText(position));
                break;

            default:
                // No action taken...
                console.log('Could not create element ' + elementName);
                break;
        }

        if (embedInto) {
            for (let elToAdd of elementsToAdd) {
                embedInto.embed(elToAdd);
            }
        }

        umlActivityGraph.addCells(elementsToAdd);

        for (let connToCreate of connectionsToCreate) {
            connectNodes(connToCreate);
        }
    }
}

// Constructor Elements
function createElements(elementName: string, positionObject: PositionObject, embedInto?: joint.dia.Element): void {

    createElementsAndConnections(elementName, positionObject, embedInto);

    clearSelElement();
}

function connectNodes(connectProperties: ConnectProperties): void {
    umlActivityGraph.addCell(new joint.shapes.devs.Link({
        source: {
            id: connectProperties.sourceId,
            port: connectProperties.sourcePort
        },
        target: {
            id: connectProperties.targetId,
            port: connectProperties.targetPort
        },
        router: {name: 'manhattan'},  // Link design for horizontal and vertical lines
        connector: {name: 'normal'},
        attrs: {'.marker-target': {d: 'M 10 0 L 0 5 L 10 10 z'}} // Arrow is horizontal or vertical
    }));
}


function initElementButtons(): void {
    let jButtons = $('#buttonsDiv').find('a');

    jButtons.on('click', (event: Event) => setSelElement(event.target as HTMLElement));

    jButtons.prop('draggable', true);
    jButtons.each((index, element: HTMLElement) => {
        element.addEventListener('dragstart', (event: DragEvent) => {
            event.dataTransfer.setData('text', $(event.target).data('elemname'));
        });
    });


}


function initPaperEvents(jPaper: JQuery): void {
    jPaper.on('dragover', (event: Event) => event.preventDefault());
    jPaper.on('drop', (event) => {
        event.preventDefault();

        createElements((event.originalEvent as DragEvent).dataTransfer.getData('text'), {
            position: {
                x: dragX,
                y: dragY
            }
        });
    });
}

function getViewForElement(element: joint.dia.Element): typeof joint.dia.ElementView {

    if (element instanceof ActionInput) {
        return ActionInputView;
    }

    if (element instanceof WhileLoopText) {
        return WhileLoopView;
    }

    if (element instanceof ForLoopEmbed) {
        return ForLoopEmbedView;
    }

    if (element instanceof ForLoopText) {
        return ForLoopTextView;
    }

    if (element instanceof EndState) {
        return MyElementView;
    }

    if (element instanceof StartState) {
        return MyElementView;
    }

    if (element instanceof IfElseText) {
        return IfElseTextView;
    }

    if (element instanceof Edit) {
        return EditView;
    }

    //TODO: other...
    return joint.dia.ElementView;

}

$(() => {
    let jPaper = $('#umlActivityPaper');

    initElementButtons();

    initPaperEvents(jPaper);

    parentChildNodes = [];

    function preparePaper() {
        mainStartNode = createStartState(GRID_SIZE, GRID_SIZE);
        mainEndNode = createEndState(jPaper.width() - (START_END_SIZE + GRID_SIZE),
            jPaper.height() - (START_END_SIZE + GRID_SIZE));

        // try {
        let actionNodeStart = new ActionInput()
            .position(100, 100)
            .setContent(['solution = ' + $('#defaultSolution').val()]); // EXERCISE_PARAMETERS.output.defaultValue

        let actionNodeEnd = new ActionInput()
            .position(jPaper.width() - 300, jPaper.height() - 150)
            .setContent(['return solution']);

        umlActivityGraph.addCell(mainStartNode);
        umlActivityGraph.addCell(mainEndNode);
        umlActivityGraph.addCell(actionNodeStart);
        umlActivityGraph.addCell(actionNodeEnd);
        // umlActivityGraph.addCells([mainStartNode, mainEndNode, actionNodeStart, actionNodeEnd]);

        connectNodes({
            sourceId: mainStartNode.id as string, sourcePort: "out",
            targetId: actionNodeStart.id as string, targetPort: "in"
        });

        connectNodes({
            sourceId: actionNodeEnd.id  as string, sourcePort: "out",
            targetId: mainEndNode.id  as string, targetPort: "in"
        });

        parentChildNodes.push({
            parentId: 'Startknoten-startId', endName: 'end',
            startId: 'Startknoten-startId', endId: 'Endknoten-endId'
        });
        // } catch (err) {
        //     console.error(err);
        // }

    }

    //Basics
    umlActivityPaper = new joint.dia.Paper({
        el: jPaper, model: umlActivityGraph,

        width: jPaper.width(), height: PAPER_HEIGHT,

        gridSize: GRID_SIZE, drawGrid: {name: DEF_GRID},

        defaultLink: new joint.dia.Link({
            router: {name: 'manhattan'},  // Link design for horizontal and vertical lines
            connector: {name: 'normal'},
            attrs: {'.marker-target': {d: 'M 10 0 L 0 5 L 10 10 z'}} // Arrow is horizentor or vertical
        }),

        snapLinks: {radius: 25}, linkPinning: false,

        elementView: getViewForElement,
        preventContextMenu: true
    });

    let ph = jPaper.height(); // maximal decreasing to loaded size of paper

    let currentZoom = 1.0;
    let currentOrigin = 0;
    let top = 0;

    const save_ph = ph;
    $('#paperplus').on('click', () => {
        if (ph <= 2000) {
            ph += 100;
            umlActivityPaper.setDimensions(0, ph);
            umlActivityPaper.setDimensions(0, ph);
        }
    });

    $('#paperminus').on('click', () => {
        if (ph > save_ph) {
            ph -= 100;
            umlActivityPaper.setDimensions(0, ph);
            umlActivityPaper.setDimensions(0, ph);
        }
    });

    // zoom buttons
    $('#zoomplus').on('click', () => {
        if (currentZoom < 1.5) {
            currentZoom += 0.1;
            umlActivityPaper.scale(currentZoom, currentZoom);
        }
    });

    $('#zoomminus').on('click', () => {
        if (currentZoom > 0.7) {
            currentZoom -= 0.1;
            umlActivityPaper.scale(currentZoom, currentZoom);
        }
    });

    // origin
    $('#oright').on('click', () => {
        if (currentOrigin < 400) {
            currentOrigin += 100;
            umlActivityPaper.setOrigin(currentOrigin, top);
            // rebuildGraph();
        }
    });

    $('#oleft').on('click', () => {
        if (currentOrigin > -400) {
            currentOrigin -= 100;
            umlActivityPaper.setOrigin(currentOrigin, top);
            // rebuildGraph();
        }
    });

    $('#otop').on('click', () => {
        if (top > -400) {
            top -= 100;
            umlActivityPaper.setOrigin(currentOrigin, top);
            // rebuildGraph();
        }
    });

    $('#obot').on('click', () => {
        if (top < 400) {
            top += 100;
            umlActivityPaper.setOrigin(currentOrigin, top);
            // rebuildGraph();
        }
    });

    // view reset
    $('#viewreset').on('click', () => {
        umlActivityPaper.scale(1.0, 1.0);
        umlActivityPaper.setOrigin(0, 0);
        umlActivityPaper.setDimensions(jPaper.width(), jPaper.height());
        // rebuildGraph();
    });

    umlActivityGraph.on('change', function () {
        $('#generationAlerts').html(`<div class="alert alert-warning">Ihr Diagramm hat sich geändert. Bitte generieren Sie ihren Code neu!</div>`);
        $('#mainGeneration').removeClass('btn-default').addClass('btn-primary');
        // $('#preCode').html('');
    });

    // graph.on events
    umlActivityGraph.on('change:target', (eventName, cell) => {
        // forbidInputTextarea(eventName, cell);
    });

    umlActivityGraph.on('change:source', (eventName, cell) => {
        // activateTextarea(eventName, cell);
    });

    umlActivityGraph.on('remove', (eventName, cell) => {
        // activateTextarea(eventName, cell);
    });

    umlActivityGraph.on('change:position', function (cell, newPosition, opt) {

        if (opt.skipParentHandler) return;

        if (cell.get('embeds') && cell.get('embeds').length) {
            // If we're manipulating a parent element, let's store
            // it's original position to a special property so that
            // we can shrink the parent element back while manipulating
            // its children.
            cell.set('originalPosition', cell.get('position'));
        }

        let parentId = cell.get('parent');
        if (!parentId) return;

        const parent = umlActivityGraph.getCell(parentId);
        // const parentBbox = parent.getBBox();
        if (!parent.get('originalPosition')) parent.set('originalPosition', parent.get('position'));
        if (!parent.get('originalSize')) parent.set('originalSize', parent.get('size'));

        const originalPosition = parent.get('originalPosition');
        const originalSize = parent.get('originalSize');

        let newX = originalPosition.x;
        let newY = originalPosition.y;
        let newCornerX = originalPosition.x + originalSize.width;
        let newCornerY = originalPosition.y + originalSize.height;

        _.each(parent.getEmbeddedCells(), function (child: HtmlElement) {

            const childBbox = child.getBBox();

            if (childBbox.x < newX) {
                newX = childBbox.x;
            }
            if (childBbox.y < newY) {
                newY = childBbox.y;
            }
            if (childBbox.corner().x > newCornerX) {
                newCornerX = childBbox.corner().x;
            }
            if (childBbox.corner().y > newCornerY) {
                newCornerY = childBbox.corner().y;
            }
        });

        // Note that we also pass a flag so that we know we shouldn't adjust the
        // `originalPosition` and `originalSize` in our handlers as a reaction
        // on the following `set()` call.
        parent.set({
            position: {x: newX, y: newY},
            size: {width: newCornerX - newX, height: newCornerY - newY}
        }/*, {skipParentHandler: true}*/);
    });

    umlActivityGraph.on('change:size', function (cell, newPosition, opt) {

        if (opt.skipParentHandler) return;

        if (cell.get('embeds') && cell.get('embeds').length) {
            // If we're manipulating a parent element, let's store
            // it's original size to a special property so that
            // we can shrink the parent element back while manipulating
            // its children.
            cell.set('originalSize', cell.get('size'));
        }
    });

    // paper events

    umlActivityPaper.on('contextmenu', function (event: JQuery.Event) {
        event.stopPropagation();
        event.preventDefault();

    });

    umlActivityPaper.on('blank:pointerclick', function (evt, x, y) {
        if (selElement !== '') {
            createElements(selElement, {position: {x, y}});
            clearSelElement();
        }
    });

    umlActivityPaper.on('cell:mouseenter', function (cellView) {
        MousePosElementID = cellView.model.id;
        MousePosElementName = cellView.model.attributes.name;
    });

    umlActivityPaper.on('cell:mouseleave', function () {
        MousePosElementID = 'mainId';
        MousePosElementName = 'main';
    });

    preparePaper(); // set start and endnode
});
