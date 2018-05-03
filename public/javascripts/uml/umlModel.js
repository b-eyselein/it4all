var CLASS_TYPES;
(function (CLASS_TYPES) {
    CLASS_TYPES["CLASS"] = "class";
    CLASS_TYPES["ABSTRACT"] = "abstract";
    CLASS_TYPES["INTERFACE"] = "interface";
})(CLASS_TYPES || (CLASS_TYPES = {}));
var ASSOC_TYPES;
(function (ASSOC_TYPES) {
    ASSOC_TYPES["ASSOCIATION"] = "Assozation";
    ASSOC_TYPES["AGGREGATION"] = "Aggregation";
    ASSOC_TYPES["COMPOSITION"] = "Komposition";
    ASSOC_TYPES["IMPLEMENTATION"] = "Vererbung";
})(ASSOC_TYPES || (ASSOC_TYPES = {}));
var VISIBILITIES;
(function (VISIBILITIES) {
    VISIBILITIES["PUBLIC"] = "+";
    VISIBILITIES["PRIVATE"] = "-";
    VISIBILITIES["PROTECTED"] = "#";
    VISIBILITIES["PACKAGE"] = "~";
})(VISIBILITIES || (VISIBILITIES = {}));
var CARDINALITIES;
(function (CARDINALITIES) {
    CARDINALITIES["SINGLE"] = "1";
    CARDINALITIES["UNBOUND"] = "*";
})(CARDINALITIES || (CARDINALITIES = {}));
class UmlClass {
    constructor(name, classType, attributes, methods, position) {
        this.name = name;
        this.classType = classType;
        this.attributes = attributes;
        this.methods = methods;
        this.position = position;
    }
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
    constructor(visibilityRepr, name, type, isStatic, isAbstract) {
        this.visibilityRepr = visibilityRepr;
        this.name = name;
        this.type = type;
        this.isStatic = isStatic;
        this.isAbstract = isAbstract;
        if (new.target === UmlClassMember) {
            throw new TypeError('Cannot instantiate class UmlClassMember!');
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
    static fromHtml(memberGroup) {
        return {
            visibilityRepr: $(memberGroup).find('select[data-for="visibility"]').val(),
            name: $(memberGroup).find('input[data-for="name"]').val(),
            type: $(memberGroup).find('select[data-for="type"]').val(),
        };
    }
    buildString() {
        throw new Error('This method must be overridden!');
    }
}
class UmlClassMethod extends UmlClassMember {
    constructor(visibilityRepr, name, parameters, type, isStatic, isAbstract) {
        super(visibilityRepr, name, type, isStatic, isAbstract);
        this.parameters = parameters;
    }
    static fromMethodToLoad(mtl) {
        return new UmlClassMethod(visibilityReprFromValue(mtl.visibility), mtl.name, mtl.parameters, mtl.type, mtl.isStatic, mtl.isAbstract);
    }
    static fromHtml(memberGroup) {
        let base = super.fromHtml(memberGroup);
        let modifiers = {};
        $('.dropdown-menu').find('input').each((index, elem) => {
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
    constructor(visibilityRepr, name, type, isStatic, isDerived, isAbstract) {
        super(visibilityRepr, name, type, isStatic, isAbstract);
        this.isDerived = isDerived;
    }
    static fromAttributeToLoad(atl) {
        return new UmlClassAttribute(visibilityReprFromValue(atl.visibility), atl.name, atl.type, atl.isStatic, atl.isDerived, atl.isDerived);
    }
    static fromHtml(memberGroup) {
        let base = super.fromHtml(memberGroup);
        let modifiers = {};
        $('.dropdown-menu').find('input').each((index, elem) => modifiers[elem.value] = elem.checked);
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
    return graph.getCell(id).get('className');
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
    constructor(subClass, superClass) {
        this.subClass = subClass;
        this.superClass = superClass;
    }
    static fromConnection(conn) {
        let subClass = getClassNameFromCellId(conn.attributes.source.id);
        let superClass = getClassNameFromCellId(conn.attributes.target.id);
        return new UmlImplementation(subClass, superClass);
    }
}
class UmlAssociation {
    constructor(assocType, assocName, firstEnd, firstMult, secondEnd, secondMult) {
        this.assocType = assocType;
        this.assocName = assocName;
        this.firstEnd = firstEnd;
        this.firstMult = firstMult;
        this.secondEnd = secondEnd;
        this.secondMult = secondMult;
    }
    static fromConnection(conn) {
        let assocType = getTypeName(conn.attributes.type);
        let assocName = '';
        let firstEnd = getClassNameFromCellId(conn.attributes.source.id);
        let firstMult = getMultiplicity(conn.attributes.labels[0]);
        let secondEnd = getClassNameFromCellId(conn.attributes.target.id);
        let secondMult = getMultiplicity(conn.attributes.labels[1]);
        return new UmlAssociation(assocType, assocName, firstEnd, firstMult, secondEnd, secondMult);
    }
}
//# sourceMappingURL=umlModel.js.map