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

function processWebCorrection(jsonResponseText) {
  var tests = JSON.parse(jsonResponseText);
  var toAdd = "";
  
  for(testCount = 0; testCount < tests.length; testCount++) {
    var currentTest = tests[testCount];
    
    var clazz = currentTest.successful ? "success" : "danger";
    var show = currentTest.successful ? "" : " in";
    var preClazz = currentTest.preconditionSatisfied ? "success" : "danger";
    var actionClazz = currentTest.actionPerformed ? "success" : "danger";
    var postClazz = currentTest.postconditionSatisfied ? "success" : "danger";
    
    toAdd += "<div class=\"panel panel-" + clazz + "\">";
    toAdd += "  <div class=\"panel-heading\">";
    toAdd += "    <h4 class=\"panel-title\">";
    toAdd += "      <a data-toggle=\"collapse\" href=\"#col" + testCount + "\">Test " + (testCount + 1) + "</a>";
    toAdd += "    </h4>";
    toAdd += "  </div>";
    
    toAdd += "  <div id=\"col" + testCount + "\" class=\"panel-collapse collapse" + show + "\">";
    toAdd += "    <div class=\"panel-body\">";
    
    toAdd += "      <div class=\"alert alert-" + preClazz + "\">Precondition: " + currentTest.precondition.description + "</div>";
    toAdd += "      <div class=\"alert alert-" + actionClazz + "\">Action: " + currentTest.action.description + "</div>";
    toAdd += "      <div class=\"alert alert-" + postClazz + "\">Postcondition: " + currentTest.postcondition.description + "</div>";
    
    toAdd += "    </div>";
    toAdd += "  </div>";
    toAdd += "</div>";
  }
  
  var testsDiv = document.getElementById("testsDiv");
  testsDiv.innerHTML = toAdd;
}

function testTheWebSolution(url) {
  // AJAX-Objekt erstellen, Callback-Funktion bereitstellen
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if(xhttp.readyState == 4 && xhttp.status == 200) {
      processWebCorrection(xhttp.responseText);
    }
  };
  
  // AJAX-Objekt mit Daten fuellen, absenden
  var parameters = "editorContent=" + encodeURIComponent(editor.getValue());
  xhttp.open("POST", url, true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.setRequestHeader("Accept", "application/json");
  xhttp.send(parameters);
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
