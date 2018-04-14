function discardClassEdits() {
    classEditModal.find('.modal-body').html('');
    classEditModal.modal('hide');
}

function discardCardinalityEdits() {
    cardinalityEditModal.find('.modal-body').html('');
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
    let currentAttrs = cellView.model.attributes.attributes;
    let currentMethods = cellView.model.attributes.methods;

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
     ${currentAttrs.map(attr => memberInputs(attr.name, attr.type)).join('\n')}
     <button class="btn btn-primary" id="editAttrsPlusBtn" onclick="addMember(this, true)">
       <span class="glyphicon glyphicon-plus"></span> Attribut hinzufügen
     </button>
   </div>
   <hr>
   <div id="editMethodsDiv">
     ${currentMethods.map(met => memberInputs(met.name, met.type)).join('\n')}
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
    console.log('Updating class...');
    let element = graph.getCell(elementId);

    // FIXME: update class (--> element...)
    console.warn('TODO: change type ' + element.attributes.type);
    element.attr('.uml-class-name-text/text', $('#editClassName').val());

    let attrs = $('#editAttrsDiv').find('.input-group').map((index, attrGroup) => {
        return {
            name: $(attrGroup).find('input').val(),
            type: $(attrGroup).find('select').val()
        };
    }).get();
    element.attributes.attributes = attrs;
    element.attr('.uml-class-attrs-text/text', attrs.map((a) => a.name + ': ' + a.type).join('\n'));

    // FIXME: method parameters?
    let mets = $('#editMethodsDiv').find('.input-group').map((index, metGroup) => {
        return {
            name: $(metGroup).find('input').val(),
            type: $(metGroup).find('select').val()
        }
    }).get();

    element.attributes.methods = mets;
    element.attr('.uml-class-methods-text/text', mets.map(m => m.name + ': ' + m.type).join('\n'));

    discardClassEdits();
}


function htmlForCardinalityEdit(linkId, source, sourceMult, target, targetMult) {
    return `
<div class="row">
    <div class="col-md-3">
        <div class="panel panel-default">
            <div class="panel-heading">${source}</div>
            <div class="panel-body"><hr></div>
        </div>
    </div>
    
    <div class="col-md-6">
        <div class="row">
            <div class="col-md-3 text-center">
                <select class="form-control" id="sourceMult">
                    <option${sourceMult === '1' ? ' selected' : ''}>1</option>
                    <option${sourceMult === '*' ? ' selected' : ''}>*</option>
                </select>
            </div>
                
            <div class="col-md-6"></div>
                    
            <div class="col-md-3">
                <select class="form-control" id="targetMult">
                    <option${targetMult === '1' ? ' selected' : ''}>1</option>
                    <option${targetMult === '*' ? ' selected' : ''}>*</option>
                </select>
            </div>
        </div>
    
        <hr style="border-color: black;">
    </div>
    
    <div class="col-md-3">
        <div class="panel panel-default">
            <div class="panel-heading">${target}</div>
            <div class="panel-body"><hr></div>
        </div>
    </div>
</div>

<hr>

<div class="btn-group btn-group-justified">
    <div class="btn-group">
        <button type="reset" class="btn btn-default" onclick="discardCardinalityEdits()">Änderungen verwerfen</button>
    </div>
    <div class="btn-group">
        <button class="btn btn-primary" onclick="updateCardinality('${linkId}')">Änderungen übernehmen</button>
    </div>
</div>`.trim();
}

function updateCardinality(linkId) {
    let link = graph.getCell(linkId);

    link.prop(['labels', 0, 'attrs', 'text', 'text'], $('#sourceMult').val());
    link.prop(['labels', 1, 'attrs', 'text', 'text'], $('#targetMult').val());

    discardCardinalityEdits();
}