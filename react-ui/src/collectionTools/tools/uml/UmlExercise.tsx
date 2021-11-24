import {ConcreteExerciseIProps} from '../../Exercise';
import {UmlExerciseContentFragment, UmlExPart} from '../../../graphql';
import {Navigate} from 'react-router-dom';
import {UmlClassSelection} from './UmlClassSelection';
import {UmlMemberAllocation} from './UmlMemberAllocation';
import {UmlDbClassDiagram, UmlDiagramDrawing} from './UmlDiagramDrawing';

type IProps = ConcreteExerciseIProps<UmlExerciseContentFragment, UmlDbClassDiagram>;

export function UmlExercise({exercise, content, partId, oldSolution}: IProps): JSX.Element {

  if (!content.umlPart) {
    return <Navigate to={''}/>;
  }

  const part = content.umlPart;

  if (part === UmlExPart.ClassSelection) {
    return <UmlClassSelection exercise={exercise} content={content}/>;
  } else if (part === UmlExPart.MemberAllocation) {
    return <UmlMemberAllocation exercise={exercise} content={content}/>;
  } else {
    return <UmlDiagramDrawing exercise={exercise} content={content} withHelp={part === UmlExPart.DiagramDrawingHelp} partId={partId}
                              oldSolution={oldSolution}/>;
  }
}
