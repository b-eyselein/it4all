const graph = new joint.dia.Graph;
const STD_ZOOM_LEVEL = 1.4;

let paper;

let dragX; // Postion within div : X
let dragY;	// Postion within div : Y

let sel_elementname; // used for select element and click on paper to generate Element

const SIGNALS = {
    'a': 1, 'b': 1, 'c': 1, 'd': 1
};

const CONSTRUCTORS = {
    elementAND: {
        func: joint.shapes.logic.And, svg: 'https://upload.wikimedia.org/wikipedia/commons/0/0f/AND_IEC.svg'
    },
    elementNAND: {
        func: joint.shapes.logic.Nand, svg: 'https://upload.wikimedia.org/wikipedia/commons/d/d8/NAND_IEC.svg'
    },
    elementOR: {
        func: joint.shapes.logic.Or, svg: 'https://upload.wikimedia.org/wikipedia/commons/4/42/OR_IEC.svg'
    },
    elementNOR: {
        func: joint.shapes.logic.Nor, svg: 'https://upload.wikimedia.org/wikipedia/commons/6/6d/NOR_IEC.svg'
    },
    elementXOR: {
        func: joint.shapes.logic.Xor, svg: 'https://upload.wikimedia.org/wikipedia/commons/4/4e/XOR_IEC.svg'
    },
    elementXNOR: {
        func: joint.shapes.logic.Xnor, svg: 'https://upload.wikimedia.org/wikipedia/commons/5/56/XNOR_IEC.svg'
    },
    elementNOT: {
        func: joint.shapes.logic.Not, svg: 'https://upload.wikimedia.org/wikipedia/commons/e/ef/NOT_IEC.svg'
    }
};

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

document.addEventListener("dragover", function (e) {
    e = e || window.event;
    const offset = $('#paper').offset();
    dragX = e.pageX - offset.left;
    dragY = e.pageY - offset.top;
}, false);

// Drag and Drop
function allowDrop(ev) {
    ev.preventDefault();
}

function drag(ev) {
    let elementName = ev.target.innerHTML;
    if (ev.target.getAttribute('data-baseform') != null) {
        elementName = ev.target.getAttribute('data-baseform');
    }
    ev.dataTransfer.setData("text", elementName);
}

function drop(ev) {
    ev.preventDefault();
    // scale: Coordinates offset by graph scale, correct with factor
    const scale = V(paper.viewport).scale();
    createElement(ev.dataTransfer.getData("text"), dragX / scale.sx, dragY / scale.sy);
}

// --> paper.on click --> sel_elementname
function setName(button) {
    sel_elementname = button.name;
    unmarkAllElementButtons();
    $(button).removeClass('btn-default').addClass('btn-primary');
}

function unmarkAllElementButtons() {
    $('#creationButtonsDiv').find('button').removeClass('btn-primary').addClass('btn-default');
}

function createElement(elementName, xCoord, yCoord) {
    let elementToAdd = new CONSTRUCTORS[elementName].func({position: {x: xCoord, y: yCoord}});
    elementToAdd.attr('image/xlink:href', CONSTRUCTORS[elementName].svg); //'https://upload.wikimedia.org/wikipedia/commons/0/0f/AND_IEC.svg');

    unmarkAllElementButtons();
    sel_elementname = "";

    graph.addCell(elementToAdd);
}

function preparePaper() {
    let leftBlock = $('#leftblock');
    const xMaxPos = leftBlock.width() / 1.65, yPos = leftBlock.height() / 2;

    let gatesToAdd = [
        {symbol: 'a', gateType: 'input', position: {x: 20, y: 20}},
        {symbol: 'b', gateType: 'input', position: {x: 20, y: yPos / 3}},
        {symbol: 'c', gateType: 'input', position: {x: 20, y: yPos * 2 / 3}},
        {symbol: 'd', gateType: 'input', position: {x: 20, y: yPos}},

        {symbol: 'y', gateType: 'output', position: {x: xMaxPos, y: yPos / 3},},
        {symbol: 'z', gateType: 'output', position: {x: xMaxPos, y: yPos * 2 / 3},}
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

function toggleLive(model, signal) {
    // add 'live' class to the element if there is a positive signal
    V(paper.findViewByModel(model).el).toggleClass('live', signal > 0);
}

function broadcastSignal(gate, signal) {
    // broadcast signal to all output ports
    toggleLive(gate, signal);
    _.defer(_.invoke, graph.getConnectedLinks(gate, {outbound: true}), 'set', 'signal', signal);
}

function initializeSignals() {
    // cancel all signals stores in wires
    _.invoke(graph.getLinks(), 'set', 'signal', 0);

    // remove all 'live' classes
    $('.live').each(function () {
        V(this).removeClass('live');
    });

    _.each(graph.getElements(), function (element) {
        if (element instanceof joint.shapes.logic.Input && graph.getConnectedLinks(element, {outbound: true}).length > 0) {
            broadcastSignal(element, SIGNALS[element.attr('logicSymbol')]);
        }
    });
}

/**
 * generating formula for given gate recursively, starting from outputs
 *
 * @return {{formula: string, success: boolean}}
 */
function getOutputFormula(gate) {
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
        let sourceInput = getOutputFormula(graph.getCell(ingoingWires[0].prop('source').id));
        success = sourceInput.success;
        if (success) {
            formula = (modificator.isNegated ? '&not; ' : '') + sourceInput.formula;
        }
    } else if (modificator.neededInputs === 2) {
        let firstInput = getOutputFormula(graph.getCell(ingoingWires[0].prop('source').id));
        let secondInput = getOutputFormula(graph.getCell(ingoingWires[1].prop('source').id));

        success = firstInput.success && secondInput.success;
        if (success) {
            formula = (modificator.isNegated ? '&not;' : '') + (firstInput.formula + ' ' + modificator.infixOperator + ' ' + secondInput.formula);
        }
    }

    return {success, formula};
}

$(document).ready(function () {
        let paperSelector = $('#paper');

        paper = new joint.dia.Paper({
            el: paperSelector,
            model: graph,

            width: paperSelector.width(),
            height: 700,

            gridSize: 10,
            drawGrid: 'dot',

            defaultLink: new joint.shapes.logic.Wire,

            snapLinks: true,
            linkPinning: false,

            validateConnection(vs, ms, vt, mt, e, vl) {
                if (e === 'target') {

                    //        target requires an input port to connect
                    if (!mt || !mt.getAttribute('class') || mt.getAttribute('class').indexOf('input') < 0) return false;

                    //  check whether the port is being already used
                    let portUsed = _.find(this.model.getLinks(), function (link) {

                        return (link.id !== vl.model.id &&
                            link.get('target').id === vt.model.id &&
                            link.get('target').port === mt.getAttribute('port'));
                    });

                    return !portUsed;

                } else { // e === 'source'
                    //     source requires an output port to connect
                    return ms && ms.getAttribute('class') && ms.getAttribute('class').indexOf('output') >= 0;
                }
            }
        });

        preparePaper();


        // Every logic gate needs to know how to handle a situation, when a signal comes to their ports.
        joint.shapes.logic.Gate.prototype.onSignal = function (signal, handler) {
            handler.call(this, signal);
        };

        // The repeater delays a signal handling by 400ms
        joint.shapes.logic.Repeater.prototype.onSignal = function (signal, handler) {
            _.delay(handler, 400, signal);
        };

        // Output element just marks itself as alive.
        joint.shapes.logic.Output.prototype.onSignal = function (signal) {
            toggleLive(this, signal);
        };


        graph.on('change:source change:target', function (model, end) {

            const e = 'target' in model.changed ? 'target' : 'source';

            if ((model.previous(e).id && !model.get(e).id) || (!model.previous(e).id && model.get(e).id)) {
                // if source/target has been connected to a port or disconnected from a port reinitialize signals
                initializeSignals();
            }
        });

        graph.on('change:signal', function (wire, signal) {

            toggleLive(wire, signal);

            const magnitude = Math.abs(signal);

            const gate = graph.getCell(wire.get('target').id);

            if (gate) {

                gate.onSignal(signal, function () {

                    // get an array of signals on all input ports
                    const inputs = _.chain(graph.getConnectedLinks(gate, {inbound: true}))
                        .groupBy(function (wire) {
                            return wire.get('target').port;
                        })
                        .map(function (wires) {
                            return Math.max.apply(this, _.invoke(wires, 'get', 'signal')) > 0;
                        })
                        .value();

                    // calculate the output signal
                    const output = magnitude * (gate.operation.apply(gate, inputs) ? 1 : -1);

                    broadcastSignal(gate, output);
                });
            }
        });

        $("#generateFormula").click(function () {
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

            _.each(parent.getEmbeddedCells(), function (child) {

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
            }, {skipParentHandler: true});
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

                    SIGNALS[logicSymbol] *= -1;
                    let newSignal = SIGNALS[logicSymbol];

                    toggleLive(cellView.model, newSignal);

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
