import * as joint from 'jointjs';

import {calcRectHeight, fontSize, STD_ELEMENT_WIDTH, STD_PADDING} from "../umlConsts";
import {UmlClass, UmlClassAttribute, UmlClassMethod} from "../umlInterfaces";

export {CustomClass} //, MyJointClass}

const CLASS_MARKUP = `
<g class="rotatable">
  <g class="scalable">
    <rect class="uml-class-name-rect"/><rect class="uml-class-attrs-rect"/><rect class="uml-class-methods-rect"/>
  </g>
  <text class="uml-class-name-text"/><text class="uml-class-attrs-text"/><text class="uml-class-methods-text"/>
</g>`.trim();

// class MyJointClass extends joint.shapes.basic.Generic {
//
//     constructor(attributes?: any, options?: any) {
//         super(attributes, options);
//         this.set("markup", CLASS_MARKUP);
//     }
//
//     defaults(): Backbone.ObjectHash {
//         return _.defaultsDeep({
//             attrs: {
//                 rect: {width: STD_ELEMENT_WIDTH, stroke: 'black', strokeWidth: 2},
//                 text: {fill: 'black', fontSize, fontFamily: 'Times New Roman'},
//
// //                Do not delete, needed for later!
// '.uml-class-name-rect': {},
// '.uml-class-attrs-rect': {},
// '.uml-class-methods-rect': {},
//
// '.uml-class-name-text': {
//     ref: '.uml-class-name-rect', refY: .5, refX: .5,
//     textAnchor: 'middle', yAlignment: 'middle', fontWeight: 'bold',
// },
// '.uml-class-attrs-text': {ref: '.uml-class-attrs-rect', refY: STD_PADDING, refX: STD_PADDING,},
// '.uml-class-methods-text': {ref: '.uml-class-methods-rect', refY: STD_PADDING, refX: STD_PADDING,}
// },
//
// className: <string[]> [],
// classType: <string>  '',
// attributes: <UmlClassAttribute[]> [],
// methods: <UmlClassMethod[]> []
// }, joint.shapes.basic.Rect.prototype.defaults);
// }
//
// }

const CustomClass: joint.dia.Cell.Constructor<joint.dia.Element> = joint.shapes.basic.Generic.define('customUml.CustomClass', {
    attrs: {
        rect: {width: STD_ELEMENT_WIDTH, stroke: 'black', strokeWidth: 2},
        text: {fill: 'black', fontSize, fontFamily: 'Times New Roman'},

        // Do not delete, needed for later!
        '.uml-class-name-rect': {},
        '.uml-class-attrs-rect': {},
        '.uml-class-methods-rect': {},

        '.uml-class-name-text': {
            ref: '.uml-class-name-rect', refY: .5, refX: .5,
            textAnchor: 'middle', yAlignment: 'middle', fontWeight: 'bold',
        },
        '.uml-class-attrs-text': {ref: '.uml-class-attrs-rect', refY: STD_PADDING, refX: STD_PADDING,},
        '.uml-class-methods-text': {ref: '.uml-class-methods-rect', refY: STD_PADDING, refX: STD_PADDING,}
    },

    className: <string[]> [],
    classType: <string>  '',
    attributes: <UmlClassAttribute[]> [],
    methods: <UmlClassMethod[]> []
}, {
    markup: CLASS_MARKUP,

    initialize() {
        this.on('change:className change:classType change:attributes change:methods', function () {
            this.updateRectangles();
            this.trigger('uml-update');
        }, this);

        this.updateRectangles();

        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    },

    getClassTypeRepresentant(): string {
        switch (this.get('classType')) {
            case 'ABSTRACT':
                return '<<abstract>>';
            case 'INTERFACE':
                return '<<interface>>';
            case 'CLASS':
            default:
                return '';
        }
    },

    getAsUmlClass(): UmlClass {
        return new UmlClass(this.get('className'), this.get('classType'), this.get('attributes'), this.get('methods'), this.get('position'));
    },

    getClassRectText(): string[] {
        let classType = this.getClassTypeRepresentant();
        let className = this.get('className');

        if (classType.length > 0) {
            return [classType, className]
        } else {
            return [className];
        }
    },

    getAttributes(): string[] {
        let attributes: UmlClassAttribute[] = this.get('attributes');
        return attributes.map((a) => a.buildString())
    },

    getMethods(): string[] {
        let methods: UmlClassMethod[] = this.get('methods');
        return methods.map((m) => m.buildString());
    },

    updateRectangles(): void {
        const attrs = this.get('attrs');
        const rects = [
            {type: 'name', text: this.getClassRectText().filter((str) => str.length !== 0)},
            {type: 'attrs', text: this.getAttributes().filter((str) => str.length !== 0)},
            {type: 'methods', text: this.getMethods().filter((str) => str.length !== 0)}
        ];

        let offsetY = 0;

        rects.forEach(function (rect) {
            const rectHeight = calcRectHeight(rect.text);

            attrs['.uml-class-' + rect.type + '-text'].text = rect.text.join('\n');
            attrs['.uml-class-' + rect.type + '-rect'].height = rectHeight;
            attrs['.uml-class-' + rect.type + '-rect'].transform = 'translate(0,' + offsetY + ')';

            offsetY += rectHeight;
        });

        this.resize(STD_ELEMENT_WIDTH, offsetY);
    }

});

const CustomClassView = joint.dia.ElementView.extend({}, {

    initialize: function () {

        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
    }
});