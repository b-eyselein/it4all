const EXTERN_PORT_WIDTH = 250; // all except if-then-else

const ACTION_HEIGHT = 40;
const ACTION_WIDTH = 250;

const FOR_LOOP_HEIGHT = 100;
const WHILE_LOOP_HEIGHT = 120;
const IF_ELSE_HEIGHT = 180;

const START_END_SIZE = 50;

const FOR_LOOP_WIDTH = 250;
const STD_FOR_LOOP_HEIGHT = 100;

const ACTION_INPUT_TEMPLATE = `
<div class="action-element">
    <button class="delete">x</button>
    <textarea onkeyup="textAreaAdjust(this)" placeholder="Anweisungen" data-attribute="actionElementContent"></textarea>
</div>`.trim();

const IN_PORT = {
    position: 'top',
    attrs: {circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}}
};

const OUT_PORT = {
    position: 'bottom',
    attrs: {circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}}
};

const COLORS = Object.freeze({
    BLACK: '#000000',
    WHITE: '#ffffff',
    LIGHT_GREY: '#dddddd'
});

function clamp(number, min, max) {
    return Math.min(Math.max(number, min), max);
}

joint.shapes.basic.Generic.define('uml.ForLoop', {
    size: {width: FOR_LOOP_WIDTH, height: STD_FOR_LOOP_HEIGHT},

    attrs: {
        rect: {width: FOR_LOOP_WIDTH, stroke: COLORS.BLACK, 'stroke-width': 2},
        '.for-header-rect': {fill: COLORS.WHITE},
        '.for-body-rect': {fill: COLORS.LIGHT_GREY, transform: 'translate(0, 50)'},

        text: {fill: COLORS.BLACK, fontSize: 16, refY: 7, refX: 5},
        '.for-header-text': {ref: '.for-header-rect',},
        '.for-body-text': {ref: '.for-body-rect'}
    },

    ports: {
        groups: {in: IN_PORT, out: OUT_PORT},
        items: [
            {id: 'in', group: 'in', args: {x: FOR_LOOP_WIDTH / 2}},
            {id: 'out', group: 'out', args: {x: FOR_LOOP_WIDTH / 2}}
        ]
    },

    // name is for converter.js!
    name: 'uml.ForLoop',

    // Attributes
    variable: '',
    collection: '',
    loopContent: [],
}, {

    markup: `
<g class="rotatable">
    <g class="scalable">
        <rect class="for-header-rect"/><rect class="for-body-rect"/>
    </g>
    <text class="for-header-text"/><text class="for-body-text"/>
</g>`.trim(),

    initialize() {

        this.on('change:variable change:collection change:loopContent', function () {
            this.updateRectangles();
            this.trigger('uml-update');
        }, this);

        this.updateRectangles();

        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    },

    getLoopHeader() {
        const variable = this.get('variable') || '___';
        const collection = this.get('collection') || '___';
        return `for ${variable} in ${collection}:`;
    },

    updateRectangles() {
        const attrs = this.get('attrs');

        const rects = [
            {type: 'header', text: this.getLoopHeader(), maxHeight: 30, minHeight: 30},
            {type: 'body', text: this.get('loopContent'), minHeight: 70, maxHeight: 270}
        ];

        let offSetY = 0;

        rects.forEach(function (rect) {
            const lines = Array.isArray(rect.text) ? rect.text : [rect.text];
            const rectHeight = clamp(lines.length * 25 + 10, rect.minHeight, rect.maxHeight);

            attrs['.for-' + rect.type + '-text'].text = lines.join('\n');
            attrs['.for-' + rect.type + '-rect'].height = rectHeight;
            attrs['.for-' + rect.type + '-rect'].transform = 'translate(0, ' + offSetY + ')';

            offSetY += rectHeight;
        })
    }
});

joint.shapes.uml.ForLoopView = joint.dia.ElementView.extend({
    initialize() {
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
    },

    handleLeftClick() {
        $('#variableInput').val(this.model.attributes.variable);
        $('#collectionInput').val(this.model.attributes.collection);
        $('#loopContentTextarea').val(this.model.attributes.loopContent);
        $('#loopCellId').val(this.model.id);

        $('#loopEditModal').modal('show');
    },

    handleRightClick() {
        if (confirm('Löschen?'))
            this.remove();
    }
});

joint.shapes.basic.Generic.define('uml.WhileLoop', {
    size: {width: FOR_LOOP_WIDTH, height: STD_FOR_LOOP_HEIGHT},

    attrs: {
        rect: {width: FOR_LOOP_WIDTH, stroke: COLORS.BLACK, 'stroke-width': 2},
        '.while-header-rect': {fill: COLORS.WHITE},
        '.while-body-rect': {fill: COLORS.LIGHT_GREY, transform: 'translate(0, 50)'},

        text: {fill: COLORS.BLACK, fontSize: 16, refY: 7, refX: 5},
        '.while-header-text': {ref: '.while-header-rect',},
        '.while-body-text': {ref: '.while-body-rect'}
    },

    ports: {
        groups: {in: IN_PORT, out: OUT_PORT},
        items: [
            {id: 'in', group: 'in', args: {x: FOR_LOOP_WIDTH / 2}},
            {id: 'out', group: 'out', args: {x: FOR_LOOP_WIDTH / 2}}
        ]
    },

    // name is for converter.js!
    name: 'uml.WhileLoop',

    // Attributes
    condition: '',
    loopContent: [],
}, {

    markup: `
<g class="rotatable">
    <g class="scalable">
        <rect class="while-header-rect"/><rect class="while-body-rect"/>
    </g>
    <text class="while-header-text"/><text class="while-body-text"/>
</g>`.trim(),

    initialize() {

        this.on('change:condition change:loopContent', function () {
            this.updateRectangles();
            this.trigger('uml-update');
        }, this);

        this.updateRectangles();

        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    },

    getLoopHeader() {
        return `while ${this.get('condition')}:`;
    },

    updateRectangles() {
        const attrs = this.get('attrs');

        const rects = [
            {type: 'header', text: this.getLoopHeader(), maxHeight: 30, minHeight: 30},
            {type: 'body', text: this.get('loopContent'), minHeight: 70, maxHeight: 270}
        ];

        let offSetY = 0;

        rects.forEach(function (rect) {
            const lines = Array.isArray(rect.text) ? rect.text : [rect.text];
            const rectHeight = clamp(lines.length * 25 + 10, rect.minHeight, rect.maxHeight);

            attrs['.while-' + rect.type + '-text'].text = lines.join('\n');
            attrs['.while-' + rect.type + '-rect'].height = rectHeight;
            attrs['.while-' + rect.type + '-rect'].transform = 'translate(0, ' + offSetY + ')';

            offSetY += rectHeight;
        })
    }
});

joint.shapes.uml.WhileLoopView = joint.dia.ElementView.extend({
    initialize() {
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
    },

    handleLeftClick() {
        $('#conditionInput').val(this.model.attributes.condition);
        $('#loopContentTextarea').val(this.model.attributes.loopContent);
        $('#loopCellId').val(this.model.id);

        $('#loopEditModal').modal('show');
    },

    handleRightClick() {
        this.remove();
    }
});

function updateForLoop() {
    $('#loopEditModal').modal('hide');

    const model = graph.getCell($('#loopCellId').val());
    model.prop('variable', $('#variableInput').val());
    model.prop('collection', $('#collectionInput').val());
    model.prop('loopContent', $('#loopContentTextarea').val().split('\n'));
}

function createActionInput(xCoord, yCoord) {
    return new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: ACTION_WIDTH, height: ACTION_HEIGHT},
        name: 'actionInput',
        cleanname: 'Aktionsknoten',
        template: ACTION_INPUT_TEMPLATE,
        actionElementContent: '',
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

const ACTION_SELECT_TEMPALTE = `
<div class="action-element">
    <button class="delete">x</button>
    <select data-attribute="actionElementContent">
        <option></option>
        <option>getNutzlast()</option>
        <option>getContent()</option>
        <option>Example()</option>
    </select>
</div>`.trim();

function createActionSelect(xCoord, yCoord) {
    return new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: ACTION_WIDTH, height: ACTION_HEIGHT},
        name: 'actionSelect',
        cleanname: 'Aktionsknoten',
        template: ACTION_SELECT_TEMPALTE,
        actionElementContent: '',
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

const ACTION_DECLARE_TEMPLATE = `
<div class="actionDeclare">
    <button class="delete">x</button>
    <select data-attribute="varContent1">
        <option></option>
        <option>String</option>
        <option>Double</option>
        <option>Boolean</option>
    </select>
    <input placeholder="Var" class="smallInput" data-attribute="varContent2" type="text">
    <span> = </span>
    <input placeholder="Anweisung" class="normalInput" data-attribute="varContent3" type="text">
</div>`.trim();

function createActionDeclare(xCoord, yCoord) {
    return new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: ACTION_WIDTH, height: ACTION_HEIGHT},
        template: ACTION_DECLARE_TEMPLATE,
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

const FOR_LOOP_TEMPLATE = `
<div class="for_element">
    <button class="delete">x</button>
    <div class="dashed-bot">
        <span> for </span>
        <input placeholder="Element" data-attribute="efor" type="text">
        <span> in </span>
        <input placeholder="Collection" data-attribute="collectionName" type="text">
    </div>
    <textarea onkeyup="textAreaAdjust(this)" disabled  placeholder="Anweisungen" data-attribute="area"></textarea>
</div>`.trim();

function createForLoop(xCoord, yCoord) {
    let forElement = new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: ACTION_WIDTH, height: FOR_LOOP_HEIGHT},
        template: FOR_LOOP_TEMPLATE,
        efor: '',
        collectionName: '',
        area: '',
        name: 'forLoop',
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

const IF_THEN_TEMPLATE = `
<div class="wd_element">
    <button class="delete">x</button>
            
    <div class="dashed-bot">
        <span> if </span>
        <input placeholder="Bedingung" data-attribute="eif" type="text"/></input>
    </div>

    <span>then</span>
    
    </br>
    
    <textarea disabled onkeyup="textAreaAdjust(this)" placeholder="Anweisungen" data-attribute="ethen"></textarea>
</div>`.trim();

function createIfThen(xCoord, yCoord) {
    let ifThenElem = new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: ACTION_WIDTH, height: WHILE_LOOP_HEIGHT},
        template: IF_THEN_TEMPLATE,
        eif: '',
        ethen: '',
        name: 'ifThen',
        cleanname: 'If-Then-Knoten',
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

    connectProperties.sourceId = ifThenElem.id;
    connectProperties.sourcePort = "extern";

    return ifThenElem;

}

const IF_ELSE_TEMPLATE = `
<div class="if_element">
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
</div>`.trim();

function createIfElse(xCoord, yCoord) {
    let ifElseElem = new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: ACTION_WIDTH, height: IF_ELSE_HEIGHT},
        template: IF_ELSE_TEMPLATE,
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

const DO_WHILE_TEMPLATE = `
<div class="wd_element">
    <button class="delete">x</button>

    <span>do</span>
    <textarea disabled onkeyup="textAreaAdjust(this)"  placeholder="Anweisungen"  data-attribute="edo"></textarea>

    <div class="dashed-top">
        <span> while </span>
        <input placeholder="Bedingung" data-attribute="ewhile" type="text"/></input>
    </div>
</div>`.trim();

function createDoWhile(xCoord, yCoord) {
    let doWhileElem = new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: ACTION_WIDTH, height: WHILE_LOOP_HEIGHT},
        template: DO_WHILE_TEMPLATE,
        ewhile: '',
        edo: '',
        name: 'doWhile',
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

const WHILE_DO_TEMPLATE = `
<div class="wd_element">
    <button class="delete">x</button>
            
    <div class="dashed-bot">
        <span> while </span>
        <input placeholder="Bedingung" data-attribute="ewhile" type="text"/></input>
    </div>

    <span>do</span>
    
    </br>
    
    <textarea disabled onkeyup="textAreaAdjust(this)" placeholder="Anweisungen" data-attribute="edo"></textarea>
</div>`;

function createWhileDo(xCoord, yCoord) {
    let whileDoElem = new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: ACTION_WIDTH, height: WHILE_LOOP_HEIGHT},
        template: WHILE_DO_TEMPLATE,
        ewhile: '',
        edo: '',
        name: 'whileDo',
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

const EDIT_TEMPLATE = `
<div class="edit_element">
    <button class="delete">x</button>
</div>`;

function createEdit(xCoord, yCoord) {
    let edit = new joint.shapes.html.Element({
        position: {x: xCoord, y: yCoord},
        size: {width: 170, height: WHILE_LOOP_HEIGHT},
        template: EDIT_TEMPLATE,
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
            items: [
                {id: 'extern', group: 'extern', args: {x: -10, y: 60}}
            ]
        }
    });

    const start = createStartCircle('Externer Startknoten', edit.id, xCoord, yCoord, 'Start');
    const end = createEndCircle('Externer Endknoten', edit.id, xCoord + 118, yCoord + 68, 'Ende');

    graph.addCell(edit);

    edit.embed(start);
    edit.embed(end);

    start.toFront({deep: true});
    end.toFront({deep: true});

    graph.addCell(start);
    graph.addCell(end);

    parentChildNodes.push({'parentId': edit.id, 'startId': start.id, 'endId': end.id, 'endName': end.name});
    parentChildNodes.push({"parentId": edit.id, "startId": start.id, "endId": end.id, "endName": end.name});

    connectProperties.targetId = edit.id;
    connectProperties.targetPort = "extern";

    // Add edit twice --> bug...
    return edit;
}

const END_CIRCLE_TEMPLATE = `
<div class="circle1">
    <div class="circle2">
        <div class="circle3">
            <label class="endlabelpos" data-attribute="label"></label>
        </div>
    </div>
</div>`;

function createEndCircle(endName, endId, endXCoord, endYCoord, labelText) {
    return new joint.shapes.html.Element({
        position: {x: endXCoord, y: endYCoord},
        size: {width: START_END_SIZE, height: START_END_SIZE},
        id: 'Endknoten-' + endId,
        name: endName,
        cleanname: 'Endknoten',
        template: END_CIRCLE_TEMPLATE,
        label: labelText,
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

const START_CIRCLE_TEMPLATE = `
<div class="circle1">
    <label class="startlabelpos" data-attribute="label"></label>
</div>`;

function createStartCircle(startName, startId, startXCoord, startYCoord, labelText) {
    return new joint.shapes.html.Element({
        position: {x: startXCoord, y: startYCoord},
        size: {width: START_END_SIZE, height: START_END_SIZE},
        id: 'Startknoten-' + startId,
        name: startName,
        cleanname: 'Startknoten',
        template: START_CIRCLE_TEMPLATE,
        label: labelText,
        ports: {
            groups: {
                'out': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 22, magnet: true}}
                },
            },
            items: [
                {id: 'out', group: 'out', args: {x: START_END_SIZE / 2, y: START_END_SIZE / 2}}
            ]
        }
    });
}
