function processCorrection(jsonResponseText) {
  var loesungsraum = document.getElementById("loesungsraum");
  loesungsraum.innerHTML = "";
  
  var results = JSON.parse(jsonResponseText);
  var toAdd = "";
  
  for(resultCounter = 0; resultCounter < results.length; resultCounter++) {
    var result = results[resultCounter];
    if(result.success === "COMPLETE") {
      toAdd += "<div class=\"alert alert-success\">Test von <code>" + result.evaluated
          + "</code> war erfolgreich.</div>";
    } else if(result.success === "PARTIALLY") {
      // TODO: momentan nicht verwendet... evtl. CompileFehler -> NONE?
      toAdd += "<div class=\"alert alert-danger\"><p>Test von <code>" + result.evaluated
          + "</code> war nicht erfolgreich.</p>";
      toAdd += "<p>Erwartet: " + result.awaitedResult + ",</p><p> bekommen: " + result.realResult + ".</p></div>";
    } else if(result.success === "NONE") {
      toAdd += "<div class=\"alert alert-danger\"><p>Test von <code>" + result.evaluated
          + "</code> war nicht erfolgreich.</p><p>Es gab einen Fehler beim Ausführen des Codes.</p></div>";
    }
  }
  loesungsraum.innerHTML += toAdd;
}
