function updateBtn(username) {
    var sel = $('#sel' + username);
    $('#btn' + username).prop('disabled', sel.val() === sel.data('stdrol'));
}

function onError(jqXHR) {
    $('#repl').append('<div class="alert alert-danger alert-dismissable"><a href="" class="close" data-dismiss="alert" aria-label="close">&times;</a>'
        + '<strong>' + jqXHR.responseText + '</strong></div>');
}

/**
 * @param {{name: string, stdRole: string}} changedUser
 * @param oldRole: string
 */
function updateRoles(changedUser, oldRole) {
    $('#sel' + changedUser.name).data('stdrole', changedUser.stdRole);
    $('#btn' + changedUser.name).prop('disabled', true);
    $('#repl').append('<div class="alert alert-success alert-dismissable"><a href="" class="close" data-dismiss="alert" aria-label="close">&times;</a>'
        + '<strong>' + changedUser.name + '</strong> hat jetzt die neue Rolle <strong>' + changedUser.stdRole + '</strong> statt' + oldRole + '</div>');
}

function saveRole(username, roleChangeUrl) {
    var sel = $('#sel' + username);
    $.ajax({
        type: 'PUT',
        url: roleChangeUrl,
        data: {
            username: username,
            newrole: sel.val()
        },
        success: updateRoles(_, sel.data('stdrole')),
        error: onError
    });
}