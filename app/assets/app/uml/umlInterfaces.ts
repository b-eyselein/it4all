export {
    UmlClass,
    UmlClassMember,
    UmlClassMethod,
    UmlClassAttribute,
    UmlImplementation,
    UmlAssociation,
    UmlSolution,
    VISIBILITIES,
    CLASS_TYPES,
    ExerciseParameters,
    buildAttributeString,
    buildMethodString
}

const CLASS_TYPES = ['CLASS', 'ABSTRACT', 'INTERFACE'];

export enum ASSOC_TYPES {
    ASSOCIATION = 'Assozation',
    AGGREGATION = 'Aggregation',
    COMPOSITION = 'Komposition',
}

const VISIBILITIES = ['+', '-', '#', '~'];

export enum CARDINALITIES {
    SINGLE = '1',
    UNBOUND = '*'
}

function buildMethodString(cm: UmlClassMethod): string {
    let modifier = [];

    if (cm.isAbstract)
        modifier.push('a');

    if (cm.isStatic)
        modifier.push('s');

    return cm.visibility + ' ' + (modifier.length === 0 ? '' : '{' + modifier.join(', ') + '} ') + cm.name + '(' + cm.parameters + '): ' + cm.type;
}

function buildAttributeString(ca: UmlClassAttribute): string {
    let modifier = [];

    if (ca.isAbstract)
        modifier.push('a');

    if (ca.isStatic)
        modifier.push('s');

    if (ca.isDerived)
        modifier.push('d');

    return ca.visibility + ' ' + (modifier.length === 0 ? '' : '{' + modifier.join(', ') + '} ') + ca.name + ': ' + ca.type;
}

interface MyPosition {
    x: number,
    y: number
}

interface UmlSolution {
    classes: UmlClass[],
    associations: UmlAssociation[],
    implementations: UmlImplementation[]
}

interface UmlClass {
    name: string
    classType: string
    attributes: UmlClassAttribute[]
    methods: UmlClassMethod[]
    position?: MyPosition
}

interface UmlClassMember {
    visibility: string
    name: string
    type: string
    isStatic: boolean
    isAbstract: boolean
}

interface UmlClassMethod extends UmlClassMember {
    parameters: string
}

interface UmlClassAttribute extends UmlClassMember {
    isDerived: boolean
}

interface UmlImplementation {
    subClass: string
    superClass: string
}

interface UmlAssociation {
    assocType: string
    assocName: string
    firstEnd: string
    firstMult: string
    secondEnd: string
    secondMult: string
}

interface ExerciseParameters {
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
