function changeFontsize(value) {
  var fontsizeElement = document.getElementById('fontsize');
  var fontsize = parseInt(fontsizeElement.innerHTML) + value;
  document.getElementById('editor').style.fontSize = fontsize + 'px';
  fontsizeElement.innerHTML = fontsize;
}

function updatePlayground() {
   var toWrite = unescapeHTML(editor.getValue());
  
   var theIFrame = document.getElementById("preview").contentWindow.document;
  theIFrame.open();
  theIFrame.write(toWrite);
  theIFrame.close();
}

function unescapeHTML(escapedHTML) {
  return escapedHTML.replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&amp;/g, '&');
}
