import * as $ from 'jquery';

let testBtn: JQuery, solutionInput: JQuery;

interface NaryAdditionResult {
    correct: boolean
}

function onNaryAdditionSuccess(response: NaryAdditionResult): void {
    testBtn.prop('disabled', false);

    if (response.correct) {
        solutionInput.removeClass('is-invalid').addClass('is-valid');
    } else {
        solutionInput.removeClass('is-valid').addClass('is-invalid');
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
            solution: (solutionInput.val() as string).split('').reverse().join('')
        }),
        async: true,
        success: onNaryAdditionSuccess,
        error: onNaryAdditionError
    });
}

$(() => {
    testBtn = $('#testBtn');
    testBtn.on('click', testSol);

    solutionInput = $('#solution');
});
