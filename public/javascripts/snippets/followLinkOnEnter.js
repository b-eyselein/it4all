$(document).ready(function() {
  // Follow link on enter
  $('#theLink').click(function(e) {
    // e.preventDefault();
  });
  
  $(document).keypress(function(e) {
    if((e.keyCode || e.which) == 13) {
      $('#theLink')[0].click();
    }
  });
});
