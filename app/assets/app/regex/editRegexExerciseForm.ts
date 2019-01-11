import * as $ from 'jquery';

let addSampleSolBtn: JQuery<HTMLButtonElement>;
let sampleSolsDiv: JQuery<HTMLDivElement>;

let addTestDataBtn: JQuery<HTMLButtonElement>;
let testDataDiv: JQuery<HTMLDivElement>;

function deleteSampleSolution(event: Event): void {
    if (sampleSolsDiv.children().length <= 1) {
        alert("There has got to be at least one sample solution!");
        return;
    }

    const solutionID: number = $(event.target).data('solutionid');
    if (confirm("Wollen Sie diese Musterlösung mit der ID " + solutionID + " wirklich löschen?")) {
        $('#sample_row_' + solutionID).remove();

        // FIXME: count down all greater solution ids?
    }
}

function instantiateDeleteSampleSolBtnClickEvents(): void {
    $('.deleteSampleSolBtn').unbind('click').on('click', deleteSampleSolution);
}

function deleteTestData(event: Event): void {
    if (testDataDiv.children().length <= 1) {
        alert("There has got to be at least one test data!");
        return;
    }

    const testDataID: number = $(event.target).data('testdataid');
    if (confirm("Wollen Die diese Testdaten mit der ID " + testDataID + " wirklich löschen?")) {
        $('#testdata_row_' + testDataID).remove();
    }
}

function instantiateDeleteTestDataBtnClickEvents(): void {
    $('.deleteTestDataBtn').unbind('click').on('click', deleteTestData);
}

$(() => {
    // Sample solution

    sampleSolsDiv = $('#sampleSolsDiv');

    addSampleSolBtn = $('#addSampleSolBtn');
    addSampleSolBtn.on('click', () => {
        const newId: number = sampleSolsDiv.children().length;

        sampleSolsDiv.append(`
<div class="form-group row" id="sample_row_${newId}">
    <div class="col-md-2">
        <input type="number" class="form-control" name="samples[${newId}].id" id="samples_${newId}_id" value="${newId + 1}" readonly>
    </div>
    <div class="col-md-8">
        <input type="text" class="form-control" name="samples[${newId}].sample" id="samples_${newId}_sample" value="" required  placeholder="Musterlösung">
    </div>
    <div class="col-md-2">
        <button type="button" class="btn btn-danger btn-block deleteSampleSolBtn" title="Musterlösung löschen" data-solutionid="${newId}">
            <span class="octicon octicon-x"></span>
        </button>
    </div>
</div>`.trim());
        instantiateDeleteSampleSolBtnClickEvents();
    });

    instantiateDeleteSampleSolBtnClickEvents();

    // Test data

    testDataDiv = $('#testDataDiv');
    addTestDataBtn = $('#addTestDataBtn');
    addTestDataBtn.on('click', () => {
        const newId: number = testDataDiv.children().length;

        testDataDiv.append(`
<div class="form-group row" id="testdata_row_${newId}">
    <div class="col-md-2">
        <input type="number" class="form-control" name="testData[${newId}].id" id="testData_${newId}_id" value="${newId + 1}" readonly>
    </div>
    <div class="col-md-6">
        <input type="text" class="form-control" name="testData[${newId}].data" id="testData_${newId}_data" value="" required>
    </div>
    <div class="col-md-2">
        <input type="text" class="form-control" name="testData[${newId}].included" id="testData_${newId}_included" value="false" required>
    </div>
    <div class="col-md-2">
        <button type="button" class="btn btn-danger btn-block deleteTestDataBtn" title="Testdateneintrag löschen" data-testdataid="${newId}">
            <span class="octicon octicon-x"></span>
        </button>
    </div>
</div>`.trim());
        instantiateDeleteTestDataBtnClickEvents();
    });

    instantiateDeleteTestDataBtnClickEvents();
});