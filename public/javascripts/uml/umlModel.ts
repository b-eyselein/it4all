enum CLASS_TYPES {
    CLASS = 'class',
    ABSTRACT = 'abstract',
    INTERFACE = 'interface'
}


enum ASSOC_TYPES {
    ASSOCIATION = 'Assozation',
    AGGREGATION = 'Aggregation',
    COMPOSITION = 'Komposition',
    IMPLEMENTATION = 'Vererbung'
}

enum VISIBILITIES {
    PUBLIC = '+',
    PRIVATE = '-',
    PROTECTED = '#',
    PACKAGE = '~'
}

enum CARDINALITIES {
    SINGLE = '1',
    UNBOUND = '*'
}

class UmlClass {
    constructor(public name: string, public classType: CLASS_TYPES, public attributes: UmlClassAttribute[], public methods: UmlClassMethod[], public position: Position) {
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

class UmlClassMember {
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

    static fromHtml(memberGroup): ClassMemberAttrs {
        return {
            visibilityRepr: $(memberGroup).find('select[data-for="visibility"]').val(),
            name: $(memberGroup).find('input[data-for="name"]').val(),
            type: $(memberGroup).find('select[data-for="type"]').val(),
        }
    }

    buildString() {
        throw new Error('This method must be overridden!');
    }
}

class UmlClassMethod extends UmlClassMember {

    constructor(visibilityRepr, name, public parameters: string, type, isStatic, isAbstract) {
        super(visibilityRepr, name, type, isStatic, isAbstract);
    }

    static fromMethodToLoad(mtl: MethodToLoad): UmlClassMethod {
        return new UmlClassMethod(visibilityReprFromValue(mtl.visibility), mtl.name, mtl.parameters, mtl.type, mtl.isStatic, mtl.isAbstract);
    }

    static fromHtml(memberGroup): UmlClassMethod {
        let base: ClassMemberAttrs = super.fromHtml(memberGroup);

        let modifiers = {};
        $('.dropdown-menu').find('input').each((index: number, elem: HTMLInputElement) => {
            modifiers[elem.value] = elem.checked;
        });

        let parameters = $(memberGroup).find('input[data-for="parameters"]').val();

        return new UmlClassMethod(base.visibilityRepr, base.name, parameters, base.type, modifiers['static'], modifiers['abstract']);
    }

    static buildStringFrom(visibilityRepl, isAbstract, isStatic, name, parameters, type) {
        let modifier = [];

        if (isAbstract)
            modifier.push('a');

        if (isStatic)
            modifier.push('s');

        return visibilityRepl + ' ' + (modifier.length === 0 ? '' : '{' + modifier.join(', ') + '} ') + name + '(' + parameters + '): ' + type;
    }

    buildString() {
        return UmlClassMethod.buildStringFrom(this.visibilityRepr, this.isAbstract, this.isStatic, this.name, this.parameters, this.type);
    }
}

class UmlClassAttribute extends UmlClassMember {
    constructor(visibilityRepr, name, type, isStatic, public isDerived: boolean, isAbstract) {
        super(visibilityRepr, name, type, isStatic, isAbstract);
    }

    static fromAttributeToLoad(atl: AttributeToLoad): UmlClassAttribute {
        return new UmlClassAttribute(visibilityReprFromValue(atl.visibility), atl.name, atl.type, atl.isStatic, atl.isDerived, atl.isDerived);
    }

    static fromHtml(memberGroup): UmlClassAttribute {
        let base = super.fromHtml(memberGroup);

        let modifiers = {};
        $('.dropdown-menu').find('input').each((index: number, elem: HTMLInputElement) => modifiers[elem.value] = elem.checked);

        return new UmlClassAttribute(base.visibilityRepr, base.name, base.type, modifiers['static'], modifiers['derived'], modifiers['abstract']);
    }

    static buildStringFrom(visibilityRepl, isAbstract, isStatic, isDerived, name, type) {
        let modifier = [];

        if (isAbstract)
            modifier.push('a');

        if (isStatic)
            modifier.push('s');

        if (isDerived)
            modifier.push('d');

        return visibilityRepl + ' ' + (modifier.length === 0 ? '' : '{' + modifier.join(', ') + '} ') + name + ': ' + type;
    }

    buildString() {
        return UmlClassAttribute.buildStringFrom(this.visibilityRepr, this.isAbstract, this.isStatic, this.isDerived, this.name, this.type);
    }
}

function getClassNameFromCellId(id) {
    return graph.getCell(id).attributes.name;
}

function getTypeName(type) {
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

function getMultiplicity(label) {
    return label.attrs.text.text === '1' ? 'SINGLE' : 'UNBOUND';
}

class UmlImplementation {
    constructor(public subClass: string, public superClass: string) {
    }

    static fromConnection(conn): UmlImplementation {
        let subClass = getClassNameFromCellId(conn.attributes.source.id);
        let superClass = getClassNameFromCellId(conn.attributes.target.id);
        return new UmlImplementation(subClass, superClass)
    }
}

class UmlAssociation {
    constructor(public assocType: string, public assocName: string, public firstEnd: string, public firstMult: string, public secondEnd: string, public secondMult: string) {
    }

    static fromConnection(conn): UmlAssociation {
        let assocType = getTypeName(conn.attributes.type);
        let assocName = '';        // TODO: name of association!?!
        let firstEnd = getClassNameFromCellId(conn.attributes.source.id);
        let firstMult = getMultiplicity(conn.attributes.labels[0]);
        let secondEnd = getClassNameFromCellId(conn.attributes.target.id);
        let secondMult = getMultiplicity(conn.attributes.labels[1]);
        return new UmlAssociation(assocType, assocName, firstEnd, firstMult, secondEnd, secondMult);
    }
}
