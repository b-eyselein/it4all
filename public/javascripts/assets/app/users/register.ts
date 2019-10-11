import * as $ from 'jquery';

let pw1: JQuery, pw2: JQuery;

/*
 * FIXME:
 * - alert if checks fail!
 */

$(() => {
    pw1 = $('#passwort');
    pw2 = $('#passwort_wieder');

    $('#registerForm').on('submit', testFields);
});

function checkPws(): boolean {
    const firstPwOk = (pw1.val() as string).length >= 8

    if (!firstPwOk) {
        alert("Das Passwort ist zu kurz!");
    }

    return firstPwOk && checkSecondPw();
}

function checkSecondPw(): boolean {
    return pw1.val() === pw2.val();
}

function testFields(): boolean {
    return checkPws() && !$('#name').data('taken');
}

