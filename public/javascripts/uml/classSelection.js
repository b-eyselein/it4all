// Class selection

let chosenClasses = [];

function prepareFormForSubmitting() {
    $("#learnerSolution").val(JSON.stringify({
        classes: chosenClasses.map(function (clazz) {
            return {
                name: clazz,
                classType: "CLASS",
                methods: [],
                attributes: []
            };
        }),
        associations: [], implementations: []
    }))
}

function asList(array) {
    return array.length === 0 ? "<li>--</li>" : "<li>" + array.join("</li><li>") + "</li>";
}

function select(span) {
    let baseform = span.dataset.baseform;

    if (chosenClasses.indexOf(baseform) < 0) {
        chosenClasses.push(baseform);
    } else {
        chosenClasses.splice(chosenClasses.indexOf(baseform), 1);
    }

    $("#classesList").html(asList(chosenClasses));

    for (let otherSpan of document.getElementById("exercisetext").getElementsByTagName("span")) {
        if (chosenClasses.indexOf(otherSpan.dataset.baseform) < 0) {
            otherSpan.className = "non-marked";
        } else {
            otherSpan.className = "marked bg-info";
        }
    }
}
