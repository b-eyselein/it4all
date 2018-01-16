//Basis Main
const graph = new joint.dia.Graph;
let paper;

//Basics both
let dragX; // Postion within div : X
let dragY;	// Postion within div : Y

const list_nameOfChangingElements = ['basic', 'manual_ifend', 'manual_ifstart', 'manual_loopstart', 'manual_loopendct', 'manual_loopendcf', 'unknown']; // gültige einträge beim wechseln der elementbezeichnungen --> Verwendung für paper.on link elements

// used for select element and click on paper to generate Element
let selElement;

// Parameters for Task
const parameters = {
    methodName: 'heckeSchneiden',
    startNode: {
        inputType: 'String',
        input: 'W1'
    },
    endNode: {
        outputType: 'String',
        output: 'W2'
    }
};

//vars
let MousePosElementName;
let MousePosElementID;
let parentChildNodes; // Array with all subgraphs (startid,endid,..)

const list_externPorts = ['extern', 'extern-eelse', 'extern-ethen'];

/* versions
1 = small fields, no textarea for extendable nodes
2 = expanding fields, textara always disabled for extendable nodes
3 = expanding fields, auto act/deact for textareas
*/
const version = 3;


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

// Ändern des Elementtyps Basic während des Zeichnens
function getTypeByLinks(node) {
    //Belegung der vier Ports speichern

    // TOP BOTTOM LEFT RIGHT
    //  I = IN , O = OUT , N = NOT SET
    const portDirections = ['N', 'N', 'N', 'N'];

    const outboundLinks = graph.getConnectedLinks(node, {outbound: true});
    for (let i = 0; i < outboundLinks.length; i++) {
        switch (outboundLinks[i].attributes.source.port) {
            case 'top':
                portDirections[0] = 'O';
                break;
            case 'bot':
                portDirections[1] = 'O';
                break;
            case 'left':
                portDirections[2] = 'O';
                break;
            case 'right':
                portDirections[3] = 'O';
                break;
        }
    }

    const inboundLinks = graph.getConnectedLinks(node, {inbound: true});
    for (let i = 0; i < inboundLinks.length; i++) {
        switch (inboundLinks[i].attributes.target.port) {
            case 'top':
                portDirections[0] = 'I';
                break;
            case 'bot':
                portDirections[1] = 'I';
                break;
            case 'left':
                portDirections[2] = 'I';
                break;
            case 'right':
                portDirections[3] = 'I';
                break;
        }
    }

    //Festlegung des Typs
    switch (portDirections.join()) {
        //2 cases: from top to bot, bot to top (IF)
        //top --> bot
        case 'N,O,I,I':
            return ('manual_ifend');
        case 'I,N,O,O':
            return ('manual_ifstart');
        //bot --> top
        case 'O,N,I,I':
            return ('manual_ifend');
        case 'N,I,O,O':
            return ('manual_ifstart');
        //2 cases: from top to bot, bot to top   (LOOP)
        //top --> bot
        case 'I,O,N,I':
            return ('manual_loopstart');
        case 'I,O,I,N':
            return ('manual_loopstart');
        //Loopende mit bedingung true
        case 'I,O,O,N':
            return ('manual_loopendct');
        case 'I,O,N,O':
            return ('manual_loopendcf');
        // bot --> top
        case 'O,I,N,I':
            return ('manual_loopstart');
        case 'O,I,I,N':
            return ('manual_loopstart');
        //Loopende mit bedingung true
        case 'O,I,O,N':
            return ('manual_loopendct');
        case 'O,I,N,O':
            return ('manual_loopendcf');
        default:
            return 'unknown';
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

function unMarkButtons() {
    $('#buttonDivs').find('button').removeClass('btn-primary').addClass('btn-default');
}

// --> paper.on click --> selElement
function setSelElement(anchor) {
    unMarkButtons();

    let buttonJQ = $(anchor).parent().parent().parent().find('button');

    buttonJQ.removeClass('btn-default').addClass('btn-primary');
    buttonJQ.text($(anchor).text());

    selElement = anchor.name;
}

function clearSelElement() {
    unMarkButtons();
    selElement = '';
}

// Constructor Elements
function createElement(elementName, xCoord, yCoord) {
    let ele;
    switch (elementName) {
        // case 'elementAction':
        //     ele = get_action(xCoord, yCoord);
        //     break;

        case 'elementActionInput':
            ele = createActionInput(xCoord, yCoord);
            break;

        case 'elementActionSelect':
            ele = createActionSelect(xCoord, yCoord);
            break;

        case 'elementActionDeclare':
            ele = createActionDeclare(xCoord, yCoord);
            break;

        case 'elementFor':
            ele = get_for(xCoord, yCoord);
            break;

        case 'elementDoWhile':
            ele = get_dw(xCoord, yCoord);
            break;

        case 'elementWhileDo':
            ele = get_wd(xCoord, yCoord);
            break;

        case 'elementIf':
            ele = get_if(xCoord, yCoord);
            break;

        case 'elementBasic':
            ele = get_basic(xCoord, yCoord);
            break;

        case 'elementStart':
            ele = createStartCircle('teststart', 'teststartnem', xCoord, yCoord);
            break;

        case 'elementEnde':
            ele = createEndCircle('testende', 'testende', xCoord, yCoord);
            break;
        case 'elementEdit':
            ele = get_edit(xCoord, yCoord);
            break;

        default:
            // No action taken...
            // console.log('Element nicht gefunden');
            break;
    }
    if (MousePosElementName === 'edit') {
        graph.getCell(MousePosElementID).embed(ele);
    }
    graph.addCell(ele);
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

        const functionDeclaration = parameters.methodName + '(' + parameters.startNode.inputType + ' ' + parameters.startNode.input + ')';

        let start = createStartCircle('start', 'startId', 10, 10, functionDeclaration);
        let end = createEndCircle('end', 'endId', paperJQ.width() - 100, paperJQ.height() - 100, parameters.endNode.outputType + ' ' + parameters.endNode.output);

        graph.addCells([end, start]);
        parentChildNodes.push({'parentId': 'Startknoten-startId', 'startId': 'Startknoten-startId', 'endId': 'Endknoten-endId', 'endName': 'end'});
    }

    //Basics
    paper = new joint.dia.Paper({
        el: paperJQ,
        width: paperJQ.width(),
        height: $('#leftblock').height(),
        gridSize: 15,  // distance between the dots from drawGrid
        model: graph,
        drawGrid: 'dot',  // backgrounddesign for paper --> mesh
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
        console.log('top: ' + top);
        if (top > -400) {
            top -= 100;
            paper.setOrigin(currentOrigin, top);
            rebuildGraph();
        }
    });

    $('#obot').click(function () {
        console.log('bot: ' + top);
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
        updateHighlight();
    }

    // make the value in the view visible
    function reSetSelection() {
        const allElements = graph.getElements();
        for (let i = 0; i < allElements.length; i++) {
            switch (allElements[i].attributes.name) {
                case 'actionSelect':
                    allElements[i].findView(paper).$attributes['0'].value = allElements[i].get('varContent');
                    break;
                case 'actionDeclare':
                    allElements[i].findView(paper).$attributes['0'].value = allElements[i].get('varContent1');
                    break;
            }
        }
    }

    // Aktualisierung nachdem sich ein Link ändert
    function setNameForTarget(eventName, cell) {
        try {
            if (list_nameOfChangingElements.includes(graph.getCell(arguments['0'].attributes.target.id).attributes.name)) {
                graph.getCell(arguments['0'].attributes.target.id).set('name', getTypeByLinks(getNodeById(arguments['0'].attributes.target.id)));
                console.log(graph.getCell(arguments['0'].attributes.target.id).get('name'));
            }
        } catch (e) {
        }
    }

    function setNameForSource(eventName, cell) {
        try {
            if (list_nameOfChangingElements.includes(graph.getCell(arguments['0'].attributes.source.id).attributes.name)) {
                graph.getCell(arguments['0'].attributes.source.id).set('name', getTypeByLinks(getNodeById(arguments['0'].attributes.source.id)));
                console.log(graph.getCell(arguments['0'].attributes.source.id).get('name'));
            }
        } catch (e) {
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

    //graph.on events
    graph.on('change:target', function (eventName, cell) {
        setNameForTarget(eventName, cell);
        if (version === 3) {
            forbidInputTextarea(eventName, cell);
        }
    });

    graph.on('change:source', function (eventName, cell) {
        setNameForSource(eventName, cell);
        activateTextarea(eventName, cell);
    });

    graph.on('change', function (eventName, cell) {
        try {
            if (list_nameOfChangingElements.includes(graph.getCell(arguments['0'].attributes.source.id).attributes.name)) {
                let cell = graph.getCell(arguments['0'].attributes.source.id);
                graph.getCell(arguments['0'].attributes.source.id).set('name', getTypeByLinks(cell));
                console.log(graph.getCell(arguments['0'].attributes.source.id).get('name'));
            }
        } catch (e) {
        }
        try {
            if (list_nameOfChangingElements.includes(graph.getCell(arguments['0'].attributes.target.id).attributes.name)) {
                let cell = graph.getCell(arguments['0'].attributes.target.id);
                graph.getCell(arguments['0'].attributes.target.id).set('name', getTypeByLinks(cell));
                console.log(graph.getCell(arguments['0'].attributes.target.id).get('name'));
            }
        } catch (e) {
        }
    });

    graph.on('batch:stop', function (eventName, cell) {
        if (arguments['0'].batchName === 'add-link') {
            try {
                //console.log(graph.getCell(arguments['0'].cell.id));
                if (list_nameOfChangingElements.includes(graph.getCell(arguments['0'].cell.id).attributes.name)) {
                    graph.getCell(arguments['0'].cell.id).set('name', getTypeByLinks(getNodeById(arguments['0'].cell.id)));
                    console.log(graph.getCell(arguments['0'].cell.id).get('name'));
                }
            } catch (e) {
            }
        }
    });

    graph.on('remove', function (eventName, cell) {
        setNameForSource(eventName, cell);
        setNameForTarget(eventName, cell);
        activateTextarea(eventName, cell);
    });

    function activateTextarea(eventName, cell) {
        if (arguments['0'].attributes.type === 'link' && list_externPorts.includes(arguments['0'].attributes.source.port)) {
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
        const parentBbox = parent.getBBox();
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

    // Create a custom element.
    joint.shapes.html = {};
    joint.shapes.html.Element = joint.shapes.basic.Generic.extend({
        markup: '<rect/>',
        defaults: _.defaultsDeep({
            type: 'html.Element',
            attrs: {
                rect: {
                    'ref-width': '100%',
                    'ref-height': '100%',
                    'stroke': 'dashed'
                },
                '.': {magnet: false}  // Force Port selection
            }
        }, joint.shapes.basic.Generic.prototype.defaults)
    });

    // Create a custom view for that element that displays an HTML div above it.
    joint.shapes.html.ElementView = joint.dia.ElementView.extend({

        init: function () {
            // this.set('');
            // Update the box position whenever the underlying model changes.

            this.listenTo(this.model, 'change', this.updateBox);
        },

        onBoxChange: function (evt) {
            const input = evt.target;
            const attribute = input.dataset.attribute;
            if (attribute) {
                this.model.set(attribute, input.value);
            }
            console.log(this.model);
        },

        onRender: function () {
            if (this.$box) this.$box.remove();
            const boxMarkup = joint.util.template(this.model.get('template'))();
            const $box = this.$box = $(boxMarkup);
            this.$attributes = $box.find('[data-attribute]');
            // React on all box changes. e.g. input change
            $box.on('change', _.bind(this.onBoxChange, this));
            // Update the box size and position whenever the paper transformation changes.
            // Note: there is no paper yet on `init` method.
            this.listenTo(this.paper, 'scale', this.updateBox);
            this.$box.find('.delete').on('click', _.bind(this.model.remove, this.model));
            $box.appendTo(this.paper.el);
            this.updateBox();
            return this;
        },

        updateBox: function () {
            // Set the position and the size of the box so that it covers the JointJS element
            // (taking the paper transformations into account).
            const bbox = this.getBBox({useModelGeometry: true});
            const scale = V(this.paper.viewport).scale();
            this.$box.css({
                transform: 'scale(' + scale.sx + ',' + scale.sy + ')',
                transformOrigin: '0 0',
                width: bbox.width / scale.sx,
                height: bbox.height / scale.sy,
                left: bbox.x,
                top: bbox.y
            });
            changeSize(this.model, this.$box, bbox);
            //SET: textfields if inputfield is set
            switch (this.model.attributes.name) {
                /*
                Activating True False description with input according to object
                0 : button
                1 : div with inputfield
                2 : left
                3 : right
                4 : bot
                5 : top
                */
                case 'manual_ifstart':
                    //console.log(this.$box['0'].children[1].childNodes['0']);
                    if (this.$box['0'].children[1].childNodes['0'].value.length > 0) {
                        this.$box['0'].children[2].innerText = '[TRUE]';
                        this.$box['0'].children[3].innerText = '[FALSE]';
                        this.$box['0'].children[2].classList.add('colorTrue');
                        this.$box['0'].children[3].classList.add('colorFalse');
                    } else {
                        this.$box['0'].children[2].innerText = '';
                        this.$box['0'].children[3].innerText = '';

                    }
                    this.$box['0'].children[4].innerText = '';
                    this.$box['0'].children[5].innerText = '';
                    break;

                case 'manual_loopendcf':
                    if (this.$box['0'].children[1].childNodes['0'].value.length > 0) {
                        this.$box['0'].children[3].innerText = '[FALSE]';
                        this.$box['0'].children[3].classList.add('colorFalse');
                        // Differ bot and top side generated loop
                        var con = graph.getConnectedLinks(graph.getCell(this.model.attributes.id), {outbound: true});
                        for (var i = 0; i < con.length; i++) {
                            if (con[i].attributes.source.port == 'top') {
                                this.$box['0'].children[5].innerText = '[TRUE]';
                                this.$box['0'].children[5].classList.add('colorTrue');
                                this.$box['0'].children[4].innerText = '';
                            } else if (con[i].attributes.source.port == 'bot') {
                                this.$box['0'].children[4].innerText = '[TRUE]';
                                this.$box['0'].children[4].classList.add('colorTrue');
                                this.$box['0'].children[5].innerText = '';
                            }
                        }
                    } else {
                        // deactivate
                        this.$box['0'].children[3].innerText = '';
                        this.$box['0'].children[4].innerText = '';
                        this.$box['0'].children[5].innerText = '';
                    }
                    this.$box['0'].children[2].innerText = '';
                    break;

                case 'manual_loopendct':
                    if (this.$box['0'].children[1].childNodes['0'].value.length > 0) {
                        this.$box['0'].children[2].innerText = '[FALSE]';
                        this.$box['0'].children[2].classList.add('colorFalse');
                        var con = graph.getConnectedLinks(graph.getCell(this.model.attributes.id), {outbound: true});
                        for (var i = 0; i < con.length; i++) {
                            if (con[i].attributes.source.port == 'top') {
                                this.$box['0'].children[5].innerText = '[TRUE]';
                                this.$box['0'].children[5].classList.add('colorTrue');
                                this.$box['0'].children[4].innerText = '';
                            } else if (con[i].attributes.source.port == 'bot') {
                                this.$box['0'].children[4].innerText = '[TRUE]';
                                this.$box['0'].children[4].classList.add('colorTrue');
                                this.$box['0'].children[5].innerText = '';
                            }
                        }
                    } else {

                        this.$box['0'].children[2].innerText = '';
                        this.$box['0'].children[4].innerText = '';
                        this.$box['0'].children[5].innerText = '';
                    }
                    this.$box['0'].children[3].innerText = '';
                    break;

                case 'unknown':
                    this.$box['0'].children[2].innerText = '';
                    this.$box['0'].children[3].innerText = '';
                    this.$box['0'].children[4].innerText = '';
                    this.$box['0'].children[5].innerText = '';
                    break;
            }
            this.updateAttributes();
        },

        updateAttributes: function () {
            const model = this.model;
            this.$attributes.each(function () {
                const value = model.get(this.dataset.attribute);
                switch (this.tagName.toUpperCase()) {
                    case 'LABEL':
                        this.textContent = value;
                        break;
                    case 'INPUT':
                        this.value = value;
                        //console.log(model.attributes.template);
                        break;
                    case 'TEXTAREA':
                        this.value = value;
                        break;
                }
            });
        },

        onRemove: function () {
            this.$box.find('.delete').on('click', _.bind(this.model.remove, this.model));
            this.model.on('remove', this.removeBox, this);
            this.$box.remove();
            //console.log(this.model);  // remove entries  parentChildNodes
            removeIdFromArray(this.model.id);
        }
    });

    function changeSize(model, box, bbox) {
        if (model.attributes.name === 'actionInput') {
            var nheight = box['0'].children[1].style.height;
            nheight = Number(nheight.replace('px', ''));
            if (nheight > 15) {
                model.resize(bbox.width, nheight + 15);
                model.prop('ports/items/1/args/y', (nheight + 15));
            }
        }
        if (version !== '1') {
            if (model.attributes.name === 'forin') {
                var nheight = box['0'].children[2].style.height;
                nheight = Number(nheight.replace('px', ''));
                if (nheight > 50) {
                    model.resize(bbox.width, nheight + 35);
                    model.prop('ports/items/2/args/y', (nheight + 35));
                }
            }
            if (model.attributes.name === 'dw') {
                var nheight = box['0'].children[2].style.height;
                nheight = Number(nheight.replace('px', ''));
                if (nheight > 50) {
                    model.resize(bbox.width, nheight + 50);
                    model.prop('ports/items/1/args/y', (nheight + 50));
                }
            }
            if (model.attributes.name === 'wd') {
                var nheight = box['0'].children[4].style.height;
                nheight = Number(nheight.replace('px', ''));
                if (nheight > 50) {
                    model.resize(bbox.width, nheight + 52);
                    model.prop('ports/items/1/args/y', (nheight + 52));
                }
            }
            if (model.attributes.name === 'if') {
                var nheight = box['0'].children[2].children[1].style.height;
                let nheight2 = box['0'].children[3].children[1].style.height;
                nheight = Number(nheight.replace('px', ''));
                nheight2 = Number(nheight2.replace('px', ''));
                if (nheight + nheight2 > 100) {
                    nheight = Math.max(nheight, 75);
                    nheight2 = Math.max(nheight2, 75);
                    model.resize(bbox.width, nheight + nheight2 + 130);
                    model.prop('ports/items/2/args/y', (nheight));
                    model.prop('ports/items/3/args/y', (nheight + nheight2 + 25));
                    model.prop('ports/items/1/args/y', (nheight + nheight2 + 130));
                }
            }
        }
    }


//paperevents
    paper.on('blank:pointerclick', function (evt, x, y) {
        try {
            if (selElement !== '') {
                createElement(selElement, x, y);
                clearSelElement();
            }
        } catch (e) {
        }
    });

    paper.on('cell:mouseenter', function (cellView, evt, x, y) {
        MousePosElementID = cellView.model.id;
        MousePosElementName = cellView.model.attributes.name;
        //	console.log(MousePosElementID);
        //	console.log(MousePosElementName);
    });

    paper.on('cell:mouseleave', function (cellView, evt, x, y) {
        MousePosElementID = 'mainId';
        MousePosElementName = 'main';
    });

    paper.on('cell:pointerclick', function (cellView, evt, x, y) {
        if (cellView.model.attributes.name === 'edit' && selElement !== '') {
            createElement(selElement, x, y);
            selElement = '';
        }
    });

    preparePaper(); // set start and endnode
});

function refreshElement(el) {
    const x = el.get('position').x;
    const y = el.get('position').y;
    el.set('position', {x: x + 1, y: y + 1});
    el.set('position', {x: x, y: y});
}

//calculate each elements height and sum up --_> Fail
function calculateFinalHeight(htmlCollection, finalHeightValue) {
    for (let i = 0; i < htmlCollection.length; i++) {
        switch (htmlCollection[i].nodeName) {
            case 'INPUT':
            case 'TEXTAREA':
                finalHeightValue += Number((htmlCollection[i].style.height).replace('px', ''));
                break;
        }
        if (htmlCollection[i].nodeName === 'DIV') {
            finalHeightValue += calculateFinalHeight(htmlCollection[i].childNodes, finalHeightValue);
        }
    }
    return finalHeightValue;
}