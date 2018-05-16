import * as $ from 'jquery';

/**
 *
 * @param result
 */
function updateAfterDelete(result) {
    let tr = $('#tr1_' + result.id);
    tr.addClass('danger');
    tr.attr('title', 'Diese Aufgabe wurde erfolgreich gelöscht.');

    let delButton = $('#del_' + result.id);
    delButton.prop('disabled', true);
    delButton.attr('title', 'Diese Aufgabe wurde bereits gelöscht!');

    let editButton = $('#edit_' + result.id);
    editButton.addClass('disabled');
    editButton.removeAttr('href');
    editButton.attr('title', 'Diese Aufgabe wurde bereits gelöscht!');
}

function deleteExercise(exerciseId) {
    let toolType = $('#toolType').val();

    // noinspection JSUnresolvedVariable, JSUnresolvedFunction
    let url = jsRoutes.controllers.ExerciseController.adminDeleteExercise(toolType, exerciseId).url;

    console.log(url);
    $.ajax({
        type: 'DELETE',
        url,
        success: updateAfterDelete
    });
}

function updateStateChangeButton(id) {
    let select = $('#state_' + id);
    $('#scb_' + id).prop('disabled', select.val() === select.data('val'));
}

/**
 * @param response
 * @param {int} response.id
 * @param {string} response.newState
 */
function onChangeStateSuccess(response): void {
    $('#scb_' + response.id).prop('disabled', true);
    $('#state_' + response.id).data('val', response.newState);
}

function changeState(exerciseId) {
    let toolType = $('#toolType').val();

    // noinspection JSUnresolvedVariable, JSUnresolvedFunction
    let url = jsRoutes.controllers.ExerciseController.adminChangeExState(toolType, exerciseId).url;

    $.ajax({
        type: 'PUT',
        url,
        data: {
            state: $('#state_' + exerciseId).val()
        },
        success: onChangeStateSuccess
    });
}
