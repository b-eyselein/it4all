//Basis Main
const graph = new joint.dia.Graph;
let paper;

//Basics both
let dragX; // Postion within div : X
let dragY;	// Postion within div : Y

// used for select element and click on paper to generate Element
let selElement;

//vars
let MousePosElementName;
let MousePosElementID;
let parentChildNodes; // Array with all subgraphs (startid,endid,..)

const list_externPorts = ['extern', 'extern-eelse', 'extern-ethen'];
const list_addEditNodesByCreateName = ["elementFor", "elementDoWhile", "elementWhileDo", "elementIf", "elementIfThen"];

let connectProperties = {
    sourceId: "sourceId",
    targetId: "targetId",
    sourcePort: "sourcePort",
    targetPort: "targetPort"
};

//force refresh by function
function refreshDia() {
    document.getElementById('zoomplus').click();
    document.getElementById('zoomminus').click();
}

function removeIdFromArray(id) {
    for (let i = 0; i < parentChildNodes.length; i++) {
        if (parentChildNodes[i].parentId === id) {
            parentChildNodes.splice(i, 1);
        }
    }
}


//Define MousePos  within the different papers
document.addEventListener('dragover', function (e) {
    e = e || window.event;
    let offset;
    if ($('#editDiagramModal').hasClass('in')) {
        offset = $('#activitydiagram').offset();
    } else {
        offset = $('#paper').offset();
    }
    dragX = e.pageX - offset.left;
    dragY = e.pageY - offset.top;
}, false);

const notSelectedElemButtonClass = 'btn-warning';
const selectedElemButtonClass = 'btn-primary';

// --> paper.on click --> selElement
function setSelElement(anchor) {
    let anchorJQ = $(anchor);

    let elemToSelect = anchorJQ.data('elemname');

    if (selElement === elemToSelect) {
        // Element was already selected
        clearSelElement();
    } else {
        anchorJQ.siblings().removeClass(selectedElemButtonClass).addClass(notSelectedElemButtonClass);
        anchorJQ.removeClass(notSelectedElemButtonClass).addClass(selectedElemButtonClass);
        selElement = elemToSelect;
    }
}

function clearSelElement() {
    // Unmark all buttons
    $('#buttonsDiv').find('a').removeClass(selectedElemButtonClass).addClass(notSelectedElemButtonClass);
    selElement = '';
}

// Constructor Elements
function createElement(elementName, xCoord, yCoord) {
    let position = {
        position: {x: xCoord, y: yCoord}
    };

    let elementToAdd;
    switch (elementName) {
        case 'elementActionInput':
            elementToAdd = new joint.shapes.uml.ActionInput(position); //createActionInput(xCoord, yCoord);
            break;

        case 'elementActionSelect':
            elementToAdd = createActionSelect(xCoord, yCoord);
            break;

        case 'elementActionDeclare':
            elementToAdd = createActionDeclare(xCoord, yCoord);
            break;

        case 'elementFor':
            elementToAdd = createForLoop(xCoord, yCoord);
            break;

        case 'elementDoWhile':
            elementToAdd = createDoWhile(xCoord, yCoord);
            break;

        case 'elementWhileDo':
            elementToAdd = createWhileDo(xCoord, yCoord);
            break;

        case 'elementIfThen':
            elementToAdd = createIfThen(xCoord, yCoord);
            break;

        case 'elementIf':
            elementToAdd = createIfElse(xCoord, yCoord);
            break;

        case 'elementEdit':
            elementToAdd = createEdit(xCoord, yCoord);
            break;

        case 'uml.ForLoop':
            elementToAdd = new joint.shapes.uml.ForLoop(position);
            break;

        case 'uml.WhileLoop':
            console.log(elementName);
            try {
                elementToAdd = new joint.shapes.uml.WhileLoop(position);
            } catch (err) {
                console.error(err);
            }

            console.warn(elementToAdd);
            break;

        default:
            // No action taken...
            console.log('Could not create element ' + elementName);
            break;
    }
    if (MousePosElementName === 'edit') {
        graph.getCell(MousePosElementID).embed(elementToAdd);
    }
    graph.addCell(elementToAdd);
}

function textAreaAdjust(o) {
    o.style.height = '1px';
    o.style.height = (25 + o.scrollHeight) + 'px';
}

// JOINTJS
$(document).ready(function () {

    let paperJQ = $('#paper');

    autosize(document.querySelectorAll('textarea'));
    parentChildNodes = [];

    //SET start- and endNode
    function preparePaper() {
        let start = createStartCircle('start', 'startId', GRID_SIZE, GRID_SIZE, EXERCISE_PARAMETERS.methodDisplay);
        let end = createEndCircle('end', 'endId', paperJQ.width() - (START_END_SIZE + GRID_SIZE), paperJQ.height() - (START_END_SIZE + GRID_SIZE),
            EXERCISE_PARAMETERS.output.outputType + ' ' + EXERCISE_PARAMETERS.output.output);

        let actionNodeStart = new joint.shapes.uml.ActionInput({
            position: {x: 100, y: 100},
            content: 'solution = ' + EXERCISE_PARAMETERS.output.defaultValue
        });

        let actionNodeEnd = new joint.shapes.uml.ActionInput({
            position: {
                x: paperJQ.width() - 300, y: paperJQ.height() - 150
            }, content: 'return solution'
        });


        graph.addCells([end, start, actionNodeStart, actionNodeEnd]);

        connectNodes(start.id, actionNodeStart.id, "out", "in");
        connectNodes(actionNodeEnd.id, end.id, "out", "in");

        parentChildNodes.push({
            'parentId': 'Startknoten-startId',
            'startId': 'Startknoten-startId',
            'endId': 'Endknoten-endId',
            'endName': 'end'
        });
    }

    //Basics
    paper = new joint.dia.Paper({
        el: paperJQ,
        model: graph,

        width: paperJQ.width(),
        height: PAPER_HEIGHT,

        gridSize: GRID_SIZE,
        drawGrid: DEF_GRID,  // backgrounddesign for paper --> mesh

        defaultLink: new joint.dia.Link({
            router: {name: 'manhattan'},  // Link design for horizontal and vertical lines
            connector: {name: 'normal'},
            attrs: {'.marker-target': {d: 'M 10 0 L 0 5 L 10 10 z'}} // Arrow is horizentor or vertical
        }),

        snapLinks: {radius: 25},//Snaps links to port inbetween the radius
        linkPinning: false, // dropping links on paper fail
        setLinkVertices: true
    });


//paper expand buttons
    let ph = paperJQ.height(); // maximal decreasing to loaded size of paper

    let currentZoom = 1.0;
    let currentOrigin = 0;
    let top = 0;

    const save_ph = ph;
    $('#paperplus').click(function () {
        if (ph <= 2000) {
            ph += 100;
            paper.setDimensions(0, ph);
            paper.setDimensions(0, ph);
        }
    });

    $('#paperminus').click(function () {
        if (ph > save_ph) {
            ph -= 100;
            paper.setDimensions(0, ph);
            paper.setDimensions(0, ph);
        }
    });

    // zoom buttons
    $('#zoomplus').click(function () {
        if (currentZoom < 1.5) {
            currentZoom += 0.1;
            paper.scale(currentZoom, currentZoom);
        }
    });

    $('#zoomminus').click(function () {
        if (currentZoom > 0.7) {
            currentZoom -= 0.1;
            paper.scale(currentZoom, currentZoom);
        }
    });

    // origin
    $('#oright').click(function () {
        if (currentOrigin < 400) {
            currentOrigin += 100;
            paper.setOrigin(currentOrigin, top);
            rebuildGraph();
        }
    });

    $('#oleft').click(function () {
        if (currentOrigin > -400) {
            currentOrigin -= 100;
            paper.setOrigin(currentOrigin, top);
            rebuildGraph();
        }
    });

    $('#otop').click(function () {
        if (top > -400) {
            top -= 100;
            paper.setOrigin(currentOrigin, top);
            rebuildGraph();
        }
    });

    $('#obot').click(function () {
        if (top < 400) {
            top += 100;
            paper.setOrigin(currentOrigin, top);
            rebuildGraph();
        }
    });

    // view reset
    $('#viewreset').click(function () {
        paper.scale(1.0, 1.0);
        paper.setOrigin(0, 0);
        paper.setDimensions(paperJQ.width(), paperJQ.height());
        rebuildGraph();
    });

    // rebuild graph
    function rebuildGraph() {
        localStorage.setItem('parentChildNodes', JSON.stringify(parentChildNodes));
        graph.fromJSON(graph.toJSON());
        parentChildNodes = JSON.parse(localStorage.getItem('parentChildNodes'));

        reSetSelection();
        refreshDia();
        updateHighlight(graph.getElements(), highlightedCells);
    }

    // make the value in the view visible
    function reSetSelection() {
        const allElements = graph.getElements();
        for (let i = 0; i < allElements.length; i++) {
            switch (allElements[i].attributes.name) {
                case 'actionSelect':
                    allElements[i].findView(paper).$attributes['0'].value = allElements[i].get('actionElementContent');
                    break;
                case 'actionDeclare':
                    allElements[i].findView(paper).$attributes['0'].value = allElements[i].get('varContent1');
                    break;
            }
        }
    }

    function forbidInputTextarea(eventName, cell) {
        try {
            const cellname = graph.getCell(cell.id).attributes.name;
            if (cellname === 'edit') {
                console.log(eventName);
                console.log(cell);
                const parentCell = graph.getCell(eventName.attributes.source.id);
                const parentPort = eventName.attributes.source.port;
                if (list_externPorts.includes(parentPort)) {
                    const parentView = parentCell.findView(paper);
                    if (parentCell.attributes.name === 'if') {
                        const testString = parentPort.substring(7, parentPort.length);
                        for (let i = 0; i < parentView.$attributes.length; i++) {
                            if (parentView.$attributes[i].dataset.attribute === testString) {
                                parentView.$attributes[i].setAttributeNode(document.createAttribute('disabled'));
                            }
                        }
                    } else {
                        for (let j = 0; j < parentView.$box['0'].children.length; j++) {
                            console.log(parentView.$box['0'].children[j].nodeName);
                            if (parentView.$box['0'].children[j].nodeName === 'TEXTAREA') {
                                parentView.$box['0'].children[j].setAttributeNode(document.createAttribute('disabled'));
                            }
                        }
                    }
                }
            }
        } catch (e) {
        }
    }

    graph.on('change', function () {
        $('#generationAlerts').html(`<div class="alert alert-warning">Ihr Diagramm hat sich geändert. Bitte generieren Sie ihren Code neu!</div>`);
        $('#mainGeneration').removeClass('btn-default').addClass('btn-primary');
        // $('#preCode').html('');
    });

    // graph.on events
    graph.on('change:target', function (eventName, cell) {
        forbidInputTextarea(eventName, cell);
    });

    graph.on('change:source', function (eventName, cell) {
        activateTextarea(eventName, cell);
    });

    graph.on('remove', function (eventName, cell) {
        activateTextarea(eventName, cell);
    });

    function activateTextarea(eventName, cell) {
        if ((arguments["0"].attributes.type === "link" || arguments["0"].attributes.type === "devs.Link") && list_externPorts.includes(arguments["0"].attributes.source.port)) {
            const sourceCell = graph.getCell(arguments['0'].attributes.source.id);
            const parentView = sourceCell.findView(paper);
            const parentPort = eventName.attributes.source.port;
            if (sourceCell.attributes.name === 'if') {
                const testString = parentPort.substring(7, parentPort.length);
                for (let i = 0; i < parentView.$attributes.length; i++) {
                    if (parentView.$attributes[i].dataset.attribute === testString) {
                        parentView.$attributes[i].removeAttribute('disabled');
                    }
                }
            } else {
                for (let j = 0; j < parentView.$box['0'].children.length; j++) {
                    if (parentView.$box['0'].children[j].nodeName === 'TEXTAREA') {
                        parentView.$box['0'].children[j].removeAttribute('disabled');
                    }
                }
            }
        }
    }

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
        // const parentBbox = parent.getBBox();
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


    function connectNodes(sourceId, targetId, sourcePort, targetPort) {
        let link = new joint.shapes.devs.Link({
            source: {
                id: sourceId,
                port: sourcePort
            },
            target: {
                id: targetId,
                port: targetPort
            },
            router: {name: 'manhattan'},  // Link design for horizontal and vertical lines
            connector: {name: 'normal'},
            attrs: {'.marker-target': {d: 'M 10 0 L 0 5 L 10 10 z'}} // Arrow is horizontal or vertical
        });
        graph.addCell(link);
    }


    // paperevents
    paper.on('blank:pointerclick', function (evt, x, y) {
        try {
            if (selElement !== '') {
                connectProperties = []; // reset properties from older ones
                createElement(selElement, x, y);

                if (list_addEditNodesByCreateName.includes(selElement)) {
                    createElement("elementEdit", x + 350, y);
                    connectNodes(connectProperties.sourceId, connectProperties.targetId, connectProperties.sourcePort, connectProperties.targetPort);
                    if (selElement === "elementIf") {
                        createElement("elementEdit", x + 350, y + 150);
                        connectNodes(connectProperties.sourceId, connectProperties.targetId, connectProperties.sourcePort2, connectProperties.targetPort);
                    }
                }

                clearSelElement();
            }
        } catch (e) {
        }
    });

    paper.on('cell:mouseenter', function (cellView) {
        MousePosElementID = cellView.model.id;
        MousePosElementName = cellView.model.attributes.name;
    });

    paper.on('cell:mouseleave', function () {
        MousePosElementID = 'mainId';
        MousePosElementName = 'main';
    });

    paper.on('cell:pointerclick', function (cellView, evt, x, y) {
        let type = cellView.model.get('type');
        switch (type) {
            case  'uml.ForLoop':
                cellView.handleLeftClick(evt, x, y);
                break;
            case 'uml.ActionInput':
                cellView.handleLeftClick(evt, x, y);
                break;
            default:
                console.warn(type);
                break;
        }

        if (cellView.model.attributes.name === 'edit' && selElement !== '') {
            connectProperties = []; // reset properties from older ones
            createElement(selElement, x, y);
            if (list_addEditNodesByCreateName.includes(selElement)) {
                createElement("elementEdit", x + 350, y);
                connectNodes(connectProperties.sourceId, connectProperties.targetId, connectProperties.sourcePort, connectProperties.targetPort);
                console.log(graph.getCell(connectProperties.sourceId));
                if (selElement === "elementIf") {
                    createElement("elementEdit", x + 350, y + 150);
                    connectNodes(connectProperties.sourceId, connectProperties.targetId, connectProperties.sourcePort2, connectProperties.targetPort);
                }
            }

            clearSelElement();
        }
    });

    paper.on('cell:contextmenu', function (cellView, evt, x, y) {
        cellView.handleRightClick(evt, x, y);
    });

    preparePaper(); // set start and endnode
});

function refreshElement(el) {
    const x = el.get('position').x;
    const y = el.get('position').y;
    el.set('position', {x: x + 1, y: y + 1});
    el.set('position', {x: x, y: y});
}
