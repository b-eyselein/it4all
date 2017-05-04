function extractParameters() {
  var parameters = LEARNER_SOLUTION_VALUE + "=" + encodeURIComponent(editor.getValue());
  var testData = getTestData();
  parameters += "&count=" + testData.length;
  parameters += "&inputs=" + testData[0].input.length;
  
  for(var i = 0; i < testData.length; i++) {
    var currentData = testData[i];
    var theData = "";
    for(var j = 0; j < currentData.input.length; j++) {
      theData += "&" + getInputName(inputCounter, testCounter) + "=" + currentData.input[j];
    }
    theData += "&" + getOutputName(i) + "=" + currentData.output;
    parameters += theData;
  }
  
  return parameters;
}

function getInputName(inputCounter, testCounter) {
  return "inp_" + inputCounter + "_" + testCounter;
}

function getOutputName(testCounter) {
  return "outp_" + testCounter;
}

function getTestData() {
  return [0, 1, 2, 3].map(function(testCounter) {
    var inputs = [];
    for(var inputCounter = 0; inputCounter < inputCount; inputCounter++) {
      inputs.push({
        id: inputCounter,
        value: document.getElementById(getInputName(inputCounter, testCounter)).value
      });
    }
    return {
      test: testCounter,
      input: inputs,
      output: document.getElementById(getOutputName(testCounter)).value
    };
  });
}

function validateTestData(url) {
  var table = document.getElementById("testDataTable");
  for(var i = 1; i < table.rows.lenth - 1; i++) {
    table.rows[i].className = "";
  }
  
  var testData = getTestData();
  
  var parameters = testData.map(function(data) {
    var inputs = data.input.map(function(input) {
      return getInputName(input.id, data.test) + "=" + input.value;
    }).join("&");
    return inputs + "&" + getOutputName(data.test) + "=" + data.output;
  }).join("&");
  
  console.log(parameters);
  
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if(xhttp.readyState == 4 && xhttp.status == 200) {
      writeTestData(xhttp.responseText);
    }
  };
  xhttp.open("POST", url, true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.setRequestHeader("Accept", "application/json");
  xhttp.send(parameters);
}

function writeTestData(responseText) {
  // var testData = JSON.parse(responseText);
  // var table = document.getElementById("testDataTable");
  // for(var i = 0; i < testData.length; i++) {
  // var data = testData[i];
  // var row = table.rows[parseInt(data.id) + 1];
  // if(data.ok) {
  // row.className = "success";
  // } else {
  // row.className = "danger";
  // }
  // }
}

function updatePreview() {
  var toWrite = unescapeHTML(document.getElementById("anterior").innerHTML);
  toWrite += unescapeHTML(editor.getValue());
  toWrite += unescapeHTML(document.getElementById("posterior").innerHTML);
  
  var theIFrame = document.getElementById("preview").contentWindow.document;
  theIFrame.open();
  theIFrame.write(toWrite);
  theIFrame.close();
}

function unescapeHTML(escapedHTML) {
  return escapedHTML.replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&amp;/g, '&');
}
