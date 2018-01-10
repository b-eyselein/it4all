// Some helper functions...

let inputCount;

$(document).ready(function () {
    inputCount = $('#inputCount').val()
});

function getTestCount() {
    return $('#testCount').val();
}

function getInputName(inputCounter, testCounter) {
    return 'inp_' + inputCounter + '_' + testCounter;
}

function getOutputName(testCounter) {
    return 'outp_' + testCounter;
}

// Real functions

function moreTestData() {
    let testDataRows = document.getElementById('testDataRows');
    let newTestId = parseInt(getTestCount());

    let newRow = document.createElement('tr');
    newRow.id = 'tr_' + newTestId;
    let newRowInner = '<td>' + newTestId + '</td>';
    for (let ic = 0; ic < inputCount; ic++) {
        newRowInner += '<td><input class="form-control" name="' + getInputName(ic, newTestId) + '" id="'
            + getInputName(ic, newTestId) + '" placeholder="Test ' + (newTestId + 1) + ', Input ' + (ic + 1) + '"></td>';
    }
    newRowInner += '<td><input class="form-control" name="' + getOutputName(newTestId) + '" id="' + getOutputName(newTestId)
        + '" placeholder="Test ' + newTestId + ', Output"></td>';

    newRow.innerHTML = newRowInner;

    testDataRows.insertBefore(newRow, testDataRows.children[newTestId]);
    $('#testCount').val(newTestId + 1);
}

function getTestData() {
    let testData = [];
    for (let testCounter = 0; testCounter < getTestCount(); testCounter++) {
        let inputs = [];
        for (let inputCounter = 0; inputCounter < inputCount; inputCounter++) {
            inputs.push({
                id: inputCounter,
                input: $('#inp_' + inputCounter + '_' + testCounter).val()
            });
        }
        testData.push({
            id: testCounter,
            inputs,
            output: $('#outp_' + testCounter).val()
        });
    }
    return testData;
}

function validateTestData(theUrl) {
    let table = document.getElementById('testDataTable');
    for (let i = 1; i < table.rows.length - 1; i++) {
        table.rows[i].className = '';
    }

    $.ajax({
        type: 'PUT',
        dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url: theUrl,
        data: JSON.stringify(getTestData()),
        async: true,
        success: validateTDSuccess
    });
}

function validateTDSuccess(response) {
    for (let data of response) {
        let row = document.getElementById('tr_' + data.id);
        row.title = data.titleForValidation;
        row.className = data.successful ? 'success' : 'danger';
    }

}