// Some helper functions...

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
 * @param {object[]} response
 * @param {number} response.id
 * @param {boolean} response.correct
 */
function onValidateTDSuccess(response) {
    for (let data of response) {
        let row = document.getElementById('tr_' + data.id);
        row.className = data.correct ? 'success' : 'danger';
    }
}

function onValidateTDError(jqXHR) {
    console.error(jqXHR.responseText);
}

function validateTestData(url, part) {
    let testDataRows = $('#testDataBody').find('tr');

    testDataRows.removeClass('success danger');

    let dataToSend = {
        part,
        solution: testDataRows.map((index, elem) => {
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