function changeFontsize(value) {
  var fontsizeElement = document.getElementById('fontsize');
  var fontsize = parseInt(fontsizeElement.innerHTML) + value;
  document.getElementById('editor').style.fontSize = fontsize + 'px';
  fontsizeElement.innerHTML = fontsize;
}
