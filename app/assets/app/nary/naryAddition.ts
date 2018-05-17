import * as $ from 'jquery';

let testBtn: JQuery;

interface NaryAdditionResult {
    correct: boolean
}

function onNaryAdditionSuccess(response: NaryAdditionResult): void {
    testBtn.prop('disabled', false);

    let solInputParent = $('#solution').parent();
    if (response.correct) {
        solInputParent.removeClass('has-error').addClass('has-success');
    } else {
        solInputParent.removeClass('has-success').addClass('has-error');
    }
}

function onNaryAdditionError(jqXHR): void {
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
            summand1: $('#firstSummand').val(),
            summand2: $('#secondSummand').val(),
            base: $('#base').data('base'),
            solution: ($('#solution').val() as string).split('').reverse().join('')
        }),
        async: true,
        success: onNaryAdditionSuccess,
        error: onNaryAdditionError
    });
}

$(() => {
    testBtn = $('#testBtn');
    testBtn.on('click', testSol);
});

