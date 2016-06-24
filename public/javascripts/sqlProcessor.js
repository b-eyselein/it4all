function processCorrection(correction) {
  var parsedCorr = JSON.parse(correction);
  
  document.getElementById("resultPanel").hidden = false;
  
  var resultDiv = document.getElementById("resultDiv");
  var elementsToAdd = "";
  if(parsedCorr.success === "COMPLETE") {
    elementsToAdd += "<div class=\"alert alert-success\">" + parsedCorr.message + "</div>";
  } else if(parsedCorr.success === "PARTIALLY") {
    elementsToAdd += "<div class=\"alert alert-warning\">" + parsedCorr.message + "</div>";
  } else {
    elementsToAdd += "<div class=\"alert alert-danger\">" + parsedCorr.message + "</div>";
  }
  
  var tableCompResult = parsedCorr.tableComparisonResult;
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
  
  resultDiv.innerHTML = elementsToAdd;
}
