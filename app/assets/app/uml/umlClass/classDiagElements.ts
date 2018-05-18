import * as joint from 'jointjs';

import {calcRectHeight, COLORS, fontSize, STD_ELEMENT_WIDTH, STD_PADDING} from "../umlConsts";
import {
    buildAttributeString,
    buildMethodString,
    CLASS_TYPES,
    UmlClass,
    UmlClassAttribute,
    UmlClassMethod
} from "../umlInterfaces";

import * as _ from "lodash";
import {editClass} from "./classDiagEdit";

export {MyJointClass, MyJointClassView, STD_CLASS_WIDTH, STD_CLASS_HEIGHT}

const STD_CLASS_HEIGHT = 160, STD_CLASS_WIDTH = 200;

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

    initialize(): void {
        this.on('change:classType change:className change:attributes change:methods', function () {
            // noinspection JSPotentiallyInvalidUsageOfClassThis
            this.updateRectangles();
            this.trigger('uml-update');
        }, this);

        this.updateRectangles();

        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    }

    updateRectangles() {
        const attrs = this.get('attrs');

        const rects = [
            {type: 'name', text: this.getClassRectText()},
            {type: 'attrs', text: this.getAttributesAsStrings()},
            {type: 'methods', text: this.getMethodsAsStrings()}
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

        this.resize(STD_CLASS_WIDTH, offsetY);
    }

    defaults() {
        return _.defaultsDeep({
            type: 'MyJointClass',
            size: {width: STD_CLASS_WIDTH, height: STD_CLASS_HEIGHT},
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


            className: <string> '',
            classType: '',
            attributes: <UmlClassAttribute[]> [],
            methods: <UmlClassMethod[]> []
        }, joint.shapes.basic.Generic.prototype.defaults);
    }

    getClassType(): string {
        return this.get('classType');
    }

    setClassType(classType: string): void {
        if (CLASS_TYPES.indexOf(classType) >= 0) {
            this.prop('classType', classType);
        } else {
            console.error(">>" + classType + "<< is no legal value for a class type!");
        }
    }

    getClassName(): string {
        return this.get('className');
    }

    setClassName(className: string) {
        this.set('className', className);
    }

    getAsUmlClass(): UmlClass {
        return {
            name: this.getClassName(),
            classType: this.getClassType(),
            attributes: this.getAttributes(),
            methods: this.getMethods(),
            position: this.get('position')
        };
    }

    getClassRectText(): string[] {
        let className = this.getClassName();

        switch (this.getClassType()) {
            case 'ABSTRACT':
                return ['<<abstract>>', className];
            case 'INTERFACE':
                return ['<<interface>>', className];
            case 'CLASS':
            default:
                return [className];
        }
    }


    getAttributesAsStrings(): string[] {
        let attributes: UmlClassAttribute[] = this.get('attributes');
        return attributes.map(buildAttributeString);
    }

    getAttributes(): UmlClassAttribute[] {
        return this.get('attributes');
    }

    setAttributes(attributes: UmlClassAttribute[]): void {
        this.set('attributes', attributes);
    }


    getMethods(): UmlClassMethod[] {
        return this.get('methods');
    }

    getMethodsAsStrings(): string[] {
        let methods: UmlClassMethod[] = this.get('methods');
        return methods.map(buildMethodString);
    }

    setMethods(methods: UmlClassMethod[]): void {
        this.set('methods', methods);
    }

}

class MyJointClassView extends joint.dia.ElementView {

    constructor(opt?: joint.dia.CellView.Options<joint.dia.Element>) {
        super(opt);

        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
    }

    pointerdblclick() {
        editClass(this.model as MyJointClass);
    }

}