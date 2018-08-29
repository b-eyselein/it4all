import * as $ from 'jquery';
import {ProgStringSolution} from "../../programming/progExercise";
import {onProgCorrectionError, onProgCorrectionSuccess} from "../../programming/progCorrectionHandler";

let testBtn: JQuery;

function activityCorrection(): void {
    let exercisePart = $('#exercisePart').val();

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

$(() => {
    testBtn = $('#testBtn');
    testBtn.on('click', activityCorrection);
});
