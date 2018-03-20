/**
 * @param {{correct: boolean}} response
 */
function onAjaxSuccess(response) {
    let solInputParent = $('#solution').parent();
    if (response.correct) {
        solInputParent.removeClass('has-error').addClass('has-success');
    } else {
        solInputParent.removeClass('has-success').addClass('has-error');
    }
}

function testSol() {
    let toolType = $('#toolType').val(), exerciseType = $('#exerciseType').val();

    // noinspection JSUnresolvedFunction, JSUnresolvedVariable
    let url = jsRoutes.controllers.RandomExerciseController.correctLive(toolType, exerciseType).url;

    $.ajax({
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        url,
        data: JSON.stringify({
            summand1: $('#firstSummand').val(),
            summand2: $('#secondSummand').val(),
            base: $('#base').data('base'),
            solution: $('#solution').val().split('').reverse().join('')
        }),
        async: true,
        success: onAjaxSuccess
    });
}

$(document).ready(function () {
    $('#testBtn').click(testSol);
});

