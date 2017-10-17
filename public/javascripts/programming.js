// Some helper functions...

function getTestCount() {
  return document.getElementById('testCount').value;
}

function getInputCount() {
  return document.getElementById('inputCount').value;
}

function getInputName(inputCounter, testCounter) {
  return 'inp_' + inputCounter + '_' + testCounter;
}

function getOutputName(testCounter) {
  return 'outp_' + testCounter;
}

// Real functions

function moreTestData() {
  var testDataRows = document.getElementById('testDataRows');
  var newTestId = getTestCount();

  var newRow = document.createElement('tr');
  newRow.id = 'tr_' + newTestId;
  var newRowInner = '<td>' + newTestId + '</td>';
  for (var ic = 0; ic < getInputCount(); ic++) {
    newRowInner += '<td><input class=\'form-control\' '
        + 'name=\'' + getInputName(ic, newTestId) + '\' '
        + 'id=\'' + getInputName(ic, newTestId) + '\' '
        + 'placeholder=\'Test ' + newTestId + ', Input ' + ic + '\'></td>';
  }
  newRowInner += '<td><input class=\'form-control\' '
      + 'name=\'' + getOutputName(newTestId) + '\' '
      + 'id=\'' + getOutputName(newTestId) + '\' '
      + 'placeholder=\'Test ' + newTestId + ', Output\'></td>';

  newRow.innerHTML = newRowInner;

  testDataRows.insertBefore(newRow, testDataRows.children[newTestId]);
  document.getElementById('testCount').value = parseInt(newTestId) + 1;
}

function getTestData() {
  var testData = [];
  for (var testCounter = 0; testCounter < getTestCount(); testCounter++) {
    var inputs = [];
    for (var inputCounter = 0; inputCounter < getInputCount(); inputCounter++) {
      inputs.push({
        id: inputCounter,
        value: document.getElementById(getInputName(inputCounter, testCounter)).value
      });
    }
    testData.push({
      test: testCounter,
      input: inputs,
      output: document.getElementById(getOutputName(testCounter)).value
    });
  }
  return testData;
}

function validateTestData(theUrl) {
  var table = document.getElementById('testDataTable');
  for (var i = 1; i < table.rows.lenth - 1; i++) {
    table.rows[i].className = '';
  }

  var parameters = 'testCount=' + getTestCount() + '&' + getTestData().map(function (data) {
    var inputs = data.input.map(function (input) {
      return getInputName(input.id, data.test) + '=' + input.value;
    }).join('&');
    return inputs + '&' + getOutputName(data.test) + '=' + data.output;
  }).join('&');

  $.ajax({
    type: 'PUT',
    url: theUrl,
    data: parameters,
    async: true,
    success: function (response) {
      for (var data of response) {
        var row = document.getElementById('tr_' + data.id);
        row.title = data.titleForValidation;
        row.className = data.successful ? 'success' : 'danger';
      }
    }
  });
}