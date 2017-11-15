// Some helper functions...

var inputCount;

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
    var testDataRows = document.getElementById('testDataRows');
    var newTestId = parseInt(getTestCount());

    var newRow = document.createElement('tr');
    newRow.id = 'tr_' + newTestId;
    var newRowInner = '<td>' + newTestId + '</td>';
    for (var ic = 0; ic < inputCount; ic++) {
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
    var testData = [];
    for (var testCounter = 0; testCounter < getTestCount(); testCounter++) {
        var inputs = [];
        for (var inputCounter = 0; inputCounter < inputCount; inputCounter++) {
            inputs.push({
                id: inputCounter,
                input: $('#inp_' + inputCounter + '_' + testCounter).val()
            });
        }
        testData.push({
            id: testCounter,
            inputs: inputs,
            output: $('#outp_' + testCounter).val()
        });
    }
    return testData;
}

function validateTestData(theUrl) {
    var table = document.getElementById('testDataTable');
    for (var i = 1; i < table.rows.length - 1; i++) {
        table.rows[i].className = '';
    }

    $.ajax({
        type: 'PUT',
        dataType: 'json', // return type
        contentType: "application/json", // type of message to server
        url: theUrl,
        data: JSON.stringify(getTestData()),
        async: true,
        success: validateTDSuccess
    });
}

function validateTDSuccess(response) {
    for (var data of response) {
        var row = document.getElementById('tr_' + data.id);
        row.title = data.titleForValidation;
        row.className = data.successful ? 'success' : 'danger';
    }

}