const EXTERN_PORT_WIDTH = 250;
const FOR_LOOP_HEIGHT = 150;
const WHILE_LOOP_HEIGHT = 120;
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
        { id: 'in', group: 'in', args: { x: STD_ACTIVITY_ELEMENT_WIDTH / 2 } },
        { id: 'out', group: 'out', args: { x: STD_ACTIVITY_ELEMENT_WIDTH / 2 } }
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
    size: { width: STD_ACTIVITY_ELEMENT_WIDTH, height: STD_FOR_LOOP_HEIGHT },
    attrs: {
        rect: { width: STD_ACTIVITY_ELEMENT_WIDTH },
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
    variable: '', collection: '', loopContent: [],
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
        const currectVar = this.get('variable');
        const variable = currectVar.length === 0 ? '___' : currectVar;
        const currentColl = this.get('collection');
        const collection = currentColl.length === 0 ? '___' : currentColl;
        return `for ${variable} in ${collection}:`;
    },
    isOkay() {
        return (this.get('variable') !== 0) && (this.get('collection').length !== 0);
    },
    getVariable() {
        return this.get('variable');
    },
    getCollection() {
        return this.get('collection');
    },
    getLoopContent() {
        const currentContent = this.get('loopContent');
        return (currentContent.length === 0) ? ['pass'] : currentContent;
    },
    updateRectangles() {
        const attrs = this.get('attrs');
        attrs['.for-header-text'].text = this.getLoopHeader();
        let loopContent = this.getLoopContent();
        let loopRectHeight = calcRectHeight(loopContent);
        attrs['.for-body-text'].text = loopContent.join('\n');
        attrs['.for-body-rect'].height = loopRectHeight;
        attrs['.for-complete-rect'].height = loopRectHeight + MIN_HEIGHT + 4;
        this.resize(STD_ACTIVITY_ELEMENT_WIDTH, loopRectHeight + MIN_HEIGHT + 4);
    }
});
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
        forLoopEditor.setValue(this.model.get('loopContent').join('\n'));
        forLoopEditor.clearSelection();
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
function updateForLoopText(button) {
    const model = graph.getCell($(button).data('cellId'));
    model.prop('variable', $('#forLoopVariableInput').val());
    model.prop('collection', $('#forLoopCollectionInput').val());
    let loopContent = forLoopEditor.getValue().split('\n').filter((l) => l.trim().length !== 0);
    model.prop('loopContent', loopContent);
    resetForLoopText();
}
joint.shapes.basic.Generic.define('uml.ForLoopEmbed', {
    size: { width: STD_ACTIVITY_ELEMENT_WIDTH, height: STD_FOR_LOOP_HEIGHT },
    attrs: {
        rect: { width: STD_ACTIVITY_ELEMENT_WIDTH },
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
    ports: {
        groups: {
            in: {
                position: 'top',
                attrs: { circle: { fill: 'transparent', stroke: COLORS.RoyalBlue, strokeWidth: 1, r: 10, magnet: true } }
            },
            externout: {
                position: 'absolute',
                attrs: { circle: { fill: 'transparent', stroke: COLORS.IndianRed, strokeWidth: 1, r: 10, magnet: true } }
            },
            externin: {
                position: 'absolute',
                attrs: { circle: { fill: 'transparent', stroke: COLORS.YellowGreen, strokeWidth: 1, r: 10, magnet: true } }
            },
            out: {
                position: 'bottom',
                attrs: { circle: { fill: 'transparent', stroke: COLORS.ForestGreen, strokeWidth: 1, r: 10, magnet: true } }
            },
        },
        items: [
            { id: 'in', group: 'in', args: { x: STD_ACTIVITY_ELEMENT_WIDTH / 2 } },
            { id: 'externout', group: 'externout', args: { x: STD_ACTIVITY_ELEMENT_WIDTH, y: STD_FOR_LOOP_HEIGHT / 3 } },
            { id: 'externin', group: 'externin', args: { x: STD_ACTIVITY_ELEMENT_WIDTH, y: 2 * STD_FOR_LOOP_HEIGHT / 3 } },
            { id: 'out', group: 'out', args: { x: STD_ACTIVITY_ELEMENT_WIDTH / 2 } }
        ]
    },
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
    },
    resizeForEmbedding(ownPosition, isResized) {
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
    size: { width: STD_ACTIVITY_ELEMENT_WIDTH, height: STD_FOR_LOOP_HEIGHT },
    attrs: {
        rect: { width: STD_ACTIVITY_ELEMENT_WIDTH },
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
        this.resize(STD_ACTIVITY_ELEMENT_WIDTH, loopRectHeight + MIN_HEIGHT + 4);
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
    size: { width: STD_ACTIVITY_ELEMENT_WIDTH, height: MIN_HEIGHT },
    attrs: {
        rect: { width: STD_ACTIVITY_ELEMENT_WIDTH, stroke: COLORS.Black, strokeWidth, rx: 5, ry: 5 },
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
            { id: 'in', group: 'in', args: { x: STD_ACTIVITY_ELEMENT_WIDTH / 2 } },
            { id: 'out', group: 'out', args: { x: STD_ACTIVITY_ELEMENT_WIDTH / 2 } }
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
        this.resize(STD_ACTIVITY_ELEMENT_WIDTH, offSetY);
    }
});
function resetActionInput() {
    $('#actionInputButton').data('cellId', '');
    $('#actionInputContent').val('');
    $('#actionInputEditSection').prop('hidden', true);
}
function updateActionInput(button) {
    graph.getCell($(button).data('cellId')).set('content', actionInputEditor.getValue());
    resetActionInput();
}
joint.shapes.uml.ActionInputView = joint.dia.ElementView.extend({
    events: STD_TEXT_ELEMENT_EVENTS,
    onLeftClick(event) {
        event.preventDefault();
        actionInputEditor.setValue(this.model.get('content'));
        actionInputEditor.clearSelection();
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
const NEW_IF_ELSE_HEIGHT = (4 * MIN_HEIGHT + 8);
const IF_ELSE_TEXT_MARKUP = `
<g class="rotatable">
    <g class="scalable">
        <rect class="if-header-rect"/>
        <rect class="if-header-separator-rect"/>
        <rect class="if-body-rect"/>
        
        <rect class="else-header-rect"/>
        <rect class="else-header-separator-rect"/>
        <rect class="else-body-rect"/>
        
        <rect class="body-separator-rect"/>
        
        <rect class="ifelse-complete-rect"/>
    </g>
    
    <text class="if-header-text"/>
    <text class="if-body-text"/>
    
    <text class="else-header-text"/>
    <text class="else-body-text"/>
</g>`.trim();
joint.shapes.basic.Generic.define('uml.IfElseText', {
    size: { width: STD_ACTIVITY_ELEMENT_WIDTH, height: NEW_IF_ELSE_HEIGHT },
    attrs: {
        rect: { width: STD_ACTIVITY_ELEMENT_WIDTH },
        '.ifelse-complete-rect': {
            height: NEW_IF_ELSE_HEIGHT, rx: 5, ry: 5, fill: 'none',
            stroke: COLORS.Black, strokeWidth, strokeDasharray: '5,5'
        },
        '.if-header-rect': { height: MIN_HEIGHT },
        '.if-header-separator-rect': {
            height: 1, transform: 'translate(0,' + (MIN_HEIGHT + 2) + ')',
            stroke: COLORS.Black, strokeWidth: 1
        },
        '.if-body-rect': {
            height: MIN_HEIGHT, transform: 'translate(0,' + (MIN_HEIGHT + 4) + ')',
            fill: COLORS.Gainsboro
        },
        '.body-separator-rect': {
            height: 1, transform: 'translate(0,' + (2 * MIN_HEIGHT + 4) + ')',
            stroke: COLORS.Black, strokeWidth: 1
        },
        '.else-header-rect': { height: MIN_HEIGHT, transform: 'translate(0,' + (2 * MIN_HEIGHT + 6) + ')' },
        '.else-header-separator-rect': {
            height: 1, transform: 'translate(0,' + (3 * MIN_HEIGHT + 6) + ')',
            stroke: COLORS.Black, strokeWidth: 1
        },
        '.else-body-rect': {
            height: MIN_HEIGHT, transform: 'translate(0,' + (3 * MIN_HEIGHT + 8) + ')',
            fill: COLORS.Gainsboro
        },
        text: { fill: COLORS.Black, fontSize, refY: STD_PADDING, refX: STD_PADDING },
        '.if-header-text': { ref: '.if-header-rect' }, '.if-body-text': { ref: '.if-body-rect' },
        '.else-header-text': { ref: '.else-header-rect' }, '.else-body-text': { ref: '.else-body-rect' }
    },
    ports: STD_PORTS,
    condition: '', ifContent: [], elseContent: []
}, {
    markup: IF_ELSE_TEXT_MARKUP,
    initialize() {
        this.on('change:condition change:ifContent change:elseContent', function () {
            this.updateRectangles();
            this.trigger('uml-update');
        }, this);
        this.updateRectangles();
        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    },
    getCondition() {
        return this.get('condition');
    },
    getIfHeaderContent() {
        const currectCond = this.get('condition');
        const condition = currectCond.length === 0 ? '___' : currectCond;
        return [`if ${condition}:`];
    },
    isOkay() {
        return this.get('condition').length !== 0;
    },
    getIfContent() {
        const currentContent = this.get('ifContent');
        return (currentContent.length === 0) ? ['pass'] : currentContent;
    },
    getElseTextContent() {
        const currentContent = this.get('elseContent');
        return (currentContent.length === 0) ? ['pass'] : currentContent;
    },
    getElseContent() {
        return this.get('elseContent');
    },
    updateRectangles() {
        const attrs = this.get('attrs');
        let offSetY = 0;
        let ifHeaderContent = this.getIfHeaderContent();
        let ifHeaderHeight = calcRectHeight(ifHeaderContent);
        let ifContent = this.getIfContent();
        let ifRectHeight = calcRectHeight(ifContent);
        let elseHeaderContent = ['else:'];
        let elseHeaderHeight = calcRectHeight(elseHeaderContent);
        let elseContent = this.getElseTextContent();
        let elseRectHeight = calcRectHeight(elseContent);
        attrs['.if-header-text'].text = ifHeaderContent;
        attrs['.if-header-rect'].transform = 'translate(0, ' + offSetY + ')';
        attrs['.if-header-rect'].height = ifHeaderHeight;
        offSetY += ifHeaderHeight;
        attrs['.if-header-separator-rect'].transform = 'translate(0, ' + offSetY + ')';
        attrs['.if-header-separator-rect'].height = 1;
        offSetY += 1;
        attrs['.if-body-text'].text = ifContent.join('\n');
        attrs['.if-body-rect'].transform = 'translate(0,' + offSetY + ')';
        attrs['.if-body-rect'].height = ifRectHeight;
        offSetY += ifRectHeight;
        attrs['.body-separator-rect'].transform = 'translate(0,' + offSetY + ')';
        attrs['.body-separator-rect'].height = 1;
        offSetY += 1;
        attrs['.else-header-text'].text = elseHeaderContent;
        attrs['.else-header-rect'].transform = 'translate(0, ' + offSetY + ')';
        attrs['.else-header-rect'].height = elseHeaderHeight;
        offSetY += elseHeaderHeight;
        attrs['.else-header-separator-rect'].transform = 'translate(0, ' + offSetY + ')';
        attrs['.else-header-separator-rect'].height = 1;
        offSetY += 1;
        attrs['.else-body-text'].text = elseContent.join('\n');
        attrs['.else-body-rect'].transform = 'translate(0, ' + offSetY + ')';
        attrs['.else-body-rect'].height = elseRectHeight;
        offSetY += elseRectHeight;
        attrs['.ifelse-complete-rect'].height = offSetY;
        this.resize(STD_ACTIVITY_ELEMENT_WIDTH, offSetY);
    }
});
function resetIfElseText() {
    $('#ifElseButton').data('cellId', '');
    $('#ifElseContent').val('');
    $('#ifElseEditSection').prop('hidden', true);
}
joint.shapes.uml.IfElseTextView = joint.dia.ElementView.extend({
    events: STD_TEXT_ELEMENT_EVENTS,
    onLeftClick(event) {
        event.preventDefault();
        $('#ifElseConditionInput').val(this.model.get('condition'));
        ifEditor.setValue(this.model.get('ifContent').join('\n'));
        ifEditor.clearSelection();
        elseEditor.setValue(this.model.get('elseContent').join('\n'));
        elseEditor.clearSelection();
        $('#ifElseButton').data('cellId', this.model.id);
        $('#ifElseEditSection').prop('hidden', false);
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
function updateIfElseText(button) {
    const model = graph.getCell($(button).data('cellId'));
    model.prop('condition', $('#ifElseConditionInput').val());
    let ifContent = ifEditor.getValue().split('\n').filter((l) => l.trim().length !== 0);
    let elseContent = elseEditor.getValue().split('\n').filter((l) => l.trim().length !== 0);
    model.prop('ifContent', ifContent);
    model.prop('elseContent', elseContent);
    resetIfElseText();
}
const FOR_LOOP_WIDTH = 400;
const FOR_LOOP_TEMPLATE = `
<div class="for_element">
    <button class="delete">x</button>
    
    <div class="dashed-bot">
        <div class="input-group">
            <span class="input-group-addon">for</span>
            <input placeholder="Element" class="form-control" data-attribute="efor" type="text">
            <span class="input-group-addon">in</span>
            <input placeholder="Collection" class="form-control" data-attribute="collectionName" type="text">
        </div>
    </div>
    
    <textarea onkeyup="textAreaAdjust(this)" class="form-control" disabled  placeholder="Anweisungen" data-attribute="area"></textarea>
</div>`.trim();
joint.shapes.html.NewForLoop = joint.shapes.html.Element.extend({
    defaults: _.defaultsDeep({
        size: { width: FOR_LOOP_WIDTH, height: FOR_LOOP_HEIGHT },
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
                                y: FOR_LOOP_HEIGHT,
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
                { id: 'in', group: 'in', args: { x: FOR_LOOP_WIDTH / 2 } },
                { id: 'extern', group: 'extern', args: { x: FOR_LOOP_WIDTH, y: 55 } },
                { id: 'out', group: 'out', args: { x: FOR_LOOP_WIDTH / 2 } }
            ]
        }
    }, joint.shapes.html.Element.prototype.defaults)
});
function createForLoop(xCoord, yCoord) {
    return new joint.shapes.html.NewForLoop({ position: { x: xCoord, y: yCoord } });
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
        size: { width: STD_ACTIVITY_ELEMENT_WIDTH, height: WHILE_LOOP_HEIGHT },
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
                { id: 'in', group: 'in', args: { x: STD_ACTIVITY_ELEMENT_WIDTH / 2, y: 0 } },
                { id: 'out', group: 'out', args: { x: STD_ACTIVITY_ELEMENT_WIDTH / 2, y: WHILE_LOOP_HEIGHT } },
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
        size: { width: STD_ACTIVITY_ELEMENT_WIDTH, height: WHILE_LOOP_HEIGHT },
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
                { id: 'in', group: 'in', args: { x: STD_ACTIVITY_ELEMENT_WIDTH / 2, y: 0 } },
                { id: 'out', group: 'out', args: { x: STD_ACTIVITY_ELEMENT_WIDTH / 2, y: WHILE_LOOP_HEIGHT } },
                { id: 'extern', group: 'extern', args: { x: EXTERN_PORT_WIDTH, y: WHILE_LOOP_HEIGHT / 2 } }
            ]
        }
    });
}
const EDIT_WIDTH = 320;
const EDIT_HEIGHT = 200;
joint.shapes.basic.Generic.define('uml.Edit', {
    size: { width: EDIT_WIDTH, height: EDIT_HEIGHT },
    attrs: {
        rect: { width: STD_ACTIVITY_ELEMENT_WIDTH },
        '.edit-complete-rect': {
            height: EDIT_HEIGHT, rx: 5, ry: 5,
            stroke: COLORS.Black, strokeWidth, strokeDasharray: '5,5'
        },
    },
    ports: {
        groups: {
            in: {
                position: 'top',
                attrs: { circle: { fill: 'transparent', stroke: COLORS.IndianRed, strokeWidth: 1, r: 10, magnet: true } }
            },
            out: {
                position: 'bottom',
                attrs: { circle: { fill: 'transparent', stroke: COLORS.YellowGreen, strokeWidth: 1, r: 10, magnet: true } }
            }
        },
        items: [
            { id: 'in', group: 'in', args: { x: 0, y: EDIT_HEIGHT / 3 } },
            { id: 'out', group: 'out', args: { x: 0, y: 2 * EDIT_HEIGHT / 3 } }
        ]
    }
}, {
    markup: `
<g class="rotatable">
    <g class="scalable">
        <rect class="edit-complete-rect"/>
    </g>
</g>`.trim(),
    initialize() {
        this.on('', function () {
            this.trigger('uml-update');
        }, this);
        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    },
});
joint.shapes.uml.EditView = joint.dia.ElementView.extend({
    events: STD_TEXT_ELEMENT_EVENTS,
    onLeftClick(event) {
        event.preventDefault();
        if (selElement !== '') {
            console.warn(event);
            let position = { position: { x: event.offsetX, y: event.offsetY } };
            console.warn(position);
            createElements(selElement, position, this.model);
        }
    },
    onRightClick(event) {
        event.preventDefault();
        if (confirm('Löschen?')) {
            for (let embedded of this.model.getEmbeddedCells()) {
                this.model.unembed(embedded);
                embedded.remove();
            }
            this.remove();
        }
    },
    initialize() {
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);
        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
    }
});
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
                'extern-in': {
                    position: 'absolute',
                    attrs: {
                        circle: { fill: 'transparent', stroke: COLORS.RoyalBlue, strokeWidth: 1, r: 10, magnet: true }
                    }
                },
                'extern-out': {
                    position: 'abolute',
                    attrs: {
                        circle: { fill: 'transparent', stroke: COLORS.ForestGreen, strokeWidth: 1, r: 10, magnet: true }
                    }
                }
            },
            items: [
                { id: 'extern-in', group: 'extern-in', args: { x: 0, y: WHILE_LOOP_HEIGHT / 3 } },
                { id: 'extern-out', group: 'extern-out', args: { x: 0, y: 2 * WHILE_LOOP_HEIGHT / 3 } }
            ]
        }
    });
    graph.addCell(edit);
    const start = createStartState(xCoord + STD_PADDING, yCoord + STD_PADDING);
    const end = createEndState(xCoord + 118, yCoord + 68);
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