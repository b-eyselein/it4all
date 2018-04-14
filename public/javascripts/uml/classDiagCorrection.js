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
 * @class Implementation
 * @private
 * @property {string} subClass
 * @property {string} superClass
 */
class Implementation {
    constructor(subClass, superClass) {
        this.subClass = subClass;
        this.superClass = superClass;
    }
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
    constructor(assocType, assocName, firstEnd, firstMult, secondEnd, secondMult) {
        this.assocType = assocType;
        this.assocName = assocName;
        this.firstEnd = firstEnd;
        this.firstMult = firstMult;
        this.secondEnd = secondEnd;
        this.secondMult = secondMult;
    }
}

/**
 * @param {object} classResult
 *
 * @return string
 */
function displayClassResult(classResult) {
    // TODO: implement...
    console.warn("Displaying class result...");
    return 'TODO: Class result...';
}

/**
 *
 * @param {object} matchingRes
 * @param {string} matchingRes.success
 * @param {string[]} matchingRes.explanations
 * @param {function} explanationFunc
 *
 * @return {string}
 */
function displayMatchResult(matchingRes, explanationFunc) {
    let alertClass, glyphicon;

    switch (matchingRes.success) {
        case 'SUCCESSFUL_MATCH':
            alertClass = 'success';
            glyphicon = 'ok';
            break;
        case 'PARTIAL_MATCH':
            alertClass = 'warning';
            glyphicon = 'question-sign';
            break;
        case 'UNSUCCESSFUL_MATCH':
            alertClass = 'danger';
            glyphicon = 'remove';
            break;
        case 'ONLY_USER':
            alertClass = 'danger';
            glyphicon = 'remove';
            break;
        case 'ONLY_SAMPLE':
            alertClass = 'danger';
            glyphicon = 'remove';
            break;
        default:
            alertClass = 'info';
            glyphicon = 'remove';
    }

    let mainExplanation = explanationFunc(matchingRes);

    let subExplanations = matchingRes.explanations.join(" ");

    return `
<p class="text-${alertClass}"><span class="glyphicon glyphicon-${glyphicon}"></span> ${mainExplanation} ${subExplanations}</p>`.trim();
}

/**
 * @param {object} assocRes
 * @param {string} assocRes.success
 * @param {Association} assocRes.userArg
 * @param {Association} assocRes.sampleArg
 *
 * @return {string}
 */
function explainAssocResult(assocRes) {
    let successExplanation;

    switch (assocRes.success) {
        case 'SUCCESSFUL_MATCH':
            successExplanation = 'war korrekt.';
            break;
        case 'PARTIAL_MATCH' :
        case 'UNSUCCESSFUL_MATCH':
            successExplanation = 'war nicht korrekt:';
            break;
        case 'ONLY_USER':
            successExplanation = 'ist falsch:';
            break;
        case 'ONLY_SAMPLE':
            successExplanation = 'fehlt:';
            break;
        default:
            successExplanation = 'Es gab einen internen Fehler!';
    }

    let firstEnd = assocRes.userArg != null ? assocRes.userArg.firstEnd : assocRes.sampleArg.firstEnd;
    let secondEnd = assocRes.userArg != null ? assocRes.userArg.secondEnd : assocRes.sampleArg.secondEnd;

    return 'Die Assoziation von <code>' + firstEnd + '</code> nach <code>' + secondEnd + '</code>' + successExplanation;
}

/**
 * @param {object} implRes
 * @param {string} implRes.success
 * @param {string} implRes.explanations
 * @param {Implementation} implRes.userArg
 * @param {Implementation} implRes.sampleArg
 *
 * @return {string}
 */
function explainImplResult(implRes) {
    let successExplanation;

    switch (implRes.success) {
        case  'SUCCESSFUL_MATCH':
            successExplanation = 'war korrekt.';
            break;
        case 'PARTIAL_MATCH':
        case 'UNSUCCESSFUL_MATCH':
            successExplanation = 'war nicht korrekt:';
            break;
        case 'ONLY_USER':
            successExplanation = 'ist falsch:';
            break;
        case  'ONLY_SAMPLE':
            successExplanation = 'fehlt:';
            break;
        default:
            successExplanation = '';
            break;
    }

    let subClass = implRes.userArg != null ? implRes.userArg.subClass : implRes.sampleArg.subClass;
    let superClass = implRes.userArg != null ? implRes.userArg.superClass : implRes.sampleArg.superClass;

    return 'Die Vererbungsbeziegung von <code>' + subClass + '</code> nach <code>' + superClass + '</code>' + successExplanation;
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
    console.warn(JSON.stringify(matchingResultList, null, 2));
    if (matchingResultList.success) {
        return `
<div class="alert alert-success">
    <span class="glyphicon glyphicon-ok"></span> Die ${name} waren korrekt.
</div>`.trim();
    } else {
        return `
<div class="panel panel-danger">
    <div class="panel-heading">
        <h4 class="panel-title">Bewertung ${name}:</h4>
    </div>
    <div class="panel-body">
    ${matchingResultList.matches.map(s => displayMatchResult(s, explainFunc)).join('\n')}
    </div>
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
    // console.log(JSON.stringify(response, null, 2));

    let html = '';

    if (response.classResult != null) {
        html += displayClassResult(response.classResult);
    }

    if (response.assocAndImplResult != null) {
        html += displayMatchingResultList(response.assocAndImplResult.implResult, 'der Vererbungsbeziehungen', explainImplResult);
        html += displayMatchingResultList(response.assocAndImplResult.assocResult, 'der Assoziationen', explainAssocResult);
    }

    $('#resultDiv').html(html);

    $('#testButton').prop('disabled', false);
    $('#resultTabToggle').click();
}

function onUmlClassDiagCorrectionError(jqXHR) {
    console.log(jqXHR);

    $('#testButton').prop('disabled', false);
}


function getMultiplicity(label) {
    return label.attrs.text.text === '1' ? 'SINGLE' : 'UNBOUND';
}

/**
 * @return {{classes: object[], associations: object[], implementations: object[]}}
 */
function extractParametersAsJson() {
    return {
        classes: graph.getCells().filter(cell => cell.attributes.name !== undefined)
            .map(function (cell) {
                return {
                    name: cell.attributes.name,
                    classType: cell.attributes.type,
                    methods: cell.attributes.methods,
                    attributes: cell.attributes.attributes,
                    position: cell.attributes.position
                };
            }),

        associations: graph.getLinks().filter(conn => conn.attributes.type !== 'uml.Implementation')
            .map(function (conn) {
                return {
                    assocType: getTypeName(conn.attributes.type),
                    assocName: '',

                    firstEnd: getClassNameFromCellId(conn.attributes.source.id),
                    firstMult: getMultiplicity(conn.attributes.labels[0]),

                    secondEnd: getClassNameFromCellId(conn.attributes.target.id),
                    secondMult: getMultiplicity(conn.attributes.labels[1])
                };
            }),

        implementations: graph.getLinks().filter(conn => conn.attributes.type === 'uml.Implementation')
            .map(function (conn) {
                return {
                    subClass: getClassNameFromCellId(conn.attributes.source.id),
                    superClass: getClassNameFromCellId(conn.attributes.target.id)
                };
            })
    };
}


function testSol() {
    let toolType = $('#toolType').val(), exerciseId = $('#exerciseId').val(), exercisePart = $('#exercisePart').val();

    // noinspection JSUnresolvedFunction, JSUnresolvedVariable
    let url = jsRoutes.controllers.ExerciseController.correctLive(toolType, exerciseId, exercisePart).url;

    $('#testButton').prop('disabled', true);

    let solution = extractParametersAsJson();

    $.ajax({
        type: 'PUT',
        // dataType: 'json', // return type
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