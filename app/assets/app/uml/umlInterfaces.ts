export const CLASS_TYPES = ['CLASS', 'ABSTRACT', 'INTERFACE'];

export enum ASSOC_TYPES {
    ASSOCIATION = 'Assozation',
    AGGREGATION = 'Aggregation',
    COMPOSITION = 'Komposition',
}

export const VISIBILITIES = ['+', '-', '#', '~'];

export enum CARDINALITIES {
    SINGLE = '1',
    UNBOUND = '*'
}

export function buildMethodString(cm: UmlClassMethod): string {
    let modifier = [];

    if (cm.isAbstract)
        modifier.push('a');

    if (cm.isStatic)
        modifier.push('s');

    return cm.visibility + ' ' + (modifier.length === 0 ? '' : '{' + modifier.join(', ') + '} ') + cm.name + '(' + cm.parameters + '): ' + cm.type;
}

export function buildAttributeString(ca: UmlClassAttribute): string {
    let modifier = [];

    if (ca.isAbstract)
        modifier.push('a');

    if (ca.isStatic)
        modifier.push('s');

    if (ca.isDerived)
        modifier.push('d');

    return ca.visibility + ' ' + (modifier.length === 0 ? '' : '{' + modifier.join(', ') + '} ') + ca.name + ': ' + ca.type;
}

export interface MyPosition {
    x: number,
    y: number
}

export interface UmlSolution {
    classes: UmlClass[],
    associations: UmlAssociation[],
    implementations: UmlImplementation[]
}

export interface UmlClass {
    name: string
    classType: string
    attributes: UmlClassAttribute[]
    methods: UmlClassMethod[]
    position?: MyPosition
}

export interface UmlClassMember {
    visibility: string
    name: string
    type: string
    isStatic: boolean
    isAbstract: boolean
}

export interface UmlClassMethod extends UmlClassMember {
    parameters: string
}

export interface UmlClassAttribute extends UmlClassMember {
    isDerived: boolean
}

export interface UmlImplementation {
    subClass: string
    superClass: string
}

export type LinkType = 'COMPOSITION' | 'AGGREGATION' | 'ASSOCIATION';

export interface UmlAssociation {
    assocType: LinkType
    assocName: string
    firstEnd: string
    firstMult: string
    secondEnd: string
    secondMult: string
}

export interface ExerciseParameters {
    methodDisplay: string,
    methodDeclaration: string,
    methodName: string,
    methodParameters: string,
    output: {
        outputType: string,
        output: string,
        defaultValue: string
    }
}
