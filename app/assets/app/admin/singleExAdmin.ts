import * as $ from 'jquery';

interface ExerciseDeleteResult {
    id: number
}

function updateAfterDelete(result: ExerciseDeleteResult): void {
    $('#tr1_' + result.id).addClass('danger').attr('title', 'Diese Aufgabe wurde erfolgreich gelöscht.');
    $('#del_' + result.id).prop('disabled', true).attr('title', 'Diese Aufgabe wurde bereits gelöscht!');
    $('#edit_' + result.id).addClass('disabled').removeAttr('href').attr('title', 'Diese Aufgabe wurde bereits gelöscht!');
}

function deleteExercise(element: HTMLElement): void {
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
        success: updateAfterDelete
    });
}

function updateStateChangeButton(select: HTMLSelectElement): void {
    let jQSelect = $(select);
    $('#scb_' + jQSelect.data('id')).prop('disabled', jQSelect.val() === jQSelect.data('val'));
}

interface StateChangeSuccess {
    id: number
    newState: string
}

function onChangeStateSuccess(response: StateChangeSuccess): void {
    $('#scb_' + response.id).prop('disabled', true);
    $('#state_' + response.id).data('val', response.newState);
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
        success: onChangeStateSuccess
    });
}

$(() => {
    $('select').on('click', event => updateStateChangeButton(event.target as HTMLSelectElement));
    $('button.btn-info').on('click', event => changeState(event.target as HTMLElement));
    $('button.btn-danger').on('click', event => deleteExercise(event.target as HTMLElement));
});