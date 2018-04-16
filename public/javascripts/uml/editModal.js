/**
 * @class UmlClassMember
 * @property {string} name
 * @property {string} type
 */
class UmlClassMember {
    constructor(memberGroup) {
        this.name = $(memberGroup).find('input').val();
        this.type = $(memberGroup).find('select').val();
    }
}

function discardClassEdits() {
    classEditModal.find('.modal-body').html('');
    classEditModal.modal('hide');
}

function discardCardinalityEdits() {
    cardinalityEditModal.modal('hide');
}

function addMember(button, isAttr) {
    $(button).before(memberInputs('', '', isAttr));
}

function deleteMember(button) {
    $(button).parent().parent().remove();
}

function memberInputs(memberName, memberType = '', isAttr = true) {
    return `
<div class="form-group">
  <div class="input-group">
    <input class="form-control" placeholder="${isAttr ? 'Attribut' : 'Methode'} für diese Klasse" value="${memberName}" required>
    <span class="input-group-addon">:</span>
    <select class="form-control">
      ${UmlTypes.map(umlType => `<option ${umlType === memberType ? 'selected' : ''}>${umlType}</option>`).join("")}
    </select>
    <span class="input-group-addon"></span>
    <button class="form-control btn-warning" title="Löschen" onclick="deleteMember(this)"><span class="glyphicon glyphicon-remove"></span></button>
  </div>
</div>`.trim();
}

function htmlForClassEdit(cellView) {
    let classType = cellView.model.attributes.type;
    let className = cellView.model.attributes.name;

    let attrInput = cellView.model.attributes.attributesObject.map((attr) => memberInputs(attr.name, attr.type)).join('\n');
    let memberInput = cellView.model.attributes.methodsObject.map((method) => memberInputs(method.name, method.type)).join('\n');

    return `
<div class="panel panel-default">
 <div class="panel-heading">
   <div class="form-group">
     <select class="form-control text-center">
       <option value="${'CLASS'}" ${classType === 'uml.Class' ? 'selected' : ''}>&lt;&lt;class&gt;&gt;</option>
       <option value="${'ABSTRACT'}" ${classType === 'uml.Abstract' ? 'selected' : ''}>&lt;&lt;abstract&gt;&gt;</option>
       <option value="${'INTERFACE'}" ${classType === 'uml.Interface' ? 'selected' : ''}>&lt;&lt;interface&gt;&gt;</option>
     </select>
   </div>
   <div class="form-group">
     <input value="${className}" id="editClassName" class="form-control text-center" placeholder="Name der Klasse">
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
</div>`.trim();
}

// noinspection JSUnusedGlobalSymbols
function updateClass(elementId) {
    let element = graph.getCell(elementId);

    // FIXME: update class (--> element...)
    element.attr('.uml-class-name-text/text', $('#editClassName').val());

    let attrs = $('#editAttrsDiv').find('.input-group').map((index, attrGroup) => new UmlClassMember(attrGroup)).get();

    element.prop('attributesObject', attrs);
    element.prop('attributes.attributes', attrs.map((a) => a.name + ": " + a.type).join('\n'));
    element.attr('.uml-class-attrs-text/text', attrs.map((a) => a.name + ': ' + a.type).join('\n'));

    let methods = $('#editMethodsDiv').find('.input-group').map((index, metGroup) => new UmlClassMember(metGroup)).get();

    element.prop('methodsObject', methods);
    element.prop('attributes.methods', methods.map((m) => m.name + ": " + m.type).join('\n'));
    element.attr('.uml-class-methods-text/text', methods.map(m => m.name + ': ' + m.type).join('\n'));

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