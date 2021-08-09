import React from 'react';
import {MatchType, UmlAssociationMatchFragment, UmlMultiplicity} from '../../../graphql';


function printCardinality(c: UmlMultiplicity): string {
  return c === UmlMultiplicity.Unbound ? '*' : '1';
}

export function UmlAssocMatchResult({m}: { m: UmlAssociationMatchFragment }): JSX.Element {

  const {matchType, userArg, sampleArg, analysisResult} = m;

  const isCorrect = matchType === MatchType.SuccessfulMatch;

  const gottenCardinalities = `${printCardinality(userArg.firstMult)} : ${printCardinality(userArg.secondMult)}`;

  const correctCardinalities = (userArg.firstEnd === sampleArg.firstEnd)
    ? `${printCardinality(sampleArg.firstMult)} : ${printCardinality(sampleArg.secondMult)}`
    : `${printCardinality(sampleArg.secondMult)} : ${printCardinality(sampleArg.firstMult)}`;

  return (
    <div className={isCorrect ? 'has-text-dark-success' : 'has-text-danger'}>
      Die Assoziation von <code>{userArg.firstEnd}</code> nach <code>{userArg.secondEnd}</code> ist {isCorrect ? '' : 'nicht'} korrekt.

      {!isCorrect && <div className="content">
        <ul>
          <li className={analysisResult.assocTypeEqual ? 'has-text-dark-success' : 'has-text-danger'}>
            Der Typ der Assoziation <code>{userArg.assocType}</code> war {analysisResult.assocTypeEqual ? '' : 'nicht'} korrekt.
          </li>
          <li className={analysisResult.multiplicitiesEqual ? 'has-text-dark-success' : 'has-text-danger'}>
            Die Kardinalitäten der Assoziation <code>{gottenCardinalities}</code> waren {analysisResult.multiplicitiesEqual ? '' : 'nicht'} korrekt.
            {!analysisResult.multiplicitiesEqual && <> Erwartet wurde <code>{correctCardinalities}</code></>}
          </li>
        </ul>
      </div>}
    </div>
  );
}
