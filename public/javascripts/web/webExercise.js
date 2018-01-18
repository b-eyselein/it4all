function onWebCorrectionSuccess(correction) {
    $('#correction').html(correction);
    $('#testButton').prop('disabled', false);
}

/**
 *
 * @param jqXHR {{responseText: string, responseJSON: string}}
 */
function onWebCorrectionError(jqXHR) {
    console.error(jqXHR.responseText);
    $('#correction').html('<div class="alert alert-danger">' + jqXHR.responseText + '</div>');
    $('#testButton').prop('disabled', false);
}

function testSol(theUrl, part) {
    let dataToSend = {
        part,
        solution: editor.getValue()
    };

    console.log(JSON.stringify(dataToSend));

    $.ajax({
        type: 'PUT',
        // dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url: theUrl,
        data: JSON.stringify(dataToSend),
        async: true,
        success: onWebCorrectionSuccess,
        error: onWebCorrectionError
    });
}