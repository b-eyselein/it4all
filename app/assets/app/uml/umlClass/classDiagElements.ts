import * as joint from 'jointjs';

import {calcRectHeight, COLORS, fontSize, STD_ELEMENT_WIDTH, STD_PADDING} from "../umlConsts";
import {UmlClass, UmlClassAttribute, UmlClassMethod} from "../umlInterfaces";

import * as _ from "lodash";

export {MyJointClass}

const CLASS_MARKUP = `
<g class="rotatable">
  <g class="scalable">
    <rect class="uml-class-name-rect"/><rect class="uml-class-attrs-rect"/><rect class="uml-class-methods-rect"/>
  </g>
  <text class="uml-class-name-text"/><text class="uml-class-attrs-text"/><text class="uml-class-methods-text"/>
</g>`.trim();

class MyJointClass extends joint.shapes.basic.Generic {

    constructor(attributes?: joint.dia.Element.Attributes, options?: joint.dia.Graph.Options) {
        super(attributes, options);
        this.set('markup', CLASS_MARKUP);
    }

    initialize() {
        this.on('change:name change:attributes change:methods', function () {
            this.updateRectangles();
            this.trigger('uml-update');
        }, this);

        this.updateRectangles();

        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    }

    updateRectangles() {

        const attrs = this.get('attrs');

        const rects = [
            {type: 'name', text: this.get('className')},
            {type: 'attrs', text: this.get('attributes')},
            {type: 'methods', text: this.get('methods')}
        ];

        let offsetY = 0;

        rects.forEach(function (rect) {

            const lines = Array.isArray(rect.text) ? rect.text : [rect.text];
            const rectHeight = calcRectHeight(lines);

            attrs['.uml-class-' + rect.type + '-text'].text = lines.join('\n');
            attrs['.uml-class-' + rect.type + '-rect'].height = rectHeight;
            attrs['.uml-class-' + rect.type + '-rect'].transform = 'translate(0,' + offsetY + ')';

            offsetY += rectHeight;
        });
    }

    defaults() {
        return _.defaultsDeep({
            type: 'MyJointClass',
            size: {width: 300, height: 300},
            attrs: {
                rect: {width: STD_ELEMENT_WIDTH, stroke: COLORS.Black, strokeWidth: 2},
                text: {fill: COLORS.Black, fontSize, fontFamily: 'Times New Roman'},

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
        }, joint.shapes.basic.Generic.prototype.defaults);
    }


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
    }

    getAsUmlClass(): UmlClass {
        return new UmlClass(this.get('className'), this.get('classType'), this.get('attributes'), this.get('methods'), this.get('position'));
    }

    getClassRectText(): string[] {
        let classType = this.getClassTypeRepresentant();
        let className = this.get('className');

        if (classType.length > 0) {
            return [classType, className]
        } else {
            return [className];
        }
    }

    getAttributes(): string[] {
        let attributes: UmlClassAttribute[] = this.get('attributes');
        return attributes.map((a) => a.buildString())
    }

    getMethods(): string[] {
        let methods: UmlClassMethod[] = this.get('methods');
        return methods.map((m) => m.buildString());
    }

}
