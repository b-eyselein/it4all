import * as joint from 'jointjs';
import * as _ from 'lodash';
import 'bootstrap';

//FIXME: type!

import {
    createElements,
    editActionInput,
    elseEditor,
    forLoopEditor,
    ifEditor,
    parentChildNodes,
    selElement,
    umlActivityGraph
} from './umlActivityDrawing';


import {
    calcRectHeight,
    COLORS,
    fontSize,
    MIN_HEIGHT,
    START_END_SIZE,
    STD_ACTIVITY_ELEMENT_WIDTH,
    STD_PADDING,
    STD_TEXT_ELEMENT_EVENTS,
    strokeWidth
} from '../umlConsts';
import {HtmlElement} from "./umlADHtmlElement";


export {
    StartState, StartStateView, createStartState,
    EndState, EndStateView, createEndState,
    ForLoopText, ForLoopTextView,
    ActionInput, ActionInputView,
    ForLoopEmbed, ForLoopEmbedView,
    WhileLoop, WhileLoopView,
    IfElseText, IfElseTextView,
    Edit, EditView,
    EDIT_HEIGHT, EDIT_WIDTH,
    createElements, createDoWhile, createEdit, createForLoop, createWhileDo
}

const EXTERN_PORT_WIDTH = 250; // all except if-then-else

const FOR_LOOP_HEIGHT = 150;
const WHILE_LOOP_HEIGHT = 120;

const STD_FOR_LOOP_HEIGHT = 100;

const IN_PORT = {
    position: 'top',
    attrs: {circle: {fill: 'transparent', stroke: COLORS.RoyalBlue, strokeWidth: 1, r: 10, magnet: true}}
};

const OUT_PORT = {
    position: 'bottom',
    attrs: {circle: {fill: 'transparent', stroke: COLORS.ForestGreen, strokeWidth: 1, r: 10, magnet: true}}
};

const STD_PORTS = {
    groups: {in: IN_PORT, out: OUT_PORT},
    items: [
        {id: 'in', group: 'in', args: {x: STD_ACTIVITY_ELEMENT_WIDTH / 2}},
        {id: 'out', group: 'out', args: {x: STD_ACTIVITY_ELEMENT_WIDTH / 2}}
    ]
};

// Markups

const FOR_LOOP_TEXT_MARKUP: string = `
<g class="rotatable">
    <g class="scalable">
        <rect class="for-header-rect"/><rect class="for-body-rect"/><rect class="for-separator-rect"/><rect class="for-complete-rect"/>
    </g>
    <text class="for-body-text"/><text class="for-header-text"/>
</g>`.trim();

const WHILE_LOOP_TEXT_MARKUP: string = `
<g class="rotatable">
    <g class="scalable">
        <rect class="whileDo-header-rect"/><rect class="whileDo-body-rect"/><rect class="whileDo-separator-rect"/><rect class="whileDo-complete-rect"/>
    </g>
    <text class="whileDo-body-text"/><text class="whileDo-header-text"/>
</g>`.trim();

// Element definitions

const stateCircleTransform = 'translate(' + (START_END_SIZE / 2) + ', ' + (START_END_SIZE / 2) + ')';

class StartState extends joint.shapes.basic.Generic {

    private static MARKUP = `<g class="rotatable"><g class="scalable"><circle/></g></g>`;

    constructor(attributes?: joint.dia.Element.Attributes, options?: joint.dia.Graph.Options) {
        super(attributes, options);
        this.set('markup', StartState.MARKUP);
    }

    defaults() {
        return _.defaultsDeep({
            size: {width: START_END_SIZE, height: START_END_SIZE},

            attrs: {circle: {transform: stateCircleTransform, r: START_END_SIZE / 2, fill: COLORS.Black}},

            ports: {
                groups: {
                    out: {
                        attrs: {
                            circle: {
                                fill: 'transparent', stroke: COLORS.ForestGreen,
                                r: START_END_SIZE / 4, magnet: true
                            }
                        }
                    }
                },
                items: [{id: 'out', group: 'out', args: {x: START_END_SIZE / 2}}]
            }

        }, joint.shapes.basic.Generic.prototype.defaults);
    }

}

class StartStateView extends joint.dia.ElementView {
    initialize() {
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
    }
}

function createStartState(x: number, y: number): joint.dia.Element {
    return new StartState().position(x, y);
}


class EndState extends joint.shapes.basic.Generic {

    private static MARKUP = '<g class="rotatable"><g class="scalable"><circle class="outer"/><circle class="inner"/></g></g>'

    constructor(attributes?: joint.dia.Element.Attributes, options?: joint.dia.Graph.Options) {
        super(attributes, options);
        this.set('markup', EndState.MARKUP);
    }

    defaults() {
        return _.defaultsDeep({
            type: 'EndState',
            size: {width: START_END_SIZE, height: START_END_SIZE},

            attrs: {
                'circle.outer': {
                    transform: stateCircleTransform, r: START_END_SIZE / 2,
                    fill: COLORS.White, stroke: COLORS.Black
                },
                'circle.inner': {transform: stateCircleTransform, r: START_END_SIZE / 3, fill: COLORS.Black}
            },

            ports: {
                groups: {
                    in: {
                        attrs: {
                            circle: {
                                fill: 'transparent',
                                stroke: COLORS.RoyalBlue,
                                r: START_END_SIZE / 4,
                                magnet: true
                            }
                        }
                    }
                },
                items: [{id: 'in', group: 'in', args: {x: START_END_SIZE / 2}}]
            }
        }, joint.shapes.basic.Generic.prototype.defaults);
    }

}

class EndStateView extends joint.dia.ElementView {
    initialize() {
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
    }
}

function createEndState(x: number, y: number): joint.dia.Element {
    return new EndState().position(x, y);
}

// For loop - text

class ForLoopText extends joint.shapes.basic.Generic {

    private static MARKUP = FOR_LOOP_TEXT_MARKUP;

    constructor(attributes?: joint.dia.Element.Attributes, options?: joint.dia.Graph.Options) {
        super(attributes, options);
        this.set('markup', ForLoopText.MARKUP);
    }

    defaults() {
        return _.defaultsDeep({
            type: 'uml.ForLoopText',
            size: {width: STD_ACTIVITY_ELEMENT_WIDTH, height: STD_FOR_LOOP_HEIGHT},

            attrs: {
                rect: {width: STD_ACTIVITY_ELEMENT_WIDTH},
                '.for-complete-rect': {
                    height: STD_FOR_LOOP_HEIGHT, rx: 5, ry: 5, fill: 'none',
                    stroke: COLORS.Black, strokeWidth, strokeDasharray: '5,5'
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

            ports: STD_PORTS,

            variable: '', collection: '', loopContent: [],
        }, joint.shapes.basic.Generic.prototype.defaults);
    }


    initialize(): void {
        this.on('change:variable change:collection change:loopContent', function () {
            this.updateRectangles();
            this.trigger('uml-update');
        }, this);

        this.updateRectangles();

        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    }

    getLoopHeader(): string {
        const currectVar = this.get('variable');
        const variable = currectVar.length === 0 ? '___' : currectVar;

        const currentColl = this.get('collection');
        const collection = currentColl.length === 0 ? '___' : currentColl;

        return `for ${variable} in ${collection}:`;
    }

    isOkay() {
        return (this.get('variable') !== 0) && (this.get('collection').length !== 0);
    }

    getVariable(): string {
        return this.get('variable');
    }

    getCollection(): string {
        return this.get('collection');
    }

    getLoopContent(): string[] {
        const currentContent: string[] = this.get('loopContent');
        return (currentContent.length === 0) ? ['pass'] : currentContent;
    }

    updateRectangles(): void {
        const attrs = this.get('attrs');

        attrs['.for-header-text'].text = this.getLoopHeader();

        let loopContent: string[] = this.getLoopContent();
        let loopRectHeight = calcRectHeight(loopContent);

        attrs['.for-body-text'].text = loopContent.join('\n');
        attrs['.for-body-rect'].height = loopRectHeight;

        attrs['.for-complete-rect'].height = loopRectHeight + MIN_HEIGHT + 4;

        this.resize(STD_ACTIVITY_ELEMENT_WIDTH, loopRectHeight + MIN_HEIGHT + 4);
    }
}

function resetForLoopText(): void {
    $('#forLoopButton').data('cellId', '');
    $('#forLoopContent').val('');
    $('#forLoopEditSection').prop('hidden', true);
}

class ForLoopTextView extends joint.dia.ElementView {
    // events: STD_TEXT_ELEMENT_EVENTS,

    onLeftClick(event) {
        event.preventDefault();
        $('#forLoopVariableInput').val(this.model.get('variable'));
        $('#forLoopCollectionInput').val(this.model.get('collection'));

        forLoopEditor.setValue(this.model.get('loopContent').join('\n'));

        $('#forLoopButton').data('cellId', this.model.id);
        $('#forLoopEditSection').prop('hidden', false);
    }

    onRightClick(event) {
        event.preventDefault();
        if (confirm('Löschen?'))
            this.remove();
    }

    initialize() {
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
    }
}

function updateForLoopText(button: HTMLButtonElement) {
    const model = umlActivityGraph.getCell($(button).data('cellId'));

    model.prop('variable', $('#forLoopVariableInput').val());
    model.prop('collection', $('#forLoopCollectionInput').val());

    let loopContent = forLoopEditor.getValue().split('\n').filter((l) => l.trim().length !== 0);

    model.prop('loopContent', loopContent);

    resetForLoopText();
}

class ForLoopEmbed extends joint.shapes.basic.Generic {

    private static MARKUP = FOR_LOOP_TEXT_MARKUP;

    constructor(attributes?: joint.dia.Element.Attributes, options?: joint.dia.Graph.Options) {
        super(attributes, options);
        this.set('markup', ForLoopEmbed.MARKUP);
    }

    defaults() {
        return _.defaultsDeep({
            type: 'uml.ForLoopEmbed',
            size: {width: STD_ACTIVITY_ELEMENT_WIDTH, height: STD_FOR_LOOP_HEIGHT},

            attrs: {
                rect: {width: STD_ACTIVITY_ELEMENT_WIDTH},
                '.for-complete-rect': {
                    height: STD_FOR_LOOP_HEIGHT, rx: 5, ry: 5, fill: 'none',
                    stroke: COLORS.Black, strokeWidth, strokeDasharray: '5,5'
                },
                '.for-header-rect': {height: MIN_HEIGHT},
                '.for-separator-rect': {
                    height: 1, transform: 'translate(0,' + (MIN_HEIGHT + 2) + ')',
                    stroke: COLORS.Black, strokeWidth: 1
                },
                '.for-body-rect': {
                    height: STD_FOR_LOOP_HEIGHT - MIN_HEIGHT, transform: 'translate(0,' + (MIN_HEIGHT + 4) + ')'
                },

                text: {fill: COLORS.Black, fontSize, refY: STD_PADDING, refX: STD_PADDING},
                '.for-header-text': {ref: '.for-header-rect'},
                '.for-body-text': {ref: '.for-body-rect'}
            },

            ports: {
                groups: {
                    in: {
                        position: 'top',
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
                    externout: {
                        position: 'absolute',
                        attrs: {
                            circle: {
                                fill: 'transparent',
                                stroke: COLORS.IndianRed,
                                strokeWidth: 1,
                                r: 10,
                                magnet: true
                            }
                        }
                    },
                    externin: {
                        position: 'absolute',
                        attrs: {
                            circle: {
                                fill: 'transparent',
                                stroke: COLORS.YellowGreen,
                                strokeWidth: 1,
                                r: 10,
                                magnet: true
                            }
                        }
                    },
                    out: {
                        position: 'bottom',
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

                },
                items: [
                    {id: 'in', group: 'in', args: {x: STD_ACTIVITY_ELEMENT_WIDTH / 2}},
                    {
                        id: 'externout',
                        group: 'externout',
                        args: {x: STD_ACTIVITY_ELEMENT_WIDTH, y: STD_FOR_LOOP_HEIGHT / 3}
                    },
                    {
                        id: 'externin',
                        group: 'externin',
                        args: {x: STD_ACTIVITY_ELEMENT_WIDTH, y: 2 * STD_FOR_LOOP_HEIGHT / 3}
                    },
                    {id: 'out', group: 'out', args: {x: STD_ACTIVITY_ELEMENT_WIDTH / 2}}
                ]
            },

            isResized: false, variable: 'x', collection: '[ ]', loopContent: []
        }, joint.shapes.basic.Generic.prototype.defaults);
    }


    initialize(): void {
        this.on('change:variable change:collection change:loopContent', function () {
            this.updateRectangles();
            this.trigger('uml-update');
        }, this);

        this.updateRectangles();

        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    }


    handleClick() {
        // let ownPosition: { x: number, y: number } = this.position();
        // let isResized: boolean = this.get('isResized');
        //
        // if (selElement === '') {
        //     this.resizeForEmbedding(ownPosition, isResized);
        // } else {
        //     let positionObject = {
        //         position: {x: ownPosition.x + 50, y: ownPosition.y + 50}
        //     };
        //
        //     createElements(selElement, positionObject, this);
        // }
    }

    resizeForEmbedding(ownPosition: { x: number, y: number }, isResized: boolean) {
        // if (isResized) {
        // // FIXME: generate code!
        // let languageBuilder = getLangBuilder($('#langSelect').val());
        // let embeddedCells = this.getEmbeddedCells();
        //
        // let startNode = embeddedCells.filter((cell) => {
        //     return cell instanceof joint.shapes.uml.CustomStartState;
        // })[0];
        //
        // let endNode = embeddedCells.filter((cell) => {
        //     return cell instanceof joint.shapes.uml.CustomEndState;
        // })[0];
        //
        // let code = readContentFromTo(languageBuilder, startNode, endNode);
        //
        // console.warn(code);
        //
        // // for (let c of embeddedCells) {
        // //    this.unembed(c);
        // //    c.remove();
        // //}
        // } else {
        //     console.warn(ownPosition.x + " :: " + ownPosition.y);
        //
        //     const attrs = this.get('attrs');
        //
        //     let newWidth: number = 2 * STD_ACTIVITY_ELEMENT_WIDTH;
        //
        //     let loopContent: string[] = this.get('loopContent');
        //     let loopRectHeight = 300;
        //
        //     attrs['.for-body-text'].text = loopContent.join('\n');
        //     attrs['.for-body-rect'].height = loopRectHeight;
        //
        //     attrs['.for-complete-rect'].height = loopRectHeight + MIN_HEIGHT + 4;
        //
        // //  FIXME: add start and end node..
        // let startNode = createStartState(ownPosition.x + 10, ownPosition.y + 10 + MIN_HEIGHT);
        // let endNode = createEndState(ownPosition.x + 100, ownPosition.y + 100);
        //
        // this.embed(startNode);
        // this.embed(endNode);
        //
        // umlActivityGraph.addCell(startNode);
        // umlActivityGraph.addCell(endNode);
        //
        // this.resize(newWidth, loopRectHeight + MIN_HEIGHT + 4);
        // this.trigger('uml-update');
        // this.set('isResized', true);
        //
        // console.warn(this.get('isResized'));
        // }
    }

    getLoopHeader(): string {
        const variable = this.get('variable') || '___';
        const collection = this.get('collection') || '___';
        return `for ${variable} in ${collection}:`;
    }

    updateRectangles(): void {
        const attrs = this.get('attrs');

        attrs['.for-header-text'].text = this.getLoopHeader();

        let loopContent: string[] = this.get('loopContent');
        let loopRectHeight = calcRectHeight(loopContent);

        attrs['.for-body-text'].text = loopContent.join('\n');
        attrs['.for-body-rect'].height = loopRectHeight;

        attrs['.for-complete-rect'].height = loopRectHeight + MIN_HEIGHT + 4;

        // this.resize(STD_ACTIVITY_ELEMENT_WIDTH, loopRectHeight + MIN_HEIGHT + 4);
    }
}

class ForLoopEmbedView extends joint.dia.ElementView {
    // events: STD_TEXT_ELEMENT_EVENTS,

    onLeftClick(event) {
        event.preventDefault();
        (this.model as ForLoopEmbed).handleClick();
    }

    onRightClick(event) {
        event.preventDefault();
        if (confirm('Löschen?'))
            this.remove();
    }

    initialize() {
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
    }
}


class WhileLoop extends joint.shapes.basic.Generic {

    private static MARKUP = WHILE_LOOP_TEXT_MARKUP;

    constructor(attributes?: joint.dia.Element.Attributes, options?: joint.dia.Graph.Options) {
        super(attributes, options);
        this.set('markup', WhileLoop.MARKUP);
    }

    defaults() {
        return _.defaultsDeep({
            type: 'uml.WhileLoop',
            size: {width: STD_ACTIVITY_ELEMENT_WIDTH, height: STD_FOR_LOOP_HEIGHT},

            attrs: {
                rect: {width: STD_ACTIVITY_ELEMENT_WIDTH},
                '.whileDo-complete-rect': {
                    height: STD_FOR_LOOP_HEIGHT, rx: 5, ry: 5, fill: 'none',
                    stroke: COLORS.Black, strokeWidth, strokeDasharray: '5,5'
                },
                '.whileDo-header-rect': {height: MIN_HEIGHT},
                '.whileDo-separator-rect': {
                    height: 1, transform: 'translate(0,' + (MIN_HEIGHT + 2) + ')',
                    stroke: COLORS.Black, strokeWidth: 1
                },
                '.whileDo-body-rect': {
                    height: STD_FOR_LOOP_HEIGHT - MIN_HEIGHT, transform: 'translate(0,' + (MIN_HEIGHT + 4) + ')',
                    fill: COLORS.Gainsboro
                },

                text: {fill: COLORS.Black, fontSize, refY: STD_PADDING, refX: STD_PADDING},
                '.whileDo-header-text': {ref: '.whileDo-header-rect'},
                '.whileDo-body-text': {ref: '.whileDo-body-rect'}
            },

            ports: STD_PORTS,

            condition: 'false',
            loopContent: [],
        })
    }


    initialize() {

        this.on('change:condition change:loopContent', function () {
            this.updateRectangles();
            this.trigger('uml-update');
        }, this);

        this.updateRectangles();

        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    }

    getLoopHeader() {
        return `while ${this.get('condition')}:`;
    }

    updateRectangles() {
        const attrs = this.get('attrs');

        attrs['.whileDo-header-text'].text = this.getLoopHeader();

        let loopContent: string[] = this.get('loopContent');
        let loopRectHeight = calcRectHeight(loopContent);

        attrs['.whileDo-body-text'].text = loopContent.join('\n');
        attrs['.whileDo-body-rect'].height = loopRectHeight;

        attrs['.whileDo-complete-rect'].height = loopRectHeight + MIN_HEIGHT + 4;

        this.resize(STD_ACTIVITY_ELEMENT_WIDTH, loopRectHeight + MIN_HEIGHT + 4);
    }
}

const WhileLoopView = joint.dia.ElementView.extend({
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

class ActionInput extends joint.shapes.basic.Generic {

    private static MARKUP = `
<g class="rotatable">
    <g class="scalable">
        <rect class="action-input-rect"/>
    </g>
    <text class="action-input-text"/>
</g>`.trim();

    constructor(attributes?: joint.dia.Element.Attributes, options?: joint.dia.Graph.Options) {
        super(attributes, options);
        this.set('markup', ActionInput.MARKUP);
    }

    defaults() {
        return _.defaultsDeep({
            type: 'uml.ActionInput',
            size: {width: STD_ACTIVITY_ELEMENT_WIDTH, height: MIN_HEIGHT},

            attrs: {
                rect: {
                    width: STD_ACTIVITY_ELEMENT_WIDTH, stroke: COLORS.Black, strokeWidth,
                    fill: COLORS.White, rx: 5, ry: 5
                },
                '.action-input-rect': {},

                text: {fill: COLORS.Black, fontSize, refY: STD_PADDING, refX: STD_PADDING},
                '.action-input-text': {ref: '.action-input-rect'},
            },

            ports: {
                groups: {
                    'in': {
                        position: 'top',
                        attrs: {
                            circle: {
                                fill: 'transparent', stroke: COLORS.RoyalBlue,
                                strokeWidth: 1, r: 10, magnet: true
                            }
                        }
                    },
                    'out': {
                        position: 'bottom',
                        attrs: {
                            circle: {
                                fill: 'transparent', stroke: COLORS.ForestGreen,
                                strokeWidth: 1, r: 10, magnet: true
                            }
                        }
                    }
                },
                items: [
                    {id: 'in', group: 'in', args: {x: STD_ACTIVITY_ELEMENT_WIDTH / 2}},
                    {id: 'out', group: 'out', args: {x: STD_ACTIVITY_ELEMENT_WIDTH / 2}}
                ]
            },

            // Attributes
            content: <string> '',
        })
    }

    initialize() {
        this.on('change:content', function () {
            this.updateRectangles();
            this.trigger('uml-update');
        }, this);

        this.updateRectangles();

        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    }

    getContent(): string[] {
        return this.get('content').split('\n');
    }

    updateRectangles() {
        const attrs = this.get('attrs');

        const rects = [
            {type: 'action-input', text: this.getContent()},
        ];

        let offSetY = 0;

        rects.forEach(function (rect) {
            const rectHeight = calcRectHeight(rect.text);

            attrs['.' + rect.type + '-text'].text = rect.text.join('\n');
            attrs['.' + rect.type + '-rect'].height = rectHeight;
            attrs['.' + rect.type + '-rect'].transform = 'translate(0, ' + offSetY + ')';

            offSetY += rectHeight;
        });

        this.resize(STD_ACTIVITY_ELEMENT_WIDTH, offSetY);
    }

}


class ActionInputView extends joint.dia.ElementView {
    // events: STD_TEXT_ELEMENT_EVENTS,

    pointerdblclick() {
        let model = this.model;
        if (model instanceof ActionInput) {
            editActionInput(model);
        } else {
            console.error('ActionInputView has wrong type of model!');
        }
    }

    onRightClick(event) {
        event.preventDefault();
        if (confirm('Löschen?'))
            this.remove();
    }

    initialize() {
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
    }
}


// If - else

const NEW_IF_ELSE_HEIGHT = (4 * MIN_HEIGHT + 8);

const IF_ELSE_TEXT_MARKUP: string = `
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

class IfElseText extends joint.shapes.basic.Generic {

    private static MARKUP = IF_ELSE_TEXT_MARKUP;

    constructor(attributes?: joint.dia.Element.Attributes, options?: joint.dia.Graph.Options) {
        super(attributes, options);
        this.set('markup', IfElseText.MARKUP);
    }

    defaults() {
        return _.defaultsDeep({
            type: 'uml.IfElseText',

            size: {width: STD_ACTIVITY_ELEMENT_WIDTH, height: NEW_IF_ELSE_HEIGHT},

            attrs: {
                rect: {width: STD_ACTIVITY_ELEMENT_WIDTH},

                '.ifelse-complete-rect': {
                    height: NEW_IF_ELSE_HEIGHT, rx: 5, ry: 5, fill: 'none',
                    stroke: COLORS.Black, strokeWidth, strokeDasharray: '5,5'
                },

                '.if-header-rect': {height: MIN_HEIGHT},
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

                '.else-header-rect': {height: MIN_HEIGHT, transform: 'translate(0,' + (2 * MIN_HEIGHT + 6) + ')'},
                '.else-header-separator-rect': {
                    height: 1, transform: 'translate(0,' + (3 * MIN_HEIGHT + 6) + ')',
                    stroke: COLORS.Black, strokeWidth: 1
                },
                '.else-body-rect': {
                    height: MIN_HEIGHT, transform: 'translate(0,' + (3 * MIN_HEIGHT + 8) + ')',
                    fill: COLORS.Gainsboro
                },

                text: {fill: COLORS.Black, fontSize, refY: STD_PADDING, refX: STD_PADDING},
                '.if-header-text': {ref: '.if-header-rect'}, '.if-body-text': {ref: '.if-body-rect'},
                '.else-header-text': {ref: '.else-header-rect'}, '.else-body-text': {ref: '.else-body-rect'}

            },

            ports: STD_PORTS,

            condition: '', ifContent: [], elseContent: []
        })
    }

    initialize(): void {
        this.on('change:condition change:ifContent change:elseContent', function () {
            this.updateRectangles();
            this.trigger('uml-update');
        }, this);

        this.updateRectangles();

        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    }

    getCondition(): string {
        return this.get('condition');
    }

    getIfHeaderContent(): string[] {
        const currectCond = this.get('condition');
        const condition = currectCond.length === 0 ? '___' : currectCond;

        return [`if ${condition}:`];
    }

    isOkay(): boolean {
        return this.get('condition').length !== 0;
    }

    getIfContent(): string[] {
        const currentContent: string[] = this.get('ifContent');
        return (currentContent.length === 0) ? ['pass'] : currentContent;
    }

    getElseTextContent(): string[] {
        const currentContent: string[] = this.get('elseContent');
        return (currentContent.length === 0) ? ['pass'] : currentContent;
    }

    getElseContent(): string[] {
        return this.get('elseContent');
    }

    updateRectangles(): void {
        const attrs = this.get('attrs');

        let offSetY = 0;

        let ifHeaderContent: string[] = this.getIfHeaderContent();
        let ifHeaderHeight = calcRectHeight(ifHeaderContent);

        let ifContent: string[] = this.getIfContent();
        let ifRectHeight = calcRectHeight(ifContent);

        let elseHeaderContent: string[] = ['else:'];
        let elseHeaderHeight = calcRectHeight(elseHeaderContent);

        let elseContent: string[] = this.getElseTextContent();
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
}

function resetIfElseText(): void {
    $('#ifElseButton').data('cellId', '');
    $('#ifElseContent').val('');
    $('#ifElseEditSection').prop('hidden', true);
}

class IfElseTextView extends joint.dia.ElementView {
    // events: STD_TEXT_ELEMENT_EVENTS,

    onLeftClick(event) {
        event.preventDefault();
        $('#ifElseConditionInput').val(this.model.get('condition'));

        ifEditor.setValue(this.model.get('ifContent').join('\n'));
        elseEditor.setValue(this.model.get('elseContent').join('\n'));

        $('#ifElseButton').data('cellId', this.model.id);
        $('#ifElseEditSection').prop('hidden', false);
    }

    onRightClick(event) {
        event.preventDefault();
        if (confirm('Löschen?'))
            this.remove();
    }

    initialize() {
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
    }
}

function updateIfElseText(button: HTMLButtonElement) {
    const model = umlActivityGraph.getCell($(button).data('cellId'));

    model.prop('condition', $('#ifElseConditionInput').val());

    let ifContent = ifEditor.getValue().split('\n').filter((l) => l.trim().length !== 0);
    let elseContent = elseEditor.getValue().split('\n').filter((l) => l.trim().length !== 0);

    model.prop('ifContent', ifContent);
    model.prop('elseContent', elseContent);

    resetIfElseText();
}

// Other elements!

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

class NewForLoop extends HtmlElement {
    defaults() {
        return _.defaultsDeep({
            size: {width: FOR_LOOP_WIDTH, height: FOR_LOOP_HEIGHT},
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
                            // label layout definition:
                            position: {
                                name: 'manual', args: {
                                    y: FOR_LOOP_HEIGHT,
                                    attrs: {
                                        '.': {'text-anchor': 'middle'},
                                        text: {fill: COLORS.Black, 'pointer-events': 'none'}
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
                        attrs: {
                            circle: {
                                fill: 'transparent',
                                stroke: COLORS.Black,
                                strokeWidth: 1,
                                r: 10,
                                magnet: true
                            }
                        }
                    }
                },
                items: [
                    {id: 'in', group: 'in', args: {x: FOR_LOOP_WIDTH / 2}},
                    {id: 'extern', group: 'extern', args: {x: FOR_LOOP_WIDTH, y: 55}},
                    {id: 'out', group: 'out', args: {x: FOR_LOOP_WIDTH / 2}}]
            }
        }, HtmlElement.prototype.defaults)
    }
}

function createForLoop(xCoord, yCoord): HtmlElement {
    return new NewForLoop({position: {x: xCoord, y: yCoord}});
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
    return new HtmlElement({
        position: {x: xCoord, y: yCoord},
        size: {width: STD_ACTIVITY_ELEMENT_WIDTH, height: WHILE_LOOP_HEIGHT},
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
                {id: 'in', group: 'in', args: {x: STD_ACTIVITY_ELEMENT_WIDTH / 2, y: 0}},
                {id: 'out', group: 'out', args: {x: STD_ACTIVITY_ELEMENT_WIDTH / 2, y: WHILE_LOOP_HEIGHT}},
                {id: 'extern', group: 'extern', args: {x: EXTERN_PORT_WIDTH, y: WHILE_LOOP_HEIGHT / 2}}]
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
    return new HtmlElement({
        position: {x: xCoord, y: yCoord},
        size: {width: STD_ACTIVITY_ELEMENT_WIDTH, height: WHILE_LOOP_HEIGHT},
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
                {id: 'in', group: 'in', args: {x: STD_ACTIVITY_ELEMENT_WIDTH / 2, y: 0}},
                {id: 'out', group: 'out', args: {x: STD_ACTIVITY_ELEMENT_WIDTH / 2, y: WHILE_LOOP_HEIGHT}},
                {id: 'extern', group: 'extern', args: {x: EXTERN_PORT_WIDTH, y: WHILE_LOOP_HEIGHT / 2}}
            ]
        }
    });
}

// Edit element

const EDIT_WIDTH = 320;
const EDIT_HEIGHT = 200;

class Edit extends joint.shapes.basic.Generic {

    private static MARKUP = `<g class="rotatable">
    <g class="scalable">
        <rect class="edit-complete-rect"/>
    </g>
</g>`.trim();

    defaults() {
        return _.defaultsDeep({
            type: 'uml.Edit',
            size: {width: EDIT_WIDTH, height: EDIT_HEIGHT},

            attrs: {
                rect: {width: STD_ACTIVITY_ELEMENT_WIDTH},

                '.edit-complete-rect': {
                    height: EDIT_HEIGHT, rx: 5, ry: 5,
                    stroke: COLORS.Black, strokeWidth, strokeDasharray: '5,5'
                },
            },
            ports: {
                groups: {
                    in: {
                        position: 'top',
                        attrs: {
                            circle: {
                                fill: 'transparent', stroke: COLORS.IndianRed, strokeWidth: 1,
                                r: 10, magnet: true
                            }
                        }
                    },
                    out: {
                        position: 'bottom',
                        attrs: {
                            circle: {
                                fill: 'transparent', stroke: COLORS.YellowGreen, strokeWidth: 1,
                                r: 10, magnet: true
                            }
                        }
                    }
                },
                items: [
                    {id: 'in', group: 'in', args: {x: 0, y: EDIT_HEIGHT / 3}},
                    {id: 'out', group: 'out', args: {x: 0, y: 2 * EDIT_HEIGHT / 3}}
                ]
            }
        })
    }

    initialize(): void {
        this.on('', function () {
            this.trigger('uml-update');
        }, this);

        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    }

}


class EditView extends joint.dia.ElementView {
    // events: STD_TEXT_ELEMENT_EVENTS,

    onLeftClick(event) {
        event.preventDefault();
        if (selElement !== '') {
            console.warn(event);
            let position = {position: {x: event.offsetX, y: event.offsetY}};
            console.warn(position);
            createElements(selElement, position, this.model);
        }
    }

    onRightClick(event) {
        event.preventDefault();
        if (confirm('Löschen?')) {
            for (let embedded of this.model.getEmbeddedCells()) {
                this.model.unembed(embedded);
                embedded.remove();
            }
            this.remove();
        }
    }

    initialize() {
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
    }
}


const EDIT_TEMPLATE = `
<div class="edit_element">
    <button class="delete">x</button>
</div>`;

function createEdit(xCoord, yCoord) {
    let edit = new HtmlElement({
        position: {x: xCoord, y: yCoord},
        size: {width: 170, height: WHILE_LOOP_HEIGHT},
        template: EDIT_TEMPLATE,
        name: 'edit',
        cleanname: 'Externer Knoten',
        ports: {
            groups: {
                'extern-in': {
                    position: 'absolute',
                    attrs: {
                        circle: {fill: 'transparent', stroke: COLORS.RoyalBlue, strokeWidth: 1, r: 10, magnet: true}
                    }
                },
                'extern-out': {
                    position: 'abolute',
                    attrs: {
                        circle: {fill: 'transparent', stroke: COLORS.ForestGreen, strokeWidth: 1, r: 10, magnet: true}
                    }
                }
            },
            items: [
                {id: 'extern-in', group: 'extern-in', args: {x: 0, y: WHILE_LOOP_HEIGHT / 3}},
                {id: 'extern-out', group: 'extern-out', args: {x: 0, y: 2 * WHILE_LOOP_HEIGHT / 3}}
            ]
        }
    });

    umlActivityGraph.addCell(edit);

    const start = createStartState(xCoord + STD_PADDING, yCoord + STD_PADDING);
    const end = createEndState(xCoord + 118, yCoord + 68);

    edit.embed(start);
    edit.embed(end);

    start.toFront({deep: true});
    end.toFront({deep: true});

    umlActivityGraph.addCell(start);
    umlActivityGraph.addCell(end);

    parentChildNodes.push({
        'parentId': edit.id as string,
        'startId': start.id as string,
        'endId': end.id as string,
        'endName': end.get('name')
    });

    // Add edit twice --> bug...
    return edit;
}
