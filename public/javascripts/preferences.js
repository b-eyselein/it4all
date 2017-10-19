const prefUrl = $('#commit').data('url');

function savePreferences(value) {
  let inp = $('#' + value);
  $.ajax({
    url: prefUrl,
    type: 'PUT',
    data: value + '=' + inp.val(),
    success: function (result) {
      inp.data('std', result.todo);
      update(value);
    }
  });
}

function update(value) {
  let inp = $('#' + value);
  $('#btn_' + value).prop('disabled', inp.val() === inp.data('std'));
}