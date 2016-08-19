function testTheSolution(url) {
  // AJAX-Objekt erstellen, Callback-Funktion bereitstellen
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if(xhttp.readyState == 4 && xhttp.status == 200) {
      processCorrection(xhttp.responseText);
    }
  };
  
  // AJAX-Objekt mit Daten fuellen, absenden
  var parameters = "editorContent=" + encodeURIComponent(editor.getValue());
  xhttp.open("POST", url, true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.setRequestHeader("Accept", "application/json");
  xhttp.send(parameters);
}

function prepareFormForSubmitting() {
  document.getElementById("editorContent").value = editor.getValue();
}

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

function processWebCorrection(jsonResponseText) {
  var results = JSON.parse(jsonResponseText);
  var toAdd = "";
  
  for(resultCount = 0; resultCount < results.length; resultCount++) {
    var currentResult = results[resultCount];
    
    var show = currentResult.success == "COMPLETE" ? "" : " in";
    // var preClazz = currentTest.preconditionSatisfied ? "success" : "danger";
    // var actionClazz = currentTest.actionPerformed ? "success" : "danger";
    // var postClazz = currentTest.postconditionSatisfied ? "success" :
    // "danger";
    
    toAdd += "<div class=\"panel panel-" + currentResult.bsclass + "\">";
    toAdd += "  <div class=\"panel-heading\">";
    toAdd += "    <h4 class=\"panel-title\">";
    toAdd += "      <a data-toggle=\"collapse\" href=\"#col" + resultCount + "\">Test " + (resultCount + 1) + "</a>";
    toAdd += "    </h4>";
    toAdd += "  </div>";
    
    toAdd += "  <div id=\"col" + resultCount + "\" class=\"panel-collapse collapse" + show + "\">";
    toAdd += "    <div class=\"panel-body\">";
    
    for(messageCount = 0; messageCount < currentResult.messages.length; messageCount++) {
      toAdd += "      <div class=\"alert alert-" + currentResult.bsclass + "\">" + currentResult.messages[messageCount]
          + "</div>";
    }
    toAdd += "    </div>";
    toAdd += "  </div>";
    toAdd += "</div>";
  }
  
  var testsDiv = document.getElementById("testsDiv");
  testsDiv.innerHTML = toAdd;
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
