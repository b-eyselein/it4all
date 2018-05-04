const EXTERN_PORT_WIDTH = 250;
const FOR_LOOP_HEIGHT = 100;
const WHILE_LOOP_HEIGHT = 120;
const IF_ELSE_HEIGHT = 180;
const STD_FOR_LOOP_HEIGHT = 100;
const IN_PORT = {
    position: 'top',
    attrs: { circle: { fill: 'transparent', stroke: COLORS.RoyalBlue, strokeWidth: 1, r: 10, magnet: true } }
};
const OUT_PORT = {
    position: 'bottom',
    attrs: { circle: { fill: 'transparent', stroke: COLORS.ForestGreen, strokeWidth: 1, r: 10, magnet: true } }
};
const STD_PORTS = {
    groups: { in: IN_PORT, out: OUT_PORT },
    items: [
        { id: 'in', group: 'in', args: { x: STD_ELEMENT_WIDTH / 2 } },
        { id: 'out', group: 'out', args: { x: STD_ELEMENT_WIDTH / 2 } }
    ]
};
const FOR_LOOP_TEXT_MARKUP = `
<g class="rotatable">
    <g class="scalable">
        <rect class="for-header-rect"/><rect class="for-body-rect"/><rect class="for-separator-rect"/><rect class="for-complete-rect"/>
    </g>
    <text class="for-body-text"/><text class="for-header-text"/>
</g>`.trim();
const WHILE_LOOP_TEXT_MARKUP = `
<g class="rotatable">
    <g class="scalable">
        <rect class="whileDo-header-rect"/><rect class="whileDo-body-rect"/><rect class="whileDo-separator-rect"/><rect class="whileDo-complete-rect"/>
    </g>
    <text class="whileDo-body-text"/><text class="whileDo-header-text"/>
</g>`.trim();
const stateCircleTransform = 'translate(' + (START_END_SIZE / 2) + ', ' + (START_END_SIZE / 2) + ')';
joint.shapes.basic.Generic.define('uml.CustomStartState', {
    size: { width: START_END_SIZE, height: START_END_SIZE },
    attrs: {
        circle: {
            transform: stateCircleTransform, r: START_END_SIZE / 2,
            fill: COLORS.Black, stroke: COLORS.Black, strokeWidth,
        }
    },
    ports: {
        groups: {
            out: {
                attrs: { circle: { fill: 'transparent', stroke: COLORS.ForestGreen, r: START_END_SIZE / 4, magnet: true } }
            }
        },
        items: [{ id: 'out', group: 'out', args: { x: START_END_SIZE / 2 } }]
    },
}, {
    markup: `<g class="rotatable"><g class="scalable"><circle/></g></g>`
});
joint.shapes.uml.CustomStartStateView = joint.dia.ElementView.extend({
    initialize() {
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);
        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
    }
});
function createStartState(x, y) {
    return new joint.shapes.uml.CustomStartState({ position: { x, y } });
}
joint.shapes.basic.Generic.define('uml.CustomEndState', {
    size: { width: START_END_SIZE, height: START_END_SIZE },
    attrs: {
        'circle.outer': {
            transform: stateCircleTransform, r: START_END_SIZE / 2,
            fill: COLORS.White, stroke: COLORS.Black
        },
        'circle.inner': { transform: stateCircleTransform, r: START_END_SIZE / 3, fill: COLORS.Black }
    },
    ports: {
        groups: {
            in: {
                attrs: { circle: { fill: 'transparent', stroke: COLORS.RoyalBlue, r: START_END_SIZE / 4, magnet: true } }
            }
        },
        items: [{ id: 'in', group: 'in', args: { x: START_END_SIZE / 2 } }]
    },
}, {
    markup: '<g class="rotatable"><g class="scalable"><circle class="outer"/><circle class="inner"/></g></g>'
});
joint.shapes.uml.CustomEndStateView = joint.dia.ElementView.extend({
    initialize() {
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);
        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
    }
});
function createEndState(x, y) {
    return new joint.shapes.uml.CustomEndState({ position: { x, y } });
}
joint.shapes.basic.Generic.define('uml.ForLoopText', {
    size: { width: STD_ELEMENT_WIDTH, height: STD_FOR_LOOP_HEIGHT },
    attrs: {
        rect: { width: STD_ELEMENT_WIDTH },
        '.for-complete-rect': {
            height: STD_FOR_LOOP_HEIGHT, rx: 5, ry: 5, fill: 'none',
            stroke: COLORS.Black, strokeWidth, strokeDasharray: '5,5'
        },
        '.for-header-rect': { height: MIN_HEIGHT },
        '.for-separator-rect': {
            height: 1, transform: 'translate(0,' + (MIN_HEIGHT + 2) + ')',
            stroke: COLORS.Black, strokeWidth: 1
        },
        '.for-body-rect': {
            height: STD_FOR_LOOP_HEIGHT - MIN_HEIGHT, transform: 'translate(0,' + (MIN_HEIGHT + 4) + ')',
            fill: COLORS.Gainsboro
        },
        text: { fill: COLORS.Black, fontSize, refY: STD_PADDING, refX: STD_PADDING },
        '.for-header-text': { ref: '.for-header-rect' },
        '.for-body-text': { ref: '.for-body-rect' }
    },
    ports: STD_PORTS,
    variable: 'x', collection: '[ ]', loopContent: ['pass'],
}, {
    markup: FOR_LOOP_TEXT_MARKUP,
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
        attrs['.for-header-text'].text = this.getLoopHeader();
        let loopContent = this.get('loopContent');
        let loopRectHeight = calcRectHeight(loopContent);
        attrs['.for-body-text'].text = loopContent.join('\n');
        attrs['.for-body-rect'].height = loopRectHeight;
        attrs['.for-complete-rect'].height = loopRectHeight + MIN_HEIGHT + 4;
        this.resize(STD_ELEMENT_WIDTH, loopRectHeight + MIN_HEIGHT + 4);
    }
});
function updateForLoopText(button) {
    const model = graph.getCell($(button).data('cellId'));
    model.prop('variable', $('#forLoopVariableInput').val());
    model.prop('collection', $('#forLoopCollectionInput').val());
    model.prop('loopContent', $('#forLoopContent').val().split('\n'));
    resetForLoopText();
}
function resetForLoopText() {
    $('#forLoopButton').data('cellId', '');
    $('#forLoopContent').val('');
    $('#forLoopEditSection').prop('hidden', true);
}
joint.shapes.uml.ForLoopTextView = joint.dia.ElementView.extend({
    events: STD_TEXT_ELEMENT_EVENTS,
    onLeftClick(event) {
        event.preventDefault();
        $('#forLoopVariableInput').val(this.model.get('variable'));
        $('#forLoopCollectionInput').val(this.model.get('collection'));
        $('#forLoopContent').val(this.model.get('loopContent'));
        $('#forLoopButton').data('cellId', this.model.id);
        $('#forLoopEditSection').prop('hidden', false);
    },
    onRightClick(event) {
        event.preventDefault();
        if (confirm('Löschen?'))
            this.remove();
    },
    initialize() {
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);
        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
    }
});
joint.shapes.basic.Generic.define('uml.ForLoopEmbed', {
    size: { width: STD_ELEMENT_WIDTH, height: STD_FOR_LOOP_HEIGHT },
    attrs: {
        rect: { width: STD_ELEMENT_WIDTH },
        '.for-complete-rect': {
            height: STD_FOR_LOOP_HEIGHT, rx: 5, ry: 5, fill: 'none',
            stroke: COLORS.Black, strokeWidth, strokeDasharray: '5,5'
        },
        '.for-header-rect': { height: MIN_HEIGHT },
        '.for-separator-rect': {
            height: 1, transform: 'translate(0,' + (MIN_HEIGHT + 2) + ')',
            stroke: COLORS.Black, strokeWidth: 1
        },
        '.for-body-rect': {
            height: STD_FOR_LOOP_HEIGHT - MIN_HEIGHT, transform: 'translate(0,' + (MIN_HEIGHT + 4) + ')'
        },
        text: { fill: COLORS.Black, fontSize, refY: STD_PADDING, refX: STD_PADDING },
        '.for-header-text': { ref: '.for-header-rect' },
        '.for-body-text': { ref: '.for-body-rect' }
    },
    ports: STD_PORTS,
    isResized: false, variable: 'x', collection: '[ ]', loopContent: []
}, {
    markup: FOR_LOOP_TEXT_MARKUP,
    initialize() {
        this.on('change:variable change:collection change:loopContent', function () {
            this.updateRectangles();
            this.trigger('uml-update');
        }, this);
        this.updateRectangles();
        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    },
    handleClick() {
        let ownPosition = this.position();
        let isResized = this.get('isResized');
        if (selElement === '') {
            this.resizeForEmbedding(ownPosition, isResized);
        }
        else {
            let positionObject = {
                position: { x: ownPosition.x + 50, y: ownPosition.y + 50 }
            };
            createElements(selElement, positionObject, this);
        }
    },
    resizeForEmbedding(ownPosition, isResized) {
        if (isResized) {
            let languageBuilder = getLangBuilder($('#langSelect').val());
            let embeddedCells = this.getEmbeddedCells();
            let startNode = embeddedCells.filter((cell) => {
                return cell instanceof joint.shapes.uml.CustomStartState;
            })[0];
            let endNode = embeddedCells.filter((cell) => {
                return cell instanceof joint.shapes.uml.CustomEndState;
            })[0];
            let code = readContentFromTo(languageBuilder, startNode, endNode);
            console.warn(code);
            for (let c of embeddedCells) {
                this.unembed(c);
                c.remove();
            }
        }
        else {
            console.warn(ownPosition.x + " :: " + ownPosition.y);
            const attrs = this.get('attrs');
            let newWidth = 2 * STD_ELEMENT_WIDTH;
            let loopContent = this.get('loopContent');
            let loopRectHeight = 300;
            attrs['.for-body-text'].text = loopContent.join('\n');
            attrs['.for-body-rect'].height = loopRectHeight;
            attrs['.for-complete-rect'].height = loopRectHeight + MIN_HEIGHT + 4;
            let startNode = createStartState(ownPosition.x + 10, ownPosition.y + 10 + MIN_HEIGHT);
            let endNode = createEndState(ownPosition.x + 100, ownPosition.y + 100);
            this.embed(startNode);
            this.embed(endNode);
            graph.addCell(startNode);
            graph.addCell(endNode);
            this.resize(newWidth, loopRectHeight + MIN_HEIGHT + 4);
            this.trigger('uml-update');
            this.set('isResized', true);
            console.warn(this.get('isResized'));
        }
    },
    getLoopHeader() {
        const variable = this.get('variable') || '___';
        const collection = this.get('collection') || '___';
        return `for ${variable} in ${collection}:`;
    },
    updateRectangles() {
        const attrs = this.get('attrs');
        attrs['.for-header-text'].text = this.getLoopHeader();
        let loopContent = this.get('loopContent');
        let loopRectHeight = calcRectHeight(loopContent);
        attrs['.for-body-text'].text = loopContent.join('\n');
        attrs['.for-body-rect'].height = loopRectHeight;
        attrs['.for-complete-rect'].height = loopRectHeight + MIN_HEIGHT + 4;
        this.resize(STD_ELEMENT_WIDTH, loopRectHeight + MIN_HEIGHT + 4);
    }
});
joint.shapes.uml.ForLoopEmbedView = joint.dia.ElementView.extend({
    events: STD_TEXT_ELEMENT_EVENTS,
    onLeftClick(event) {
        event.preventDefault();
        this.model.handleClick();
    },
    onRightClick(event) {
        event.preventDefault();
        if (confirm('Löschen?'))
            this.remove();
    },
    initialize() {
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);
        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
    }
});
joint.shapes.basic.Generic.define('uml.WhileLoop', {
    size: { width: STD_ELEMENT_WIDTH, height: STD_FOR_LOOP_HEIGHT },
    attrs: {
        rect: { width: STD_ELEMENT_WIDTH },
        '.whileDo-complete-rect': {
            height: STD_FOR_LOOP_HEIGHT, rx: 5, ry: 5, fill: 'none',
            stroke: COLORS.Black, strokeWidth, strokeDasharray: '5,5'
        },
        '.whileDo-header-rect': { height: MIN_HEIGHT },
        '.whileDo-separator-rect': {
            height: 1, transform: 'translate(0,' + (MIN_HEIGHT + 2) + ')',
            stroke: COLORS.Black, strokeWidth: 1
        },
        '.whileDo-body-rect': {
            height: STD_FOR_LOOP_HEIGHT - MIN_HEIGHT, transform: 'translate(0,' + (MIN_HEIGHT + 4) + ')',
            fill: COLORS.Gainsboro
        },
        text: { fill: COLORS.Black, fontSize, refY: STD_PADDING, refX: STD_PADDING },
        '.whileDo-header-text': { ref: '.whileDo-header-rect' },
        '.whileDo-body-text': { ref: '.whileDo-body-rect' }
    },
    ports: STD_PORTS,
    condition: 'false',
    loopContent: [],
}, {
    markup: WHILE_LOOP_TEXT_MARKUP,
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
        attrs['.whileDo-header-text'].text = this.getLoopHeader();
        let loopContent = this.get('loopContent');
        let loopRectHeight = calcRectHeight(loopContent);
        attrs['.whileDo-body-text'].text = loopContent.join('\n');
        attrs['.whileDo-body-rect'].height = loopRectHeight;
        attrs['.whileDo-complete-rect'].height = loopRectHeight + MIN_HEIGHT + 4;
        this.resize(STD_ELEMENT_WIDTH, loopRectHeight + MIN_HEIGHT + 4);
    }
});
joint.shapes.uml.WhileLoopView = joint.dia.ElementView.extend({
    events: STD_TEXT_ELEMENT_EVENTS,
    onLeftClick(event) {
        event.preventDefault();
        $('#conditionInput').val(this.model.attributes.condition);
        $('#forLoopContent').val(this.model.attributes.loopContent);
        $('#loopCellId').val(this.model.id);
        $('#loopEditModal').modal('show');
    },
    onRightClick(event) {
        event.preventDefault();
        if (confirm('Löschen?'))
            this.remove();
    },
    initialize() {
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);
        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
    }
});
joint.shapes.basic.Generic.define('uml.ActionInput', {
    size: { width: STD_ELEMENT_WIDTH, height: MIN_HEIGHT },
    attrs: {
        rect: { width: STD_ELEMENT_WIDTH, stroke: COLORS.Black, strokeWidth, rx: 5, ry: 5 },
        '.action-input-rect': {},
        text: { fill: COLORS.Black, fontSize, refY: STD_PADDING, refX: STD_PADDING },
        '.action-input-text': { ref: '.action-input-rect' },
    },
    ports: {
        groups: {
            'in': {
                position: 'top',
                attrs: { circle: { fill: 'transparent', stroke: COLORS.RoyalBlue, strokeWidth: 1, r: 10, magnet: true } }
            },
            'out': {
                position: 'bottom',
                attrs: { circle: { fill: 'transparent', stroke: COLORS.ForestGreen, strokeWidth: 1, r: 10, magnet: true } }
            }
        },
        items: [
            { id: 'in', group: 'in', args: { x: STD_ELEMENT_WIDTH / 2 } },
            { id: 'out', group: 'out', args: { x: STD_ELEMENT_WIDTH / 2 } }
        ]
    },
    content: '',
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
    getContent() {
        return this.get('content').split('\n');
    },
    updateRectangles() {
        const attrs = this.get('attrs');
        const rects = [
            { type: 'action-input', text: this.get('content').split('\n') },
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
function resetActionInput() {
    $('#actionInputButton').data('cellId', '');
    $('#actionInputContent').val('');
    $('#actionInputEditSection').prop('hidden', true);
}
function updateActionInput(button) {
    graph.getCell($(button).data('cellId')).set('content', $('#actionInputContent').val());
    resetActionInput();
}
joint.shapes.uml.ActionInputView = joint.dia.ElementView.extend({
    events: STD_TEXT_ELEMENT_EVENTS,
    onLeftClick(event) {
        event.preventDefault();
        $('#actionInputContent').val(this.model.get('content'));
        $('#actionInputButton').data('cellId', this.model.id);
        $('#actionInputEditSection').prop('hidden', false);
    },
    onRightClick(event) {
        event.preventDefault();
        if (confirm('Löschen?'))
            this.remove();
    },
    initialize() {
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);
        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
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
        position: { x: xCoord, y: yCoord },
        size: { width: STD_ELEMENT_WIDTH, height: MIN_HEIGHT },
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
                { id: 'in', group: 'in', args: { x: STD_ELEMENT_WIDTH / 2, y: 0 } },
                { id: 'out', group: 'out', args: { x: STD_ELEMENT_WIDTH / 2, y: MIN_HEIGHT } }
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
        position: { x: xCoord, y: yCoord },
        size: { width: STD_ELEMENT_WIDTH, height: MIN_HEIGHT },
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
                        position: {
                            name: 'manual', args: {
                                y: 250,
                                attrs: {
                                    '.': { 'text-anchor': 'middle' }, text: { fill: COLORS.Black, 'pointer-events': 'none' }
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
                { id: 'in', group: 'in', args: { x: STD_ELEMENT_WIDTH / 2, y: 0 } },
                { id: 'out', group: 'out', args: { x: STD_ELEMENT_WIDTH / 2, y: MIN_HEIGHT } }
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
    return new joint.shapes.html.Element({
        position: { x: xCoord, y: yCoord },
        size: { width: STD_ELEMENT_WIDTH, height: FOR_LOOP_HEIGHT },
        template: FOR_LOOP_TEMPLATE,
        efor: '',
        collectionName: '',
        area: '',
        name: 'forLoop',
        cleanname: 'For-In-Schleife',
        ports: {
            groups: {
                'in': {
                    position: 'top',
                    label: {
                        position: {
                            name: 'manual', args: {
                                y: 250,
                                attrs: {
                                    '.': { 'text-anchor': 'middle' }, text: { fill: COLORS.Black, 'pointer-events': 'none' }
                                }
                            }
                        }
                    },
                    attrs: {
                        circle: {
                            fill: 'transparent', stroke: COLORS.RoyalBlue, strokeWidth: 1, r: 10, magnet: true
                        }
                    }
                },
                'out': {
                    position: 'bottom',
                    attrs: {
                        circle: {
                            fill: 'transparent', stroke: COLORS.ForestGreen, strokeWidth: 1, r: 10, magnet: true
                        }
                    }
                },
                'extern': {
                    position: 'absolute',
                    attrs: { circle: { fill: 'transparent', stroke: COLORS.Black, strokeWidth: 1, r: 10, magnet: true } }
                }
            },
            items: [
                { id: 'in', group: 'in', args: { x: STD_ELEMENT_WIDTH / 2 } },
                { id: 'extern', group: 'extern', args: { x: EXTERN_PORT_WIDTH, y: 55 } },
                { id: 'out', group: 'out', args: { x: STD_ELEMENT_WIDTH / 2 } }
            ]
        }
    });
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
    return new joint.shapes.html.Element({
        position: { x: xCoord, y: yCoord },
        size: { width: STD_ELEMENT_WIDTH, height: WHILE_LOOP_HEIGHT },
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
                    attrs: { circle: { fill: 'transparent', stroke: COLORS.Black, strokeWidth: 1, r: 10, magnet: true } }
                }
            },
            items: [
                { id: 'in', group: 'in', args: { x: STD_ELEMENT_WIDTH / 2, y: 0 } },
                { id: 'out', group: 'out', args: { x: STD_ELEMENT_WIDTH / 2, y: WHILE_LOOP_HEIGHT } },
                { id: 'extern', group: 'extern', args: { x: EXTERN_PORT_WIDTH, y: WHILE_LOOP_HEIGHT / 2 } }
            ]
        }
    });
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
    return new joint.shapes.html.Element({
        position: { x: xCoord, y: yCoord },
        size: { width: STD_ELEMENT_WIDTH, height: IF_ELSE_HEIGHT },
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
                    attrs: { circle: { fill: 'transparent', stroke: COLORS.Black, strokeWidth: 1, r: 10, magnet: true } }
                },
                'extern-eelse': {
                    position: 'absolute',
                    attrs: { circle: { fill: 'transparent', stroke: COLORS.Black, strokeWidth: 1, r: 10, magnet: true } }
                }
            },
            items: [
                { id: 'in', group: 'in', args: { x: STD_ELEMENT_WIDTH / 2, y: 0 } },
                { id: 'out', group: 'out', args: { x: STD_ELEMENT_WIDTH / 2, y: IF_ELSE_HEIGHT } },
                { id: 'extern-ethen', group: 'extern-ethen', args: { x: EXTERN_PORT_WIDTH, y: 75 } },
                { id: 'extern-eelse', group: 'extern-eelse', args: { x: EXTERN_PORT_WIDTH, y: STD_ELEMENT_WIDTH / 2 } }
            ]
        }
    });
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
    return new joint.shapes.html.Element({
        position: { x: xCoord, y: yCoord },
        size: { width: STD_ELEMENT_WIDTH, height: WHILE_LOOP_HEIGHT },
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
                    attrs: { circle: { fill: 'transparent', stroke: COLORS.Black, strokeWidth: 1, r: 10, magnet: true } }
                }
            },
            items: [
                { id: 'in', group: 'in', args: { x: STD_ELEMENT_WIDTH / 2, y: 0 } },
                { id: 'out', group: 'out', args: { x: STD_ELEMENT_WIDTH / 2, y: WHILE_LOOP_HEIGHT } },
                { id: 'extern', group: 'extern', args: { x: EXTERN_PORT_WIDTH, y: WHILE_LOOP_HEIGHT / 2 } }
            ]
        }
    });
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
    return new joint.shapes.html.Element({
        position: { x: xCoord, y: yCoord },
        size: { width: STD_ELEMENT_WIDTH, height: WHILE_LOOP_HEIGHT },
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
                    attrs: { circle: { fill: 'transparent', stroke: COLORS.Black, strokeWidth: 1, r: 10, magnet: true } }
                }
            },
            items: [
                { id: 'in', group: 'in', args: { x: STD_ELEMENT_WIDTH / 2, y: 0 } },
                { id: 'out', group: 'out', args: { x: STD_ELEMENT_WIDTH / 2, y: WHILE_LOOP_HEIGHT } },
                { id: 'extern', group: 'extern', args: { x: EXTERN_PORT_WIDTH, y: WHILE_LOOP_HEIGHT / 2 } }
            ]
        }
    });
}
const EDIT_TEMPLATE = `
<div class="edit_element">
    <button class="delete">x</button>
</div>`;
function createEdit(xCoord, yCoord) {
    let edit = new joint.shapes.html.Element({
        position: { x: xCoord, y: yCoord },
        size: { width: 170, height: WHILE_LOOP_HEIGHT },
        template: EDIT_TEMPLATE,
        name: 'edit',
        cleanname: 'Externer Knoten',
        ports: {
            groups: {
                'extern': {
                    position: 'absolute',
                    attrs: {
                        circle: { fill: 'transparent', stroke: COLORS.RoyalBlue, strokeWidth: 1, r: 10, magnet: true }
                    }
                }
            },
            items: [
                { id: 'extern', group: 'extern', args: { x: 0, y: 60 } }
            ]
        }
    });
    const start = createStartState(xCoord + STD_PADDING, yCoord + STD_PADDING);
    const end = createEndState(xCoord + 118, yCoord + 68);
    graph.addCell(edit);
    edit.embed(start);
    edit.embed(end);
    start.toFront({ deep: true });
    end.toFront({ deep: true });
    graph.addCell(start);
    graph.addCell(end);
    parentChildNodes.push({ 'parentId': edit.id, 'startId': start.id, 'endId': end.id, 'endName': end.name });
    return edit;
}
//# sourceMappingURL=umlActivityElements.js.map