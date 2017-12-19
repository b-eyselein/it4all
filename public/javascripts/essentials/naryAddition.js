function readValues() {
    return {
        summand1: $('#firstSummand').val(),
        summand2: $('#secondSummand').val(),
        base: $('#base').data('base'),
        solution: $('#solution').val().split('').reverse().join('')
    };
}

/**
 *
 * @param {{correct: boolean}} response
 */
function onAjaxSuccess(response) {
    let solInputParent = $('#solution').parent();
    if (response.correct) {
        solInputParent.removeClass('has-error').addClass('has-success');
    } else {
        solInputParent.removeClass('has-success').addClass('has-error');
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

