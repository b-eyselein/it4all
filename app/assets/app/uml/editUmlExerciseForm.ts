import * as $ from 'jquery';

let addIgnoreWordBtn: JQuery;
let ignoreWordsRow: JQuery;

let addMappingsBtn: JQuery;
let mappingsRow: JQuery;

let addAttrBtn: JQuery;
let attrsRow: JQuery;

let addMethodBtn: JQuery;
let methodsRow: JQuery;

let addAssocBtn: JQuery;
let assocsRow: JQuery;

let addImplBtn: JQuery;
let implsRow: JQuery;

const sampleId = 0; // TODO: calculate per button...

$(() => {

    ignoreWordsRow = $('#ignoreWordsRow');
    addIgnoreWordBtn = $('#addIgnoreWordBtn');
    addIgnoreWordBtn.on('click', () => {
        const newId: number = ignoreWordsRow.children().length - 1;

        addIgnoreWordBtn.parent().before(`
<div class="col-md-4">
    <div class="form-group">
        <input type="text" class="form-control" name="ignoreWords[${newId}]" id="ignoreWords_${newId}" placeholder="Ignoriertes Wort">
    </div>
</div>`.trim())
    });

    mappingsRow = $('#mappingsRow');
    addMappingsBtn = $('#addMappingBtn');
    addMappingsBtn.on('click', () => {
        const newId: number = mappingsRow.children().length - 1;

        addMappingsBtn.parent().before(`
<div class="col-md-6">
    <div class="form-group">
        <div class="input-group">
            <input type="text" class="form-control" name="mappings[${newId}].key" id="mappings_${newId}_key" placeholder="Ersetzt">
            <div class="input-group-text">&rarr;</div>
            <input type="text" class="form-control" name="mappings[${newId}].value" id="mappings_${newId}_value" placeholder="Ersetzung">
        </div>
    </div>
</div>`.trim())
    });


    assocsRow = $('#assocsRow');
    addAssocBtn = $('#addAssocBtn');
    addAssocBtn.on('click', addAssocBtnClick);

    implsRow = $('#implsRow');
    addImplBtn = $('#addImplBtn');
    addImplBtn.on('click', addImplBtnClick)
});

function addAssocBtnClick(): void {
    const newAssocId: number = assocsRow.children().length - 1;

    addAssocBtn.parent().before(`
<div class="form-group row">
    <div class="col-md-2">
        <select class="form-control" name="samples[${sampleId}].associations[${newAssocId}].assocType"
            id="samples_${sampleId}_associations_${newAssocId}_assocType">
            <option value="ASSOCIATION">Assoziation</option>
            <option value="AGGREGATION">Aggregation</option>
            <option value="COMPOSITION">Komposition</option>
        </select>
    </div>
    
    <div class="col-md-2">
        <input type="text" class="form-control" name="samples[${sampleId}].associations[${newAssocId}].assocName"
            id="samples_${sampleId}_associations_${newAssocId}_assocName" placeholder="Name">
    </div>

    <div class="col-md-2"> 
        <input type="text" class="form-control" name="samples[${sampleId}].associations[${newAssocId}].firstEnd"
            id="samples_${sampleId}_associations_${newAssocId}_firstEnd" placeholder="Klassenname">
    </div>
    
    <div class="col-md-4">
        <div class="input-group">
            <select class="form-control" name="samples[${sampleId}].associations[${newAssocId}].firstMult"
                id="samples_${sampleId}_associations_${newAssocId}_firstMult">
                <option value="SINGLE">1</option>
                <option value="UNBOUND">*</option>
            </select>
            <div class="input-group-text">:</div>
            <select class="form-control" name="samples[${sampleId}].associations[${newAssocId}].secondMult"
                id="samples_${sampleId}_associations_${newAssocId}_secondMult">
                <option value="SINGLE">1</option>
                <option value="UNBOUND">*</option>
            </select>
        </div>
    </div>
    
    <div class="col-md-2">
        <input type="text" class="form-control" name="samples[${sampleId}].associations[${newAssocId}].secondEnd"
        id="samples_${sampleId}_associations_${newAssocId}_secondEnd" placeholder="Klassenname">
    </div>
</div>`.trim());
}

function addImplBtnClick(): void {
    const newImplId: number = implsRow.children().length - 1;

    addImplBtn.parent().before(`
<div class="col-md-6">
    <div class="form-group">
        <div class="input-group">
            <input type="text" class="form-control" name="samples[${sampleId}].implementations[${newImplId}].subClass"
                id="samples_${sampleId}_implementations_${newImplId}_subClass" required placeholder="Unterklasse">
            <div class="input-group-text">&Rarr;</div>
            <input type="text" class="form-control" name="samples[${sampleId}].implementations[${newImplId}].superClass"
                id="samples_${sampleId}_implementations_${newImplId}_superClass" required placeholder="Oberklasse">
        </div>
    </div>
</div>`.trim());
}
