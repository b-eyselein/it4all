import {CollectionTool, ToolPart} from '../../../_interfaces/tool';
import {ProgExerciseContentSolveFieldsFragment} from "../../../_services/apollo_services";
import {UnitTestType} from "../../../_interfaces/graphql-types";

// Exercise Parts

const ProgrammingTestCreationPart: ToolPart = {id: 'testCreation', name: 'Erstellen der Unittests'};

export const ProgrammingImplementationToolPart: ToolPart = {id: 'implementation', name: 'Implementierung'};

// Tool

export const ProgrammingTool: CollectionTool = new class ProgrammingToolClass extends CollectionTool {
  constructor() {
    super('programming', 'Programmierung', [ProgrammingTestCreationPart, ProgrammingImplementationToolPart], 'beta', true);
  }

  exerciseHasPart(exerciseContent: ProgExerciseContentSolveFieldsFragment, part: ToolPart): boolean {
    if (part === ProgrammingTestCreationPart) {
      // FIXME: simplified test execution is disabled...
      return exerciseContent.unitTestPart.unitTestType === UnitTestType.Normal;
    } else {
      return true;
    }
  }

}();
