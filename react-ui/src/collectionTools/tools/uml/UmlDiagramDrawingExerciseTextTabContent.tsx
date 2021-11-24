import {UmlExerciseText} from './UmlExerciseText';
import classNames from 'classnames';
import {ExerciseSolveFieldsFragment, UmlExerciseContentFragment, UmlExPart} from '../../../graphql';
import {SelectableClassDiagramObject} from './UmlDiagramDrawing';
import {ChildLink, ExerciseControlButtons} from '../../../helpers/ExerciseControlButtons';
import {useTranslation} from 'react-i18next';
import {collectionsUrlFragment, exercisesUrlFragment, partsUrlFragment, toolsUrlFragment} from '../../../urls';

interface IProps {
  exercise: ExerciseSolveFieldsFragment;
  content: UmlExerciseContentFragment;
  creatableClassDiagramObjects: SelectableClassDiagramObject[];
  correct: () => void;
  onClassClick: (name: string) => void;
  toggle: (x: SelectableClassDiagramObject) => void;
  selectedCreatableObject: SelectableClassDiagramObject | undefined;
  isCorrecting: boolean;
}

export function UmlDiagramDrawingExerciseTextTabContent(
  {exercise, content, creatableClassDiagramObjects, correct, onClassClick, toggle, selectedCreatableObject}: IProps
): JSX.Element {

  const {t} = useTranslation('common');

  const isCorrecting = false;
  const corrected = false;

  // FIXME: memberAllocation?
  const childLinks: ChildLink[] = content.umlPart === UmlExPart.DiagramDrawingHelp
    ? [{
      text: t('goToNextPart'),
      classNames: classNames('button', 'is-fullwidth', corrected ? 'is-link' : 'is-dark'),
      to: `/${toolsUrlFragment}/${exercise.toolId}/${collectionsUrlFragment}/${exercise.collectionId}/${exercisesUrlFragment}/${exercise.exerciseId}/${partsUrlFragment}/memberAllocation`
    }]
    : [];

  return (
    <>
      <UmlExerciseText exercise={exercise} content={content} onClassClick={onClassClick}/>

      <div className="columns">
        {creatableClassDiagramObjects.map((x) =>
          <div className="column" key={x.name}>
            <button className={classNames('button is-fullwidth', x.name === selectedCreatableObject?.name ? 'is-link' : '')} onClick={() => toggle(x)}
                    disabled={x.disabled}>
              {x.name}
            </button>
          </div>
        )}
      </div>

      <ExerciseControlButtons isCorrecting={isCorrecting} correct={correct} endLink={''} childLinks={childLinks}/>
    </>
  );
}
