import {UmlExerciseText} from './UmlExerciseText';
import classNames from 'classnames';
import {ExerciseSolveFieldsFragment, UmlExerciseContentFragment, UmlExPart} from '../../../graphql';
import {CreatableClassDiagramObject, SelectableClassDiagramObject} from './UmlDiagramDrawing';
import {ChildLink, ExerciseControlButtons} from '../../../helpers/ExerciseControlButtons';
import {useTranslation} from 'react-i18next';
import {collectionsUrlFragment, exercisesUrlFragment, partsUrlFragment, toolsUrlFragment} from '../../../urls';

interface IProps {
  exercise: ExerciseSolveFieldsFragment;
  content: UmlExerciseContentFragment;
  creatableClassDiagramObjects: SelectableClassDiagramObject[];
  correct: () => void;
  onClassClick: (name: string) => void;
  toggle: (x: CreatableClassDiagramObject) => void;
  selectedCreatableObject: CreatableClassDiagramObject | undefined;
  part: UmlExPart;
}

export function UmlDiagramDrawingExerciseTextTabContent({
  exercise,
  content,
  creatableClassDiagramObjects,
  correct,
  onClassClick,
  toggle,
  selectedCreatableObject,
  part
}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  const isCorrecting = false;
  const corrected = false;

  // FIXME: memberAllocation?
  const childLinks: ChildLink[] = part === UmlExPart.DiagramDrawingHelp
    ? [
      {
        text: t('goToNextPart'),
        otherClassNames: 'bg-black text-white text-center',
        to: `/${toolsUrlFragment}/${exercise.toolId}/${collectionsUrlFragment}/${exercise.collectionId}/${exercisesUrlFragment}/${exercise.exerciseId}/${partsUrlFragment}/memberAllocation`
      }
    ]
    : [];

  return (
    <>
      <UmlExerciseText exercise={exercise} content={content} onClassClick={onClassClick}/>

      <div className="grid grid-cols-3 gap-2">
        {creatableClassDiagramObjects.map(({name, key, disabled}) =>
          <button key={name} onClick={() => toggle(key)} disabled={disabled}
                  className={classNames('flex-grow p-2 rounded', key === selectedCreatableObject ? 'bg-blue-500 text-white' : 'border border-slate-500')}>
            {name}
          </button>
        )}
      </div>

      <ExerciseControlButtons isCorrecting={isCorrecting} correct={correct} endLink={''} childLinks={childLinks}/>
    </>
  );
}
