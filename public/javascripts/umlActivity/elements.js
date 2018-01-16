const externPortWidth = 250; // all excecpt if-then-else

const ACTION_HEIGHT = 40;
const ACTION_WIDTH = 250;

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
    let codeTextarea = '';
    let height = 50;
    let portExternHeight = 25;
    let portOutHeight = 50;
    if (version !== 1) {
        codeTextarea = '<textarea disabled onkeyup="textAreaAdjust(this)" placeholder="Anweisungen" data-attribute="area"></textarea>';
        height = 100;
        portExternHeight = 55;
        portOutHeight = 100;
    }
    if (version === 3) {
        codeTextarea = '<textarea onkeyup="textAreaAdjust(this)" placeholder="Anweisungen" data-attribute="area"></textarea>';
    }
    return new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: ACTION_WIDTH, height: height},
        template:
            `<div class="for_element">
               <button class="delete">x</button>
               <div class="dashed-bot">
                 <span> for </span>
                 <input placeholder="Element" data-attribute="efor" type="text"/></input>
                 <span> in </span>
                 <input placeholder="Collection" data-attribute="ein"  type="text"/></input>
               </div>
               ${codeTextarea}
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
                {id: 'extern', group: 'extern', args: {x: externPortWidth, y: portExternHeight}},
                {id: 'out', group: 'out', args: {x: ACTION_WIDTH / 2, y: portOutHeight}}]
        }
    });
}

function get_if(xCoord, yCoord) {
    let codeTextareaEthen = '';
    let codeTextareaEelse = '';
    let height = 75;
    let portExternHeight_Ethen = 38;
    let portExternHeight_Eelse = 61;
    let portOutHeight = 75;
    if (version !== 1) {
        codeTextareaEthen = '<textarea disabled onkeyup="textAreaAdjust(this)"  placeholder="Anweisungen" data-attribute="ethen"></textarea>';
        codeTextareaEelse = '<textarea disabled onkeyup="textAreaAdjust(this)"  placeholder="Anweisungen" data-attribute="eelse"></textarea>';
        height = 180;
        portExternHeight_Ethen = 75;
        portExternHeight_Eelse = ACTION_WIDTH / 2;
        portOutHeight = 180;
    }
    if (version === 3) {
        codeTextareaEthen = '<textarea  onkeyup="textAreaAdjust(this)"  placeholder="Anweisungen" data-attribute="ethen"></textarea>';
        codeTextareaEelse = '<textarea  onkeyup="textAreaAdjust(this)"  placeholder="Anweisungen" data-attribute="eelse"></textarea>';
    }
    return new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: ACTION_WIDTH, height: height},
        template:
            `<div class="if_element">
               <button class="delete">x</button>
               
               <div class="dashed-bot">
                 <span> if </span>
                 <input placeholder="Bedingung" data-attribute="eif" type="text"/></input>
               </div>
               <div class="dashed-bot">
                 <span>then</span>
                 ${codeTextareaEthen}
               </div>
               <div>
                 <span>else</span>
                 ${codeTextareaEelse}
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
                {id: 'out', group: 'out', args: {x: ACTION_WIDTH / 2, y: portOutHeight}},
                {id: 'extern-ethen', group: 'extern-ethen', args: {x: externPortWidth, y: portExternHeight_Ethen}},
                {id: 'extern-eelse', group: 'extern-eelse', args: {x: externPortWidth, y: portExternHeight_Eelse}}
            ]
        }
    });
}

function get_dw(xCoord, yCoord) {
    let codeTextarea = '';
    let height = 50;
    let portExternHeight = 15;
    let portOutHeight = 50;
    if (version !== 1) {
        codeTextarea = '<textarea disabled onkeyup="textAreaAdjust(this)"  placeholder="Anweisungen"  data-attribute="edo"></textarea>';
        height = 120;
        portExternHeight = 60;
        portOutHeight = 120;
    }
    if (version === 3) {
        codeTextarea = '<textarea onkeyup="textAreaAdjust(this)"  placeholder="Anweisungen"  data-attribute="edo"></textarea>';
    }

    return new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: ACTION_WIDTH, height: height},
        template:
            `<div class="wd_element">
               <button class="delete">x</button>
               <span>do</span>
               ${codeTextarea}
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
                {id: 'out', group: 'out', args: {x: ACTION_WIDTH / 2, y: portOutHeight}},
                {id: 'extern', group: 'extern', args: {x: externPortWidth, y: portExternHeight}}]
        }
    });
}

function get_wd(xCoord, yCoord) {
    let codeTextarea = '';
    let height = 50;
    let portExternHeight = 35;
    let portOutHeight = 50;

    if (version !== 1) {
        codeTextarea = '<textarea disabled onkeyup="textAreaAdjust(this)" placeholder="Anweisungen" data-attribute="edo"></textarea>';
        height = 120;
        portExternHeight = 60;
        portOutHeight = 120;
    }
    if (version === 3) {
        codeTextarea = '<textarea onkeyup="textAreaAdjust(this)" placeholder="Anweisungen" data-attribute="edo"></textarea>';
    }

    return new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: ACTION_WIDTH, height: height},
        template:
            `<div class="wd_element">
               <button class="delete">x</button>
            
               <div class="dashed-bot">
                 <span> while </span>
                 <input placeholder="Bedingung" data-attribute="ewhile" type="text"/></input>
               </div>
               
               <span>do</span>
               </br>
               ${codeTextarea}
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
                {id: 'out', group: 'out', args: {x: ACTION_WIDTH / 2, y: portOutHeight}},
                {id: 'extern', group: 'extern', args: {x: externPortWidth, y: portExternHeight}}
            ]
        }
    });
}

function get_edit(xCoord, yCoord) {
    let edit = new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: 170, height: 120},
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