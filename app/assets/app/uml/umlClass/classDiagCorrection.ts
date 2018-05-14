import * as $ from 'jquery';

import {UmlAssociation, UmlClassAttribute, UmlClassMethod, UmlImplementation} from "../umlInterfaces";
import {classDiagGraph} from "./classDiagDrawing";
import {CustomClass} from "./classDiagElements";

interface AnalysisResult {
    success: string
}

interface Match<T, AR> {
    success: string
    analysisResult: AR
    userArg: T
    sampleArg: T
}

interface UmlAttributeAnalysisResult extends AnalysisResult {
    visibilityCorrect: boolean

    abstractCorrect: boolean
    correctAbstract: boolean

    typeCorrect: boolean
    correctType: string

    staticCorrect: boolean
    correctStatic: string

    derivedCorrect: boolean
    correctDerived: boolean
}

interface UmlClassAttributeMatch extends Match<UmlClassAttribute, UmlAttributeAnalysisResult> {
}

interface UmlClassMethodAnalysisResult extends AnalysisResult {
    visibilityCorrect: boolean
    correctVisibility: string

    abstractCorrect: boolean
    correctAbstract: boolean

    typeCorrect: boolean
    correctType: string

    staticCorrect: boolean
    correctStatic: string

    parametersCorrect: boolean
    correctParameters: string
}

interface UmlClassMethodMatch extends Match<UmlClassMethod, UmlClassMethodAnalysisResult> {
}

interface UmlClassAnalysisResult extends AnalysisResult {
    classTypeCorrect: boolean
    correctClassType: string

    attributesResult: UmlClassAttributeMatch[]
    methodsResult: UmlClassMethodMatch[]
}

interface MatchUmlClass {
    name: string
    classType: string
}

interface UmlClassMatch extends Match<MatchUmlClass, UmlClassAnalysisResult> {
}

function displayUmlAttributeMatch(umlAttributeMatch: UmlClassAttributeMatch) {
    let explanation, textClass;

    switch (umlAttributeMatch.success) {
        case 'SUCCESSFUL_MATCH':
            textClass = 'success';
            explanation = `Das Attribut <code>${umlAttributeMatch.userArg.name}</code> ist korrekt.`;
            break;
        case 'PARTIAL_MATCH':
        case 'UNSUCCESSFUL_MATCH':
            textClass = 'warning';

            let explanations = [];

            if (!umlAttributeMatch.analysisResult.visibilityCorrect) {
                explanations.push('Die Sichtbarkeit des Attributs war falsch.');
            }
            if (!umlAttributeMatch.analysisResult.abstractCorrect) {
                explanations.push('Das Attribut sollte ' + (umlAttributeMatch.analysisResult.correctAbstract ? '' : 'nicht ') + 'abstrakt sein.');
            }
            if (!umlAttributeMatch.analysisResult.typeCorrect) {
                explanations.push('Das Attribut hat den falschen Typ.');
            }
            if (!umlAttributeMatch.analysisResult.staticCorrect) {
                explanations.push('Das Attribut sollte ' + (umlAttributeMatch.analysisResult.correctStatic ? '' : 'nicht ') + 'statisch sein.');
            }
            if (!umlAttributeMatch.analysisResult.derivedCorrect) {
                explanations.push('Das Attribut sollte ' + (umlAttributeMatch.analysisResult.correctDerived ? '' : 'nicht ') + 'abgeleitet sein.');
            }

            explanation = explanations.join(' ');
            break;
        case 'ONLY_SAMPLE':
            textClass = 'danger';
            explanation = `Das Attribut <code>${umlAttributeMatch.sampleArg.name}</code> fehlt!`;
            break;
        case 'ONLY_USER':
            textClass = 'danger';
            explanation = `Das Attribut <code>${umlAttributeMatch.userArg.name}: ${umlAttributeMatch.userArg.type}</code> ist falsch!`;
            break;
    }

    // return `<li>${displayUmlAttributeMatch(umlAttributeMatch)}</li>`;

    return `<li><span class="text text-${textClass}">${explanation}</li>`;
}

/**
 * @param {object} memberResult
 * @param {boolean} memberResult.success
 * @param {object[]} memberResult.matches
 *
 * @return {string}
 */
function displayAttributeMatchingResult(memberResult) {
    if (memberResult.success) {
        return `<p class="text text-success">Die Attribute waren korrekt.</p>`
    } else {
        return `
<p class="text-danger">Die Attribute waren nicht korrekt:</p>
<ul>
    ${memberResult.matches.map(displayUmlAttributeMatch).join('\n')}
</ul>`.trim();
    }
}


function displayUmlMethodMatch(umlMethodMatch: UmlClassMethodMatch) {
    let explanation, textClass;

    switch (umlMethodMatch.success) {
        case 'SUCCESSFUL_MATCH':
            textClass = 'success';
            explanation = `Die Methode <code>${umlMethodMatch.userArg.name}</code> ist korrekt.`;
            break;
        case 'PARTIAL_MATCH':
        case 'UNSUCCESSFUL_MATCH':
            textClass = 'warning';

            let explanations = [];

            if (!umlMethodMatch.analysisResult.visibilityCorrect) {
                explanations.push('Die Sichtbarkeit der Methode war falsch.');
            }
            if (!umlMethodMatch.analysisResult.abstractCorrect) {
                explanations.push('Die Methode sollte ' + (umlMethodMatch.analysisResult.correctAbstract ? '' : 'nicht ') + 'abstrakt sein.');
            }
            if (!umlMethodMatch.analysisResult.typeCorrect) {
                explanations.push('Die Methode hat den falschen Typ.');
            }
            if (!umlMethodMatch.analysisResult.staticCorrect) {
                explanations.push('Die Methode sollte ' + (umlMethodMatch.analysisResult.correctStatic ? '' : 'nicht ') + 'statisch sein.');
            }
            if (!umlMethodMatch.analysisResult.parametersCorrect) {
                explanations.push(`Die Parameter der Methode sollten <code>${umlMethodMatch.analysisResult.correctParameters}</code> lauten.`);
            }

            explanation = explanations.join(' ');
            break;
        case 'ONLY_SAMPLE':
            textClass = 'danger';
            explanation = `Die Methode <code>${umlMethodMatch.sampleArg.name}</code> fehlt!`;
            break;
        case 'ONLY_USER':
            textClass = 'danger';
            explanation = `Die Methode <code>${umlMethodMatch.userArg.name}: ${umlMethodMatch.userArg.type}</code> ist falsch!`;
            break;
    }

    return `<li><span class="text text-${textClass}">${explanation}</li>`;
}

/**
 * @param {object} memberResult
 * @param {boolean} memberResult.success
 * @param {object[]} memberResult.matches
 *
 * @return {string}
 */
function displayMethodMatchingResult(memberResult) {
    if (memberResult.success) {
        return `<p class="text text-success">Die Methoden waren korrekt.</p>`
    } else {
        return `
<p class="text-danger">Die Methoden waren nicht korrekt:</p>
<ul>
    ${memberResult.matches.map(displayUmlMethodMatch).join('\n')}
</ul>`.trim();
    }
}

function explainClassResult(classResult: UmlClassMatch, alertClass, glyphicon, successExplanation) {
    let className = classResult.userArg != null ? classResult.userArg.name : classResult.sampleArg.name;

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
        let explanationClassType: string = '';

        if (classResult.analysisResult.classTypeCorrect) {
            explanationClassType = `<p class="text-success">Der Typ der Klasse ist korrekt.</p>`;
        } else {
            explanationClassType = `<p class="text-warning">Der Typ der Klasse <code>${className}</code> ist falsch. Erwartet wurde <code>${classResult.analysisResult.correctClassType}</code>.</p>`;
        }

        return `
<p class="text-${alertClass}">
    <span class="glyphicon glyphicon-${glyphicon}"></span> Die Klasse <code>${className}</code> ${successExplanation}
</p>
<ul>
    <li>${explanationClassType}</li>
    <li>${displayAttributeMatchingResult(classResult.analysisResult.attributesResult)}</li>
    <li>${displayMethodMatchingResult(classResult.analysisResult.methodsResult)}</li>
</ul>`.trim();
    }
}

/**
 * @param {object} assocRes
 * @param {string} assocRes.success
 * @param {UmlAssociation} assocRes.userArg
 * @param {UmlAssociation} assocRes.sampleArg
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
            explanations.push(`<p class="text-warning">Der Typ der Assozation <code>${userArg.assocType}</code> war nicht korrekt. Erwartet wurde <code>${sampleArg.assocType}</code>!</p>`);
        } else {
            explanations.push('<p class="text-success">Der Typ der Assozation war korrekt.</p>');
        }

        // Cardinalities
        let endsParallel = userArg.firstEnd === sampleArg.firstEnd,
            gottenCardinalities = userArg.firstMult + ": " + userArg.secondMult;
        let cardinalitiesEqual, correctCardinalities;

        if (endsParallel) {
            cardinalitiesEqual = userArg.firstMult === sampleArg.firstMult && userArg.secondMult === sampleArg.secondMult;
            correctCardinalities = sampleArg.firstMult + ": " + sampleArg.secondMult;
        } else {
            cardinalitiesEqual = userArg.firstMult === sampleArg.secondMult && userArg.secondMult === sampleArg.firstMult;
            correctCardinalities = sampleArg.secondMult + ": " + sampleArg.firstMult;
        }

        if (cardinalitiesEqual) {
            explanations.push(`<p class="text-success">Die Kardinalitäten der Assoziation waren korrekt.</p>`);
        } else {
            explanations.push(`<p class="text-danger">Die Kardinalitätan der Assoziation <code>${gottenCardinalities}</code> war nicht korrekt. Erwartet wurde <code>${correctCardinalities}</code>!</p>`);
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
 * @param {UmlImplementation} implRes.userArg
 * @param {UmlImplementation} implRes.sampleArg
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
    console.error(jqXHR.responseJSON);
    $('#testButton').prop('disabled', false);
}


function getClassNameFromCellId(id) {
    return (classDiagGraph.getCell(id) as CustomClass).get('className');
}

function getTypeName(type: string): string {
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

function getMultiplicity(label): "SINGLE" | "UNBOUND" {
    return label.attrs.text.text === '1' ? 'SINGLE' : 'UNBOUND';
}

function umlImplfromConnection(conn): UmlImplementation {
    return {
        subClass: getClassNameFromCellId(conn.attributes.source.id),
        superClass: getClassNameFromCellId(conn.attributes.target.id)
    }
}

function umlAssocfromConnection(conn): UmlAssociation {
    return {
        assocType: getTypeName(conn.attributes.type),
        assocName: '',        // TODO: name of association!?!
        firstEnd: getClassNameFromCellId(conn.attributes.source.id),
        firstMult: getMultiplicity(conn.attributes.labels[0]),
        secondEnd: getClassNameFromCellId(conn.attributes.target.id),
        secondMult: getMultiplicity(conn.attributes.labels[1])
    };
}

function testSol(): void {
    let toolType = $('#toolType').val(), exerciseId = $('#exerciseId').val(), exercisePart = $('#exercisePart').val();

    // noinspection JSUnresolvedFunction, JSUnresolvedVariable
    let url = ''; // FIXME: jsRoutes.controllers.ExerciseController.correctLive(toolType, exerciseId, exercisePart).url;

    $('#testButton').prop('disabled', true);

    let solution = {
        classes: classDiagGraph.getCells().filter((cell) => cell.get('type') === 'customUml.CustomClass').map((cell: CustomClass) => cell.getAsUmlClass()),
        associations: classDiagGraph.getLinks().filter((conn) => conn.get('type') !== 'uml.Implementation').map((conn) => umlAssocfromConnection(conn)),
        implementations: classDiagGraph.getLinks().filter((conn) => conn.get('type') === 'uml.Implementation').map((conn) => umlImplfromConnection(conn))
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