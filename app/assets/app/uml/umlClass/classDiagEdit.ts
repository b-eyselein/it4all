import * as $ from 'jquery';

import {ASSOC_TYPES, CARDINALITIES, UmlClassAttribute, UmlClassMethod, VISIBILITIES} from "../umlInterfaces";
import {MyJointClass} from "./classDiagElements";
import {classDiagGraph} from "./classDiagDrawing";

export {editClass, editLink};

let editDiv, classEditDiv, linkEditDiv;

const UmlTypes = ['String', 'int', 'double', 'char', 'boolean', 'void'];

interface ClassMemberAttrs {
    visibility: string;
    name: string;
    type: string;
}

function deleteMember(element: HTMLElement): void {
    let jButton: JQuery;
    if (element instanceof HTMLButtonElement) {
        jButton = $(element);
    } else if (element instanceof HTMLSpanElement) {
        jButton = $(element).parent();
    } else {
        console.error('Wrong element clicked!');
        return;
    }
    jButton.parent().parent().remove();
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

    $(event.target).trigger('blur');

    return false;
});

function attributeInputLine(umlAttribute: UmlClassAttribute, index: number): string {
    let visibilityOptions = VISIBILITIES.map((v) => `<option ${umlAttribute.visibility === v ? 'selected' : ''}>${v}</option>`).join('');

    return `
<div class="form-group">
    <div class="input-group">
        <select class="form-control" data-for="visibility">${visibilityOptions}</select>
        
        <span class="input-group-addon">
            <label title="Statisch / Klassenattribut" style="font-weight: normal;" for="att_static_${index}"><input type="checkbox" id="att_static_${index}"> s</label>
            <label title="Abstrakt" style="font-weight: normal;" for="att_abstract_${index}"><input type="checkbox" id="att_abstract_${index}"> a</label>
            <label title="Abgeleitet (derived)" style="font-weight: normal;" for="att_derived_${index}"><input type="checkbox" id="att_derived_${index}"> d</label>
        </span>

        <input class="form-control" placeholder="Attributname" data-for="name" value="${umlAttribute.name}" required>
        
        <span class="input-group-addon">:</span>
        
        <select class="form-control" data-for="type">
            ${UmlTypes.map(umlType => `<option ${umlType === umlAttribute.type ? 'selected' : ''}>${umlType}</option>`).join('')}
        </select>
        
        <span class="input-group-addon"></span>
        
        <button class="form-control btn-danger" title="Löschen"><span class="glyphicon glyphicon-remove"></span></button>
    </div>
</div>`.trim();
}

function methodInputLine(umlMethod: UmlClassMethod, index: number): string {
    let visibilityOptions = VISIBILITIES.map((v) => `<option ${umlMethod.visibility === v ? 'selected' : ''}>${v}</option>`).join('');

    return `
<div class="form-group">
    <div class="input-group">
        <select class="form-control" data-for="visibility">${visibilityOptions}</select>
        
        <span class="input-group-addon">
            <span title="Statisch / Klassenmethode"><input type="checkbox"> s</span>
            <span title="Abstrakt"><input type="checkbox"> a</span>
        </span>
        
        <input class="form-control" placeholder="Methodenname" data-for="name" value="${umlMethod.name}" required>
        
        <span class="input-group-addon">(</span>
        
        <input class="form-control" placeholder="Parameter" data-for="parameters" value="${umlMethod.parameters}" required>
        
        <span class="input-group-addon">):</span>
        
        <select class="form-control" data-for="type">
            ${UmlTypes.map(umlType => `<option ${umlType === umlMethod.type ? 'selected' : ''}>${umlType}</option>`).join("")}
        </select>
        
        <span class="input-group-addon"></span>
        
        <button class="form-control btn-danger" title="Löschen"><span class="glyphicon glyphicon-remove"></span></button>
    </div>
</div>`.trim();
}

function cardinalityOptions(current: string): string {
    return Object.keys(CARDINALITIES).map((c) => `<option value="${CARDINALITIES[c]}" ${CARDINALITIES[c] === current ? 'selected' : ''}>${CARDINALITIES[c]}</option>`).join('\n')
}

function htmlForCardinalityEdit(linkId, assocType, source, sourceMult, target, targetMult): string {
    let sourceMultOptions = cardinalityOptions(sourceMult);
    let assocTypeOptions = Object.keys(ASSOC_TYPES).map((c) => `<option value="${ASSOC_TYPES[c]}" ${c === assocType ? 'selected' : ''}>${ASSOC_TYPES[c]}</option>`).join('\n');
    let targetMultOptions = cardinalityOptions(targetMult);

    return `
<div class="row">
    <div class="col-md-3">
        <div class="panel panel-default">
            <div class="panel-heading" id="sourceClass">${source}</div>
            <div class="panel-body"><hr></div>
        </div>
    </div>

    <div class="col-md-6">
        <div class="row">
            <div class="col-md-3 text-center">
                <select class="form-control" id="sourceMult" title="empty">${sourceMultOptions}</select>
            </div>

            <div class="col-md-6">
                <!-- FIXME: use! -->
                <!--<select class="form-control" id="assocType" title="empty">${assocTypeOptions}</select>-->
            </div>

            <div class="col-md-3">
                <select class="form-control" id="targetMult" title="empty">${targetMultOptions}</select>
            </div>
        </div>

        <hr class="assocLine" id="linkId" data-id="${linkId}">
    </div>

    <div class="col-md-3">
        <div class="panel panel-default">
            <div class="panel-heading" id="targetClass">${target}</div>
            <div class="panel-body"><hr></div>
        </div>
    </div>
</div>

<hr>

<div class="btn-group btn-group-justified">
    <div class="btn-group">
        <button type="reset" class="btn btn-default" onclick="discardLinkEdits()">
            Änderungen verwerfen</button>
    </div>
    <div class="btn-group">
        <button class="btn btn-primary" onclick="updateCardinality()">Änderungen übernehmen</button>
    </div>
</div>

<hr>`.trim();
}

function classMemberFromHtml(memberGroup): ClassMemberAttrs {
    return {
        visibility: $(memberGroup).find('select[data-for="visibility"]').val() as string,
        name: $(memberGroup).find('input[data-for="name"]').val() as string,
        type: $(memberGroup).find('select[data-for="type"]').val() as string,
    }
}

function umlClassAttributeFromHtml(memberGroup: HTMLElement): UmlClassAttribute {
    let base: ClassMemberAttrs = classMemberFromHtml(memberGroup);

    let modifiers = {};
    $('.dropdown-menu').find('input').each((index: number, elem: HTMLInputElement) => {
        modifiers[elem.value] = elem.checked
    });

    console.warn(modifiers);

    return {
        visibility: base.visibility,
        name: base.name,
        type: base.type,
        isStatic: modifiers['static'],
        isDerived: modifiers['derived'],
        isAbstract: modifiers['abstract']
    };
}

function umlClassMethodFromHtml(memberGroup: HTMLElement): UmlClassMethod {
    let base: ClassMemberAttrs = classMemberFromHtml(memberGroup);

    let modifiers = {};
    $('.dropdown-menu').find('input').each((index: number, elem: HTMLInputElement) => {
        modifiers[elem.value] = elem.checked;
    });

    let parameters = $(memberGroup).find('input[data-for="parameters"]').val() as string;

    return {
        visibility: base.visibility,
        name: base.name,
        parameters: parameters,
        type: base.type,
        isStatic: modifiers['static'],
        isAbstract: modifiers['abstract']
    };
}

function editClass(model: MyJointClass): void {
    discardClassEdits();

    classEditDiv.prop('hidden', false);

    $('#classTypeSelect').val(model.getClassType());
    $('#editClassName').val(model.getClassName());
    $('#classEditSubmit').data('id', model.id);

    addAttributes(model.getAttributes(), 0);
    addMethods(model.getMethods(), 0);
}

function discardClassEdits(): void {
    classEditDiv.prop('hidden', true);
    $('#editAttrsDiv').find('div.form-group').remove();
    $('#editMethodsDiv').find('.form-group').remove();
}

function updateClass(button: HTMLButtonElement): void {
    let element = classDiagGraph.getCell($(button).data('id'));

    if (element instanceof MyJointClass) {
        element.setClassType($('#classTypeSelect').val() as string);
        element.setClassName($('#editClassName').val() as string);

        let attrs: UmlClassAttribute[] = [];
        $('#editAttrsDiv').find('.form-group').each((index, attrGroup) => {
            attrs.push(umlClassAttributeFromHtml(attrGroup))
        });
        element.setAttributes(attrs);

        let methods: UmlClassMethod[] = [];
        $('#editMethodsDiv').find('.form-group').each((index, metGroup) => {
            methods.push(umlClassMethodFromHtml(metGroup))
        });
        element.setMethods(methods);
    } else {
        console.error("Has there been an error?");
    }

    discardClassEdits();
}

function editLink(cellView: joint.dia.CellView): void {
    let cellModel = cellView.model;

    let assocType = 'ASSOCIATION';

    let sourceId: string = cellModel.get('source').id;
    let targetId: string = cellModel.get('target').id;

    let source = classDiagGraph.getCell(sourceId);
    let target = classDiagGraph.getCell(targetId);

    let sourceMult: string = cellModel.attributes.labels[0].attrs.text.text;
    let targetMult: string = cellModel.attributes.labels[1].attrs.text.text;

    linkEditDiv.prop('hidden', false);

    editDiv.html(htmlForCardinalityEdit(cellModel.id, assocType, source.get('className'), sourceMult, target.get('className'), targetMult));
}

function discardLinkEdits() {

    linkEditDiv.prop('hidden', true);
    editDiv.html('');
}

function updateCardinality(): void {
    let link: joint.dia.Cell = classDiagGraph.getCell($('#linkId').data('id'));

    link.prop(['labels/0/attrs/text/text'], $('#sourceMult').val());
    link.prop(['labels/1/attrs/text/text'], $('#targetMult').val());

    discardLinkEdits();
}

function addAttributes(umlAttributes: UmlClassAttribute[], startIndex: number): void {
    $('#editAttrsPlusBtn').before(umlAttributes.map((a, i) => attributeInputLine(a, startIndex + i)).join('\n'));
    $('.glyphicon-remove').parent().on('click', (event) => deleteMember(event.target as HTMLElement));
}

function addMethods(umlMethods: UmlClassMethod[], startIndex: number): void {
    $('#editMethodsPlusBtn').before(umlMethods.map((m, i) => methodInputLine(m, startIndex + i)).join('\n'));
    $('.glyphicon-remove').parent().on('click', (event) => deleteMember(event.target as HTMLElement));
}

$(() => {
    editDiv = $('#editDiv');
    classEditDiv = $('#classEditDiv');
    linkEditDiv = $('#linkEditDiv');

    let editAttrsPlusBtn = $('#editAttrsPlusBtn');
    editAttrsPlusBtn.on('click', () => {
        let index = editAttrsPlusBtn.parent().find('.form-group').length;
        console.warn(index);
        addAttributes([
            {visibility: '', name: '', type: '', isStatic: false, isAbstract: false, isDerived: false}
        ], index);
    });


    let editMethodsPlusBtn = $('#editMethodsPlusBtn');
    editMethodsPlusBtn.on('click', () => {
        let index = editMethodsPlusBtn.parent().find('.form-group').length;
        console.warn(index);
        addMethods([
            {visibility: '', name: '', parameters: '', type: '', isAbstract: false, isStatic: false}
        ], index);
    });

    $('#classEditReset').on('click', discardClassEdits);
    $('#classEditSubmit').on('click', (event: JQuery.Event) => updateClass(event.target as HTMLButtonElement));
});