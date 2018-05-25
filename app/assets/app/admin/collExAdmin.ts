import * as $ from 'jquery';

interface CollectionDeleteResult {
    id: number
}

function onDeleteCollSuccess(result: CollectionDeleteResult): void {
    $('#tr1_' + result.id).addClass('danger').attr('title', 'Diese Aufgabe wurde erfolgreich gelöscht.');
    $('#del_' + result.id).prop('disabled', true).attr('title', 'Diese Aufgabe wurde bereits gelöscht!');
    $('#edit_' + result.id).addClass('disabled').removeAttr('href').attr('title', 'Diese Aufgabe wurde bereits gelöscht!');
}

function onDeleteCollError(jqXHR): void {
    console.error(jqXHR);
}

function deleteCollOrExercise(element: HTMLElement): void {
    let jButton: JQuery;

    if (element instanceof HTMLButtonElement) {
        jButton = $(element);
    } else if (element instanceof HTMLSpanElement) {
        jButton = $(element).parent();
    } else {
        console.error('Wrong element clicked: ' + element);
        return;
    }

    $.ajax({
        type: 'DELETE',
        url: jButton.data('url'),
        success: onDeleteCollSuccess,
        error: onDeleteCollError
    });
}

function updateStateChangeButton(select: HTMLSelectElement): void {
    let jSelect = $(select);
    $('#scb_' + jSelect.data('id')).prop('disabled', jSelect.val() === jSelect.data('val'));
}

interface CollectionStateChangeResult {
    id: number
    newState: string
}

function onChangeStateSuccess(response: CollectionStateChangeResult): void {
    $('#scb_' + response.id).prop('disabled', true);
    $('#state_' + response.id).data('val', response.newState);
}

function onChangeStateError(jqXHR): void {
    console.error(jqXHR);
}

function changeState(element: HTMLElement): void {
    let jButton: JQuery;

    if (element instanceof HTMLButtonElement) {
        jButton = $(element);
    } else if (element instanceof HTMLSpanElement) {
        jButton = $(element).parent();
    } else {
        console.error('Wrong element clicked: ' + element);
        return;
    }

    $.ajax({
        type: 'PUT',
        url: jButton.data('url'),
        data: {state: $('#state_' + jButton.data('id')).val()},
        success: onChangeStateSuccess,
        error: onChangeStateError
    });
}

$(() => {
    $('select').on('click', event => updateStateChangeButton(event.target as HTMLSelectElement));
    $('button.btn-info').on('click', event => changeState(event.target as HTMLElement));
    $('button.btn-danger').on('click', event => deleteCollOrExercise(event.target as HTMLElement));
});