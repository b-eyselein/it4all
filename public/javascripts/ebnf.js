var variables = new Set(["S"])

const variablePattern = /\b(?!')\w+(?!')\b/g

const ruleInputsSelector = 'input[name^="rule"]'
  
function changeStartSymbol() {
  $('#rule1Variable').html($("#start").val())
}

function checkAndCreateRuleInputs(variables) {
  // Disable all inputs
  $(ruleInputsSelector).prop('disabled', true)
  
  for(var v of variables) {
    var ruleVInput = $('#rule_' + v)
    if(ruleVInput.length === 0) {
      // Input does not exist, needs to be created
      $('#rulesList').append(
          "<div class=\"form-group\">"
        + "  <label class=\"control-label col-sm-2\" for=\"rule_" + v + "\"><span id=\"rule1Variable\">" + v + "</span> =</label>"
        + "  <div class=\"col-sm-10\">"
        + "    <input class=\"form-control\" rows=\"10\" id=\"rule_" + v + "\" name=\"rule[]\" onchange=\"updateVars()\" required "
        + "           placeholder=\"Ersetzung f&uuml;r " + v + "\">"
        + "  </div>"
        + "</div>");
    } else {
      // Input already exists, only needs to be disabled
      ruleVInput.prop('disabled', false)
    }
  }
}

function updateVars() {
  // Add start symbol to variables
  var newVars = new Set([$('#start').val()])
  $(ruleInputsSelector).each(function() {
    var matches = $(this).val().match(variablePattern)
    if(matches !== null) {
      matches.forEach(function(match) {
        newVars.add(match)
      })
    }
  })
  checkAndCreateRuleInputs([...newVars])
  $('#variables').val([...newVars].join(", "))
}
