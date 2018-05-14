export {
    UmlImplementation,
    UmlAssociation,
    UmlClass,
    UmlClassMethod,
    UmlClassAttribute,
    UmlClassMember,
    UmlSolution,
    ClassMemberAttrs,
    visibilityReprFromValue
}

export enum CLASS_TYPES {
    CLASS = 'class',
    ABSTRACT = 'abstract',
    INTERFACE = 'interface'
}

export enum ASSOC_TYPES {
    ASSOCIATION = 'Assozation',
    AGGREGATION = 'Aggregation',
    COMPOSITION = 'Komposition',
    IMPLEMENTATION = 'Vererbung'
}

export enum VISIBILITIES {
    PUBLIC = '+',
    PRIVATE = '-',
    PROTECTED = '#',
    PACKAGE = '~'
}

export enum CARDINALITIES {
    SINGLE = '1',
    UNBOUND = '*'
}

interface UmlSolution {
    classes: UmlClass[],
    associations: UmlAssociation[],
    implementations: UmlImplementation[]
}

class UmlClass {
    constructor(public name: string, public classType: CLASS_TYPES, public attributes: UmlClassAttribute[], public methods: UmlClassMethod[], public position?: Position) {
    }
}

interface ClassMemberAttrs {
    visibilityRepr: string;
    name: string;
    type: string;
}

function visibilityReprFromValue(value) {
    switch (value) {
        case 'PUBLIC':
            return '+';
        case 'PRIVATE':
            return '-';
        case 'PROTECTED':
            return '#';
        case 'PACKAGE':
        default:
            return '~';
    }
}

abstract class UmlClassMember {
    visibility: string;

    constructor(public visibilityRepr: string, public name: string, public type: string, public isStatic: boolean, public isAbstract: boolean) {
        if (new.target === UmlClassMember) {
            throw new TypeError('Cannot instantiate class UmlClassMember!')
        }

        this.visibility = '';
        switch (this.visibilityRepr) {
            case '+':
                this.visibility = 'public';
                break;
            case '-':
                this.visibility = 'private';
                break;
            case '#':
                this.visibility = 'protected';
                break;
            case '~':
            default:
                this.visibility = 'package';
                break;
        }
    }

    abstract buildString(): string;
}

class UmlClassMethod extends UmlClassMember {

    constructor(visibilityRepr, name, public parameters: string, type, isStatic, isAbstract) {
        super(visibilityRepr, name, type, isStatic, isAbstract);
    }


    static buildStringFrom(visibilityRepl, isAbstract, isStatic, name, parameters, type) {
        let modifier = [];

        if (isAbstract)
            modifier.push('a');

        if (isStatic)
            modifier.push('s');

        return visibilityRepl + ' ' + (modifier.length === 0 ? '' : '{' + modifier.join(', ') + '} ') + name + '(' + parameters + '): ' + type;
    }

    buildString(): string {
        return UmlClassMethod.buildStringFrom(this.visibilityRepr, this.isAbstract, this.isStatic, this.name, this.parameters, this.type);
    }
}

class UmlClassAttribute extends UmlClassMember {
    constructor(visibilityRepr, name, type, isStatic, public isDerived: boolean, isAbstract) {
        super(visibilityRepr, name, type, isStatic, isAbstract);
    }

    private static buildStringFrom(visibilityRepl, isAbstract, isStatic, isDerived, name, type) {
        let modifier = [];

        if (isAbstract)
            modifier.push('a');

        if (isStatic)
            modifier.push('s');

        if (isDerived)
            modifier.push('d');

        return visibilityRepl + ' ' + (modifier.length === 0 ? '' : '{' + modifier.join(', ') + '} ') + name + ': ' + type;
    }

    buildString(): string {
        return UmlClassAttribute.buildStringFrom(this.visibilityRepr, this.isAbstract, this.isStatic, this.isDerived, this.name, this.type);
    }
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
