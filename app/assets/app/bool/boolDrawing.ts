import * as $ from 'jquery';
import * as joint from 'jointjs';
import * as _ from 'underscore';

import './boolDrawingElements';

import 'jointjs';
import {domReady} from "../otherHelpers";

export {toggleLive};

const graph = new joint.dia.Graph();
const STD_ZOOM_LEVEL = 1.4;

let paper;

let dragX; // Postion within div : X
let dragY;	// Postion within div : Y

let sel_elementname; // used for select element and click on paper to generate Element

const SIGNALS = {
    'a': true, 'b': true, 'c': true, 'd': true
};

function createElement(elementName: string, x: number, y: number): void {
    let element: joint.shapes.logic.Gate, svg: string;
    const elementPos = {position: {x, y}};

    switch (elementName) {
        case     'elementAND':
            element = new joint.shapes.logic.And(elementPos);
            svg = 'https://upload.wikimedia.org/wikipedia/commons/0/0f/AND_IEC.svg';
            break;
        case     'elementNAND':
            element = new joint.shapes.logic.Nand(elementPos);
            svg = 'https://upload.wikimedia.org/wikipedia/commons/d/d8/NAND_IEC.svg';
            break;
        case     'elementOR':
            element = new joint.shapes.logic.Or(elementPos);
            svg = 'https://upload.wikimedia.org/wikipedia/commons/4/42/OR_IEC.svg';
            break;
        case     'elementNOR':
            element = new joint.shapes.logic.Nor(elementPos);
            svg = 'https://upload.wikimedia.org/wikipedia/commons/6/6d/NOR_IEC.svg';
            break;
        case     'elementXOR':
            element = new joint.shapes.logic.Xor(elementPos);
            svg = 'https://upload.wikimedia.org/wikipedia/commons/4/4e/XOR_IEC.svg';
            break;
        case     'elementXNOR':
            element = new joint.shapes.logic.Xnor(elementPos);
            svg = 'https://upload.wikimedia.org/wikipedia/commons/5/56/XNOR_IEC.svg';
            break;
        case     'elementNOT':
            element = new joint.shapes.logic.Not(elementPos);
            svg = 'https://upload.wikimedia.org/wikipedia/commons/e/ef/NOT_IEC.svg';
            break;
    }

    element.attr('image/xlink:href', svg);

    unmarkAllElementButtons();
    sel_elementname = "";

    graph.addCell(element);

}

const MODIFICATORS = {
    'logic.Output': {neededInputs: 1, isNegated: false, infixOperator: ''},
    'logic.Not': {neededInputs: 1, isNegated: true, infixOperator: ''},
    'logic.And': {neededInputs: 2, isNegated: false, infixOperator: '&and;'},
    'logic.Nand': {neededInputs: 2, isNegated: true, infixOperator: '&and;'},
    'logic.Or': {neededInputs: 2, isNegated: false, infixOperator: '&or;'},
    'logic.Nor': {neededInputs: 2, isNegated: true, infixOperator: '&or;'},
    'logic.Xor': {neededInputs: 2, isNegated: false, infixOperator: '&oplus;'},
    'logic.Xnor': {neededInputs: 2, isNegated: true, infixOperator: '&oplus;'},
};

document.addEventListener("dragover", function (e: DragEvent) {
    const offset = $('#paper').offset();
    dragX = e.pageX - offset.left;
    dragY = e.pageY - offset.top;
}, false);


function drag(dragEvent: DragEvent): void {
    dragEvent.dataTransfer.setData('text', (dragEvent.target as HTMLButtonElement).name);
}

function drop(dragEvent: DragEvent): void {
    dragEvent.preventDefault();

    // scale: Coordinates offset by graph scale, correct with factor
    const scale = joint.V(paper.viewport).scale();

    createElement(dragEvent.dataTransfer.getData('text'), dragX / scale.sx, dragY / scale.sy);
}

function setName(button: HTMLButtonElement) {
    sel_elementname = button.name;

    unmarkAllElementButtons();
    $(button).removeClass('btn-default').addClass('btn-primary');
}

function unmarkAllElementButtons() {
    $('#creationButtonsDiv').find('button').removeClass('btn-primary').addClass('btn-default');
}

function preparePaper() {
    let leftBlock = $('#leftblock');
    const xMaxPos = leftBlock.width() / 1.65, yPos = leftBlock.height() / 2;

    let gatesToAdd = [
        {symbol: 'a', gateType: 'input', position: {x: 20, y: 20}},
        {symbol: 'b', gateType: 'input', position: {x: 20, y: yPos / 3}},
        {symbol: 'c', gateType: 'input', position: {x: 20, y: yPos * 2 / 3}},
        {symbol: 'd', gateType: 'input', position: {x: 20, y: yPos}},

        {symbol: 'y', gateType: 'output', position: {x: xMaxPos, y: yPos / 3}},
        {symbol: 'z', gateType: 'output', position: {x: xMaxPos, y: yPos * 2 / 3}}
    ];

    for (let gateToAdd of gatesToAdd) {
        let newGate;

        if (gateToAdd.gateType === 'input') {
            newGate = new joint.shapes.logic.Input(gateToAdd);
        } else if (gateToAdd.gateType === 'output') {
            newGate = new joint.shapes.logic.Output(gateToAdd);
        }

        newGate.attr('logicSymbol', gateToAdd.symbol);
        newGate.attr('text', {text: gateToAdd.gateType + ' ' + gateToAdd.symbol});

        graph.addCell(newGate);
    }

    paper.scale(STD_ZOOM_LEVEL, STD_ZOOM_LEVEL);
}

function toggleLive(model: joint.dia.Cell, signal: boolean): void {
    // add 'live' class to the element if there is a positive signal
    joint.V(paper.findViewByModel(model).el).toggleClass('live', signal);
}

function broadcastSignal(gate: joint.shapes.logic.Gate, signal: boolean): void {
    toggleLive(gate, signal);

    // broadcast signal to all output ports
    let outGoingWires = graph.getConnectedLinks(gate, {outbound: true}) as joint.shapes.logic.Wire[];
    for (let wire of outGoingWires) {
        wire.set('signal', signal);
    }
}

function initializeSignals() {
    // cancel all signals stores in wires
    for (let wire of graph.getLinks()) {
        wire.set('signal', false);
    }

    // remove all 'live' classes
    $('.live').each(function () {
        // TODO:        joint.V(this).removeClass('live');
    });

    for (let element of graph.getElements()) {
        let outGoingLinks = graph.getConnectedLinks(element, {outbound: true});
        if (element instanceof joint.shapes.logic.Input && outGoingLinks.length > 0) {
            broadcastSignal(element, SIGNALS[element.attr('logicSymbol')]);
        }
    }
}

interface OutputFormula {
    formula: string
    success: boolean
}

function getOutputFormula(gate: joint.shapes.logic.Gate): OutputFormula {
    if (gate.attributes.type === 'logic.Input') {
        return {success: true, formula: gate.attr('logicSymbol')};
    }

    let modificator = MODIFICATORS[gate.attributes.type];

    let formula = '', success;

    const ingoingWires = graph.getConnectedLinks(gate, {inbound: true});

    if (modificator.neededInputs !== ingoingWires.length) {
        paper.findViewByModel(gate.id).highlight();
        return {formula: '', success: false}
    }

    if (modificator.neededInputs === 1) {
        let sourceInput = getOutputFormula(graph.getCell(ingoingWires[0].prop('source').id) as joint.shapes.logic.Gate);
        success = sourceInput.success;
        if (success) {
            formula = (modificator.isNegated ? '&not; ' : '') + sourceInput.formula;
        }
    } else if (modificator.neededInputs === 2) {
        let firstInput = getOutputFormula(graph.getCell(ingoingWires[0].prop('source').id) as joint.shapes.logic.Gate);
        let secondInput = getOutputFormula(graph.getCell(ingoingWires[1].prop('source').id) as joint.shapes.logic.Gate);

        success = firstInput.success && secondInput.success;
        if (success) {
            formula = (modificator.isNegated ? '&not;' : '') + (firstInput.formula + ' ' + modificator.infixOperator + ' ' + secondInput.formula);
        }
    }

    return {success, formula};
}


function initButtons(): void {
    const elementButtons = $('#creationButtonsDiv').find('button');
    elementButtons.on('click', (event) => setName(event.target as HTMLButtonElement));
    elementButtons.each((index, button: HTMLButtonElement) => {
        button.addEventListener('dragstart', drag);
        button.draggable = true;
    });
}

domReady(() => {
        initButtons();

        const paperElem = document.getElementById('paper');

        const paperSelector = $('#paper');

        paperElem.addEventListener('dragover', (event: DragEvent) => event.preventDefault());
        paperElem.addEventListener('drop', drop);

        paper = new joint.dia.Paper({
            el: paperSelector,
            model: graph,

            width: paperSelector.width(), height: 700,

            gridSize: 20, drawGrid: {name: 'dot'},

            defaultLink: (cellView: joint.dia.CellView, magnet: SVGElement) => new joint.shapes.logic.Wire(cellView, magnet),

            snapLinks: true,
            linkPinning: false,

            validateConnection(cellViewSource: joint.dia.CellView, magnetSource: SVGElement,
                               cellViewTarget: joint.dia.CellView, magnetTarget: SVGElement,
                               end: string, linkView: joint.dia.LinkView) {
                if (end === 'target') {

                    //        target requires an input port to connect
                    if (!magnetTarget || !magnetTarget.getAttribute('class') || magnetTarget.getAttribute('class').indexOf('input') < 0) return false;

                    //  check whether the port is being already used
                    let portUsed = _.find(this.model.getLinks(), function (link: joint.dia.Link) {

                        return (link.id !== linkView.model.id &&
                            link.get('target').id === cellViewTarget.model.id &&
                            link.get('target').port === magnetTarget.getAttribute('port'));
                    });

                    return !portUsed;

                } else { // e === 'source'
                    //     source requires an output port to connect
                    return magnetSource && magnetSource.getAttribute('class') && magnetSource.getAttribute('class').indexOf('output') >= 0;
                }
            }
        });

        preparePaper();

        graph.on('change:source change:target', function (model, end) {

            const e: string = 'target' in model.changed ? 'target' : 'source';

            if ((model.previous(e).id && !model.get(e).id) || (!model.previous(e).id && model.get(e).id)) {
                // if source/target has been connected to a port or disconnected from a port reinitialize signals
                initializeSignals();
            }
        });

        graph.on('change:signal', function (wire: joint.shapes.logic.Wire, signal: boolean) {

            toggleLive(wire, signal);

            const gate = graph.getCell(wire.get('target').id);

            if (gate) {
                if (gate instanceof joint.shapes.logic.Gate11) {
                    gate.onSignal(signal, () => {
                        const maybeInput = graph.getConnectedLinks(gate, {inbound: true});
                        let input: boolean = (maybeInput.length === 1) ? maybeInput[0].get('signal') : false;

                        // calculate the output signal
                        const output: boolean = gate.operation(input);

                        broadcastSignal(gate, output);
                    });
                } else if (gate instanceof joint.shapes.logic.Gate21) {
                    const maybeInput = graph.getConnectedLinks(gate, {inbound: true});

                    let input1: boolean = (maybeInput.length >= 1) ? maybeInput[0].get('signal') : false;
                    let input2: boolean = (maybeInput.length === 2) ? maybeInput[1].get('signal') : false;

                    // calculate the output signal
                    const output: boolean = gate.operation(input1, input2);

                    broadcastSignal(gate, output);
                } else {
                    // TODO!
                }
            }
        });

        $("#generateFormula").on('click', () => {
            let target = $('#preCode');


            let allElementsInGraph = graph.getElements();

            for (let element of allElementsInGraph) {
                paper.findViewByModel(element).unhighlight();
            }

            let html = '';

            for (let element of allElementsInGraph) {
                if (element instanceof joint.shapes.logic.Output) {
                    let formulaResult = getOutputFormula(element);

                    if (formulaResult.success) {
                        html += `<p><code>${element.attr('logicSymbol')} = ${formulaResult.formula}</code></p>`;
                    } else {
                        html += `<p class="text-danger">${element.attr('logicSymbol')}: ${formulaResult.formula}</p>`;
                    }
                }
            }

            target.html(html);
        });

        graph.on('change:position', function (cell, newPosition, opt) {

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

            const parent = graph.getCell(parentId);
            if (!parent.get('originalPosition')) parent.set('originalPosition', parent.get('position'));
            if (!parent.get('originalSize')) parent.set('originalSize', parent.get('size'));

            const originalPosition = parent.get('originalPosition');
            const originalSize = parent.get('originalSize');

            let newX = originalPosition.x;
            let newY = originalPosition.y;
            let newCornerX = originalPosition.x + originalSize.width;
            let newCornerY = originalPosition.y + originalSize.height;

            _.each(parent.getEmbeddedCells(), function (child: joint.dia.Element) {

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

        graph.on('change:size', function (cell, newPosition, opt) {

            if (opt.skipParentHandler) return;

            if (cell.get('embeds') && cell.get('embeds').length) {
                // If we're manipulating a parent element, let's store
                // it's original size to a special property so that
                // we can shrink the parent element back while manipulating
                // its children.
                cell.set('originalSize', cell.get('size'));
            }
        });


        (function (joint, $) {
            paper.on('cell:contextmenu', function (evt, x, y) {
                if (!(evt.model.attributes.type === "logic.Input" || evt.model.attributes.type === "logic.Output")) {
                    graph.getCell(evt.model.id).remove();
                }
            });

            paper.on('cell:pointerclick', function (cellView, evt, x, y) {
                if (cellView.model.attributes.type === 'logic.Input') {
                    let logicSymbol = cellView.model.attr('logicSymbol');

                    let newSignal = !SIGNALS[logicSymbol];
                    SIGNALS[logicSymbol] = newSignal;

                    // toggleLive(cellView.model, newSignal);

                    broadcastSignal(cellView.model, newSignal);
                } else {
                    console.error(typeof  cellView);
                }
            });

            paper.on('blank:pointerclick', function (evt, x, y) {
                if (sel_elementname !== "") {
                    createElement(sel_elementname, x, y);
                }
            });
        })(joint, $);

    }
);
