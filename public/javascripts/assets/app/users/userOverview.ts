import * as $ from 'jquery';

function onUpdateRoleSelect(selectElem: HTMLElement): void {
    const select = $(selectElem);
    $('#btn_' + select.data('username')).prop('disabled', select.val() === select.data('stdrole'));
}

function onError(jqXHR): void {
    $('#repl').append(`
<div class="alert alert-danger alert-dismissable">
    <a href="" class="close" data-dismiss="alert" aria-label="close">&times;</a>
    <strong>${jqXHR.responseText}</strong>
</div>`.trim());
}

interface UpdateRoleResult {
    name: string
    stdRole: string
}

function updateRoles(response: UpdateRoleResult): void {
    const select = $('#sel_' + response.name);
    const oldRole = select.data('stdrole');

    select.data('stdrole', response.stdRole);

    $('#btn_' + response.name).prop('disabled', true);

    $('#reply').append(`
<div class="alert alert-success alert-dismissable">
    <a href="" class="close" data-dismiss="alert" aria-label="close">&times;</a>
    <strong>${response.name}</strong> hat jetzt die neue Rolle <strong>${response.stdRole}</strong> statt ${oldRole}
</div>`.trim());
}

function saveRole(element: HTMLElement): void {
    let jButton: JQuery;
    if (element instanceof HTMLButtonElement) {
        jButton = $(element);
    } else {
        // Span on button clicked...
        jButton = $(element).parent();
    }

    const username = jButton.data('username');
    $.ajax({
        type: 'PUT',
        url: jButton.data('url'),
        data: {
            name: username,
            role: $('#sel_' + username).val()
        },
        success: updateRoles,
        error: onError
    });
}

$(() => {
    $('select').on('change', event => onUpdateRoleSelect(event.target as HTMLElement));

    $('button.btn-info').on('click', event => saveRole(event.target as HTMLElement));

});