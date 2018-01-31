function onChangeLanguageSuccess(response) {
    $('#language').val(response.language);
    editor.setValue(response, 1000000);
}

function onRoseCorrectionSuccess(runResult) {
    // console.log(JSON.stringify(runResult, null, 2));
    $('#testBtn').prop('disabled', false);

    let correct =runResult.correct;
    if(correct) {
        $('#correction').html(`<div class="alert alert-success">Ihre Lösung war korrekt.</div>`)
    } else {
        $('#correction').html(`<div class="alert alert-danger">Ihre Lösung war nicht korrekt!</div>`)
    }

    instantiateAll(runResult);
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