function onProgCorrectionSuccess(response) {
    $('#correction').html(response);
}

function onProgCorrectionError(jqXHR) {
    console.error(jqXHR.responseText);
}

function testSol(url, part) {
    let dataToSend = {
        part,
        solution: editor.getValue()
    };

    $.ajax({
        type: 'PUT',
        // dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url,
        data: JSON.stringify(dataToSend),
        async: true,
        success: onProgCorrectionSuccess,
        error: onProgCorrectionError
    });

}