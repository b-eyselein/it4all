import * as $ from 'jquery';

let testBtn: JQuery, binaryAbs: JQuery, invertedAbs: JQuery, solution: JQuery;

interface TwoCompResult {
    correct: boolean
    invertedAbs: boolean
    binaryAbs: boolean
}

function colorElement(element: JQuery, correct: boolean): void {
    testBtn.prop('disabled', false);

    if (correct) {
        element.removeClass('is-invalid').addClass('is-valid');
    } else {
        element.removeClass('is-valid').addClass('is-invalid');
    }
}

function onAjaxSuccess(response: TwoCompResult): void {
    testBtn.prop('disabled', false);

    colorElement(binaryAbs, response.binaryAbs);
    colorElement(invertedAbs, response.invertedAbs);
    colorElement(solution, response.correct);
}

function testSol(): void {
    testBtn.prop('disabled', true);

    $.ajax({
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        url: testBtn.data('url'),
        data: JSON.stringify({
            invertedAbs: invertedAbs.val(),
            binaryAbs: binaryAbs.val(),
            value: parseInt($('#value').val() as string),
            solution: solution.val()
        }),
        async: true,
        success: onAjaxSuccess
    });
}

$(() => {
    binaryAbs = $('#binaryAbs');
    invertedAbs = $('#invertedAbs');
    solution = $('#solution');

    testBtn = $('#testBtn');
    testBtn.on('click', testSol);
});

