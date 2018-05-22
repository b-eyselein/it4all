import * as $ from 'jquery';

// Some helper functions...

const INPUT_COUNT = 5;

// Real functions

function moreTestData() {
    let testDataBody = $('#testDataBody');

    let newTestId = testDataBody.find('tr').length;

    let inputs = '';
    for (let ic = 0; ic < INPUT_COUNT; ic++) {
        inputs += `<td><input class="form-control" name="inp_${ic}_${newTestId}" id="inp_${ic}_${newTestId}" placeholder="Test ${newTestId + 1}, Input ${ic + 1}"></td>`;
    }

    testDataBody.append(
        `<tr id="tr_${newTestId}" data-testid="${newTestId}">
           <td>${newTestId}</td>
           ${inputs}
           <td><input class="form-control" name="outp_${newTestId}" id="outp_${newTestId}" placeholder="Test ${newTestId + 1}, Output"></td>
         </tr>`);
}

/**
 *
 * @param {object} response
 * @param {boolean} response.solutionSaved
 * @param {object[]} response.results
 * @param {number} response.results.id
 * @param {boolean} response.results.correct
 */
function onValidateTDSuccess(response) {
    console.log(JSON.stringify(response, null, 2));

    let msgDiv = $('#messageDiv');
    if (response.solutionSaved) {
        msgDiv.html(`<hr><div class="alert alert-success">Ihre Testdaten wurden gespeichert.</div>`);
    } else {
        msgDiv.html(`<hr><div class="alert alert-danger">Ihre Testdaten konnten nicht gespeichert werden!</div>`);
    }

    for (let data of response.results) {
        let row = document.getElementById('tr_' + data.id);
        row.className = data.correct ? 'success' : 'danger';
    }
}

function onValidateTDError(jqXHR) {
    console.error(jqXHR.responseText);
}

function testSol() {
    let toolType = $('#toolType').val(), exerciseId = $('#exerciseId').val(), exercisePart = $('#exercisePart').val();

    // noinspection JSUnresolvedFunction, JSUnresolvedVariable
    let url = ''; // FIXME: jsRoutes.controllers.ExerciseController.correctLive(toolType, exerciseId, exercisePart).url;

    let testDataRows = $('#testDataBody').find('tr');

    testDataRows.removeClass('success danger');

    let dataToSend = {
        solution: testDataRows.map((index, elem: HTMLInputElement) => {
            return {
                id: $(elem).data('testid'),
                inputs: $(elem).find('input').filter((i, e) => e.name.startsWith('inp')).map((inputIndex, e) => {
                    return {id: inputIndex, input: e.value}
                }).get(),
                output: $(elem).find('input').filter((i, e) => e.name.startsWith('outp')).val()
            }
        }).get()
    };

    $.ajax({
        type: 'PUT',
        // dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url,
        data: JSON.stringify(dataToSend),
        async: true,
        success: onValidateTDSuccess,
        error: onValidateTDError
    });
}


$(() => {
    $('#testBtn').on('click', testSol);
});