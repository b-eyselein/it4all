let editDiv, cardinalityEditModal;
const UmlTypes = ['String', 'int', 'double', 'char', 'boolean', 'void'];
$(document).ready(function () {
    editDiv = $('#editDiv');
});
function editClass(cellView) {
    editDiv.html(htmlForClassEdit(cellView));
}
function editLink(cellView) {
    let cellModel = cellView.model;
    let assocType = 'ASSOCIATION';
    let sourceId = cellModel.get('source').id;
    let targetId = cellModel.get('target').id;
    let source = graph.getCell(sourceId);
    let target = graph.getCell(targetId);
    let sourceMult = cellModel.attributes.labels[0].attrs.text.text;
    let targetMult = cellModel.attributes.labels[1].attrs.text.text;
    editDiv.html(htmlForCardinalityEdit(cellModel.id, assocType, source.get('className'), sourceMult, target.get('className'), targetMult));
}
function discardEdits() {
    editDiv.html('');
}
function addMember(button, isAttr) {
    let toInsert = '';
    if (isAttr) {
        toInsert = attributeInputLine(new UmlClassAttribute('', '', '', false, false, false));
    }
    else {
        toInsert = methodInputLine(new UmlClassMethod('', '', '', '', false, false));
    }
    $(button).before(toInsert);
}
function deleteMember(button) {
    $(button).parent().parent().remove();
}
let options = [];
$('.dropdown-menu a').on('click', function (event) {
    let $target = $(event.currentTarget), val = $target.attr('data-value'), $inp = $target.find('input'), idx;
    if ((idx = options.indexOf(val)) > -1) {
        options.splice(idx, 1);
        setTimeout(function () {
            $inp.prop('checked', false);
        }, 0);
    }
    else {
        options.push(val);
        setTimeout(function () {
            $inp.prop('checked', true);
        }, 0);
    }
    $(event.target).blur();
    return false;
});
function attributeInputLine(umlAttribute) {
    let visibilityOptions = Object.keys(VISIBILITIES).map((v) => `<option ${umlAttribute.visibility === v ? 'selected' : ''}>${VISIBILITIES[v]}</option>`).join('');
    return `
<div class="form-group">
    <div class="input-group">
        <select class="form-control" data-for="visibility">${visibilityOptions}</select>
        
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
function methodInputLine(umlMethod) {
    let visibilityOptions = Object.keys(VISIBILITIES).map((v) => `<option ${umlMethod.visibility === v ? 'selected' : ''}>${VISIBILITIES[v]}</option>`).join('');
    return `
<div class="form-group">
    <div class="input-group">
        <select class="form-control" data-for="visibility">${visibilityOptions}</select>
        
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
    let umlClass = cellView.model.getAsUmlClass();
    let attrInput = umlClass.attributes.map(attributeInputLine).join('\n');
    let memberInput = umlClass.methods.map(methodInputLine).join('\n');
    const classTypeSelectors = Object.keys(CLASS_TYPES).map((ct) => `<option value="${ct}" ${umlClass.classType === ct ? 'selected' : ''}>&lt;&lt;${ct.toLocaleLowerCase()}&gt;&gt;</option>`);
    return `
<h3 class="text-center">Klasse bearbeiten</h3>
<div class="panel panel-default">
    <div class="panel-heading">
        <div class="row" id="classEditRow">
            <div class="col-sm-6">
                <div class="form-group">
                    <select class="form-control text-center" id="classTypeSelect">${classTypeSelectors}</select>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <input value="${umlClass.name}" id="editClassName" class="form-control text-center" placeholder="Name der Klasse">
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
        <button type="reset" class="btn btn-default" onclick="discardEdits()">Änderungen verwerfen</button>
    </div>
    <div class="btn-group">
        <button class="btn btn-primary" onclick="updateClass('${cellView.model.id}')">Klasse aktualisieren</button>
    </div>
</div>

<hr>`.trim();
}
function cardinalityOptions(current) {
    return Object.keys(CARDINALITIES).map((c) => `<option value="${CARDINALITIES[c]}" ${CARDINALITIES[c] === current ? 'selected' : ''}>${CARDINALITIES[c]}</option>`).join('\n');
}
function htmlForCardinalityEdit(linkId, assocType, source, sourceMult, target, targetMult) {
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
        <button type="reset" class="btn btn-default" onclick="discardEdits()">
            Änderungen verwerfen</button>
    </div>
    <div class="btn-group">
        <button class="btn btn-primary" onclick="updateCardinality()">Änderungen übernehmen</button>
    </div>
</div>

<hr>`.trim();
}
function updateClass(elementId) {
    let element = graph.getCell(elementId);
    let attrs = $('#editAttrsDiv').find('.form-group').map((index, attrGroup) => UmlClassAttribute.fromHtml(attrGroup)).get();
    let methods = $('#editMethodsDiv').find('.form-group').map((index, metGroup) => UmlClassMethod.fromHtml(metGroup)).get();
    element.prop('classType', $('#classTypeSelect').val());
    element.prop('className', $('#editClassName').val());
    element.prop('attributes', attrs);
    element.prop('methods', methods);
    discardEdits();
}
function updateCardinality() {
    let link = graph.getCell($('#linkId').data('id'));
    link.prop(['labels', 0, 'attrs', 'text', 'text'], $('#sourceMult').val());
    link.prop(['labels', 1, 'attrs', 'text', 'text'], $('#targetMult').val());
    discardEdits();
}
//# sourceMappingURL=editModal.js.map