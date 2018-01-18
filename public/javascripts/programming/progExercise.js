function onChangeLanguageSuccess(response) {
    $('#language').val(response.language);
    editor.setValue(response, 1000000);
}

function changeProgLanguage(theUrl) {
    $.ajax({
        type: 'GET',
        url: theUrl,
        data: 'language=' + $('#langSelect').val(),
        async: true,
        success: onChangeLanguageSuccess
    });
}

function onProgCorrectionSuccess(response) {
    $('#correction').html(response);
}

function onProgCorrectionError(jqXHR) {
    console.error(jqXHR.responseText);
}

function testSol(url, part) {
    $('#correction').html('');

    let dataToSend = {
        part,
        solution: {
            languague: "",
            implementation: editor.getValue()
        }
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