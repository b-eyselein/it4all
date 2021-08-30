import * as joint from 'jointjs';
import * as _ from 'lodash';

import {calcRectHeight, COLORS, fontSize, STD_ELEMENT_WIDTH, STD_PADDING} from './uml-consts';
import {buildAttributeString, buildMethodString, CLASS_TYPES} from './my-uml-interfaces';
import {UmlAttribute, UmlClassType, UmlMethod} from '../../../../graphql';
import {ModelConstructorOptions, ObjectHash} from 'backbone';
import {UmlDbClass} from '../UmlDiagramDrawing';

export const STD_CLASS_HEIGHT = 160;
export const STD_CLASS_WIDTH = 200;

const CLASS_MARKUP = `
<g class="rotatable">
  <g class="scalable">
    <rect class="uml-class-name-rect"/><rect class="uml-class-attrs-rect"/><rect class="uml-class-methods-rect"/>
  </g>
  <text class="uml-class-name-text"/><text class="uml-class-attrs-text"/><text class="uml-class-methods-text"/>
</g>`.trim();

export class MyJointClass extends joint.shapes.basic.Generic {

  constructor(attributes?: joint.dia.Element.Attributes, options?: joint.dia.Graph.Options) {
    super(attributes, options);
    this.set('markup', CLASS_MARKUP);
  }

  initialize(attributes?: joint.dia.Element.Attributes, options?: ModelConstructorOptions<this> | undefined): void {
    this.on('change:classType change:className change:attributes change:methods', () => {
      // console.log('Update...');
      this.updateRectangles();
    });

    this.updateRectangles();

    super.initialize(attributes, options);
  }

  updateRectangles(): void {
    const attrs = this.get('attrs');

    const rects = [
      {type: 'name', text: this.getClassRectText()},
      {type: 'attrs', text: this.getAttributesAsStrings()},
      {type: 'methods', text: this.getMethodsAsStrings()}
    ];

    let offsetY = 0;

    console.info(JSON.stringify(attrs, null, 2));

    rects.forEach(({type, text}) => {

      const rectHeight: number = calcRectHeight(text);

      attrs[`.uml-class-${type}-text`].text = text.join('\n');
      attrs['.uml-class-' + type + '-rect'].height = rectHeight;
      attrs['.uml-class-' + type + '-rect'].transform = 'translate(0,' + offsetY + ')';

      offsetY += rectHeight;
    });

    this.resize(STD_CLASS_WIDTH, offsetY);
  }

  defaults(): ObjectHash {
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

        '.uml-class-name-text': {ref: '.uml-class-name-rect', refY: .5, refX: .5, textAnchor: 'middle', yAlignment: 'middle', fontWeight: 'bold'},
        '.uml-class-attrs-text': {ref: '.uml-class-attrs-rect', refY: STD_PADDING, refX: STD_PADDING},
        '.uml-class-methods-text': {ref: '.uml-class-methods-rect', refY: STD_PADDING, refX: STD_PADDING}
      },

      className: '' as string,
      classType: '',
      attributes: [] as UmlAttribute[],
      methods: [] as UmlMethod[]
    }, joint.shapes.basic.Generic.prototype.defaults);
  }

  getClassType(): UmlClassType {
    return this.get('classType') || UmlClassType.Class;
  }

  setClassType(classType: UmlClassType): void {
    if (CLASS_TYPES.indexOf(classType) >= 0) {
      this.prop('classType', classType);
    } else {
      console.error('>>' + classType + '<< is no legal value for a class type!');
    }
  }

  getClassName(): string {
    return this.get('className');
  }

  setClassName(className: string): void {
    this.set('className', className);
  }

  getClassRectText(): string[] {
    const classType = this.getClassType();
    const className = this.getClassName();

    if (classType === UmlClassType.Abstract) {
      return ['<<abstract>>', className];
    } else if (classType === UmlClassType.Interface) {
      return ['<<interface>>', className];
    } else {
      return [className];
    }
  }

  getAttributesAsStrings(): string[] {
    return this.get('attributes').map(buildAttributeString);
  }

  getAttributes(): UmlAttribute[] {
    return this.get('attributes');
  }

  setAttributes(attributes: UmlAttribute[]): void {
    this.set('attributes', attributes);
  }

  getMethods(): UmlMethod[] {
    return this.get('methods');
  }

  getMethodsAsStrings(): string[] {
    return this.get('methods').map(buildMethodString);
  }

  setMethods(methods: UmlMethod[]): void {
    this.set('methods', methods);
  }

  getAsUmlClass(): UmlDbClass {
    return {
      name: this.getClassName(),
      classType: this.getClassType(),
      attributes: this.getAttributes(),
      methods: this.getMethods(),
      position: this.position()
    };
  }

}


export function isMyJointClass(cell: joint.dia.Cell): cell is MyJointClass {
  return cell instanceof MyJointClass;
}

export function isAssociation(link: joint.dia.Link): link is joint.shapes.uml.Association {
  return link instanceof joint.shapes.uml.Association;
}

export function isImplementation(link: joint.dia.Link): link is joint.shapes.uml.Implementation {
  return link instanceof joint.shapes.uml.Implementation;
}
