function update(value) {
    let inp = $('#' + value);
    $('#btn_' + value).prop('disabled', inp.val() === inp.data('std'));
}


function updateShowHideAgg(theUrl) {
    $.ajax({
        url: theUrl,
        type: 'PUT',
        data: `posTests=${$('#posTests').val()}`,
        success: onShowHideAggSuccess,
        error: onShowHideAggError
    });
}

function onShowHideAggSuccess(reponse) {
    $('#posTests').data('std', reponse.todo);
    update('posTests');
}

function onShowHideAggError(jqXHR) {
    console.error(jqXHR.responseText);
}

function updatePassword(theUrl) {
    let oldPw = $('#old_pw').val();
    let newPw1 = $('#new_pw1').val();
    let newPw2 = $('#new_pw2').val();

    if (newPw1 !== newPw2) {
        return;
    }

    $('#changePwDiv').html('');
    $('#btn_pwChange').prop('disabled', true);

    $.ajax({
        url: theUrl,
        type: 'PUT',
        data: `oldpw=${oldPw}&newpw1=${newPw1}&newpw2=${newPw2}`,
        success: onPwChangeSuccess,
        error: onPwChangeError
    })
}

/**
 *
 * @param {{changed: boolean, reason: string}} response
 */
function onPwChangeSuccess(response) {
    let changePwDiv = $('#changePwDiv');
    if (response.changed) {
        changePwDiv.html('<div class="alert alert-success">Ihr Passwort wurde erfolgreich ge&auml;ndert.</div>');
    } else {
        changePwDiv.html(`<div class="alert alert-danger">Es gab einen Fehler beim &Auml;ndern ihres Passwortes: ${response.reason}</div>`);
    }
    $('#btn_pwChange').prop('disabled', false);
}

function onPwChangeError(jqXHR) {
    console.log(jqXHR.responseText);
    $('#changePwDiv').html('<div class="alert alert-success">Ihr Passwort konnte nicht ge&auml;ndert werden!</div>');
    $('#btn_pwChange').prop('disabled', false);
}