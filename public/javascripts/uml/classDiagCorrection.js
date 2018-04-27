/**
 * @class Match
 * @private
 * @property {string} success
 * @property {object} userArg
 * @property {object} sampleArg
 */
class Match {
    constructor(success, userArg, sampleArg) {
        this.success = success;
        this.userArg = userArg;
        this.sampleArg = sampleArg;
    }
}

/**
 * @class UmlClass
 * @private
 * @property {string} name
 * @property {string} classType
 * @property {UmlClassAttribute[]} attributes
 * @property {UmlClassMethod[]} methods
 * @property {object} position
 * @property {int} position.x
 * @property {int} position.y
 */
class UmlClass {

    /**
     * @param {string} name
     * @param {string} type
     * @param {UmlClassAttribute[]} attributes
     * @param {UmlClassMethod[]} methods
     * @param {Position} position
     */
    constructor(name, type, attributes, methods, position) {
        this.name = name;
        this.classType = type;
        this.attributes = attributes;
        this.methods = methods;
        this.position = position;
    }

    static fromCell(cell) {
        let cas = cell.attributes;
        return new UmlClass(cas.name, cas.type, cas.attributesObject, cas.methodsObject, cas.position);
    }
}

function getClassNameFromCellId(id) {
    return graph.getCell(id).attributes.name;
}

/**
 * @class Implementation
 * @private
 * @property {string} subClass
 * @property {string} superClass
 */
class Implementation {
    constructor(conn) {
        this.subClass = getClassNameFromCellId(conn.attributes.source.id);
        this.superClass = getClassNameFromCellId(conn.attributes.target.id);
    }
}

function getTypeName(type) {
    switch (type) {
        case 'uml.Association':
            return 'ASSOCIATION';
        case 'uml.Aggregation':
            return 'AGGREGATION';
        case 'uml.Composition':
            return 'COMPOSITION';
        case 'uml.Implementation':
            return 'IMPLEMENTATION';
        default:
            return 'ERROR!';
    }
}

function getMultiplicity(label) {
    return label.attrs.text.text === '1' ? 'SINGLE' : 'UNBOUND';
}

/**
 * @class Association
 * @private
 * @property {string} assocType
 * @property {string} assocName
 * @property {string} firstEnd
 * @property {string} firstMult
 * @property {string} secondEnd
 * @property {string} secondMult
 */
class Association {
    constructor(conn) {
        this.assocType = getTypeName(conn.attributes.type);
        this.assocName = '';        // TODO: name of association!?!
        this.firstEnd = getClassNameFromCellId(conn.attributes.source.id);
        this.firstMult = getMultiplicity(conn.attributes.labels[0]);
        this.secondEnd = getClassNameFromCellId(conn.attributes.target.id);
        this.secondMult = getMultiplicity(conn.attributes.labels[1]);
    }
}

/**
 * @param {object} memberResult
 * @param {boolean} memberResult.success
 * @param {string} memberResult.matches
 * @param {string} memberType
 * @return {string}
 */
function displayMemberResult(memberResult, memberType) {
    if (memberResult.success) {
        return `<p class="text text-success">Die ${memberType} waren korrekt.</p>`
    } else {
        let memberMatches = memberResult.matches.map((memberMatch) => {
            console.warn(JSON.stringify(memberMatch));

            let explanation, textClass;

            switch (memberMatch.success) {
                case 'SUCCESSFUL_MATCH':
                    textClass = 'success';
                    explanation = `Das Attribut / die Methode <code>${memberMatch.userArg.name}: ${memberMatch.userArg.type}</code> ist korrekt.`;
                    break;
                case 'ONLY_SAMPLE':
                    textClass = 'danger';
                    explanation = `Das Attribut / die Methode <code>${memberMatch.sampleArg.name}: ${memberMatch.sampleArg.type}</code> fehlt!`;
                    break;
                case 'ONLY_USER':
                    textClass = 'danger';
                    explanation = `Das Attribut / die Methode <code>${memberMatch.userArg.name}: ${memberMatch.userArg.type}</code> ist falsch!`;
                    break;
                default:
                    console.error("TODO: " + memberMatch.success);
                    break;
            }

            return `<li><span class="text text-${textClass}">${explanation}</li>`;
        }).join('\n');

        return `
<p class="text-danger">Die ${memberType} waren nicht korrekt:</p>
<ul>
    ${memberMatches}
</ul>`.trim();
    }
}

/**
 * @param {object} classResult
 * @param {string} classResult.success
 * @param {{className: string, classType: string} | null} classResult.userArg
 * @param {{className: string, classType: string} | null} classResult.sampleArg
 * @param {object[]} classResult.attributesResult
 * @param {object[]} classResult.methodsResult
 * @param {string} alertClass
 * @param {string} glyphicon
 * @param {string} successExplanation
 *
 * @return {string}
 */
function explainClassResult(classResult, alertClass, glyphicon, successExplanation) {
    // TODO: implement...

    let className = classResult.userArg != null ? classResult.userArg.className : classResult.sampleArg.className;

    if (classResult.success === 'SUCCESSFUL_MATCH') {
        return `
<p class="text-${alertClass}">
    <span class="glyphicon glyphicon-${glyphicon}"></span> Die Klasse <code>${className}</code> war korrekt.
</p>`.trim();
    } else if (classResult.userArg === null) {
        return `
<p class="text-${alertClass}">
    <span class="glyphicon glyphicon-${glyphicon}"></span> Die Klasse <code>${className}</code> konnte nicht gefunden werden!
</p>`.trim();
    } else {
        return `
<p class="text-${alertClass}">
    <span class="glyphicon glyphicon-${glyphicon}"></span> Die Klasse <code>${className}</code> ${successExplanation}
</p>
<ul>
    <li>${displayMemberResult(classResult.attributesResult, 'Attribute')}</li>
    <li>${displayMemberResult(classResult.methodsResult, 'Methoden')}</li>
</ul>`.trim();
    }
}

/**
 * @param {object} assocRes
 * @param {string} assocRes.success
 * @param {Association} assocRes.userArg
 * @param {Association} assocRes.sampleArg
 * @param {string} alertClass
 * @param {string} glyphicon
 * @param {string} successExplanation
 *
 * @return {string}
 */
function explainAssocResult(assocRes, alertClass, glyphicon, successExplanation) {
    let firstEnd = assocRes.userArg != null ? assocRes.userArg.firstEnd : assocRes.sampleArg.firstEnd;
    let secondEnd = assocRes.userArg != null ? assocRes.userArg.secondEnd : assocRes.sampleArg.secondEnd;


    let explanations = [];
    if (assocRes.success === 'UNSUCCESSFUL_MATCH' || assocRes.success === 'PARTIAL_MATCH') {
        let userArg = assocRes.userArg, sampleArg = assocRes.sampleArg;


        // Type of association
        if (userArg.assocType !== sampleArg.assocType) {
            explanations.push(`Der Typ der Assozation <code>${userArg.assocType}</code> war nicht korrekt. Erwartet wurde <code>${sampleArg.assocType}</code>!`);
        } else {
            explanations.push('Der Typ der Assozation war korrekt.');
        }

        // Cardinalities
        let endsParallel = userArg.firstEnd === sampleArg.firstEnd,
            gottenCardinalities = userArg.firstMult + ": " + userArg.secondMult;
        let cardinalitiesEqual, correctCardinalities;

        if (endsParallel) {
            console.info("Ends parallel");
            cardinalitiesEqual = userArg.firstMult === sampleArg.firstMult && userArg.secondMult === sampleArg.secondMult;
            correctCardinalities = sampleArg.firstMult + ": " + sampleArg.secondMult;
        } else {
            console.info("Ends crossed");
            cardinalitiesEqual = userArg.firstMult === sampleArg.secondMult && userArg.secondMult === sampleArg.firstMult;
            correctCardinalities = sampleArg.secondMult + ": " + sampleArg.firstMult;
        }

        if (cardinalitiesEqual) {
            explanations.push('Die Kardinalitäten der Assoziation waren korrekt.');
        } else {
            explanations.push(`Die Kardinalitätan der Assoziation <code>${gottenCardinalities}</code> war nicht korrekt. Erwartet wurde <code>${correctCardinalities}</code>!`);
        }

    }

    return `
<p class="text-${alertClass}">
    <span class="glyphicon glyphicon-${glyphicon}"></span> Die Assoziation von <code>${firstEnd}</code> nach <code>${secondEnd}</code> ${successExplanation}
</p>
<ul>
    ${explanations.map((e) => `<li>${e}</li>`).join('\n')}
</ul>`.trim();
}

/**
 * @param {object} implRes
 * @param {string} implRes.success
 * @param {Implementation} implRes.userArg
 * @param {Implementation} implRes.sampleArg
 * @param {string} alertClass
 * @param {string} glyphicon
 * @param {string} successExplanation
 *
 * @return {string}
 */
function explainImplResult(implRes, alertClass, glyphicon, successExplanation) {
    let subClass = implRes.userArg != null ? implRes.userArg.subClass : implRes.sampleArg.subClass;
    let superClass = implRes.userArg != null ? implRes.userArg.superClass : implRes.sampleArg.superClass;

    let subExplanations = '';
    if (implRes.success === 'UNSUCCESSFUL_MATCH' || implRes.success === 'PARTIAL_MATCH') {
        subExplanations = `
<ul>
    <li>Die Vererbungsrichtung ist falsch.</li>
</ul>`.trim();
    }

    return `
<p class="text-${alertClass}">
    <span class="glyphicon glyphicon-${glyphicon}"></span> Die Vererbungsbeziehung von <code>${subClass}</code> nach <code>${superClass}</code> ${successExplanation}
</p>
<ul>
    ${subExplanations}
</ul>`.trim();
}

/**
 * @param {object} matchingRes
 * @param {string} matchingRes.success
 * @param {function} explanationFunc
 *
 * @return {string}
 */
function displayMatchResult(matchingRes, explanationFunc) {
    let alertClass, glyphicon, successExplanation;

    switch (matchingRes.success) {
        case 'SUCCESSFUL_MATCH':
            alertClass = 'success';
            glyphicon = 'ok';
            successExplanation = 'war korrekt.';
            break;
        case 'PARTIAL_MATCH':
            alertClass = 'warning';
            glyphicon = 'question-sign';
            successExplanation = 'war nicht korrekt:';
            break;
        case 'UNSUCCESSFUL_MATCH':
            alertClass = 'danger';
            glyphicon = 'remove';
            successExplanation = 'war nicht korrekt:';
            break;
        case 'ONLY_USER':
            alertClass = 'danger';
            glyphicon = 'remove';
            successExplanation = 'ist falsch!';
            break;
        case 'ONLY_SAMPLE':
            alertClass = 'danger';
            glyphicon = 'remove';
            successExplanation = 'fehlt!';
            break;
        default:
            alertClass = 'info';
            glyphicon = 'remove';
            successExplanation = '';
    }

    return explanationFunc(matchingRes, alertClass, glyphicon, successExplanation);
}

/**
 * @param {object} matchingResultList
 * @param {boolean} matchingResultList.success
 * @param {Match[]} matchingResultList.matches
 * @param {string} name
 * @param {function} explainFunc
 *
 * @return string
 */
function displayMatchingResultList(matchingResultList, name, explainFunc) {
    if (matchingResultList.success) {
        return `
<div class="alert alert-success">
    <span class="glyphicon glyphicon-ok"></span> Die ${name} waren korrekt.
</div>`.trim();
    } else {
        let matchingResults = matchingResultList.matches.map(mr => displayMatchResult(mr, explainFunc)).join('\n');

        return `
<div class="panel panel-danger">
    <div class="panel-heading"><h4 class="panel-title">Bewertung der ${name}:</h4></div>
    <div class="panel-body">${matchingResults}</div>
</div>`.trim();
    }
}

/**
 * @param {object} response
 * @param {object | null} response.classResult
 * @param {object | null} response.assocAndImplResult
 * @param {object} response.assocAndImplResult.assocResult
 * @param {object} response.assocAndImplResult.implResult
 */
function onUmlClassDiagCorrectionSuccess(response) {
    let html = `<h2 class="text-center">Resultate</h2>`;

    if (response.classResult != null) {
        html += displayMatchingResultList(response.classResult, "Klassen", explainClassResult);
    }

    if (response.assocAndImplResult != null) {
        html += displayMatchingResultList(response.assocAndImplResult.implResult, 'Vererbungsbeziehungen', explainImplResult);
        html += displayMatchingResultList(response.assocAndImplResult.assocResult, 'Assoziationen', explainAssocResult);
    }

    $('#resultDiv').html(html);

    $('#testButton').prop('disabled', false);
}

function onUmlClassDiagCorrectionError(jqXHR) {
    console.log(jqXHR);

    $('#testButton').prop('disabled', false);
}

function testSol() {
    let toolType = $('#toolType').val(), exerciseId = $('#exerciseId').val(), exercisePart = $('#exercisePart').val();

    // noinspection JSUnresolvedFunction, JSUnresolvedVariable
    let url = jsRoutes.controllers.ExerciseController.correctLive(toolType, exerciseId, exercisePart).url;

    $('#testButton').prop('disabled', true);

    let solution = {
        classes: graph.getCells().filter((cell) => cell.attributes.name !== undefined).map(UmlClass.fromCell),
        associations: graph.getLinks().filter((conn) => conn.attributes.type !== 'uml.Implementation').map((conn) => new Association(conn)),
        implementations: graph.getLinks().filter((conn) => conn.attributes.type === 'uml.Implementation').map((conn) => new Implementation(conn))
    };

    console.warn(JSON.stringify(solution, null, 2));

    $.ajax({
        type: 'PUT',
        dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url,
        data: JSON.stringify({exercisePart, solution}),
        async: true,
        success: onUmlClassDiagCorrectionSuccess,
        error: onUmlClassDiagCorrectionError
    });
}

$(document).ready(function () {
    $('#testButton').click(testSol);
});