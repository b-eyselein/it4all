import {MatchType, XmlElementLineMatchFragment, XmlElementLineMatchingResultFragment, XmlGrammarResultFragment} from '../../../graphql';
import classNames from 'classnames';
import {bgColors, textColors} from '../../../consts';
import {useTranslation} from 'react-i18next';

function XmlElementLineMatchResultDisplay({m}: { m: XmlElementLineMatchFragment }): JSX.Element {

  const {t} = useTranslation('common');

  const isCorrect = m.matchType === MatchType.SuccessfulMatch;
  const {contentCorrect, correctContent, attributesCorrect, correctAttributes} = m.analysisResult;

  return (
    <div className="my-2">
      <p className={isCorrect ? textColors.correct : textColors.inCorrect}>
        Die Definition des Element <code>{m.userArg.elementName}</code> ist {isCorrect ? '' : 'nicht'} korrekt.
      </p>

      {!isCorrect && <ul className="list-disc list-inside">
        {contentCorrect
          ? <li className={textColors.correct}>{t('contentCorrect')}.</li>
          : <li className={textColors.inCorrect}>{t('contentNotCorrect')}. {t('awaitedContent')} <code>{correctContent}</code>.</li>}

        {attributesCorrect
          ? <li className={textColors.correct}>{t('attributesCorrect')}.</li>
          : <li className={textColors.inCorrect}>{t('attributesNotCorrect')}. {t('awaitedContent')} <code>{correctAttributes}</code>.</li>}
      </ul>}
    </div>
  );
}

interface IProps {
  result: XmlElementLineMatchingResultFragment;
}

function XmlElementLineMatchingResultDisplay({result}: IProps): JSX.Element {

  const {t} = useTranslation('common');
  const {allMatches, notMatchedForUser, notMatchedForSample} = result;

  return (
    <div className="my-4">
      {allMatches.map((m, index) => <XmlElementLineMatchResultDisplay m={m} key={index}/>)}

      {notMatchedForUser.length > 0 && <div className={classNames('my-3', textColors.inCorrect)}>
        {t('elementDefinitionsNotCorrect')}:
        <ul>
          {notMatchedForUser.map(({elementName}, index) => <li key={index}><code>{elementName}</code></li>)}
        </ul>
      </div>}

      {notMatchedForSample.length > 0 && <div className={classNames('my-3', textColors.inCorrect)}>
        {t('elementDefinitionsMissing')}:
        <ul className="list-disc list-inside">
          {notMatchedForSample.map(({elementName}, index) => <li key={index}><code>{elementName}</code></li>)}
        </ul>
      </div>}
    </div>
  );
}

export function XmlGrammarResultDisplay({result}: { result: XmlGrammarResultFragment; }): JSX.Element {

  const {t} = useTranslation('common');

  return (
    <>
      {result.parseErrors.map((parseError, index) =>
        <div key={index} className={classNames('p-2', 'rounded', bgColors.inCorrect, 'text-white')}>
          {t('parsingError')}: <code>{parseError.msg}</code>
        </div>
      )}

      <XmlElementLineMatchingResultDisplay result={result.results}/>
    </>
  );
}
