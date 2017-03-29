var learnerSolution = {
  classes: [],
  methods: [],
  attributes: []
};

const
CLASS_NAME_EMPTY = "nomen";
const
CLASS_NAME_CLASS = "nomen-blue bg-info";
const
CLASS_NAME_METHOD = "nomen-green bg-success";
const
CLASS_NAME_ATTRIBUTE = "nomen-red bg-danger";

function mark(span) {
  var basenameOrDef = getBasenameOrDef(span);
  switch (span.className){
  case CLASS_NAME_EMPTY:
    // Add to classes
    span.className = CLASS_NAME_CLASS;
    learnerSolution.classes.push(basenameOrDef);
    break;
  case CLASS_NAME_CLASS:
    // Add to methods
    span.className = CLASS_NAME_METHOD;
    learnerSolution.methods.push(basenameOrDef);
    learnerSolution.classes.splice(learnerSolution.classes.indexOf(basenameOrDef), 1);
    break;
  case CLASS_NAME_METHOD:
    // Add to attributes
    span.className = CLASS_NAME_ATTRIBUTE;
    learnerSolution.attributes.push(basenameOrDef);
    learnerSolution.methods.splice(learnerSolution.methods.indexOf(basenameOrDef), 1);
    break;
  case CLASS_NAME_ATTRIBUTE:
    // Clear
    span.className = CLASS_NAME_EMPTY;
    learnerSolution.attributes.splice(learnerSolution.attributes.indexOf(basenameOrDef), 1);
    break;
  default:
    span.className = CLASS_NAME_EMPTY;
    break;
  }
  
  if(helpWithDoubles) {
    markDoubles();
  }
  
  updateChosenDisplay();
}

function getBasenameOrDef(span) {
  return span.getAttribute('data-baseform') != null ? span.getAttribute('data-baseform') : span.innerText;
}

function markDoubles() {
  // FIXME: implement?
  console.log("TODO: marking doubles?!?");
}

function updateChosenDisplay() {
  document.getElementById("classesList").innerHTML = asList(learnerSolution.classes);
  document.getElementById("methodsList").innerHTML = asList(learnerSolution.methods);
  document.getElementById("attributesList").innerHTML = asList(learnerSolution.attributes);
}

function asList(array) {
  return array.length == 0 ? "<li>--</li>" : "<li>" + array.join("</li><li>") + "</li>";
}

function prepareFormForSubmitting() {
  document.getElementById("learnerSolution").value = JSON.stringify(learnerSolution);
}
