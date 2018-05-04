const EXTERN_PORT_WIDTH = 250; // all except if-then-else

const ACTION_HEIGHT = calcRectHeight(['']);

const FOR_LOOP_HEIGHT = 100;
const WHILE_LOOP_HEIGHT = 120;
const IF_ELSE_HEIGHT = 180;

const STD_FOR_LOOP_HEIGHT = 100;

const IN_PORT = {
    position: 'top',
    attrs: {circle: {fill: 'transparent', stroke: COLORS.RoyalBlue, strokeWidth: 1, r: 10, magnet: true}}
};

const OUT_PORT = {
    position: 'bottom',
    attrs: {circle: {fill: 'transparent', stroke: COLORS.ForestGreen, strokeWidth: 1, r: 10, magnet: true}}
};

joint.shapes.basic.Generic.define('uml.ForLoop', {
    size: {width: STD_ELEMENT_WIDTH, height: STD_FOR_LOOP_HEIGHT},

    attrs: {
        rect: {width: STD_ELEMENT_WIDTH},
        '.for-complete-rect': {
            height: STD_FOR_LOOP_HEIGHT,
            stroke: COLORS.Black, strokeWidth, strokeDasharray: '5,5',
            rx: 5, ry: 5, fill: 'none'
        },
        '.for-header-rect': {height: MIN_HEIGHT},
        '.for-separator-rect': {
            height: 1, transform: 'translate(0,' + (MIN_HEIGHT + 2) + ')',
            stroke: COLORS.Black, strokeWidth: 1
        },
        '.for-body-rect': {
            height: STD_FOR_LOOP_HEIGHT - MIN_HEIGHT, transform: 'translate(0,' + (MIN_HEIGHT + 4) + ')',
            fill: COLORS.Gainsboro
        },

        text: {fill: COLORS.Black, fontSize, refY: STD_PADDING, refX: STD_PADDING},
        '.for-header-text': {ref: '.for-header-rect'},
        '.for-body-text': {ref: '.for-body-rect'}
    },

    ports: {
        groups: {in: IN_PORT, out: OUT_PORT},
        items: [
            {id: 'in', group: 'in', args: {x: STD_ELEMENT_WIDTH / 2}},
            {id: 'out', group: 'out', args: {x: STD_ELEMENT_WIDTH / 2}}
        ]
    },

    variable: 'x',
    collection: '[ ]',
    loopContent: ['pass'],
}, {

    markup: `
<g class="rotatable">
    <g class="scalable">
        <rect class="for-header-rect"/><rect class="for-body-rect"/><rect class="for-separator-rect"/><rect class="for-complete-rect"/>
    </g>
    <text class="for-body-text"/><text class="for-header-text"/>
</g>`.trim(),

    initialize(): void {
        this.on('change:variable change:collection change:loopContent', function () {
            this.updateRectangles();
            this.trigger('uml-update');
        }, this);

        this.updateRectangles();

        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    },

    getLoopHeader(): string {
        const variable = this.get('variable') || '___';
        const collection = this.get('collection') || '___';
        return `for ${variable} in ${collection}:`;
    },

    updateRectangles(): void {
        const attrs = this.get('attrs');

        attrs['.for-header-text'].text = this.getLoopHeader();

        let loopContent: string[] = this.get('loopContent');
        let loopRectHeight = calcRectHeight(loopContent);

        attrs['.for-body-text'].text = loopContent.join('\n');
        attrs['.for-body-rect'].height = loopRectHeight;

        attrs['.for-complete-rect'].height = loopRectHeight + MIN_HEIGHT + 4;

        this.resize(STD_ELEMENT_WIDTH, loopRectHeight + MIN_HEIGHT + 4);
    }
});

function updateForLoop(button: HTMLButtonElement) {
    const model = graph.getCell($(button).data('cellId'));

    model.prop('variable', $('#forLoopVariableInput').val());
    model.prop('collection', $('#forLoopCollectionInput').val());
    model.prop('loopContent', $('#forLoopContent').val().split('\n'));

    resetForLoop();
}


function resetForLoop(): void {
    $('#forLoopButton').data('cellId', '');
    $('#forLoopContent').val('');
    $('#forLoopEditSection').prop('hidden', true);
}


joint.shapes.uml.ForLoopView = joint.dia.ElementView.extend({
    initialize() {
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
    },

    handleLeftClick() {
        $('#forLoopVariableInput').val(this.model.get('variable'));
        $('#forLoopCollectionInput').val(this.model.get('collection'));
        $('#forLoopContent').val(this.model.get('loopContent'));
        $('#forLoopButton').data('cellId', this.model.id);
        $('#forLoopEditSection').prop('hidden', false);
    },

    handleRightClick() {
        if (confirm('Löschen?'))
            this.remove();
    }
});

joint.shapes.basic.Generic.define('uml.WhileLoop', {
    size: {width: STD_ELEMENT_WIDTH, height: STD_FOR_LOOP_HEIGHT},

    attrs: {
        rect: {width: STD_ELEMENT_WIDTH, stroke: COLORS.Black, strokeWidth},
        '.while-header-rect': {},
        '.while-body-rect': {fill: COLORS.LightGrey, transform: 'translate(0, 50)'},

        text: {fill: COLORS.Black, fontSize, refY: 7, refX: 5},
        '.while-header-text': {ref: '.while-header-rect',},
        '.while-body-text': {ref: '.while-body-rect'}
    },

    ports: {
        groups: {in: IN_PORT, out: OUT_PORT},
        items: [
            {id: 'in', group: 'in', args: {x: STD_ELEMENT_WIDTH / 2}},
            {id: 'out', group: 'out', args: {x: STD_ELEMENT_WIDTH / 2}}
        ]
    },

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
            const rectHeight = calcRectHeight(lines);

            attrs['.while-' + rect.type + '-text'].text = lines.join('\n');
            attrs['.while-' + rect.type + '-rect'].height = rectHeight;
            attrs['.while-' + rect.type + '-rect'].transform = 'translate(0, ' + offSetY + ')';

            offSetY += rectHeight;
        });

        this.resize(STD_ELEMENT_WIDTH, offSetY);
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
        $('#forLoopContent').val(this.model.attributes.loopContent);
        $('#loopCellId').val(this.model.id);

        $('#loopEditModal').modal('show');
    },

    handleRightClick() {
        this.remove();
    }
});

joint.shapes.basic.Generic.define('uml.ActionInput', {
    size: {width: STD_ELEMENT_WIDTH, height: ACTION_HEIGHT},

    attrs: {
        rect: {width: STD_ELEMENT_WIDTH, stroke: COLORS.Black, strokeWidth, rx: 5, ry: 5},
        '.action-input-rect': {},

        text: {fill: COLORS.Black, fontSize, refY: STD_PADDING, refX: STD_PADDING},
        '.action-input-text': {ref: '.action-input-rect'},
    },

    ports: {
        groups: {
            'in': {
                position: 'top',
                attrs: {circle: {fill: 'transparent', stroke: COLORS.RoyalBlue, strokeWidth: 1, r: 10, magnet: true}}
            },
            'out': {
                position: 'bottom',
                attrs: {circle: {fill: 'transparent', stroke: COLORS.ForestGreen, strokeWidth: 1, r: 10, magnet: true}}
            }
        },
        items: [
            {id: 'in', group: 'in', args: {x: STD_ELEMENT_WIDTH / 2}},
            {id: 'out', group: 'out', args: {x: STD_ELEMENT_WIDTH / 2}}
        ]
    },

    // Attributes
    content: <string> '',
}, {
    markup: `
<g class="rotatable">
    <g class="scalable">
        <rect class="action-input-rect"/>
    </g>
    <text class="action-input-text"/>
</g>`.trim(),


    initialize() {
        this.on('change:content', function () {
            this.updateRectangles();
            this.trigger('uml-update');
        }, this);

        this.updateRectangles();

        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    },

    getContent(): string[] {
        return this.get('content').split('\n');
    },

    updateRectangles() {
        const attrs = this.get('attrs');

        const rects = [
            {type: 'action-input', text: this.get('content').split('\n')},
        ];

        let offSetY = 0;

        rects.forEach(function (rect) {
            const lines = Array.isArray(rect.text) ? rect.text : [rect.text];
            const rectHeight = calcRectHeight(lines);

            attrs['.' + rect.type + '-text'].text = lines.join('\n');
            attrs['.' + rect.type + '-rect'].height = rectHeight;
            attrs['.' + rect.type + '-rect'].transform = 'translate(0, ' + offSetY + ')';

            offSetY += rectHeight;
        });

        this.resize(STD_ELEMENT_WIDTH, offSetY);
    }

});

function resetActionInput(): void {
    $('#actionInputButton').data('cellId', '');
    $('#actionInputContent').val('');
    $('#actionInputEditSection').prop('hidden', true);
}

function updateActionInput(button: HTMLButtonElement): void {
    graph.getCell($(button).data('cellId')).set('content', $('#actionInputContent').val());
    resetActionInput();
}

joint.shapes.uml.ActionInputView = joint.dia.ElementView.extend({
    initialize() {
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
    },

    handleLeftClick() {
        $('#actionInputContent').val(this.model.get('content'));
        $('#actionInputButton').data('cellId', this.model.id);
        $('#actionInputEditSection').prop('hidden', false);
    },

    handleRightClick() {
        this.remove();
    }
});

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
        size: {width: STD_ELEMENT_WIDTH, height: ACTION_HEIGHT},
        name: 'actionSelect',
        cleanname: 'Aktionsknoten',
        template: ACTION_SELECT_TEMPALTE,
        actionElementContent: '',
        ports: {
            groups: {
                'in': {
                    position: 'absolute',
                    attrs: {
                        circle: {
                            fill: 'transparent',
                            stroke: COLORS.RoyalBlue,
                            strokeWidth: 1,
                            r: 10,
                            magnet: true
                        }
                    }
                },
                'out': {
                    position: 'absolute',
                    attrs: {
                        circle: {
                            fill: 'transparent',
                            stroke: COLORS.ForestGreen,
                            strokeWidth: 1,
                            r: 10,
                            magnet: true
                        }
                    }
                }
            },
            items: [
                {id: 'in', group: 'in', args: {x: STD_ELEMENT_WIDTH / 2, y: 0}},
                {id: 'out', group: 'out', args: {x: STD_ELEMENT_WIDTH / 2, y: ACTION_HEIGHT}}
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
        size: {width: STD_ELEMENT_WIDTH, height: ACTION_HEIGHT},
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
                                    '.': {'text-anchor': 'middle'}, text: {fill: COLORS.Black, 'pointer-events': 'none'}
                                }
                            }
                        }
                    },
                    attrs: {
                        circle: {
                            fill: 'transparent',
                            stroke: COLORS.RoyalBlue,
                            strokeWidth: 1,
                            r: 10,
                            magnet: true
                        }
                    }
                },
                'out': {
                    position: 'absolute',
                    attrs: {
                        circle: {
                            fill: 'transparent',
                            stroke: COLORS.ForestGreen,
                            strokeWidth: 1,
                            r: 10,
                            magnet: true
                        }
                    }
                }
            },
            items: [
                {id: 'in', group: 'in', args: {x: STD_ELEMENT_WIDTH / 2, y: 0}},
                {id: 'out', group: 'out', args: {x: STD_ELEMENT_WIDTH / 2, y: ACTION_HEIGHT}}
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
        size: {width: STD_ELEMENT_WIDTH, height: FOR_LOOP_HEIGHT},
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
                                    '.': {'text-anchor': 'middle'}, text: {fill: COLORS.Black, 'pointer-events': 'none'}
                                }
                            }
                        }
                    },
                    attrs: {
                        circle: {
                            fill: 'transparent',
                            stroke: COLORS.RoyalBlue,
                            strokeWidth: 1,
                            r: 10,
                            magnet: true
                        }
                    }
                },
                'out': {
                    position: 'absolute',
                    attrs: {
                        circle: {
                            fill: 'transparent',
                            stroke: COLORS.ForestGreen,
                            strokeWidth: 1,
                            r: 10,
                            magnet: true
                        }
                    }
                },
                'extern': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: COLORS.Black, strokeWidth: 1, r: 10, magnet: true}}
                }
            },
            items: [
                {id: 'in', group: 'in', args: {x: STD_ELEMENT_WIDTH / 2, y: 0}},
                {id: 'extern', group: 'extern', args: {x: EXTERN_PORT_WIDTH, y: 55}},
                {id: 'out', group: 'out', args: {x: STD_ELEMENT_WIDTH / 2, y: FOR_LOOP_HEIGHT}}]
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
        size: {width: STD_ELEMENT_WIDTH, height: WHILE_LOOP_HEIGHT},
        template: IF_THEN_TEMPLATE,
        eif: '',
        ethen: '',
        name: 'ifThen',
        cleanname: 'If-Then-Knoten',
        ports: {
            groups: {
                'in': {
                    position: 'absolute',
                    attrs: {
                        circle: {
                            fill: 'transparent',
                            stroke: COLORS.RoyalBlue,
                            strokeWidth: 1,
                            r: 10,
                            magnet: true
                        }
                    }
                },
                'out': {
                    position: 'absolute',
                    attrs: {
                        circle: {
                            fill: 'transparent',
                            stroke: COLORS.ForestGreen,
                            strokeWidth: 1,
                            r: 10,
                            magnet: true
                        }
                    }
                },
                'extern': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: COLORS.Black, strokeWidth: 1, r: 10, magnet: true}}
                }
            },
            items: [
                {id: 'in', group: 'in', args: {x: STD_ELEMENT_WIDTH / 2, y: 0}},
                {id: 'out', group: 'out', args: {x: STD_ELEMENT_WIDTH / 2, y: WHILE_LOOP_HEIGHT}},
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
        size: {width: STD_ELEMENT_WIDTH, height: IF_ELSE_HEIGHT},
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
                    attrs: {
                        circle: {
                            fill: 'transparent',
                            stroke: COLORS.RoyalBlue,
                            strokeWidth: 1,
                            r: 10,
                            magnet: true
                        }
                    }
                },
                'out': {
                    position: 'absolute',
                    attrs: {
                        circle: {
                            fill: 'transparent',
                            stroke: COLORS.ForestGreen,
                            strokeWidth: 1,
                            r: 10,
                            magnet: true
                        }
                    }
                },
                'extern-ethen': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: COLORS.Black, strokeWidth: 1, r: 10, magnet: true}}
                },
                'extern-eelse': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: COLORS.Black, strokeWidth: 1, r: 10, magnet: true}}
                }
            },
            items: [
                {id: 'in', group: 'in', args: {x: STD_ELEMENT_WIDTH / 2, y: 0}},
                {id: 'out', group: 'out', args: {x: STD_ELEMENT_WIDTH / 2, y: IF_ELSE_HEIGHT}},
                {id: 'extern-ethen', group: 'extern-ethen', args: {x: EXTERN_PORT_WIDTH, y: 75}},
                {id: 'extern-eelse', group: 'extern-eelse', args: {x: EXTERN_PORT_WIDTH, y: STD_ELEMENT_WIDTH / 2}}
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
        size: {width: STD_ELEMENT_WIDTH, height: WHILE_LOOP_HEIGHT},
        template: DO_WHILE_TEMPLATE,
        ewhile: '',
        edo: '',
        name: 'doWhile',
        cleanname: 'Do-While-Knoten',
        ports: {
            groups: {
                'in': {
                    position: 'absolute',
                    attrs: {
                        circle: {
                            fill: 'transparent',
                            stroke: COLORS.RoyalBlue,
                            strokeWidth: 1,
                            r: 10,
                            magnet: true
                        }
                    }
                },
                'out': {
                    position: 'absolute',
                    attrs: {
                        circle: {
                            fill: 'transparent',
                            stroke: COLORS.ForestGreen,
                            strokeWidth: 1,
                            r: 10,
                            magnet: true
                        }
                    }
                },
                'extern': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: COLORS.Black, strokeWidth: 1, r: 10, magnet: true}}
                }
            },
            items: [
                {id: 'in', group: 'in', args: {x: STD_ELEMENT_WIDTH / 2, y: 0}},
                {id: 'out', group: 'out', args: {x: STD_ELEMENT_WIDTH / 2, y: WHILE_LOOP_HEIGHT}},
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
        size: {width: STD_ELEMENT_WIDTH, height: WHILE_LOOP_HEIGHT},
        template: WHILE_DO_TEMPLATE,
        ewhile: '',
        edo: '',
        name: 'whileDo',
        cleanname: 'While-Do-Knoten',
        ports: {
            groups: {
                'in': {
                    position: 'absolute',
                    attrs: {
                        circle: {
                            fill: 'transparent',
                            stroke: COLORS.RoyalBlue,
                            strokeWidth: 1,
                            r: 10,
                            magnet: true
                        }
                    }
                },
                'out': {
                    position: 'absolute',
                    attrs: {
                        circle: {
                            fill: 'transparent',
                            stroke: COLORS.ForestGreen,
                            strokeWidth: 1,
                            r: 10,
                            magnet: true
                        }
                    }
                },
                'extern': {
                    position: 'absolute',
                    attrs: {circle: {fill: 'transparent', stroke: COLORS.Black, strokeWidth: 1, r: 10, magnet: true}}
                }
            },
            items: [
                {id: 'in', group: 'in', args: {x: STD_ELEMENT_WIDTH / 2, y: 0}},
                {id: 'out', group: 'out', args: {x: STD_ELEMENT_WIDTH / 2, y: WHILE_LOOP_HEIGHT}},
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
                        circle: {fill: 'transparent', stroke: COLORS.RoyalBlue, strokeWidth: 1, r: 10, magnet: true}
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

function createEndCircle(endName: string, endId: string, endXCoord: number, endYCoord: number, labelText: string) {
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
                    attrs: {
                        circle: {
                            fill: 'transparent',
                            stroke: COLORS.RoyalBlue,
                            strokeWidth: 1,
                            r: 22,
                            magnet: true
                        }
                    }
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

function createStartCircle(startName: string, startId: string, startXCoord: number, startYCoord: number, labelText: string) {
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
                    attrs: {
                        circle: {
                            fill: 'transparent',
                            stroke: COLORS.RoyalBlue,
                            strokeWidth: 1,
                            r: START_END_SIZE / 2,
                            magnet: true
                        }
                    }
                },
            },
            items: [
                {id: 'out', group: 'out', args: {x: START_END_SIZE / 2, y: START_END_SIZE / 2}}
            ]
        }
    });
}
