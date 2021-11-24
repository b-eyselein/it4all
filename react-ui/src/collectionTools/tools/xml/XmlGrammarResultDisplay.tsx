import {XmlGrammarResultFragment} from '../../../graphql';
import {XmlElementLineMatcingResulthDisplay} from './XmlElementLineMatcingResulthDisplay';

interface IProps {
  result: XmlGrammarResultFragment;
}

export function XmlGrammarResultDisplay({result}: IProps): JSX.Element {

  return (
    <>
      {result.parseErrors.map((parseError, index) =>
        <div className="message is-danger" key={index}>
          <div className="message-header">
            Fehler beim Parse von &quot;<code>{parseError.parsedLine}</code>&quot;:
          </div>
          <div className="message-body">
            <pre><code className="has-text-danger">{parseError.msg}</code></pre>
          </div>
        </div>
      )}

      <XmlElementLineMatcingResulthDisplay result={result.results}/>
    </>
  );
}
