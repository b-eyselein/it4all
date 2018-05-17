import * as $ from 'jquery';

let testBtn: JQuery;

interface NaryConversionResult {
    correct: boolean
}

function onAjaxSuccess(response: NaryConversionResult) {
    testBtn.prop('disabled', false);

    let solInputParent = $('#solution').parent();
    if (response.correct) {
        solInputParent.removeClass('has-error').addClass('has-success');
    } else {
        solInputParent.removeClass('has-success').addClass('has-error');
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
            solution: $('#solution').val()
        }),
        async: true,
        success: onAjaxSuccess,
        error: onNaryConversionError
    });
}

$(() => {
    testBtn = $('#testBtn');
    testBtn.on('click', testSol);
});