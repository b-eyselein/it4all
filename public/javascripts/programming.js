function extractParameters() {
  var parameters = "editorContent=" + encodeURIComponent(editor.getValue());
  var testData = getTestData();
  parameters += "&count=" + testData.length;
  parameters += "&inputs=" + testData[0].input.length;
  
  for(var i = 0; i < testData.length; i++) {
    var currentData = testData[i];
    var theData = "";
    for(var j = 0; j < currentData.input.length; j++) {
      theData += "&inp" + j + ":" + i + "=" + currentData.input[j];
    }
    theData += "&outp" + i + "=" + currentData.output;
    parameters += theData;
  }
  
  return parameters;
}

function lessTestData() {
  var table = document.getElementById("testDataTable");
  
  // Header, first data, button must remain
  if(table.rows.length > 3) {
    table.deleteRow(table.rows.length - 2);
  }
}

function moreTestData(inputCount) {
  var table = document.getElementById("testDataTable");
  var testCount = table.rows.length - 1;
  var newRow = table.insertRow(testCount);
  
  for(var i = 0; i < inputCount; i++) {
    var cell = newRow.insertCell(i);
    cell.innerHTML = "<input class=\"form-control\" name=\"" + name + "\">";
  }
  var outputCell = newRow.insertCell(inputCount);
  outputCell.innerHTML = "<input class=\"form-control\" name=\"outp" + (testCount - 1) + "\">";
  
}

function getTestData() {
  var table = document.getElementById("testDataTable");
  var testData = [];
  
  for(var i = 1; i < table.rows.length - 1; i++) {
    var row = table.rows[i];
    var data = {
      input: [],
      output: ""
    };
    for(var j = 0; j < row.cells.length - 1; j++) {
      var cell = row.cells[j];
      data.input.push(cell.childNodes[0].value);
    }
    data.output = row.cells[row.cells.length - 1].childNodes[0].value;
    testData.push(data);
  }
  return testData;
}

function validateTestData(url) {
  var table = document.getElementById("testDataTable");
  for(var i = 1; i < table.rows.lenth - 1; i++) {
    table.rows[i].className = "";
  }
  
  var testData = getTestData();
  var parameters = "";
  parameters += "count=" + testData.length + "&inputs=" + testData[0].input.length;
  for(var i = 0; i < testData.length; i++) {
    var currentData = testData[i];
    var theData = "";
    for(var j = 0; j < currentData.input.length; j++) {
      theData += "&inp" + j + ":" + i + "=" + currentData.input[j];
    }
    theData += "&outp" + i + "=" + currentData.output;
    parameters += theData;
  }
  
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
  var testData = JSON.parse(responseText);
  var table = document.getElementById("testDataTable");
  for(var i = 0; i < testData.length; i++) {
    var data = testData[i];
    var row = table.rows[parseInt(data.id) + 1];
    if(data.ok) {
      row.className = "success";
    } else {
      row.className = "danger";
    }
  }
}

function processCorrection(jsonResponseText) {
  document.getElementById("loesungsraum").innerHTML = JSON.parse(jsonResponseText).asHtml;
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
