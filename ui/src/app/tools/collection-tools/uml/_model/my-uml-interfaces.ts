import {IUmlAssociation, IUmlClass, IUmlImplementation} from '../uml-interfaces';

export const CLASS_TYPES = ['CLASS', 'ABSTRACT', 'INTERFACE'];

export const VISIBILITIES = ['+', '-', '#', '~'];

export interface ExportedUmlClassDiagram {
  classes: ExportedUmlClass[];
  implementations: IUmlImplementation[];
  associations: IUmlAssociation[];
}

// @ts-ignore
export interface ExportedUmlClass extends IUmlClass {
  position: { x: number, y: number };

  attributes: UmlClassAttribute[];
  methods: UmlClassMethod[];
}

export function buildMethodString(cm: UmlClassMethod): string {
  const modifier = [];

  if (cm.isAbstract) {
    modifier.push('a');
  }

  if (cm.isStatic) {
    modifier.push('s');
  }

  return cm.visibility + ' ' + (
    modifier.length === 0 ? '' : '{' + modifier.join(', ') + '} '
  ) + cm.name + '(' + cm.parameters + '): ' + cm.type;
}

export function buildAttributeString(ca: UmlClassAttribute): string {
  const modifier = [];

  if (ca.isAbstract) {
    modifier.push('a');
  }

  if (ca.isStatic) {
    modifier.push('s');
  }

  if (ca.isDerived) {
    modifier.push('d');
  }

  return ca.visibility + ' ' + (modifier.length === 0 ? '' : '{' + modifier.join(', ') + '} ') + ca.name + ': ' + ca.type;
}

interface UmlClassMember {
  visibility: string;
  name: string;
  type: string;
  isStatic: boolean;
  isAbstract: boolean;
}

export interface UmlClassMethod extends UmlClassMember {
  parameters: string;
}

export interface UmlClassAttribute extends UmlClassMember {
  isDerived: boolean;
}
