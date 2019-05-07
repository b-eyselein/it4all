import * as $ from 'jquery';
import {ProgCorrectionResult, ProgSolution, renderProgCorrectionSuccess} from "../../programming/progCorrectionHandler";
import {domReady} from "../../otherHelpers";

let testBtn: JQuery;

function activityCorrection(): void {
    testBtn.prop('disabled', true);

    let dataToSend: ProgSolution = {
        implementation: $('#preCode').text(),
        testData: [],
        unitTest: ''
    };

    $.ajax({
        type: 'PUT',
        // dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url: testBtn.data('url'),
        data: JSON.stringify(dataToSend),
        async: true,
        beforeSend: (xhr) => {
            const token = $('input[name="csrfToken"]').val() as string;
            xhr.setRequestHeader("Csrf-Token", token);
        },
        success: onActivityCorrectionSuccess,
        error: onActivityCorrectionError
    });
}


function onActivityCorrectionSuccess(result: ProgCorrectionResult): void {
    testBtn.prop('disabled', false);
    const html = renderProgCorrectionSuccess(result);

    $('#correctionDiv').prop('hidden', false);
    $('#correction').html(html);

    // solutionChanged = false;
}

function onActivityCorrectionError(jqXHR): void {
    console.error(jqXHR.responseText);
    testBtn.prop('disabled', false);
}


domReady(() => {
    testBtn = $('#testBtn');
    testBtn.on('click', activityCorrection);
});
