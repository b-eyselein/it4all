function testTheSolution(url, type) {
  // AJAX-Objekt erstellen, Callback-Funktion bereitstellen
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if(xhttp.readyState == 4 && xhttp.status == 200) {
      processCorrection(xhttp.responseText, type);
    }
  };
  
  // AJAX-Objekt mit Daten fuellen, absenden
  var parameters = "editorContent=" + encodeURIComponent(editor.getValue());
  xhttp.open("PUT", url, true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.setRequestHeader("Accept", "application/json");
  xhttp.send(parameters);
}

function prepareFormForSubmitting() {
  document.getElementById("editorContent").value = editor.getValue();
}

function processCorrection(correction, type) {
  try {
    var newCorrection = JSON.parse(correction);
    
    var numOfSuccessfulResults = 0;
    
    var correctionDiv = document.getElementById("correction");
    correctionDiv.innerHTML = "";
    
    for(i = 0; i < newCorrection.length; i++) {
      // handleHtmlResult(newCorrection[i]);
      correctionDiv.innerHTML += newCorrection[i].asHtml;
      if(newCorrection[i].success === "COMPLETE") {
        numOfSuccessfulResults++;
      }
    }
    
    var commitButton = document.getElementById("commit");
    commitButton.disabled = false;
    if(numOfSuccessfulResults == newCorrection.length) {
      commitButton.title = "Sie k&ouml;nnen ihre L&ouml;sung abgeben.";
      commitButton.className = "btn btn-success";
    } else {
      commitButton.title = "Sie k&ouml;nnen ihre L&ouml;sung abgeben, haben aber nicht alles richtig.";
      commitButton.className = "btn btn-warning";
    }
  } catch(err) {
    alert(err);
  }
  
  // TODO: Evtl. Update Live-Ansicht in andere Funktion auslagern
  // --> andere Aktivierung (onclick Data-toggle?)
  var live = document.getElementById("live");
  live.src = live.src;
}

function updatePreview() {
  var toWrite = unescapeHTML(editor.getValue());
  var theIFrame = document.getElementById("preview").contentWindow.document;
  theIFrame.open();
  theIFrame.write(toWrite);
  theIFrame.close();
}

function unescapeHTML(escapedHTML) {
  return escapedHTML.replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&amp;/g, '&');
}
