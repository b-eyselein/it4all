function testTheSolution(url) {
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if(xhttp.readyState == 4 && xhttp.status == 200) {
      processCorrection(xhttp.responseText);
    }
  };
  
  xhttp.open("PUT", url, true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.setRequestHeader("Accept", "application/json");
  xhttp.send(getParameters());
}

function getParameters() {
  var editorContent = editor.getValue();
  var parameters = "editorContent=" + encodeURIComponent(editorContent);

  var feedbackLevel = document.getElementById("feedbackLevel");
  var realLevel = feedbackLevel.options[feedbackLevel.selectedIndex].value;
  parameters += "&feedbackLevel=" + realLevel;
  return parameters;
}

function prepareFormForSubmitting() {
  document.getElementById("editorContent").value = editor.getValue();
}

function processCorrection(correction) {
  var parsedCorr = JSON.parse(correction);
  
  document.getElementById("resultPanel").hidden = false;
  document.getElementById("resultDiv").innerHTML = parsedCorr.asHtml;
}

function sqlQueryResultToTable(queryResult) {
  var i = 0;
  var table = "";
  table += "<div class=\"table-responsive\">";
  table += "<table class=\"table table-bordered table-condensed\">";
  table += "<thead><tr>";
  for(i = 0; i < queryResult.columnCount; i++) {
    table += "<th>" + queryResult.columnNames[i] + "</th>";
  }
  table += "</tr></thead>";
  
  table += "<tbody>";
  for(i = 0; i < queryResult.rows.length; i++) {
    table += "<tr>";
    var j = 0;
    for(j = 0; j < queryResult.rows[i].length; j++) {
      table += "<td>" + queryResult.rows[i][j] + "</td>";
    }
    table += "</tr>";
  }
  table += "</tbody>";
  table += "</table>";
  table += "</div>";
  return table;
}
