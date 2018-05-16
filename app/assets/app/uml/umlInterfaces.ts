export {UmlClass, UmlClassMember, UmlClassMethod, UmlClassAttribute, UmlImplementation, UmlAssociation, UmlSolution, VISIBILITIES, CLASS_TYPES}

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
