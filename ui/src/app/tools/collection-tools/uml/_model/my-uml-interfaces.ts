import {IUmlAssociation, IUmlAttribute, IUmlClass, IUmlImplementation, IUmlMethod, UmlAssociationType} from '../uml-interfaces';
import * as joint from 'jointjs';
import {MyJointClass} from './joint-class-diag-elements';

export const CLASS_TYPES = ['CLASS', 'ABSTRACT', 'INTERFACE'];

export interface ExportedUmlClassDiagram {
  classes: ExportedUmlClass[];
  implementations: IUmlImplementation[];
  associations: IUmlAssociation[];
}

export interface ExportedUmlClass extends IUmlClass {
  position: { x: number, y: number };

  attributes: IUmlAttribute[];
  methods: IUmlMethod[];
}

export function buildMethodString(cm: IUmlMethod): string {
  const modifier = [];

  if (cm.isAbstract) {
    modifier.push('a');
  }

  if (cm.isStatic) {
    modifier.push('s');
  }

  return cm.visibility + ' ' + (
    modifier.length === 0 ? '' : '{' + modifier.join(', ') + '} '
  ) + cm.memberName + '(' + cm.parameters + '): ' + cm.memberType;
}

export function buildAttributeString(ca: IUmlAttribute): string {
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

  return ca.visibility + ' ' + (modifier.length === 0 ? '' : '{' + modifier.join(', ') + '} ') + ca.memberName + ': ' + ca.memberType;
}

function getTypeName(type: string): string {
  switch (type) {
    case 'uml.Association':
      return 'ASSOCIATION';
    case 'uml.Aggregation':
      return 'AGGREGATION';
    case 'uml.Composition':
      return 'COMPOSITION';
    case 'uml.Implementation':
      return 'IMPLEMENTATION';
    default:
      return 'ERROR!';
  }
}


function getClassNameFromCellId(graph: joint.dia.Graph, id: string): string {
  return (graph.getCell(id) as MyJointClass).getClassName();
}

function getMultiplicity(label): 'SINGLE' | 'UNBOUND' {
  return label.attrs.text.text === '1' ? 'SINGLE' : 'UNBOUND';
}


export function umlImplfromConnection(graph: joint.dia.Graph, conn: joint.dia.Link): IUmlImplementation {
  return {
    subClass: getClassNameFromCellId(graph, conn.attributes.source.id),
    superClass: getClassNameFromCellId(graph, conn.attributes.target.id)
  };
}

export function umlAssocfromConnection(graph: joint.dia.Graph, conn: joint.dia.Link): IUmlAssociation {
  return {
    assocType: getTypeName(conn.attributes.type) as UmlAssociationType,
    assocName: '',        // TODO: name of association!?!
    firstEnd: getClassNameFromCellId(graph, conn.attributes.source.id),
    firstMult: getMultiplicity(conn.attributes.labels[0]),
    secondEnd: getClassNameFromCellId(graph, conn.attributes.target.id),
    secondMult: getMultiplicity(conn.attributes.labels[1])
  };
}


export function isAssociation(link: joint.dia.Link): link is joint.shapes.uml.Association {
  return link instanceof joint.shapes.uml.Association;
}

export function isImplementation(link: joint.dia.Link): link is joint.shapes.uml.Implementation {
  return link instanceof joint.shapes.uml.Implementation;
}

