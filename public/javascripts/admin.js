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

function deleteExercise() {
    let url = '';
    return;
    // $.ajax({
    //     type: 'DELETE',
    //     url,
    //     success: updateAfterDelete
    // });
}

function updateStateChangeButton(id) {
    let select = $('#state_' + id);
    $('#scb_' + id).prop('disabled', select.val() === select.data('val'));
}

function onChangeStateSuccess(response) {
    $('#scb_' + response.id).prop('disabled', true);
    $('#state_' + response.id).data('val', response.newState);
}

function changeState(id) {
    let url = '';
    return;
    // $.ajax({
    //     type: 'PUT',
    //     url: theUrl,
    //     data: {
    //         state: $('#state_' + id).val()
    //     },
    //     success: onChangeStateSuccess
    // });
}
