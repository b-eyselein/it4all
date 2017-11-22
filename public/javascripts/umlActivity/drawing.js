var graph = new joint.dia.Graph;
var paper;

//Basic zum Erfassen aller Elemente im Drawing --> wird für Elementbestimmung schon hier definiert
var jsongraph;
var graphcopy;
var list_connections;
// Needed for Converting --> language definition
var startnode_inputtype = "String";
var startnode_input = "W1";
var endnode_outputtype = "String";
var endnode_output = "W2";
var methodname = "heckeSchneiden";

const paperH = $('#leftblock').height(); // paper as tall as the left div next to him --> minimum paper size for expanding --> makes no sense :)

//Listen aktuell halten für Test und Objektbestimmung
function refreshDia() {
    document.getElementById("zoomplus").click();
    document.getElementById("zoomminus").click();
}

function test() {
    console.log(graph.getCells());
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

/*
function consoleOut() {
    console.log($('#paper').width());
    //document.getElementById("consoleOut").innerHTML=$('#paper').width()-60;
    document.getElementById("consoleOut").innerHTML = element_dist.attr('.label/text');
}
*/

// MousePostion within div
var dragX;
var dragY;

document.addEventListener("dragover", function (e) {
    e = e || window.event;
    var offset = $('#paper').offset();
    dragX = e.pageX - offset.left;
    dragY = e.pageY - offset.top;
}, false);

// Drag and Drop
function allowDrop(ev) {
    ev.preventDefault();
}

function drag(ev) {
    var elementName = ev.target.innerHTML;
    if (ev.target.getAttribute('data-baseform') != null) {
        elementName = ev.target.getAttribute('data-baseform');
    }
    ev.dataTransfer.setData("text", elementName);
}

function drop(ev) {
    ev.preventDefault();
    createElement(ev.dataTransfer.getData("text"), dragX, dragY);
}

// Constructor Elements
function createElement(elementName, xCoord, yCoord) {
    switch (elementName) {
        case "elementAction":
            var element_action = new joint.shapes.html.Element({
                position: {x: xCoord, y: yCoord},
                size: {width: 120, height: 80},
                cnr: -1,
                name: "action",
                cleanname: "Aktionsknoten",
                template: [
                    '<div class="action-element">',
                    '<button class="delete">x</button>',
                    '<textarea data-attribute="area"></textarea>',
                    '</div>'
                ].join(''),
                area: '',
                ports: {
                    groups: {
                        'in': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
                        'out': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        }
                    },
                    items: [{
                        id: 'in',
                        group: 'in',
                        args: {x: 60, y: 0}
                    },
                        {
                            id: 'out',
                            group: 'out',
                            args: {x: 60, y: 80}
                        }]
                }
            });
            graph.addCell(element_action);
            break;

        case "elementFor":
            var element_for = new joint.shapes.html.Element({
                position: {x: xCoord, y: yCoord},
                size: {width: 170, height: 100},
                template: [
                    '<div class="for_element">',
                    '<button class="delete">x</button>',
                    '<div class="dashed-bot">',
                    '<span> for </span>',
                    '<input data-attribute="efor" type="text"/></input>',
                    '<span> in </span>',
                    '<input data-attribute="ein" placeholder="Anweisungen" type="text"/></input>',
                    '</div>',
                    '<textarea  data-attribute="area"></textarea>',
                    '</div>'
                ].join(''),
                'efor': 'Element',
                'ein': 'Collection',
                area: 'Bitte geben Sie hier die Anweisung ein',
                name: "forin",
                cleanname: "For-In-Schleife",
                ports: {
                    groups: {
                        'in': {
                            position: 'absolute',
                            label: {
                                // label layout definition:
                                position: {
                                    name: 'manual', args: {
                                        y: 250,
                                        attrs: {
                                            '.': {'text-anchor': 'middle'},
                                            text: {fill: 'black', 'pointer-events': 'none'}
                                        }
                                    }
                                }
                            },
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
                        'out': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        }
                    },
                    items: [{
                        id: 'in',
                        group: 'in',
                        args: {x: 85, y: 0}
                    },
                        {
                            id: 'out',
                            group: 'out',
                            args: {x: 85, y: 100}
                        }]
                }
            });
            graph.addCell(element_for);
            break;

        case "elementDoWhile":
            var element_doWhile = new joint.shapes.html.Element({
                position: {x: xCoord, y: yCoord},
                size: {width: 170, height: 120},
                template: [
                    '<div class="wd_element">',
                    '<button class="delete">x</button>',
                    '<span> do </span>',
                    '<textarea  data-attribute="edo"></textarea>',
                    '<div class="dashed-top">',
                    '<span> while </span>',
                    '<input data-attribute="ewhile" type="text"/></input>',
                    '</div>',
                    '</div>'
                ].join(''),
                'ewhile': 'Bedingung',
                'edo': 'Bitte geben Sie hier die Anweisung ein',
                name: "dw",
                cleanname: "Do-While-Knoten",
                ports: {
                    groups: {
                        'in': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
                        'out': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        }
                    },
                    items: [{
                        id: 'in',
                        group: 'in',
                        args: {x: 85, y: 0}
                    },
                        {
                            id: 'out',
                            group: 'out',
                            args: {x: 85, y: 120}
                        }]
                }
            });
            graph.addCell(element_doWhile);
            break;

        case "elementWhileDo":
            var element_WhileDo = new joint.shapes.html.Element({
                position: {x: xCoord, y: yCoord},
                size: {width: 170, height: 120},
                template: [
                    '<div class="wd_element">',
                    '<button class="delete">x</button>',
                    '<div class="dashed-bot">',
                    '<span> while </span>',
                    '<input data-attribute="ewhile" type="text"/></input>',
                    '</div>',
                    '<span> do </span>',
                    '</br>',
                    '<textarea  data-attribute="edo"></textarea>',
                    '</div>'
                ].join(''),
                'ewhile': 'Bedingung',
                'edo': 'Bitte geben Sie hier die Anweisung ein',
                name: "wd",
                cleanname: "While-Do-Knoten",
                ports: {
                    groups: {
                        'in': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
                        'out': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        }
                    },
                    items: [{
                        id: 'in',
                        group: 'in',
                        args: {x: 85, y: 0}
                    },
                        {
                            id: 'out',
                            group: 'out',
                            args: {x: 85, y: 120}
                        }]
                }
            });
            graph.addCell(element_WhileDo);
            break;

        case "elementIf":
            var element_if = new joint.shapes.html.Element({
                position: {x: xCoord, y: yCoord},
                size: {width: 250, height: 180},
                template: [
                    '<div class="if_element">',
                    '<button class="delete">x</button>',
                    '<div class="dashed-bot">',
                    '<span> if </span>',
                    '<input data-attribute="eif" type="text"/></input>',
                    '</div>',
                    '<div class="dashed-bot">',
                    '<span> then </span>',
                    '<textarea  data-attribute="ethen"></textarea>',
                    '</div>',
                    '<div>',
                    '<span> else </span>',
                    '<textarea  data-attribute="eelse"></textarea>',
                    '</div>',
                    '</div>'
                ].join(''),
                'eif': 'Bedingung',
                'ethen': 'then_inhalt',
                'eelse': 'else_inhalt',
                name: "if",
                cleanname: "Bedingungsknoten",
                ports: {
                    groups: {
                        'in': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
                        'out': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        }
                    },
                    items: [{
                        id: 'in',
                        group: 'in',
                        args: {x: 125, y: 0}
                    },
                        {
                            id: 'out',
                            group: 'out',
                            args: {x: 125, y: 180}
                        }]
                }
            });
            graph.addCell(element_if);
            break;

        case "elementBasic":
            var element_basic = new joint.shapes.html.Element({
                position: {x: xCoord, y: yCoord},
                size: {width: 100, height: 100},
                template: [
                    '<div class="basic">',
                    '<button class="delete">x</button>',

                    '<div class="myDiv3">',
                    '<input  data-attribute="einput" class="myDiv4" type="text">',
                    '</div>',
                    '<div class="left">',
                    '<span></span>',
                    '</div>',
                    '<div class="right"></div>',
                    '<div class="bot">',
                    '<span></span>',
                    '</div>',
                    '<div class="top"></div>',
                    '</div>'
                ].join(''),
                einput: '',
                name: "unknown",
                cleanname: "Verzweigungsknoten",
                ports: {
                    groups: {
                        'top': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
                        'left': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
                        'right': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'red', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
                        'bot': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        }
                    },
                    items: [{
                        id: 'top',
                        group: 'top',
                        args: {x: 53, y: -3}
                    },
                        {
                            id: 'bot',
                            group: 'bot',
                            args: {x: 53, y: 104}
                        },
                        {
                            id: 'left',
                            group: 'left',
                            args: {x: 0, y: 50}
                        },
                        {
                            id: 'right',
                            group: 'right',
                            args: {x: 108, y: 50}
                        }]
                }
            });
            graph.addCell(element_basic);
            break;

        default:
            console.log("Element nicht gefunden");
    }
}


// JOINTJS
$(document).ready(function () {
// Endpunkt festlegen	
    var posEndheight = ($('#paper').height() - 80);
    var posEndwidth = $('#paper').width() - 80;

// Mauspostion im DIV anzeigen

    mouseTop = 0;
    mouseLeft = 0;

    $('#paper').on('mousemove', function (event) {
        mouseTop = event.clientY;
        mouseLeft = event.clientX - $(this).offset().left;
        $('#coords').text('X: ' + mouseLeft + ' Y:' + mouseTop);
    });

//Basics
    var paper = new joint.dia.Paper({
        el: document.getElementById('paper'),
        width: $('#paper').width(),
        height: paperH,
        gridSize: 10,
        model: graph,
        drawGrid: 'dot',
        defaultLink: new joint.dia.Link({
            router: {name: 'manhattan'},
            connector: {name: 'normal'},
            attrs: {'.marker-target': {d: 'M 10 0 L 0 5 L 10 10 z'}}
        }),
        snapLinks: {radius: 25},//Snap Elemente zu Raster
        linkPinning: false, // dropping on fail spot
        setLinkVertices: true
    });
//origin x and y
    var $sx = $('#sx');
    var $sy = $('#sy');
    var $w = $('#width');
    var $h = $('#height');
    var $ox = $('#ox');
    var $oy = $('#oy');


//paper expand buttons
    var ph = $('#paper').height();
    $("#paperplus").click(function () {
        if (ph < 2000) {
            ph += 100;
            paper.setDimensions(parseInt(0), parseInt(ph));
            paper.setDimensions(parseInt(0), parseInt(ph));
        }
    });

    $("#paperminus").click(function () {
        if (ph > (posEndheight + 200)) {
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


//view reseten

    $("#viewreset").click(function () {
        paper.scale(1.0, 1.0);
        paper.setOrigin(0, 0);
        paper.setDimensions($('#paper').width(), $('#paper').height());
        rebuildGraph();
    });

//rebuild graph
    function rebuildGraph() {
        graph.fromJSON(graph.toJSON());
        refreshDia();
    }

// Aktualisierung nachdem sich ein Link ändert

// gültige einträge beim wechseln der elementbezeichnungen --> Verwendung für paper.on link elements
    var list_nameOfChangingElements = ["basic", "manual_ifend", "manual_ifstart", "manual_loopstart", "manual_loopendct", "manual_loopendcf", "unknown"];

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

    graph.on('change:target', function (eventName, cell) {
        setNameForTarget(eventName, cell);
    });

    graph.on('change:source', function (eventName, cell) {
        setNameForSource(eventName, cell);
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

    /*
        graph.on('all', function(eventName, cell) {
        console.log(arguments);
    });
    */

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
    });

    /*
    graph.on('reset', function (eventName, cell) {
        console.log("reset");
        graph.resetCells(graph.get("cells"));
    });
    */

    (function (joint, $) {

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
                //console.log(this.$box);    // --> zugriff auf paperelement möglich
                //console.log(this.model); //  --> zugriff auf id möglich
                // Anzeigen von TRUE and FALSE, wenn Inhalt von Bedingung nicht leer
                // Überprüfe ob es ein Verzweigungsobjekt ist.
                // Lege aktueller Zustand des Elementes fest.
                //console.log(this.$box);
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
                //updateLists(); // Renew all Elements and Links
                //console.log(this.model); //  --> zugriff auf id möglich
                //getTypeByLinks(getNodeById(this.model.id));
                //console.log(list_connections);
                //console.log(this.$box);
                //console.log(getNodeById(this.model.id));
                //console.log(getTypeByLinks(getNodeById(this.model.id)));
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
            }

        });

// ELEMENT START
        var svgStart = [
            '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="58px" height="58px" version="1.1" content="&lt;mxfile userAgent=&quot;Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36&quot; version=&quot;7.0.9&quot; editor=&quot;www.draw.io&quot; type=&quot;google&quot;&gt;&lt;diagram&gt;jZPBcoMgEIafxrvK1KTX2jS99OShZyqrMEHXwU3VPn1RQOOkmSkHh/3+XVh+MGJ5M54N7+QHCtBRGosxYq9Rmh6Omf3OYHIgO8YO1EYJh5INFOoHPAxpVyWg3yUSoibV7WGJbQsl7Rg3Bod9WoV6v2vHa7gDRcn1Pf1UgqSnSfa8Ce+gaum3Pqb+wF+8vNQGr63fL0pZtQwnNzys5Q/aSy5wuEHsFLHcIJKbNWMOerY22Obq3h6oa98GWvpPQeoKvrm+Quh46Yum4IUtsLbb4EVSoy1L7NT23c16T9xQQZxmvVJa56jRLIUsXsacTAYv8JeyOAXCLyp4L9fANwaGYHx4uGS1zL5EwAbITDYlFGTe5Sm8LubiYbvTp4ND8uY2A+P+FdXrypuTduLNDOF2aYt282Ow0y8=&lt;/diagram&gt;&lt;/mxfile&gt;" style="background-color: rgb(255, 255, 255);">', '<defs/>', '<g transform="translate(0.5,0.5)">', '<ellipse cx="29" cy="29" rx="24.5" ry="24.5" fill="#000000" stroke="#000000" stroke-dasharray="3 3" pointer-events="none"/>', '</g>', '</svg>'
        ].join('');

        var start = new joint.shapes.devs.Model({
            markup: '<g class="rotatable"><g class="scalable"><image class="body"/></g><g class="outPorts"/></g><text/>',
            size: {
                width: 50,
                height: 50
            },
            name: 'start',
            cleanname: "Startknoten",
            id: 'startid',
            position: {
                x: 10,
                y: 10
            },
            attrs: {
                '.body': {
                    width: 1024,
                    height: 768,
                    'xlink:href': 'data:image/svg+xml;utf8,' + encodeURIComponent(svgStart),
                    preserveAspectRatio: 'none'
                },
                '.outPorts circle': {r: 3, fill: '#E74C3C', magnet: 'passive', type: 'output'},
                text: {
                    'text': startnode_inputtype + ":" + startnode_input,
                    'fill': 'black',
                    'ref-y': '10px',
                    'ref-x': '100px',
                    'text-anchor': 'middle'
                }
            },
            outPorts: [''],
            ports: {
                groups: {
                    'out': {
                        attrs: {
                            '.port-body': {
                                fill: 'transparent',
                                'ref-x': -25,
                                'ref-y': 0,
                                r: 20
                            }
                        }
                    }
                }
            }
        });

// ELEMENT Ende
        var svgEnd = [
            '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="31px" height="31px" version="1.1" content="&lt;mxfile userAgent=&quot;Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36&quot; version=&quot;7.1.0&quot; editor=&quot;www.draw.io&quot; type=&quot;google&quot;&gt;&lt;diagram&gt;jVNNc4IwEP013IGMVq9S2x7a6cFDzylZIWPIMnGt2F/fxGxEap0pB4Z9+16yH49MVN3w7GTfvqECk5W5GjLxmJVlkQv/DsApAstZjBunFVNGYKO/IekYPWgF+wmREA3pfgrWaC3UNMGkc3ic0rZoprf2soEbYFNLc4t+aEVtRBflw4i/gG7adHMxX8ZMJxOZO9m3UuHxChLrTFQOkeJXN1RgwujSXKLu6U72UpgDS/8RlFHwJc2Be+O66JSa9QI/Vx+sfLF9AMGqDUkK0FYbU6FBd+aK/PwEKjncwZ+Z1HEIGieV9qUmokUbTjXyE8xK1rvG4cGqX9kt3ggsvgaJBwofEQz03staU7DXMlzEbYLzubujKi4L8L4F7IDcyVNYMF/wztizs5xNexwtIJjSXm0/YZJN11xOHvfiP3g1KRwtcM5d/UZi/QM=&lt;/diagram&gt;&lt;/mxfile&gt;">', '<defs/>', '<g transform="translate(0.5,0.5)">', '<ellipse cx="15" cy="15" rx="11" ry="11" fill="#000000" stroke="#000000" pointer-events="none"/>', '<ellipse cx="15" cy="15" rx="15" ry="15" fill="none" stroke="#000000" pointer-events="none"/>', '</g>', '</svg>'
        ].join('');

        var end = new joint.shapes.devs.Model({
            markup: '<g class="rotatable"><g class="scalable"><image class="body"/></g><g class="outPorts"/></g><text/>',
            size: {
                width: 50,
                height: 50
            },
            name: 'end',
            id: "endid",
            cleanname: "Endknoten",
            position: {
                x: posEndwidth,
                y: posEndheight
            },
            attrs: {
                '.body': {
                    width: 1024,
                    height: 768,
                    'xlink:href': 'data:image/svg+xml;utf8,' + encodeURIComponent(svgEnd),
                    preserveAspectRatio: 'none'
                },
                '.outPorts circle': {r: 25, fill: '#transparent', magnet: 'active', type: 'output'},
                text: {
                    'text': endnode_outputtype + ":" + endnode_output,
                    'fill': 'black',
                    'ref-y': '-30px',
                    'ref-x': '25px',
                    'text-anchor': 'middle'
                }
            },
            inPorts: [''],
            ports: {
                groups: {
                    'in': {
                        attrs: {
                            '.port-body': {
                                fill: 'transparent',
                                'ref-x': 25,
                                'ref-y': 0,
                                r: 18
                            }
                        }
                    },
                    args: {
                        x: 25, y: 25
                    },
                    attrs: {
                        circle: {fill: 'transparent', stroke: 'none', 'stroke-width': 1, r: 20, magnet: true}
                    }
                }
            }
        });
        graph.addCells([end, start]);

    })(joint, $);
});
