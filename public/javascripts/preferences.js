function savePreferences() {
  $.ajax({
    url: $("#commit").data('url'),
    type: "PUT",
    data: "posTests=" + $("#posTests").val(),
    success: function (result) {
      // FIXME: verarbeiten...
      console.log(result);
    },
    error: function (xhr, status, error) {
      // FIXME: verarbeiten...
      console.log(xhr);
    }
  });
}

function update(id) {
  var inp = $("#" + id);
  $("#btn_" + id).prop("disabled", inp.val() === inp.data("std"));
}