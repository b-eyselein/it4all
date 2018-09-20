import * as $ from 'jquery';
import {onProgCorrectionSuccess, ProgStringSolution} from "../../programming/progExercise";

let testBtn: JQuery;

function activityCorrection(): void {
    testBtn.prop('disabled', true);

    let dataToSend: ProgStringSolution = {
        language: "PYTHON_3",
        implementation: $('#preCode').text()
    };

    $.ajax({
        type: 'PUT',
        // dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url: testBtn.data('url'),
        data: JSON.stringify(dataToSend),
        async: true,
        success: onProgCorrectionSuccess,
        error: onProgCorrectionError
    });
}


function onProgCorrectionError(jqXHR): void {
    console.error(jqXHR.responseText);
    testBtn.prop('disabled', false);
}


$(() => {
    testBtn = $('#testBtn');
    testBtn.on('click', activityCorrection);
});
