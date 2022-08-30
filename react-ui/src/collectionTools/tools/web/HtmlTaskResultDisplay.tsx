import {GradedHtmlTaskResultFragment} from '../../../graphql';
import {ElementSpecResultDisplay} from './ElementSpecResultDisplay';

interface IProps {
  htmlResult: GradedHtmlTaskResultFragment;
}

export function HtmlTaskResultDisplay({htmlResult}: IProps): JSX.Element {

  const {id, elementSpecResult} = htmlResult;

  const {points, maxPoints, isCorrect} = elementSpecResult;

  return (
    <>
      <p className={isCorrect ? 'has-text-success' : 'has-text-danger'}>
        ({points} / {maxPoints}) Teilaufgabe {id} ist {isCorrect ? '' : 'nicht'} korrekt:
      </p>

      <ElementSpecResultDisplay elementSpecResult={elementSpecResult}/>
    </>
  );
}
