function onUpdateRoleSelect(selectElem) {
    const select = $(selectElem);
    $('#btn_' + select.data('username')).prop('disabled', select.val() === select.data('stdrole'));
}

function onError(jqXHR) {
    console.log(jqXHR.responseText);
    $('#repl').append(`
<div class="alert alert-danger alert-dismissable">
    <a href="" class="close" data-dismiss="alert" aria-label="close">&times;</a>
    <strong>${jqXHR.responseText}</strong>
</div>`.trim());
}

/**
 * @param {object} response
 * @param {string} response.name
 * @param {string} response.stdRole
 */
function updateRoles(response) {
    console.log(JSON.stringify(response, null, 2));
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

function saveRole(button) {
    const username = $(button).data('username');

    // noinspection JSUnresolvedVariable, JSUnresolvedFunction
    let url = jsRoutes.controllers.AdminController.changeRole().url;

    $.ajax({
        type: 'PUT',
        url,
        data: {
            name: username,
            role: $('#sel_' + username).val()
        },
        success: updateRoles,
        error: onError
    });
}