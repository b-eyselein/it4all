import * as $ from 'jquery';

let updateShowHideAggBtn: JQuery, updateShowHideAggSelect: JQuery, pwChangeBtn: JQuery, pwChangeDiv: JQuery;

function onShowHideAggSelectChange(): void {
    updateShowHideAggBtn.prop('disabled', updateShowHideAggSelect.val() === updateShowHideAggSelect.data('std'));
}

interface ShowHideAggUpdateResult {
    showHideAgg: string
}

function onShowHideAggSuccess(reponse: ShowHideAggUpdateResult): void {
    updateShowHideAggSelect.data('std', reponse.showHideAgg);
    onShowHideAggSelectChange();
}

function onShowHideAggError(jqXHR): void {
    console.error(jqXHR.responseText);
}

function updateShowHideAgg(): void {
    $.ajax({
        url: updateShowHideAggBtn.data('url'),
        type: 'PUT',
        data: 'showHideAgg=' + updateShowHideAggSelect.val(),
        success: onShowHideAggSuccess,
        error: onShowHideAggError
    });
}

function updatePassword(): void {

    let oldPw = $('#old_pw').val();
    let newPw1 = $('#new_pw1').val();
    let newPw2 = $('#new_pw2').val();

    if (newPw1 !== newPw2) {
        pwChangeDiv.html('<div class="alert alert-danger">Die neuen Passwörter stimmen nicht überein!</div>');
        return;
    }

    pwChangeDiv.html('');
    pwChangeBtn.prop('disabled', true);

    console.warn(pwChangeBtn.data('url'));

    $.ajax({
        url: pwChangeBtn.data('url'),
        type: 'PUT',
        data: 'oldpw=' + oldPw + '&newpw1=' + newPw1 + '&newpw2=' + newPw2,
        success: onPwChangeSuccess,
        error: onPwChangeError
    })
}

interface PwChangeResult {
    changed: boolean
    reason: string | null
}

function onPwChangeSuccess(response: PwChangeResult): void {
    pwChangeBtn.prop('disabled', false);

    if (response.changed) {
        pwChangeDiv.html('<div class="alert alert-success">Ihr Passwort wurde erfolgreich geändert.</div>');
        $('#old_pw').val('');
        $('#new_pw1').val('');
        $('#new_pw2').val('');
    } else {
        pwChangeDiv.html(`<div class="alert alert-danger">Es gab einen Fehler beim ändern ihres Passwortes: ${response.reason}</div>`);
    }
}

function onPwChangeError(jqXHR): void {
    pwChangeBtn.prop('disabled', false);
    pwChangeDiv.html('<div class="alert alert-success">Ihr Passwort konnte nicht geändert werden!</div>');
    console.error(jqXHR.responseText);
}

$(() => {
    updateShowHideAggSelect = $('#updateShowHideAggSelect');
    updateShowHideAggSelect.on('change', onShowHideAggSelectChange);

    updateShowHideAggBtn = $('#updateShowHideAggBtn');
    updateShowHideAggBtn.on('click', updateShowHideAgg);

    pwChangeBtn = $('#pwChangeBtn');
    pwChangeBtn.on('click', updatePassword);

    pwChangeDiv = $('#changePwDiv')
});