let variables = new Set(['S']);

const variablePattern = /\b(?!')\w+(?!')\b/g;

const ruleInputsSelector = 'input[name^="rule"]';

function checkAndCreateRuleInputs(variables) {
    // Disable all inputs
    $(ruleInputsSelector).prop('disabled', true);

    for (let v of variables) {
        let ruleVInput = $('#rule_' + v);

        if (ruleVInput.length === 0) {
            // Input does not exist, needs to be created
            $('#rulesList').append(
                `<div class="form-group">
                   <label class="control-label col-sm-2" for="rule_${v}">${v} = </label>
                   <div class="col-sm-10">
                     <input class="form-control" id="rule_${v}" name="rule[${v}]" data-variable="${v}" onchange="updateVars()" required placeholder="Ersetzung für ${v}">
                   </div>
                 </div>`);
        } else {
            // Input already exists, only needs to be disabled
            ruleVInput.prop('disabled', false)
        }
    }
}

function updateVars() {
    // Add start symbol to variables
    let newVars = new Set([$('#start').val()]);

    $(ruleInputsSelector).each(function () {
        let matches = $(this).val().match(variablePattern);
        if (matches !== null) {
            matches.forEach(function (match) {
                newVars.add(match);
            });
        }
    });
    checkAndCreateRuleInputs([...newVars]);
    $('#variables').val([...newVars].join(', '));
}

function onAjaxSuccess(response) {
    console.log(response);
    $('#submitBtn').prop('disable', false);
    $('#solutionDiv').html(response);
}

function onAjaxError(jqXHR) {
    $('#submitBtn').prop('disable', false);
}

function testSol(theUrl) {
    $('#submitBtn').prop('disable', true);
    let grammar = {
        terminals: $('#terminals').val().split(", "),
        variables: $('#variables').val().split(", "),
        startSymbol: $('#start').val(),
        rules: $(ruleInputsSelector).filter((index, elem) => elem.value !== "").map((index, elem) => {
            return {
                symbol: elem.dataset.variable,
                rule: elem.value
            }
        }).get()
    };

    $.ajax({
        type: 'PUT',
        // dataType: 'json',
        contentType: 'application/json',
        url: theUrl,
        data: JSON.stringify(grammar),
        async: true,
        success: onAjaxSuccess,
        error: onAjaxError
    });
}