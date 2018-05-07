const graph = new joint.dia.Graph;
let paper;
let actionInputEditor;
let forLoopEditor;
let mainStartNode;
let mainEndNode;
let dragX;
let dragY;
let selElement;
let MousePosElementName;
let MousePosElementID;
let parentChildNodes;
const list_externPorts = ['extern', 'extern-eelse', 'extern-ethen'];
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
document.addEventListener('dragover', function (e) {
    e = e || window.event;
    let offset;
    if ($('#editDiagramModal').hasClass('in')) {
        offset = $('#activitydiagram').offset();
    }
    else {
        offset = $('#paper').offset();
    }
    dragX = e.pageX - offset.left;
    dragY = e.pageY - offset.top;
}, false);
const notSelectedElemButtonClass = 'btn-warning';
const selectedElemButtonClass = 'btn-primary';
function setSelElement(anchor) {
    let anchorJQ = $(anchor);
    let elemToSelect = anchorJQ.data('elemname');
    if (selElement === elemToSelect) {
        clearSelElement();
    }
    else {
        anchorJQ.siblings().removeClass(selectedElemButtonClass).addClass(notSelectedElemButtonClass);
        anchorJQ.removeClass(notSelectedElemButtonClass).addClass(selectedElemButtonClass);
        selElement = elemToSelect;
    }
}
function clearSelElement() {
    $('#buttonsDiv').find('a').removeClass(selectedElemButtonClass).addClass(notSelectedElemButtonClass);
    selElement = '';
}
function createElementsAndConnections(elementName, position) {
    let elementsToAdd = [];
    let connectionsToCreate = [];
    switch (elementName) {
        case 'elementActionInput':
            elementsToAdd.push(new joint.shapes.uml.ActionInput(position));
            break;
        case 'elementActionSelect':
            elementsToAdd.push(createActionSelect(position.position.x, position.position.y));
            break;
        case 'elementActionDeclare':
            elementsToAdd.push(createActionDeclare(position.position.x, position.position.y));
            break;
        case 'elementFor':
            let forElement = createForLoop(position.position.x, position.position.y);
            let forEditElement = createEdit(position.position.x + 350, position.position.y);
            elementsToAdd.push(forElement, forEditElement);
            connectionsToCreate.push({
                sourceId: forElement.id, sourcePort: 'extern',
                targetId: forEditElement.id, targetPort: 'extern'
            });
            break;
        case 'elementDoWhile':
            let doWhile = createDoWhile(position.position.x, position.position.y);
            let doWhileEditElement = createEdit(position.position.x + 350, position.position.y);
            elementsToAdd.push([doWhile, doWhileEditElement]);
            connectionsToCreate.push({
                sourceId: doWhile.id, sourcePort: 'extern',
                targetId: doWhileEditElement.id, targetPort: 'extern'
            });
            break;
        case 'elementWhileDo':
            let whileDoElement = createWhileDo(position.position.x, position.position.y);
            let whileDoEditElement = createEdit(position.position.x + 350, position.position.y);
            elementsToAdd.push([whileDoElement, whileDoEditElement]);
            connectionsToCreate.push({
                sourceId: whileDoElement, sourcePort: 'extern',
                targetId: whileDoEditElement, targetPort: 'extern'
            });
            break;
        case 'elementIfThen':
            let ifThenElement = createIfThen(position.position.x, position.position.y);
            let ifThenEditElement = createEdit(position.position.x + 350, position.position.y);
            elementsToAdd.push([ifThenElement, ifThenEditElement]);
            connectionsToCreate.push({
                sourceId: ifThenElement, sourcePort: 'extern',
                targetId: ifThenEditElement, targetPort: 'extern'
            });
            break;
        case 'elementIf':
            let ifElseElement = createIfElse(position.position.x, position.position.y);
            let ifEditElement = createEdit(position.position.x + 350, position.position.y);
            let elseEditElement = createEdit(position.position.x + 350, position.position.y + 150);
            elementsToAdd.push([ifElseElement, ifEditElement, elseEditElement]);
            connectionsToCreate.push({
                sourceId: ifElseElement, sourcePort: 'extern-ethen',
                targetId: ifEditElement, targetPort: 'extern'
            }, {
                sourceId: ifElseElement.id, sourcePort: 'extern-eelse',
                targetId: elseEditElement.id, targetPort: 'extern'
            });
            break;
        case 'elementEdit':
            elementsToAdd.push(createEdit(position.position.x, position.position.y));
            break;
        case 'uml.ForLoopText':
            elementsToAdd.push(new joint.shapes.uml.ForLoopText(position));
            break;
        case 'uml.ForLoopEmbed':
            elementsToAdd.push(new joint.shapes.uml.ForLoopEmbed(position));
            break;
        case 'uml.WhileLoop':
            elementsToAdd.push(new joint.shapes.uml.WhileLoop(position));
            break;
        default:
            console.log('Could not create element ' + elementName);
            break;
    }
    return { elementsToAdd, connectionsToCreate };
}
function createElements(elementName, positionObject, embedInto) {
    let elementsAndConnections = createElementsAndConnections(elementName, positionObject);
    if (embedInto) {
        for (let elToAdd of elementsAndConnections.elementsToAdd) {
            embedInto.embed(elToAdd);
        }
    }
    graph.addCells(elementsAndConnections.elementsToAdd);
    for (let connToCreate of elementsAndConnections.connectionsToCreate) {
        connectNodes(connToCreate);
    }
    clearSelElement();
}
function textAreaAdjust(o) {
    o.style.height = '1px';
    o.style.height = (25 + o.scrollHeight) + 'px';
}
function connectNodes(connectProperties) {
    graph.addCell(new joint.shapes.devs.Link({
        source: {
            id: connectProperties.sourceId,
            port: connectProperties.sourcePort
        },
        target: {
            id: connectProperties.targetId,
            port: connectProperties.targetPort
        },
        router: { name: 'manhattan' },
        connector: { name: 'normal' },
        attrs: { '.marker-target': { d: 'M 10 0 L 0 5 L 10 10 z' } }
    }));
}
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
        }
        else {
            for (let j = 0; j < parentView.$box['0'].children.length; j++) {
                if (parentView.$box['0'].children[j].nodeName === 'TEXTAREA') {
                    parentView.$box['0'].children[j].removeAttribute('disabled');
                }
            }
        }
    }
}
function rebuildGraph() {
    localStorage.setItem('parentChildNodes', JSON.stringify(parentChildNodes));
    graph.fromJSON(graph.toJSON());
    parentChildNodes = JSON.parse(localStorage.getItem('parentChildNodes'));
    reSetSelection();
    refreshDia();
}
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
            if (list_externPorts.indexOf(parentPort) > -1) {
                const parentView = parentCell.findView(paper);
                if (parentCell.attributes.name === 'if') {
                    const testString = parentPort.substring(7, parentPort.length);
                    for (let i = 0; i < parentView.$attributes.length; i++) {
                        if (parentView.$attributes[i].dataset.attribute === testString) {
                            parentView.$attributes[i].setAttributeNode(document.createAttribute('disabled'));
                        }
                    }
                }
                else {
                    for (let j = 0; j < parentView.$box['0'].children.length; j++) {
                        console.log(parentView.$box['0'].children[j].nodeName);
                        if (parentView.$box['0'].children[j].nodeName === 'TEXTAREA') {
                            parentView.$box['0'].children[j].setAttributeNode(document.createAttribute('disabled'));
                        }
                    }
                }
            }
        }
    }
    catch (e) {
    }
}
function initAceEditor(elementId) {
    $('#' + elementId).css({ 'fontSize': 16 + 'px' });
    let newEditor = ace.edit(elementId);
    newEditor.setTheme('ace/theme/eclipse');
    newEditor.getSession().setMode('ace/mode/python');
    newEditor.getSession().setTabSize(2);
    newEditor.getSession().setUseSoftTabs(true);
    newEditor.getSession().setUseWrapMode(true);
    newEditor.setOptions({ minLines: 5, maxLines: 10 });
    return newEditor;
}
$(document).ready(function () {
    actionInputEditor = initAceEditor('actionInputEditor');
    forLoopEditor = initAceEditor('forLoopEditor');
    let paperJQ = $('#paper');
    autosize(document.querySelectorAll('textarea'));
    parentChildNodes = [];
    function preparePaper() {
        mainStartNode = createStartState(GRID_SIZE, GRID_SIZE);
        mainEndNode = createEndState(paperJQ.width() - (START_END_SIZE + GRID_SIZE), paperJQ.height() - (START_END_SIZE + GRID_SIZE));
        let actionNodeStart = new joint.shapes.uml.ActionInput({
            position: { x: 100, y: 100 }, content: 'solution = ' + EXERCISE_PARAMETERS.output.defaultValue
        });
        let actionNodeEnd = new joint.shapes.uml.ActionInput({
            position: { x: paperJQ.width() - 300, y: paperJQ.height() - 150 }, content: 'return solution'
        });
        graph.addCells([mainStartNode, mainEndNode, actionNodeStart, actionNodeEnd]);
        connectNodes({ sourceId: mainStartNode.id, targetId: actionNodeStart.id, sourcePort: "out", targetPort: "in" });
        connectNodes({ sourceId: actionNodeEnd.id, targetId: mainEndNode.id, sourcePort: "out", targetPort: "in" });
        parentChildNodes.push({
            parentId: 'Startknoten-startId', startId: 'Startknoten-startId', endId: 'Endknoten-endId', endName: 'end'
        });
    }
    paper = new joint.dia.Paper({
        el: paperJQ, model: graph,
        width: paperJQ.width(), height: PAPER_HEIGHT,
        gridSize: GRID_SIZE, drawGrid: DEF_GRID,
        defaultLink: new joint.dia.Link({
            router: { name: 'manhattan' },
            connector: { name: 'normal' },
            attrs: { '.marker-target': { d: 'M 10 0 L 0 5 L 10 10 z' } }
        }),
        snapLinks: { radius: 25 },
        linkPinning: false,
        setLinkVertices: true
    });
    let ph = paperJQ.height();
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
    $('#viewreset').click(function () {
        paper.scale(1.0, 1.0);
        paper.setOrigin(0, 0);
        paper.setDimensions(paperJQ.width(), paperJQ.height());
        rebuildGraph();
    });
    graph.on('change', function () {
        $('#generationAlerts').html(`<div class="alert alert-warning">Ihr Diagramm hat sich geändert. Bitte generieren Sie ihren Code neu!</div>`);
        $('#mainGeneration').removeClass('btn-default').addClass('btn-primary');
    });
    graph.on('change:target', function (eventName, cell) {
        forbidInputTextarea(eventName, cell);
    });
    graph.on('change:source', function (eventName, cell) {
        activateTextarea(eventName, cell);
    });
    graph.on('remove', function (eventName, cell) {
        activateTextarea(eventName, cell);
    });
    graph.on('change:position', function (cell, newPosition, opt) {
        if (opt.skipParentHandler)
            return;
        if (cell.get('embeds') && cell.get('embeds').length) {
            cell.set('originalPosition', cell.get('position'));
        }
        let parentId = cell.get('parent');
        if (!parentId)
            return;
        const parent = graph.getCell(parentId);
        if (!parent.get('originalPosition'))
            parent.set('originalPosition', parent.get('position'));
        if (!parent.get('originalSize'))
            parent.set('originalSize', parent.get('size'));
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
        parent.set({
            position: { x: newX, y: newY },
            size: { width: newCornerX - newX, height: newCornerY - newY }
        }, { skipParentHandler: true });
    });
    graph.on('change:size', function (cell, newPosition, opt) {
        if (opt.skipParentHandler)
            return;
        if (cell.get('embeds') && cell.get('embeds').length) {
            cell.set('originalSize', cell.get('size'));
        }
    });
    paper.on('blank:pointerclick', function (evt, x, y) {
        if (selElement !== '') {
            createElements(selElement, { position: { x, y } });
            clearSelElement();
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
    preparePaper();
});
function refreshElement(el) {
    const x = el.get('position').x;
    const y = el.get('position').y;
    el.set('position', { x: x + 1, y: y + 1 });
    el.set('position', { x: x, y: y });
}
//# sourceMappingURL=umlActivityDrawing.js.map