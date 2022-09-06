import {ConcreteExerciseWithPartsProps} from '../../Exercise';
import {UmlExerciseContentFragment, UmlExPart} from '../../../graphql';
import {UmlClassSelection} from './UmlClassSelection';
import {UmlMemberAllocation} from './UmlMemberAllocation';
import {UmlDbClassDiagram, UmlDiagramDrawing} from './UmlDiagramDrawing';

type IProps = ConcreteExerciseWithPartsProps<UmlExerciseContentFragment, UmlDbClassDiagram>;

function getUmlExPart(partId: string): UmlExPart {
  switch (partId) {
    case 'classSelection':
      return UmlExPart.ClassSelection;
    case 'diagramDrawingHelp':
      return UmlExPart.DiagramDrawingHelp;
    case 'memberAllocation':
      return UmlExPart.MemberAllocation;
    case 'diagramDrawing':
    default:
      return UmlExPart.DiagramDrawing;
  }
}

export function UmlExercise({exercise, content, partId, oldSolution}: IProps): JSX.Element {

  const part = getUmlExPart(partId);

  if (part === UmlExPart.ClassSelection) {
    return <UmlClassSelection exercise={exercise} content={content}/>;
  } else if (part === UmlExPart.MemberAllocation) {
    return <UmlMemberAllocation exercise={exercise} content={content}/>;
  } else {
    return <UmlDiagramDrawing exercise={exercise} content={content} withHelp={part === UmlExPart.DiagramDrawingHelp} partId={partId} part={part}
                              oldSolution={oldSolution}/>;
  }
}
