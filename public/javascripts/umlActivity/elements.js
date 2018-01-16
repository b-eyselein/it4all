const EXTERN_PORT_WIDTH = 250; // all except if-then-else

const ACTION_HEIGHT = 40;
const ACTION_WIDTH = 250;

const FOR_LOOP_HEIGHT = 100;
const WHILE_LOOP_HEIGHT = 120;
const IF_ELSE_HEIGHT = 180;

const START_END_SIZE = 50;

function createActionInput(xCoord, yCoord) {
    return new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: ACTION_WIDTH, height: ACTION_HEIGHT},
        name: 'actionInput',
        cleanname: 'Aktionsknoten',
        template:
            `<div class="action-element">
               <button class="delete">x</button>
               <textarea onkeyup="textAreaAdjust(this)" placeholder="Anweisungen" data-attribute="varContent"></textarea>
             </div>`,
        varContent: '',
        ports: {
            groups: {
                'in': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}}
                },
                'out': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}}
                }
            },
            items: [
                {id: 'in', group: 'in', args: {x: ACTION_WIDTH / 2, y: 0}},
                {id: 'out', group: 'out', args: {x: ACTION_WIDTH / 2, y: ACTION_HEIGHT}}
            ]
        }
    });
}

function createActionSelect(xCoord, yCoord) {
    return new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: ACTION_WIDTH, height: ACTION_HEIGHT},
        name: 'actionSelect',
        cleanname: 'Aktionsknoten',
        template:
            `<div class="action-element">
               <button class="delete">x</button>
               <select data-attribute="varContent"><option></option><option>getNutzlast()</option><option>getContent()</option><option>Example()</option></select>
             </div>`,
        varContent: '',
        ports: {
            groups: {
                'in': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}}
                },
                'out': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}}
                }
            },
            items: [
                {id: 'in', group: 'in', args: {x: ACTION_WIDTH / 2, y: 0}},
                {id: 'out', group: 'out', args: {x: ACTION_WIDTH / 2, y: ACTION_HEIGHT}}
            ]
        }
    });
}

function createActionDeclare(xCoord, yCoord) {
    return new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: ACTION_WIDTH, height: ACTION_HEIGHT},
        template:
            `<div class="actionDeclare">
               <button class="delete">x</button>
               <select data-attribute="varContent1">
                 <option></option>
                 <option>String</option>
                 <option>Double</option>
                 <option>Boolean</option>
               </select>
               <input placeholder="Var" class="smallInput" data-attribute="varContent2" type="text"/></input>
               <span> = </span>
               <input placeholder="Anweisung" class="normalInput" data-attribute="varContent3" type="text"/></input>
            </div>`,
        varContent1: '',
        varContent2: '',
        varContent3: '',
        name: 'actionDeclare',
        cleanname: 'Aktionknoten',
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
                                    '.': {'text-anchor': 'middle'}, text: {fill: 'black', 'pointer-events': 'none'}
                                }
                            }
                        }
                    },
                    attrs: {circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}}
                },
                'out': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}}
                }
            },
            items: [
                {id: 'in', group: 'in', args: {x: ACTION_WIDTH / 2, y: 0}},
                {id: 'out', group: 'out', args: {x: ACTION_WIDTH / 2, y: ACTION_HEIGHT}}
            ]
        }
    });
}

function get_for(xCoord, yCoord) {
    let forElement = new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: ACTION_WIDTH, height: FOR_LOOP_HEIGHT},
        template:
            `<div class="for_element">
               <button class="delete">x</button>
               <div class="dashed-bot">
                 <span> for </span>
                 <input placeholder="Element" data-attribute="efor" type="text"/></input>
                 <span> in </span>
                 <input placeholder="Collection" data-attribute="ein"  type="text"/></input>
               </div>
               <textarea onkeyup="textAreaAdjust(this)" disabled  placeholder="Anweisungen" data-attribute="area"></textarea>
            </div>`,
        efor: '',
        ein: '',
        area: '',
        name: 'forin',
        cleanname: 'For-In-Schleife',
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
                                    '.': {'text-anchor': 'middle'}, text: {fill: 'black', 'pointer-events': 'none'}
                                }
                            }
                        }
                    },
                    attrs: {circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}}
                },
                'out': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}}
                },
                'extern': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'black', 'stroke-width': 1, r: 10, magnet: true}}
                }
            },
            items: [
                {id: 'in', group: 'in', args: {x: ACTION_WIDTH / 2, y: 0}},
                {id: 'extern', group: 'extern', args: {x: EXTERN_PORT_WIDTH, y: 55}},
                {id: 'out', group: 'out', args: {x: ACTION_WIDTH / 2, y: FOR_LOOP_HEIGHT}}]
        }
    });
    connectProperties.sourceId = forElement.id;
    connectProperties.sourcePort = "extern";

    return forElement;
}

function get_if(xCoord, yCoord) {
    let ifElseElem = new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: ACTION_WIDTH, height: IF_ELSE_HEIGHT},
        template:
            `<div class="if_element">
               <button class="delete">x</button>
               
               <div class="dashed-bot">
                 <span> if </span>
                 <input placeholder="Bedingung" data-attribute="eif" type="text"/></input>
               </div>
               <div class="dashed-bot">
                 <span>then</span>
                 <textarea onkeyup="textAreaAdjust(this)" disabled placeholder="Anweisungen" data-attribute="ethen"></textarea>
               </div>
               <div>
                 <span>else</span>
                 <textarea onkeyup="textAreaAdjust(this)" disabled placeholder="Anweisungen" data-attribute="eelse"></textarea>
               </div>
            </div>`,
        eif: '',
        ethen: '',
        eelse: '',
        name: 'if',
        cleanname: 'Bedingungsknoten',
        ports: {
            groups: {
                'in': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}}
                },
                'out': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}}
                },
                'extern-ethen': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'black', 'stroke-width': 1, r: 10, magnet: true}}
                },
                'extern-eelse': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'black', 'stroke-width': 1, r: 10, magnet: true}}
                }
            },
            items: [
                {id: 'in', group: 'in', args: {x: ACTION_WIDTH / 2, y: 0}},
                {id: 'out', group: 'out', args: {x: ACTION_WIDTH / 2, y: IF_ELSE_HEIGHT}},
                {id: 'extern-ethen', group: 'extern-ethen', args: {x: EXTERN_PORT_WIDTH, y: 75}},
                {id: 'extern-eelse', group: 'extern-eelse', args: {x: EXTERN_PORT_WIDTH, y: ACTION_WIDTH / 2}}
            ]
        }
    });

    connectProperties.sourceId = ifElseElem.id;
    connectProperties.sourcePort = "extern-ethen";
    connectProperties.sourcePort2 = "extern-eelse";

    return ifElseElem;

}

function createDoWhile(xCoord, yCoord) {
    let doWhileElem = new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: ACTION_WIDTH, height: WHILE_LOOP_HEIGHT},
        template:
            `<div class="wd_element">
               <button class="delete">x</button>
               <span>do</span>
               <textarea disabled onkeyup="textAreaAdjust(this)"  placeholder="Anweisungen"  data-attribute="edo"></textarea>
               <div class="dashed-top">
                 <span> while </span>
                 <input placeholder="Bedingung" data-attribute="ewhile" type="text"/></input>
               </div>
            </div>`,
        ewhile: '',
        edo: '',
        name: 'dw',
        cleanname: 'Do-While-Knoten',
        ports: {
            groups: {
                'in': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}}
                },
                'out': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}}
                },
                'extern': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'black', 'stroke-width': 1, r: 10, magnet: true}}
                }
            },
            items: [
                {id: 'in', group: 'in', args: {x: ACTION_WIDTH / 2, y: 0}},
                {id: 'out', group: 'out', args: {x: ACTION_WIDTH / 2, y: WHILE_LOOP_HEIGHT}},
                {id: 'extern', group: 'extern', args: {x: EXTERN_PORT_WIDTH, y: WHILE_LOOP_HEIGHT / 2}}]
        }
    });

    connectProperties.sourceId = doWhileElem.id;
    connectProperties.sourcePort = "extern";

    return doWhileElem;
}

function createWhileDo(xCoord, yCoord) {
    let whileDoElem = new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: ACTION_WIDTH, height: WHILE_LOOP_HEIGHT},
        template:
            `<div class="wd_element">
               <button class="delete">x</button>
            
               <div class="dashed-bot">
                 <span> while </span>
                 <input placeholder="Bedingung" data-attribute="ewhile" type="text"/></input>
               </div>
               
               <span>do</span>
               </br>
               <textarea disabled onkeyup="textAreaAdjust(this)" placeholder="Anweisungen" data-attribute="edo"></textarea>
            </div>`,
        ewhile: '',
        edo: '',
        name: 'wd',
        cleanname: 'While-Do-Knoten',
        ports: {
            groups: {
                'in': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}}
                },
                'out': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}}
                },
                'extern': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'black', 'stroke-width': 1, r: 10, magnet: true}}
                }
            },
            items: [
                {id: 'in', group: 'in', args: {x: ACTION_WIDTH / 2, y: 0}},
                {id: 'out', group: 'out', args: {x: ACTION_WIDTH / 2, y: WHILE_LOOP_HEIGHT}},
                {id: 'extern', group: 'extern', args: {x: EXTERN_PORT_WIDTH, y: WHILE_LOOP_HEIGHT / 2}}
            ]
        }
    });

    connectProperties.sourceId = whileDoElem.id;
    connectProperties.sourcePort = "extern";

    return whileDoElem;

}

function get_edit(xCoord, yCoord) {
    let edit = new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: 170, height: WHILE_LOOP_HEIGHT},
        template: [
            '<div class="edit_element">',
            '<button class="delete">x</button>',
            '</div>'
        ].join(''),
        name: 'edit',
        cleanname: 'Externer Knoten',
        ports: {
            groups: {
                'extern': {
                    position: 'absolute',
                    attrs: {
                        circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}
                    }
                }
            },
            items: [{
                id: 'extern',
                group: 'extern',
                args: {x: -10, y: 60}
            }]
        }
    });
    const start = createStartCircle('Externer Startknoten', edit.id, xCoord, yCoord, 'Start');
    const end = createEndCircle('Externer Endknoten', edit.id, xCoord + 118, yCoord + 68, 'Ende');

    graph.addCell(edit);
    edit.embed(start);
    start.toFront({deep: true});
    end.toFront({deep: true});
    graph.addCell(start);
    edit.embed(end);
    parentChildNodes.push({'parentId': edit.id, 'startId': start.id, 'endId': end.id, 'endName': end.name});
    //edit.attr('rect/magnet', true).attr('text/pointer-events', 'none');
    connectProperties.targetId = edit.id;
    connectProperties.targetPort = "extern";
    parentChildNodes.push({"parentId": edit.id, "startId": start.id, "endId": end.id, "endName": end.name});

    return end;
}


function get_basic(xCoord, yCoord) {
    return new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: 100, height: 100},
        template:
            `<div class="basic">
               <button class="delete">x</button>
               
               <div class="myDiv3">
                 <input placeholder="Bedingung" data-attribute="einput" class="myDiv4" type="text">
               </div>
               
               <div class="left"><span></span></div>
               
               <div class="right"></div>
               
               <div class="bot"><span></span></div>
               
               <div class="top"></div>
            </div>`,
        einput: '',
        name: 'unknown',
        cleanname: 'Verzweigungsknoten',
        ports: {
            groups: {
                'top': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}}
                },
                'left': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}}
                },
                'right': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'red', 'stroke-width': 1, r: 10, magnet: true}}
                },
                'bot': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}}
                }
            },
            items: [
                {id: 'top', group: 'top', args: {x: 53, y: -3}},
                {id: 'bot', group: 'bot', args: {x: 53, y: 104}},
                {id: 'left', group: 'left', args: {x: 0, y: 50}},
                {id: 'right', group: 'right', args: {x: 108, y: 50}}
            ]
        }
    });
}

function createEndCircle(endName, endId, endXCoord, endYCoord, labeltext) {
    return new joint.shapes.html.Element({
        position: {x: endXCoord, y: endYCoord},
        size: {width: START_END_SIZE, height: START_END_SIZE},
        id: 'Endknoten-' + endId,
        name: endName,
        cleanname: 'Endknoten',
        template:
            `<div class="circle1">
               <div class="circle2">
                 <div class="circle3">
                   <label class="endlabelpos" data-attribute="label"></label>
                 </div>
               </div>
             </div>`,
        label: labeltext,
        ports: {
            groups: {
                'in': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 22, magnet: true}}
                },
            },
            items: [
                {id: 'in', group: 'in', args: {x: START_END_SIZE / 2, y: START_END_SIZE / 2}}
            ]
        }
    });
}

function createStartCircle(startName, startId, startXCoord, startYCoord, labeltext) {
    return new joint.shapes.html.Element({
        position: {x: startXCoord, y: startYCoord},
        size: {width: START_END_SIZE, height: START_END_SIZE},
        id: 'Startknoten-' + startId,
        name: startName,
        cleanname: 'Startknoten',
        template:
            `<div class="circle1">
               <label class="startlabelpos" data-attribute="label"></label>
            </div>`,
        label: labeltext,
        ports: {
            groups: {
                'in': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 22, magnet: true}}
                },
            },
            items: [
                {id: 'in', group: 'in', args: {x: START_END_SIZE / 2, y: START_END_SIZE / 2}}
            ]
        }
    });
}