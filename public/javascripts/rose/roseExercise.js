function onChangeLanguageSuccess(response) {
    $('#language').val(response.language);
    editor.setValue(response, 1000000);
}

function onRoseCorrectionSuccess(completeResult) {
    $('#testBtn').prop('disabled', false);

    let correctionDiv = $('#correction');

    switch (completeResult.resultType) {
        case 'syntaxError':
            correctionDiv.html(`<div class="alert alert-danger"><b>Ihre Lösung hat einen Syntaxfehler:</b><hr><pre>${completeResult.cause}</pre></div>`);
            break;
        case 'success':
            let runResult = completeResult.result;
            let correct = runResult.correct;
            if (correct) {
                correctionDiv.html(`<div class="alert alert-success">Ihre Lösung war korrekt.</div>`)
            } else {
                correctionDiv.html(`<div class="alert alert-danger">Ihre Lösung war nicht korrekt!</div>`)
            }
            instantiateAll(runResult);
            break;
        default:
            console.error('Unknown runresult type: ' + completeResult.type);
            break;
    }
}

function onRoseCorrectionError(jqXHR) {
    console.error(jqXHR.responseText);
    $('#testBtn').prop('disabled', false);
}

function testSol(url) {
    $('#testBtn').prop('disabled', true);

    let dataToSend = {
        part: "",
        solution: {
            languague: "PYTHON",
            implementation: editor.getValue()
        }
    };

    $.ajax({
        type: 'PUT',
        dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url,
        data: JSON.stringify(dataToSend),
        async: true,
        success: onRoseCorrectionSuccess,
        error: onRoseCorrectionError
    });

}

function updatePreview() {
    // Do nothing
}