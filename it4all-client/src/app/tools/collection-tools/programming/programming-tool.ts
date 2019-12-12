import {CollectionTool, ToolPart} from '../../../_interfaces/tool';
import {IExercise} from '../../../_interfaces/models';

// Exercise Parts

const ProgrammingTestCreationPart: ToolPart = {id: 'testCreation', name: 'Erstellen der Unittests'};

export const ProgrammingImplementationToolPart: ToolPart = {id: 'implementation', name: 'Implementierung'};

// Tool

export const ProgrammingTool: CollectionTool = new class ProgrammingToolClass extends CollectionTool {
  constructor() {
    super('programming', 'Programmierung', [ProgrammingTestCreationPart, ProgrammingImplementationToolPart], 'beta');
  }

  exerciseHasPart(exercise: IExercise, part: ToolPart): boolean {

    if (part === ProgrammingTestCreationPart) {
      // FIXME: simplified test execution is disabled...
      return exercise.content.unitTestPart.unitTestType === 'Normal';
    } else {
      return true;
    }
  }

}();
