import * as $ from 'jquery';

let testBtn: JQuery;

interface TwoCompResult {
    correct: boolean
    invertedAbs: boolean
    binaryAbs: boolean
}

function colorParent(parent: JQuery, correct: boolean): void {
    if (correct) {
        parent.removeClass('has-error').addClass('has-success');
    } else {
        parent.removeClass('has-success').addClass('has-error');
    }
}

function onAjaxSuccess(response: TwoCompResult): void {
    console.warn(JSON.stringify(response, null, 2));
    colorParent($('#binaryAbs').parent(), response.binaryAbs);
    colorParent($('#invertedAbs').parent(), response.invertedAbs);
    colorParent($('#solution').parent(), response.correct);
}

function testSol(): void {
    testBtn.prop('disabled', true);

    $.ajax({
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        url: testBtn.data('url'),
        data: JSON.stringify({
            invertedAbs: $('#invertedAbs').val(),
            binaryAbs: $('#binaryAbs').val(),
            value: parseInt($('#value').val() as string),
            solution: $('#solution').val()
        }),
        async: true,
        success: onAjaxSuccess
    });
}

$(() => {
    testBtn = $('#testBtn');
    testBtn.on('click', testSol);
});

