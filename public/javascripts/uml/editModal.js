const VISIBILITIES = {
    PUBLIC: '+',
    PRIVATE: '-',
    PROTECTED: '#',
    PACKAGE: '~'
};

let editModal, cardinalityEditModal;

$(document).ready(function () {
    editModal = $('#editDiv');
});

function editClass(cellView) {
    editModal.html(htmlForClassEdit(cellView));
}

/**
 * @class UmlClassMember
 *
 * @property {string} visibilityRepr
 * @property {string} visibility
 * @property {string} name
 * @property {string} type
 */
class UmlClassMember {
    constructor(visibilityRepr, name, type) {
        if (new.target === UmlClassMember) {
            throw new TypeError('Cannot instantiate class UmlClassMember!')
        }
        this.visibilityRepr = visibilityRepr;
        this.name = name;
        this.type = type;

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
        }
    }

    buildString() {
        throw new Error('This method must be overridden!');
    }
}

/**
 * @class UmlClassAttribute
 *
 * @property {string} visibility
 * @property {string} name
 * @property {string} type
 *
 * @property {boolean} isStatic
 * @property {boolean} isDerived
 * @property {boolean} isAbstract
 */
class UmlClassAttribute extends UmlClassMember {
    constructor(visibilityRepr, name, type, isStatic, isDerived, isAbstract) {
        super(visibilityRepr, name, type);
        this.isStatic = isStatic;
        this.isDerived = isDerived;
        this.isAbstract = isAbstract;
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

/**
 * @class UmlClassMember
 *
 * @property {string} visibilityRepr
 * @property {string} visibility
 * @property {string} name
 * @property {string} parameters
 * @property {string} type
 * @property {boolean} isStatic
 * @property {boolean} isAbstract
 */
class UmlClassMethod extends UmlClassMember {

    constructor(visibilityRepr, name, parameters, type, isStatic, isAbstract) {
        super(visibilityRepr, name, type);
        this.isStatic = isStatic;
        this.isAbstract = isAbstract;
        this.parameters = parameters;
    }

    static fromHtml(memberGroup) {
        let base = super.fromHtml(memberGroup);

        let modifiers = {};
        $('.dropdown-menu').find('input').each((index, elem) => {
            modifiers[elem.value] = elem.checked;
        });

        console.log(modifiers);

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

function discardClassEdits() {
    editModal.html('');
}

function discardCardinalityEdits() {
    cardinalityEditModal.modal('hide');
}

function addMember(button, isAttr) {
    $(button).before(isAttr ? attributeInputLine(new UmlClassAttribute('', '', '', false, false, false)) : methodInputLine(new UmlClassMethod('', '', '', false)));
}

function deleteMember(button) {
    $(button).parent().parent().remove();
}

let options = [];

$('.dropdown-menu a').on('click', function (event) {

    let $target = $(event.currentTarget),
        val = $target.attr('data-value'),
        $inp = $target.find('input'),
        idx;

    if ((idx = options.indexOf(val)) > -1) {
        options.splice(idx, 1);
        setTimeout(function () {
            $inp.prop('checked', false)
        }, 0);
    } else {
        options.push(val);
        setTimeout(function () {
            $inp.prop('checked', true)
        }, 0);
    }

    $(event.target).blur();

    return false;
});

/**
 * @param {UmlClassAttribute} umlAttribute
 * @returns {string}
 */
function attributeInputLine(umlAttribute) {
    let visibilityOptions = Object.keys(VISIBILITIES).map((v) => `<option ${umlAttribute.visibility === v ? 'selected' : ''}>${VISIBILITIES[v]}</option>`).join('');

    return `
<div class="form-group">
    <div class="input-group">
        <select class="form-control" data-for="visibility">
            ${visibilityOptions}
        </select>
        
        <span class="input-group-addon"></span>
        
        <button type="button" class="form-control dropdown-toggle" data-toggle="dropdown">Modifier <span class="caret"></span></button>
        <ul class="dropdown-menu">
            <li><a data-value="static" tabIndex="-1"><label><input type="checkbox" value="static" ${umlAttribute.isStatic ? "checked" : ""}> Klassenattribut</label></a></li>
            <li><a data-value="derived" tabIndex="-1"><label><input type="checkbox" value="derived" ${umlAttribute.isDerived ? "checked" : ""}> Abgeleitet</label></a></li>
            <li><a data-value="abstract" tabIndex="-1"><label><input type="checkbox" value="abstract" ${umlAttribute.isAbstract ? "checked" : ""}> Abstrakt</label></a></li>
        </ul>
        
        <span class="input-group-addon"></span>
        
        <input class="form-control" placeholder="Attributname" data-for="name" value="${umlAttribute.name}" required>
        
        <span class="input-group-addon">:</span>
        
        <select class="form-control" data-for="type">
            ${UmlTypes.map(umlType => `<option ${umlType === umlAttribute.type ? 'selected' : ''}>${umlType}</option>`).join('')}
        </select>
        
        <span class="input-group-addon"></span>
        
        <button class="form-control btn-danger" title="Löschen" onclick="deleteMember(this)"><span class="glyphicon glyphicon-remove"></span></button>
    </div>
</div>`.trim();
}

/**
 * @param {UmlClassMethod} umlMethod
 * @returns {string}
 */
function methodInputLine(umlMethod) {
    let visibilityOptions = Object.keys(VISIBILITIES).map((v) => `<option ${umlMethod.visibility === v ? 'selected' : ''}>${VISIBILITIES[v]}</option>`).join('');

    return `
<div class="form-group">
    <div class="input-group">
        <select class="form-control" data-for="visibility">
            ${visibilityOptions}
        </select>
        
        <span class="input-group-addon"></span>
        
        <button type="button" class="form-control dropdown-toggle" data-toggle="dropdown">Modifier <span class="caret"></span></button>
        <ul class="dropdown-menu">
            <li><a data-value="static" tabIndex="-1"><label><input type="checkbox" value="static" ${umlMethod.isStatic ? "checked" : ""}> Klassenmethode</label></a></li>
            <li><a data-value="abstract" tabIndex="-1"><label><input type="checkbox" value="abstract" ${umlMethod.isAbstract ? "checked" : ""}> Abstrakt</label></a></li>
        </ul>
        
        <span class="input-group-addon"></span>
        
        <input class="form-control" placeholder="Methodenname" data-for="name" value="${umlMethod.name}" required>
        
        <span class="input-group-addon">(</span>
        
        <input class="form-control" placeholder="Parameter" data-for="parameters" value="${umlMethod.parameters}" required>
        
        <span class="input-group-addon">)</span>
        
        <select class="form-control" data-for="type">
            ${UmlTypes.map(umlType => `<option ${umlType === umlMethod.type ? 'selected' : ''}>${umlType}</option>`).join("")}
        </select>
        
        <span class="input-group-addon">:</span>
        
        <button class="form-control btn-danger" title="Löschen" onclick="deleteMember(this)"><span class="glyphicon glyphicon-remove"></span></button>
    </div>
</div>`.trim();
}

function htmlForClassEdit(cellView) {
    let classType = cellView.model.attributes.type;
    let className = cellView.model.attributes.name;

    let attrInput = cellView.model.attributes.attributesObject.map(attributeInputLine).join('\n');
    let memberInput = cellView.model.attributes.methodsObject.map(methodInputLine).join('\n');

    return `
<h3 class="text-center">Klasse bearbeiten</h3>
<div class="panel panel-default">
    <div class="panel-heading">
        <div class="row">
            <div class="col-sm-6">
                <div class="form-group">
                    <select class="form-control text-center">
                        <option value="${'CLASS'}" ${classType === 'uml.Class' ? 'selected' : ''}>&lt;&lt;class&gt;&gt;</option>
                        <option value="${'ABSTRACT'}" ${classType === 'uml.Abstract' ? 'selected' : ''}>&lt;&lt;abstract&gt;&gt;</option>
                        <option value="${'INTERFACE'}" ${classType === 'uml.Interface' ? 'selected' : ''}>&lt;&lt;interface&gt;&gt;</option>
                    </select>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <input value="${className}" id="editClassName" class="form-control text-center" placeholder="Name der Klasse">
                </div>
            </div>
        </div>
    </div>
    <div class="panel-body">
        <div id="editAttrsDiv">
            ${attrInput}
            <button class="btn btn-primary" id="editAttrsPlusBtn" onclick="addMember(this, true)">
                <span class="glyphicon glyphicon-plus"></span> Attribut hinzufügen
            </button>
        </div>

        <hr>
        
        <div id="editMethodsDiv">
            ${memberInput}
            <button class="btn btn-primary" id="editMethodsPlusBtn" onclick="addMember(this, false)">
                <span class="glyphicon glyphicon-plus"></span> Methode hinzufügen
            </button>
        </div>
    </div>
</div>

<hr>

<div class="btn-group btn-group-justified">
    <div class="btn-group">
        <button type="reset" class="btn btn-default" onclick="discardClassEdits()">Änderungen verwerfen</button>
    </div>
    <div class="btn-group">
        <button class="btn btn-primary" onclick="updateClass('${cellView.model.id}')">Klasse aktualisieren</button>
    </div>
</div>

<hr>`.trim();
}

// noinspection JSUnusedGlobalSymbols
function updateClass(elementId) {
    let element = graph.getCell(elementId);

    // FIXME: update class (--> element...)
    element.attr('.uml-class-name-text/text', $('#editClassName').val());

    let attrs = $('#editAttrsDiv').find('.form-group').map((index, attrGroup) => UmlClassAttribute.fromHtml(attrGroup)).get();

    element.prop('attributesObject', attrs);
    element.prop('attributes.attributes', attrs.map((a) => a.buildString()).join('\n'));
    element.attr('.uml-class-attrs-text/text', attrs.map((a) => a.buildString()).join('\n'));

    let methods = $('#editMethodsDiv').find('.form-group').map((index, metGroup) => UmlClassMethod.fromHtml(metGroup)).get();

    element.prop('methodsObject', methods);
    element.prop('attributes.methods', methods.map((m) => m.buildString()).join('\n'));
    element.attr('.uml-class-methods-text/text', methods.map(m => m.buildString()).join('\n'));

    discardClassEdits();
}


function htmlForCardinalityEdit(linkId, source, sourceMult, target, targetMult) {
    $('#sourceClass').text(source);
    $('#sourceMult').val(sourceMult);

    $('#targetClass').text(target);
    $('#targetMult').val(targetMult);

    $('#linkId').data('id', linkId);
}

function updateCardinality() {
    let link = graph.getCell($('#linkId').data('id'));

    link.prop(['labels', 0, 'attrs', 'text', 'text'], $('#sourceMult').val());
    link.prop(['labels', 1, 'attrs', 'text', 'text'], $('#targetMult').val());

    discardCardinalityEdits();
}