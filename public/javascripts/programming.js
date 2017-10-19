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
  let testDataRows = document.getElementById('testDataRows');
  let newTestId = getTestCount();

  let newRow = document.createElement('tr');
  newRow.id = 'tr_' + newTestId;
  let newRowInner = '<td>' + newTestId + '</td>';
  for (let ic = 0; ic < getInputCount(); ic++) {
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
  let testData = [];
  for (let testCounter = 0; testCounter < getTestCount(); testCounter++) {
    let inputs = [];
    for (let inputCounter = 0; inputCounter < getInputCount(); inputCounter++) {
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
  let table = document.getElementById('testDataTable');
  for (let i = 1; i < table.rows.lenth - 1; i++) {
    table.rows[i].className = '';
  }

  let parameters = 'testCount=' + getTestCount() + '&' + getTestData().map(function (data) {
    let inputs = data.input.map(function (input) {
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
      for (let data of response) {
        let row = document.getElementById('tr_' + data.id);
        row.title = data.titleForValidation;
        row.className = data.successful ? 'success' : 'danger';
      }
    }
  });
}