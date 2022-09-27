import {MatchType, RegexExtractionMatchFragment, RegexExtractionSingleResultFragment} from '../../../graphql';
import classNames from 'classnames';

interface IProps {
  m: RegexExtractionSingleResultFragment;
}

function isCorrect(m: RegexExtractionMatchFragment): boolean {
  return m.matchType === MatchType.SuccessfulMatch;
}

export const RegexExtractionSingleResultDisplay: (p: IProps) => JSX.Element = ({m: {base, extractionMatchingResult}}: IProps) => (
  <div className="m-4 p-4 rounded bg-gray-200">
    <p className="text-center">Suche in: <code>{base}</code></p>

    <div className="grid grid-cols-2 gap-2">
      {extractionMatchingResult.allMatches.map((m, index) =>
        <div key={index} className={classNames('notification', isCorrect(m) ? 'is-success' : 'is-danger')}>
          {isCorrect(m) ? '&#10004;' : '&#10008;'} Erwartet: <code>{m.sampleArg}</code>, bekommen: <code>{m.userArg}</code>
        </div>
      )}

      {extractionMatchingResult.notMatchedForUser.map((mu, index) =>
        <div className="column is-half-desktop" key={index}>
          <div className="notification is-danger">
            <code>{mu}</code> sollte nicht gefunden werden!
          </div>
        </div>
      )}

      {extractionMatchingResult.notMatchedForSample.map((ms, index) =>
        <div className="column is-half-desktop" key={index}>
          <div className="notification is-danger">
            <code>{ms}</code> sollte gefunden werden!
          </div>
        </div>
      )}

    </div>
  </div>
);
