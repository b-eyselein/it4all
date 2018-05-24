import * as $ from 'jquery';

let testBtn: JQuery, solutionInput: JQuery;

interface NaryConversionResult {
    correct: boolean
}

function onNaryConversionSuccess(response: NaryConversionResult): void {
    testBtn.prop('disabled', false);

    if (response.correct) {
        solutionInput.removeClass('is-invalid').addClass('is-valid');
    } else {
        solutionInput.removeClass('is-valid').addClass('is-invalid');
    }
}

function onNaryConversionError(jqXHR): void {
    testBtn.prop('disabled', false);
    console.error(jqXHR);
}

function testSol(): void {
    testBtn.prop('disabled', true);
    $.ajax({
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        url: testBtn.data('url'),
        data: JSON.stringify({
            startingNB: $('#startingNB').data('base'),
            targetNB: $('#targetNB').data('base'),
            value: $('#value').val(),
            solution: solutionInput.val()
        }),
        async: true,
        success: onNaryConversionSuccess,
        error: onNaryConversionError
    });
}

$(() => {
    testBtn = $('#testBtn');
    testBtn.on('click', testSol);

    solutionInput = $('#solution');
});