import * as joint from 'jointjs';
import {MyJointClass} from './joint-class-diag-elements';
import {
  UmlAssociationInput,
  UmlAssociationType,
  UmlAttribute,
  UmlImplementationInput,
  UmlMethod,
  UmlMultiplicity
} from '../../../../_services/apollo_services';

export const CLASS_TYPES = ['CLASS', 'ABSTRACT', 'INTERFACE'];

export function buildMethodString(cm: UmlMethod): string {
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

export function buildAttributeString(ca: UmlAttribute): string {
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

export function getMultiplicity(label): UmlMultiplicity {
  return label.attrs.text.text === '1' ? UmlMultiplicity.Single : UmlMultiplicity.Unbound;
}


export function umlImplfromConnection(conn: joint.dia.Link): UmlImplementationInput {
  return {
    subClass: (conn.getSourceCell() as MyJointClass).getClassName(),
    superClass: (conn.getTargetCell() as MyJointClass).getClassName()
  };
}

export function umlAssocfromConnection(conn: joint.dia.Link): UmlAssociationInput {
  return {
    assocType: getTypeName(conn.attributes.type) as UmlAssociationType,
    assocName: '',        // TODO: name of association!?!
    firstEnd: (conn.getSourceCell() as MyJointClass).getClassName(),
    firstMult: getMultiplicity(conn.attributes.labels[0]),
    secondEnd: (conn.getTargetCell() as MyJointClass).getClassName(),
    secondMult: getMultiplicity(conn.attributes.labels[1])
  };
}
