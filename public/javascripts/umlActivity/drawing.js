//Basis Main
	var graph;
	var paper;
//Basics both	
	var dragX; // Postion within div : X
	var dragY;	// Postion within div : Y
	var list_nameOfChangingElements = ["basic", "manual_ifend", "manual_ifstart", "manual_loopstart", "manual_loopendct", "manual_loopendcf", "unknown"]; // gültige einträge beim wechseln der elementbezeichnungen --> Verwendung für paper.on link elements
	var sel_elementname; // used for select element and click on paper to generate Element
//Basic zum Erfassen aller Elemente im Drawing --> wird für Elementbestimmung schon hier definiert
	var jsongraph;
	var graphcopy;
	var list_connections;
	const paperH = $('#leftblock').height(); // paper as tall as the left div next to him --> size for fullscreen
// Parameters for Task
	var startnode_inputtype = "String";
	var startnode_input = "W1";
	var endnode_outputtype = "String";
	var endnode_output = "W2";
	var methodname = "heckeSchneiden";	
	//vars
	var MousePosElementName;
	var MousePosElementID;	
	var parentChildNodes; // Array with all subgraphs (startid,endid,..)
	const list_externPorts=["extern","extern-eelse","extern-ethen"];		
	/* versions
	1 = small fields, no textarea for extendable nodes
	2 = expanding fields, textara always disabled for extendable nodes
	3 = expanding fields, auto act/deact for textareas
	*/
	var version = "3";



	
//force refresh by function
function refreshDia() {
    document.getElementById("zoomplus").click();
    document.getElementById("zoomminus").click();
}

function removeIdFromArray(id){
	for(var i = 0; i < parentChildNodes.length; i++){
		if(parentChildNodes[i].parentId === id){
			parentChildNodes.splice(i, 1);
		}
	}
}

function setOptionsForModal(buttonelement){
	currentDiagramInModalID = MousePosElementID;
	currentElementNameForModal = buttonelement.name;
	console.log("currentID: "+currentDiagramInModalID+" name: "+currentElementNameForModal);
}

// Ändern des Elementtyps Basic während des Zeichnens
function getTypeByLinks(node) {
    //Belegung der vier Ports speichern
    // TOP BOTTOM LEFT RIGHT
    //  I = IN , O = OUT , N = NOT SET
    var portdirction = ['N', 'N', 'N', 'N'];
    var outboundLinks = graph.getConnectedLinks(node, {outbound: true});
    for (i = 0; i < outboundLinks.length; i++) {
        switch (outboundLinks[i].attributes.source.port) {
            case "top":
                portdirction[0] = "O";
                break;
            case "bot":
                portdirction[1] = "O";
                break;
            case "left":
                portdirction[2] = "O";
                break;
            case "right":
                portdirction[3] = "O";
                break;
        }
    }
    var inboundLinks = graph.getConnectedLinks(node, {inbound: true});
    for (i = 0; i < inboundLinks.length; i++) {
        switch (inboundLinks[i].attributes.target.port) {
            case "top":
                portdirction[0] = "I";
                break;
            case "bot":
                portdirction[1] = "I";
                break;
            case "left":
                portdirction[2] = "I";
                break;
            case "right":
                portdirction[3] = "I";
                break;
        }
    }
    switch (portdirction.join()) {
        //Festlegung des Typs
        //2 cases: from top to bot, bot to top (IF)
        //top --> bot
        case "N,O,I,I":
            return ("manual_ifend");
            break;
        case "I,N,O,O":
            return ("manual_ifstart");
            break;
        //bot --> top
        case "O,N,I,I":
            return ("manual_ifend");
            break;
        case "N,I,O,O":
            return ("manual_ifstart");
            break;
        //2 cases: from top to bot, bot to top   (LOOP)
        //top --> bot
        case "I,O,N,I":
            return ("manual_loopstart");
            break;
        case "I,O,I,N":
            return ("manual_loopstart");
            break;
        //Loopende mit bedingung true
        case "I,O,O,N":
            return ("manual_loopendct");
            break;
        case "I,O,N,O":
            return ("manual_loopendcf");
            break;
        // bot --> top
        case "O,I,N,I":
            return ("manual_loopstart");
            break;
        case "O,I,I,N":
            return ("manual_loopstart");
            break;
        //Loopende mit bedingung true
        case "O,I,O,N":
            return ("manual_loopendct");
            break;
        case "O,I,N,O":
            return ("manual_loopendcf");
            break;
        default:
            return "unknown";
    }
}

//Define MousePos  within the different papers 
document.addEventListener("dragover", function (e) {
	e = e || window.event;
	if($('#editDiagramModal').hasClass('in')){
		var offset = $('#activitydiagram').offset();
	}else{
		var offset = $('#paper').offset();
	}
    dragX = e.pageX - offset.left;
    dragY = e.pageY - offset.top;
}, false);

// --> paper.on click --> sel_elementname
function setName(name){
	sel_elementname = name;
}

// Constructor Elements
function createElement(elementName, xCoord, yCoord) {
    switch (elementName) {
        case "elementAction":
            ele = get_action(xCoord,yCoord);
            break;

        case "elementActionInput":
            ele = get_actionInput(xCoord,yCoord);
            break;
			
		case "elementActionSelect":
            ele = get_actionSelect(xCoord,yCoord);
            break;

		case "elementActionDeclare":
            ele = get_actionDeclare(xCoord,yCoord);
            break;			
			
        case "elementFor":
            ele = get_for(xCoord,yCoord);
            break;

        case "elementDoWhile":
            ele = get_dw(xCoord,yCoord);			
            break;

        case "elementWhileDo":
            ele = get_wd(xCoord,yCoord);			
            break;

        case "elementIf":
            ele = get_if(xCoord,yCoord);
            break;

        case "elementBasic":
            ele = get_basic(xCoord,yCoord);
            break;	
			
        case "elementStart":
            ele = get_start("teststart","teststartnem",xCoord,yCoord);
            break;
			
        case "elementEnde":
            ele = get_end("testende","testende",xCoord,yCoord);
            break;
        case "elementEdit":
            ele = get_edit(xCoord,yCoord);
            break;

			
        default:
            console.log("Element nicht gefunden");
    }
	if(MousePosElementName === "edit"){
		graph.getCell(MousePosElementID).embed(ele);
	}
	graph.addCell(ele);
}

function textAreaAdjust(o) {
  o.style.height = "1px";
  o.style.height = (25+o.scrollHeight)+"px";
}
	
// JOINTJS
$(document).ready(function () {
	autosize(document.querySelectorAll('textarea')); 
	parentChildNodes =[]; 
	// Define spot for endNode	
    var posEndheight = ($('#paper').height() - 100);
    var posEndwidth = $('#paper').width() - 100;
	
	//SET start- and endNode
	function preparePaper(){	
		start = get_start("start","startId",10,10,startnode_inputtype + ":" + startnode_input);
		end = get_end("end","endId",posEndwidth,posEndheight,endnode_outputtype + ":" + endnode_output);
		graph.addCells([end, start]);
		parentChildNodes.push({"parentId":"Startknoten-startId","startId":"Startknoten-startId","endId":"Endknoten-endId","endName":"end"});
	}	

//Basics
	graph = new joint.dia.Graph;
	paper = new joint.dia.Paper({
		el: document.getElementById('paper'),
		width: $('#paper').width(),
		height: paperH,
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
    var ph = $('#paper').height(); // maximal decreasing to loaded size of paper
	var save_ph=ph;
    $("#paperplus").click(function () {
        if (ph <= 2000) {
            ph += 100;
            paper.setDimensions(parseInt(0), parseInt(ph));
            paper.setDimensions(parseInt(0), parseInt(ph));
        }
    });

    $("#paperminus").click(function () {
        if (ph > save_ph) {
            ph -= 100;
            paper.setDimensions(parseInt(0), parseInt(ph));
            paper.setDimensions(parseInt(0), parseInt(ph));
        }
    });

//zoombuttons
    var z = 1.0;
    $("#zoomplus").click(function () {
        if (z < 1.5) {
            z += 0.1;
            paper.scale(parseFloat(z), parseFloat(z));
        }
    });

    $("#zoomminus").click(function () {
        if (z > 0.7) {
            z -= 0.1;
            paper.scale(parseFloat(z), parseFloat(z));
        }
    });

//Origin
    var o = 0;
    $("#oright").click(function () {
        if (o < 400) {
            o += 100;
            paper.setOrigin(parseInt(o), t);
            rebuildGraph();
        }
    });

    $("#oleft").click(function () {
        if (o > -400) {
            o -= 100;
            paper.setOrigin(parseInt(o), t);
            rebuildGraph();
        }
    });

    var t = 0;
    $("#otop").click(function () {
        console.log("top: " + t)
        if (t > -400) {
            t -= 100;
            paper.setOrigin(o, parseInt(t));
            rebuildGraph();
        }
    });

    $("#obot").click(function () {
        console.log("bot: " + t)
        if (t < 400) {
            t += 100;
            paper.setOrigin(o, parseInt(t));
            rebuildGraph();
        }
    });


//view reset
    $("#viewreset").click(function () {
        paper.scale(1.0, 1.0);
        paper.setOrigin(0, 0);
        paper.setDimensions($('#paper').width(), $('#paper').height());
        rebuildGraph();
    });

//rebuild graph
    function rebuildGraph() {
		localStorage.setItem("parentChildNodes", JSON.stringify(parentChildNodes));
        graph.fromJSON(graph.toJSON());
		parentChildNodes = JSON.parse(localStorage.getItem("parentChildNodes"));
		reSetSelection();
        refreshDia();
		updateHighlight();
    }
	
	// make the value in the view visible
	function reSetSelection(){
		var allElements = graph.getElements();
		for(var i = 0; i < allElements.length;i++){
			switch(allElements[i].attributes.name){
				case "actionSelect":
					var currentSelection = allElements[i].get('varContent');
					allElements[i].findView(paper).$attributes["0"].value = currentSelection;
					break;
				case "actionDeclare":
					var currentSelection = allElements[i].get('varContent1');
					allElements[i].findView(paper).$attributes["0"].value = currentSelection;					
					break;				
			}
		}
	}
	
// Aktualisierung nachdem sich ein Link ändert
    function setNameForTarget(eventName, cell) {
        try {
            if (list_nameOfChangingElements.includes(graph.getCell(arguments["0"].attributes.target.id).attributes.name)) {
                graph.getCell(arguments["0"].attributes.target.id).set('name', getTypeByLinks(getNodeById(arguments["0"].attributes.target.id)));
                console.log(graph.getCell(arguments["0"].attributes.target.id).get('name'));
            }
        } catch (e) {
        }
        ;
    }

    function setNameForSource(eventName, cell) {
        try {
            if (list_nameOfChangingElements.includes(graph.getCell(arguments["0"].attributes.source.id).attributes.name)) {
                graph.getCell(arguments["0"].attributes.source.id).set('name', getTypeByLinks(getNodeById(arguments["0"].attributes.source.id)));
                console.log(graph.getCell(arguments["0"].attributes.source.id).get('name'));
            }
        } catch (e) {
        }
        ;
    }

	function forbidInputTextarea(eventName, cell){ 
		try {
			var cellname = graph.getCell(cell.id).attributes.name;
			if(cellname === "edit"){
				console.log(eventName);
				console.log(cell);
				var parentCell = graph.getCell(eventName.attributes.source.id);
				var parentPort = eventName.attributes.source.port;
				if (list_externPorts.includes(parentPort)) {
					var parentView = parentCell.findView(paper);	
					if(parentCell.attributes.name === "if"){
						var testString =parentPort.substring(7, parentPort.length);
						for(var i = 0; i< parentView.$attributes.length;i++){
							if( parentView.$attributes[i].dataset.attribute === testString){
								 parentView.$attributes[i].setAttributeNode(document.createAttribute("disabled"));
							}
						}
					}else{
						for(var i = 0 ; i<parentView.$box["0"].children.length;i++){
							console.log(parentView.$box["0"].children[i].nodeName );
							if(parentView.$box["0"].children[i].nodeName  ==="TEXTAREA"){
								parentView.$box["0"].children[i].setAttributeNode(document.createAttribute("disabled"));
							}
						}
					}
				}				
			}	
        } catch (e) {
        }
        ;
	}

//graph.on events
    graph.on('change:target', function (eventName, cell) {
        setNameForTarget(eventName, cell);
		if(version === "3"){
			forbidInputTextarea(eventName, cell);	
		}
    });
	
    graph.on('change:source', function (eventName, cell) {
        setNameForSource(eventName, cell);
		activateTextarea(eventName, cell);
    });

    graph.on('change', function (eventName, cell) {
        try {
            if (list_nameOfChangingElements.includes(graph.getCell(arguments["0"].attributes.source.id).attributes.name)) {
                var cell = graph.getCell(arguments["0"].attributes.source.id);
                graph.getCell(arguments["0"].attributes.source.id).set('name', getTypeByLinks(cell));
                console.log(graph.getCell(arguments["0"].attributes.source.id).get('name'));
            }
        } catch (e) {
        }
        try {
            if (list_nameOfChangingElements.includes(graph.getCell(arguments["0"].attributes.target.id).attributes.name)) {
                var cell = graph.getCell(arguments["0"].attributes.target.id);
                graph.getCell(arguments["0"].attributes.target.id).set('name', getTypeByLinks(cell));
                console.log(graph.getCell(arguments["0"].attributes.target.id).get('name'));
            }
        } catch (e) {
        }
    });

    graph.on('batch:stop', function (eventName, cell) {
        if (arguments["0"].batchName == "add-link") {
            try {
                //console.log(graph.getCell(arguments["0"].cell.id));
                if (list_nameOfChangingElements.includes(graph.getCell(arguments["0"].cell.id).attributes.name)) {
                    graph.getCell(arguments["0"].cell.id).set('name', getTypeByLinks(getNodeById(arguments["0"].cell.id)));
                    console.log(graph.getCell(arguments["0"].cell.id).get('name'));
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
	
	function activateTextarea(eventName, cell){
		if(arguments["0"].attributes.type === "link" && list_externPorts.includes(arguments["0"].attributes.source.port)){
			var sourceCell = graph.getCell(arguments["0"].attributes.source.id);
			var parentView = sourceCell.findView(paper);
			var parentPort = eventName.attributes.source.port;
			if(sourceCell.attributes.name === "if"){
				var testString =parentPort.substring(7, parentPort.length);
				for(var i = 0; i< parentView.$attributes.length;i++){
					if( parentView.$attributes[i].dataset.attribute === testString){
						 parentView.$attributes[i].removeAttribute("disabled");
					}
				}
			}else{
				for(var i = 0 ; i<parentView.$box["0"].children.length;i++){
					if(parentView.$box["0"].children[i].nodeName  ==="TEXTAREA"){
						parentView.$box["0"].children[i].removeAttribute("disabled");
					}
				}
			}
		}
	}

    graph.on('change:position', function(cell, newPosition, opt) {

        if (opt.skipParentHandler) return;

        if (cell.get('embeds') && cell.get('embeds').length) {
            // If we're manipulating a parent element, let's store
            // it's original position to a special property so that
            // we can shrink the parent element back while manipulating
            // its children.
            cell.set('originalPosition', cell.get('position'));
        }
        
        var parentId = cell.get('parent');
        if (!parentId) return;

        var parent = graph.getCell(parentId);
        var parentBbox = parent.getBBox();
        if (!parent.get('originalPosition')) parent.set('originalPosition', parent.get('position'));
        if (!parent.get('originalSize')) parent.set('originalSize', parent.get('size'));
        
        var originalPosition = parent.get('originalPosition');
        var originalSize = parent.get('originalSize');
        
        var newX = originalPosition.x;
        var newY = originalPosition.y;
        var newCornerX = originalPosition.x + originalSize.width;
        var newCornerY = originalPosition.y + originalSize.height;
        
        _.each(parent.getEmbeddedCells(), function(child) {

            var childBbox = child.getBBox();
            
            if (childBbox.x < newX) { newX = childBbox.x; }
            if (childBbox.y < newY) { newY = childBbox.y; }
            if (childBbox.corner().x > newCornerX) { newCornerX = childBbox.corner().x; }
            if (childBbox.corner().y > newCornerY) { newCornerY = childBbox.corner().y; }
        });

        // Note that we also pass a flag so that we know we shouldn't adjust the
        // `originalPosition` and `originalSize` in our handlers as a reaction
        // on the following `set()` call.
        parent.set({
            position: { x: newX, y: newY },
            size: { width: newCornerX - newX, height: newCornerY - newY }
        }, { skipParentHandler: true });
    });
	
    graph.on('change:size', function(cell, newPosition, opt) {
        
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
                var input = evt.target;
                var attribute = input.dataset.attribute;
                if (attribute) {
                    this.model.set(attribute, input.value);
                }
				console.log(this.model);
            },

            onRender: function () {
                if (this.$box) this.$box.remove();
                var boxMarkup = joint.util.template(this.model.get('template'))();
                var $box = this.$box = $(boxMarkup);
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
                var bbox = this.getBBox({useModelGeometry: true});
                var scale = V(this.paper.viewport).scale();
                this.$box.css({
                    transform: 'scale(' + scale.sx + ',' + scale.sy + ')',
                    transformOrigin: '0 0',
                    width: bbox.width / scale.sx,
                    height: bbox.height / scale.sy,
                    left: bbox.x,
                    top: bbox.y
                });
				changeSize(this.model,this.$box,bbox);
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
                    case "manual_ifstart":
                        //console.log(this.$box["0"].children[1].childNodes["0"]);
                        if (this.$box["0"].children[1].childNodes["0"].value.length > 0) {
                            this.$box["0"].children[2].innerText = "[TRUE]";
                            this.$box["0"].children[3].innerText = "[FALSE]";
                            this.$box["0"].children[2].classList.add('colorTrue');
                            this.$box["0"].children[3].classList.add('colorFalse');
                        } else {
                            this.$box["0"].children[2].innerText = "";
                            this.$box["0"].children[3].innerText = "";

                        }
                        this.$box["0"].children[4].innerText = "";
                        this.$box["0"].children[5].innerText = "";
                        break;

                    case "manual_loopendcf":
                        if (this.$box["0"].children[1].childNodes["0"].value.length > 0) {
                            this.$box["0"].children[3].innerText = "[FALSE]";
                            this.$box["0"].children[3].classList.add('colorFalse');
                            // Differ bot and top side generated loop
                            var con = graph.getConnectedLinks(graph.getCell(this.model.attributes.id), {outbound: true});
                            for (var i = 0; i < con.length; i++) {
                                if (con[i].attributes.source.port == "top") {
                                    this.$box["0"].children[5].innerText = "[TRUE]";
                                    this.$box["0"].children[5].classList.add('colorTrue');
                                    this.$box["0"].children[4].innerText = "";
                                } else if (con[i].attributes.source.port == "bot") {
                                    this.$box["0"].children[4].innerText = "[TRUE]";
                                    this.$box["0"].children[4].classList.add('colorTrue');
                                    this.$box["0"].children[5].innerText = "";
                                }
                            }
                        } else {
                            // deactivate
                            this.$box["0"].children[3].innerText = "";
                            this.$box["0"].children[4].innerText = "";
                            this.$box["0"].children[5].innerText = "";
                        }
                        this.$box["0"].children[2].innerText = "";
                        break;

                    case "manual_loopendct":
                        if (this.$box["0"].children[1].childNodes["0"].value.length > 0) {
                            this.$box["0"].children[2].innerText = "[FALSE]";
                            this.$box["0"].children[2].classList.add('colorFalse');
                            var con = graph.getConnectedLinks(graph.getCell(this.model.attributes.id), {outbound: true});
                            for (var i = 0; i < con.length; i++) {
                                if (con[i].attributes.source.port == "top") {
                                    this.$box["0"].children[5].innerText = "[TRUE]";
                                    this.$box["0"].children[5].classList.add('colorTrue');
                                    this.$box["0"].children[4].innerText = "";
                                } else if (con[i].attributes.source.port == "bot") {
                                    this.$box["0"].children[4].innerText = "[TRUE]";
                                    this.$box["0"].children[4].classList.add('colorTrue');
                                    this.$box["0"].children[5].innerText = "";
                                }
                            }
                        } else {

                            this.$box["0"].children[2].innerText = "";
                            this.$box["0"].children[4].innerText = "";
                            this.$box["0"].children[5].innerText = "";
                        }
                        this.$box["0"].children[3].innerText = "";
                        break;

                    case "unknown":
                        this.$box["0"].children[2].innerText = "";
                        this.$box["0"].children[3].innerText = "";
                        this.$box["0"].children[4].innerText = "";
                        this.$box["0"].children[5].innerText = "";
                        break;
                }
                this.updateAttributes();
            },

            updateAttributes: function () {
                var model = this.model;
                this.$attributes.each(function () {
                    var value = model.get(this.dataset.attribute);
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

function changeSize(model,box,bbox){
	if(model.attributes.name === "actionInput"){
		var nheight = box["0"].children[1].style.height;
		nheight = Number(nheight.replace("px", ""));
		if(nheight >15){
			model.resize(bbox.width,nheight+15);
			model.prop("ports/items/1/args/y",(nheight+15));						
		}					
	}
	if(version !== "1"){
		if(model.attributes.name === "forin"){
			var nheight = box["0"].children[2].style.height;
			nheight = Number(nheight.replace("px", ""));
			if(nheight >50){
				model.resize(bbox.width,nheight+35);
				model.prop("ports/items/2/args/y",(nheight+35));						
			}		
		}
		if(model.attributes.name === "dw"){
			var nheight = box["0"].children[2].style.height;
			nheight = Number(nheight.replace("px", ""));
			if(nheight >50){
				model.resize(bbox.width,nheight+50);
				model.prop("ports/items/1/args/y",(nheight+50));						
			}					
		}				
		if(model.attributes.name === "wd"){
			var nheight = box["0"].children[4].style.height;
			nheight = Number(nheight.replace("px", ""));
			if(nheight >50){
				model.resize(bbox.width,nheight+52);
				model.prop("ports/items/1/args/y",(nheight+52));						
			}					
		}							
		if(model.attributes.name === "if"){
			var nheight = box["0"].children[2].children[1].style.height;
			var nheight2 = box["0"].children[3].children[1].style.height;
			nheight = Number(nheight.replace("px", ""));
			nheight2 = Number(nheight2.replace("px", ""));
			if(nheight+nheight2 >100){
				nheight = Math.max(nheight,75);
				nheight2 = Math.max(nheight2,75);
				model.resize(bbox.width,nheight+nheight2+130);
				model.prop("ports/items/2/args/y",(nheight));
				model.prop("ports/items/3/args/y",(nheight+nheight2+25));	
				model.prop("ports/items/1/args/y",(nheight+nheight2+130));						
			}
		}			
	}
}
		
		

		

//paperevents
	paper.on('blank:pointerclick', function(evt, x, y) {
		try {
			if (sel_elementname != "") {
				createElement(sel_elementname,x,y);
				sel_elementname ="";
			}
		} catch (e) {
		}
		;		
	})		

	paper.on('cell:mouseenter', 
		function(cellView, evt, x, y) {
					MousePosElementID =cellView.model.id;
					MousePosElementName =cellView.model.attributes.name;
				//	console.log(MousePosElementID);
				//	console.log(MousePosElementName);					
		})

		paper.on('cell:mouseleave', 
		function(cellView, evt, x, y) {
					MousePosElementID ="mainId";
					MousePosElementName ="main";					
		})	
		
	paper.on('cell:pointerclick', 
		function(cellView, evt, x, y) {
		if(cellView.model.attributes.name === "edit" && sel_elementname !== ""){
			createElement(sel_elementname,x,y);
			sel_elementname ="";
		}				
		})
		preparePaper(); // set start and endnode	
});

function refreshElement(el){
		var x = el.get('position').x;
		var y = el.get('position').y;
		el.set('position',{ x: x+1, y: y+1 });
		el.set('position',{ x: x, y: y });		
}

//calculate each elements height and sum up --_> Fail
function calculateFinalHeight(htmlCollection,finalHeightValue){
	for(var i = 0; i < htmlCollection.length;i++){
		switch(htmlCollection[i].nodeName){
			case "INPUT":
			case "TEXTAREA":
				finalHeightValue += Number((htmlCollection[i].style.height).replace("px", ""));
				break;
		}
		if(htmlCollection[i].nodeName === "DIV"){
			finalHeightValue += calculateFinalHeight(htmlCollection[i].childNodes,finalHeightValue);
		}
	}
	return finalHeightValue;
}