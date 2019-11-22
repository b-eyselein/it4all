import {AnalysisResult, BinaryClassificationResultType, CorrectionResult, Match, MatchingResult, StringSampleSolution} from '../../basics';
import {DbSolution, ExerciseContent} from '../../../_interfaces/exercise';

export type RegexCorrectionType = 'MATCHING' | 'EXTRACTION';


export interface RegexMatchTestData {
  id: number;
  data: string;
  isIncluded: boolean;
}

// tslint:disable-next-line:no-empty-interface
export interface RegexExtractionTestData {

}

export interface RegexExerciseContent extends ExerciseContent {
  maxPoints: number;
  correctionType: RegexCorrectionType;
  sampleSolutions: StringSampleSolution[];
  matchTestData: RegexMatchTestData[];
  extractionTestData: RegexExtractionTestData[];
}

export interface RegexMatchingResult {
  matchData: string;
  isIncluded: boolean;
  resultType: BinaryClassificationResultType;
}

export interface RegexExtractionMatch {
  start: number;
  end: number;
  content: string;
}

export interface RegexExtractionMatchingResult extends MatchingResult<RegexExtractionMatch, AnalysisResult> {
  allMatches: Match<RegexExtractionMatch, AnalysisResult>[];
}

export interface RegexExtractionResult {
  base: string;
  extractionMatchingResult: RegexExtractionMatchingResult;
  correct: boolean;
}

export interface RegexCorrectionResult extends CorrectionResult<any> {
  correctionType: RegexCorrectionType;
  matchingResults: RegexMatchingResult[];
  extractionResults: RegexExtractionResult[];
}

//   for (const result of correctionResult.extractionResults) {
//
//     console.info(JSON.stringify(result.extractionMatchingResult.allMatches, null, 2));
//
//     const matches = '<ul>' + result.extractionMatchingResult.allMatches.map(m => {
//       const clazz = (m.analysisResult && m.analysisResult.success == 'SUCCESSFUL_MATCH') ? 'success' : 'danger';
//
//       return `
// <li>
//     <b>Erwartet:</b> &quot;<span>${m.sampleArg ? m.sampleArg.content : ''}</span>&quot;,
//     <b>Gefunden:</b> &quot;<span class="text-${clazz}">${m.userArg ? m.userArg.content : ''}</span>&quot;
// </li>`.trim()
//     }).join('\n') + '</ul>';
//
//     html += `
// <div class="card my-3">
//     <div class="card-body">
//         <p>${result.base}</p>
//         ${matches}
//     </div>
// </div>`;

// TODO: Dexie db defs...

export interface DbRegexSolution extends DbSolution<string> {

}
