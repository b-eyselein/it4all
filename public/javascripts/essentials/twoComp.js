function colorParent(parent, correct) {
    if (correct) {
        parent.removeClass('has-error').addClass('has-success');
    } else {
        parent.removeClass('has-success').addClass('has-error');
    }
}

/**
 *
 * @param {{correct: boolean, verbose: boolean, binaryAbs: boolean, invertedAbs: boolean}} response
 */
function onAjaxSuccess(response) {
    colorParent($('#binaryAbs').parent(), response.binaryAbs);
    colorParent($('#invertedAbs').parent(), response.invertedAbs);
    colorParent($('#solution').parent(), response.correct);
}

function testSol() {
    let toolType = $('#toolType').val(), exerciseType = $('#exerciseType').val();

    // noinspection JSUnresolvedFunction, JSUnresolvedVariable
    let url = jsRoutes.controllers.exes.RandomExerciseController.correctLive(toolType, exerciseType).url;

    $.ajax({
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        url,
        data: JSON.stringify({
            invertedAbs: $('#invertedAbs').val(),
            binaryAbs: $('#binaryAbs').val(),
            value: parseInt($('#value').val()),
            solution: $('#solution').val()
        }),
        async: true,
        success: onAjaxSuccess
    });
}

$(document).ready(function () {
    $('#testBtn').click(testSol);
});

