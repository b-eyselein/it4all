function readValues() {
    return {
        invertedAbs: $('#invertedAbs').val(),
        binaryAbs: $('#binaryAbs').val(),
        value: parseInt($('#value').val()),
        solution: $('#solution').val()
    }
}

/**
 *
 * @param {{correct: boolean, verbose: boolean, binaryAbs: boolean, invertedAbs: boolean}} response
 */
function onAjaxSuccess(response) {
    colorParent($('#binaryAbs').parent(), response.binaryAbs);
    colorParent($('#invertedAbs').parent(), response.invertedAbs);
    colorParent($('#solution').parent(), response.correct);
}

function colorParent(parent, correct) {
    if (correct) {
        parent.removeClass('has-error').addClass('has-success');
    } else {
        parent.removeClass('has-success').addClass('has-error');
    }
}

/**
 * @param {string} theUrl
 */
function testSol(theUrl) {
    $.ajax({
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        url: theUrl,
        data: JSON.stringify(readValues()),
        async: true,
        success: onAjaxSuccess
    });
}

