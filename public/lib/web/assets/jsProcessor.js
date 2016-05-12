function processCorrection(jsonResponseText) {
  var loesungsraum = document.getElementById("loesungsraum");
  loesungsraum.innerHTML = "";
  
  var results = JSON.parse(jsonResponseText);
  
  for(resultCounter = 0; resultCounter < results.length; resultCounter++) {
    var result = results[resultCounter];
    if(result.success === "COMPLETE") {
      loesungsraum.innerHTML += "<div class=\"alert alert-success\">Test von " + result.evaluated + " war erfolgreich.</div>";
    } else if(result.success === "PARTIALLY") {
      // TODO: momentan nicht verwendet... evtl. CompileFehler -> NONE?
      loesungsraum.innerHTML += "<div class=\"alert alert-danger\"><p>Test von " + result.evaluated + " war nicht erfolgreich.</p><p>Erwartet: "
          + result.awaitedResult + ", bekommen: " + result.realResult + ".</p></div>";
    } else if(result.success === "NONE") {
      loesungsraum.innerHTML += "<div class=\"alert alert-danger\"><p>Test von " + result.evaluated
          + " war nicht erfolgreich.</p><p>Es gab einen Fehler beim Ausführen des Codes.</p></div>";
    }
  }
}
