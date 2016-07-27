function processCorrection(correction) {
  var parsedCorr = JSON.parse(correction);
  
  var resultPanel = document.getElementById("resultPanel");
  resultPanel.hidden = false;
  
  var resultDiv = document.getElementById("resultDiv");
  var elementsToAdd = "";
  if(parsedCorr.success === "COMPLETE") {
    resultPanel.className = "panel panel-success";
    elementsToAdd += "<div class=\"alert alert-success\">";
  } else if(parsedCorr.success === "PARTIALLY") {
    resultPanel.className = "panel panel-warning";
    elementsToAdd += "<div class=\"alert alert-warning\">";
  } else {
    resultPanel.className = "panel panel-danger";
    elementsToAdd += "<div class=\"alert alert-danger\">";
  }
  var messages = parsedCorr.messages;
  for(i = 0; i < messages.length; i++) {
    elementsToAdd += "<p>" + messages[i] + "</p>";
  }
  elementsToAdd += "</div>";
  
  var tableCompResult = parsedCorr.usedTablesComparison;
  if(tableCompResult !== null) {
    if(tableCompResult.missingTables.length !== 0) {
      elementsToAdd += "<div class=\"alert alert-danger\">Es fehlen folgende Tabellen: "
          + tableCompResult.missingTables + "</div>";
    }
    if(tableCompResult.unneccessaryTables.length !== 0) {
      elementsToAdd += "<div class=\"alert alert-warning\">Folgende Tabellen wurden unn&ouml;tigerweise benutzt: "
          + tableCompResult.unneccessaryTables + "</div>";
    }
  }
  
  var userResult = parsedCorr.userResult;
  var sampleResult = parsedCorr.sampleResult;
  var width = 6;
  if(userResult == null || sampleResult == null) {
    width = 12;
  }
  if(userResult !== null) {
    elementsToAdd += "<div class=\"col-md-" + width + "\">";
    elementsToAdd += "<div class=\"panel panel-default\">";
    elementsToAdd += "<div class=\"panel-heading\">Ihr Ergebnis</div>";
    elementsToAdd += "<div class=\"panel-body\">";
    elementsToAdd += sqlQueryResultToTable(userResult);
    elementsToAdd += "</div>";
    elementsToAdd += "</div>";
    elementsToAdd += "</div>";
  }
  if(sampleResult !== null) {
    elementsToAdd += "<div class=\"col-md-" + width + "\">";
    elementsToAdd += "<div class=\"panel panel-default\">";
    elementsToAdd += "<div class=\"panel-heading\">Musterlösung</div>";
    elementsToAdd += "<div class=\"panel-body\">";
    elementsToAdd += sqlQueryResultToTable(sampleResult);
    elementsToAdd += "</div>";
    elementsToAdd += "</div>";
    elementsToAdd += "</div>";
  }
  
  resultDiv.innerHTML = elementsToAdd;
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
