import * as $ from 'jquery';

import {UmlClassAttribute, UmlClassMethod, VISIBILITIES} from "../umlInterfaces";
import {MyJointClass} from "./classDiagElements";
import {classDiagGraph} from "./classDiagDrawing";

export {editClass, editLink};

let classEditDiv: JQuery, linkEditDiv: JQuery;

let classEditSubmit: JQuery, linkEditSubmit: JQuery;

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

function changeAttr(button: HTMLButtonElement): void {
    console.warn(button);
}

function attributeInputLine(umlAttribute: UmlClassAttribute): string {
    let visibilityOptions = VISIBILITIES.map((v) => `<option ${umlAttribute.visibility === v ? 'selected' : ''}>${v}</option>`).join('');

    return `
<div class="form-group">
    <div class="input-group">
        <select class="form-control" data-for="visibility">${visibilityOptions}</select>
        
        <span class="modifierDiv">
            <button class="btn attrModifierBtn${umlAttribute.isStatic ? " btn-primary" : ""}" title="Statisch / Klassenattribut">s</button>
            <button class="btn attrModifierBtn${umlAttribute.isAbstract ? " btn-primary" : ""}" title="Abstrakt">a</button>
            <button class="btn attrModifierBtn${umlAttribute.isDerived ? " btn-primary" : ""}" title="Abgeleitet">d</button>
        </span>
        
        <input class="form-control" placeholder="Attributname" data-for="name" value="${umlAttribute.name}" required>
        
        <span class="input-group-text">:</span>
        
        <select class="form-control" data-for="type">
            ${UmlTypes.map(umlType => `<option ${umlType === umlAttribute.type ? 'selected' : ''}>${umlType}</option>`).join('')}
        </select>
        
        <button class="btn btn-danger" title="Löschen"><span class="octicon octicon-x"></span></button>
    </div>
</div>`.trim();
}

function methodInputLine(umlMethod: UmlClassMethod): string {
    let visibilityOptions = VISIBILITIES.map((v) => `<option ${umlMethod.visibility === v ? 'selected' : ''}>${v}</option>`).join('');

    return `
<div class="form-group">
    <div class="input-group">
        <select class="form-control" data-for="visibility">${visibilityOptions}</select>
        
        <span class="modifierDiv">
            <button class="btn methodModifierBtn${umlMethod.isStatic ? " btn-primary" : ""}" title="Statisch / Klassenmethode">s</button>
            <button class="btn methodModifierBtn${umlMethod.isAbstract ? " btn-primary" : ""}" title="Abstrakt">a</button>
        </span>
        
        <input class="form-control" placeholder="Methodenname" data-for="name" value="${umlMethod.name}" required>
        
        <span class="input-group-text">(</span>
        
        <input class="form-control" placeholder="Parameter" data-for="parameters" value="${umlMethod.parameters}" required>
        
        <span class="input-group-text">):</span>
        
        <select class="form-control" data-for="type">
            ${UmlTypes.map(umlType => `<option ${umlType === umlMethod.type ? 'selected' : ''}>${umlType}</option>`).join("")}
        </select>
        
        <button class="btn btn-danger" title="Löschen"><span class="octicon octicon-x"></span></button>
    </div>
</div>`.trim();
}

function classMemberFromHtml(memberGroup): ClassMemberAttrs {
    return {
        visibility: $(memberGroup).find('select[data-for="visibility"]').val() as string,
        name: $(memberGroup).find('input[data-for="name"]').val() as string,
        type: $(memberGroup).find('select[data-for="type"]').val() as string,
    }
}

function umlClassAttributeFromHtml(memberGroup: HTMLElement): UmlClassAttribute | null {
    let base: ClassMemberAttrs = classMemberFromHtml(memberGroup);

    let modifiers = {};
    $('.modifierDiv').find('button.attrModifierBtn').each((index: number, elem: HTMLButtonElement) => {
        const jElem = $(elem);
        modifiers[jElem.text()] = jElem.hasClass('btn-primary');
    });

    if (base.name.length === 0) {
        return null;
    } else {
        return {
            visibility: base.visibility,
            name: base.name,
            type: base.type,
            isStatic: modifiers['s'],
            isDerived: modifiers['d'],
            isAbstract: modifiers['a']
        };
    }
}

function umlClassMethodFromHtml(memberGroup: HTMLElement): UmlClassMethod | null {
    let base: ClassMemberAttrs = classMemberFromHtml(memberGroup);

    let modifiers = {};
    $('.modifierDiv').find('button.methodModifierBtn').each((index: number, elem: HTMLButtonElement) => {
        const jElem = $(elem);
        modifiers[jElem.text()] = jElem.hasClass('btn-primary');
    });

    let parameters = $(memberGroup).find('input[data-for="parameters"]').val() as string;

    if (base.name.length === 0 || parameters.length === 0) {
        return null;
    } else {
        return {
            visibility: base.visibility,
            name: base.name,
            parameters: parameters,
            type: base.type,
            isStatic: modifiers['s'],
            isAbstract: modifiers['a']
        };
    }
}

function editClass(model: MyJointClass): void {
    // Clear current edit...
    discardClassEdits();

    classEditDiv.prop('hidden', false);

    $('#classTypeSelect').val(model.getClassType());
    $('#editClassName').val(model.getClassName());
    $('#classEditSubmit').data('id', model.id);

    addAttributes(model.getAttributes());
    addMethods(model.getMethods());
}

function discardClassEdits(): void {
    classEditDiv.prop('hidden', true);
    $('#editAttrsDiv').find('div.form-group').remove();
    $('#editMethodsDiv').find('.form-group').remove();
}

function updateClass(): void {
    let element = classDiagGraph.getCell(classEditSubmit.data('id'));

    if (element instanceof MyJointClass) {
        element.setClassType($('#classTypeSelect').val() as string);
        element.setClassName($('#editClassName').val() as string);

        let attrs: UmlClassAttribute[] = [];
        $('#editAttrsDiv').find('.form-group').each((index, attrGroup: HTMLElement) => {
            const readAttr = umlClassAttributeFromHtml(attrGroup);
            if (readAttr != null) {
                attrs.push(readAttr);
            }
        });
        element.setAttributes(attrs);

        let methods: UmlClassMethod[] = [];
        $('#editMethodsDiv').find('.form-group').each((index, metGroup: HTMLElement) => {
            const readMethod = umlClassMethodFromHtml(metGroup);
            if (readMethod != null) {
                methods.push(readMethod);
            }
        });
        element.setMethods(methods);

    } else {
        console.error("Has there been an error?");
    }

    discardClassEdits();
}

function editLink(cellView: joint.dia.CellView): void {
    discardLinkEdits();

    let cellModel = cellView.model;

    let assocType = 'ASSOCIATION';

    let source: joint.dia.Cell = classDiagGraph.getCell(cellModel.get('source').id);
    let target: joint.dia.Cell = classDiagGraph.getCell(cellModel.get('target').id);

    let sourceMult: string = cellModel.prop('labels/0/attrs/text/text');
    let targetMult: string = cellModel.prop('labels/1/attrs/text/text');


    linkEditDiv.prop('hidden', false);

    linkEditSubmit.data('id', cellModel.id);

    if (source instanceof MyJointClass && target instanceof MyJointClass) {
        $('#sourceClass').text(source.getClassName());
        $('#targetClass').text(target.getClassName());
    }

    $('#sourceMult').val(sourceMult);
    $('#targetMult').val(targetMult);
}

function discardLinkEdits(): void {
    linkEditDiv.prop('hidden', true);
}

function updateLink(): void {
    const link: joint.dia.Link = classDiagGraph.getCell(linkEditSubmit.data('id')) as joint.dia.Link;

    link.prop('labels/0/attrs/text/text', $('#sourceMult').val());
    link.prop('labels/1/attrs/text/text', $('#targetMult').val());

    discardLinkEdits();
}

function addAttributes(umlAttributes: UmlClassAttribute[]): void {
    $('#editAttrsPlusBtn').before(umlAttributes.map(attributeInputLine).join('\n'));
    $('.glyphicon-remove').parent().on('click', (event) => deleteMember(event.target as HTMLElement));

    $('.attrModifierBtn').on('click', (event: JQuery.Event) => {
        $(event.target).toggleClass('btn-primary');
    });
}

function addMethods(umlMethods: UmlClassMethod[]): void {
    $('#editMethodsPlusBtn').before(umlMethods.map(methodInputLine).join('\n'));
    $('.glyphicon-remove').parent().on('click', (event) => deleteMember(event.target as HTMLElement));

    $('.methodModifierBtn').on('click', (event: JQuery.Event) => {
        $(event.target).toggleClass('btn-primary');
    });
}

$(() => {
    classEditDiv = $('#classEditDiv');
    linkEditDiv = $('#linkEditDiv');

    classEditSubmit = $('#classEditSubmit');
    linkEditSubmit = $('#linkEditSubmit');

    let editAttrsPlusBtn = $('#editAttrsPlusBtn');
    editAttrsPlusBtn.on('click', () => {
        let index = editAttrsPlusBtn.parent().find('.form-group').length;
        addAttributes([{visibility: '', name: '', type: '', isStatic: false, isAbstract: false, isDerived: false}]);
    });


    let editMethodsPlusBtn = $('#editMethodsPlusBtn');
    editMethodsPlusBtn.on('click', () => {
        let index = editMethodsPlusBtn.parent().find('.form-group').length;
        addMethods([{visibility: '', name: '', parameters: '', type: '', isAbstract: false, isStatic: false}]);
    });


    $('#classEditReset').on('click', discardClassEdits);
    classEditSubmit.on('click', updateClass);

    $('#linkEditReset').on('click', discardLinkEdits);
    linkEditSubmit.on('click', updateLink);
});