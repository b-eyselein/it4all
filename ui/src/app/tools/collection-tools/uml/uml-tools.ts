import {CollectionTool, ToolPart} from '../../../_interfaces/tool';
import {distinctStringArray} from '../../../helpers';
import {ExerciseSolveFieldsFragment, UmlExerciseContentSolveFieldsFragment} from "../../../_services/apollo_services";
import {KeyValueObject} from "../../../_interfaces/graphql-types";

export const UmlClassSelectionPart: ToolPart = {name: 'Klassenselektion', id: 'classSelection'};

export const UmlDiagramDrawingHelpPart: ToolPart = {
  name: 'Diagramm zeichnen mit Hilfe',
  id: 'diagramDrawingHelp',
  disabled: true
};

const UmlDiagramDrawingPart: ToolPart = {name: 'Diagramm zeichnen', id: 'diagramDrawing', disabled: true};

export const UmlMemberAllocationPart: ToolPart = {
  name: 'Zuordnung der Attribute und Methoden',
  id: 'memberAllocation',
  disabled: true
};

export const UmlTool: CollectionTool = new class UmlToolClass extends CollectionTool {
  constructor() {
    super('uml', 'UML-Klassendiagramme', [UmlClassSelectionPart, UmlDiagramDrawingPart, UmlDiagramDrawingHelpPart, UmlMemberAllocationPart], 'beta');
  }
}();

// Helper functions

export interface SelectableClass {
  name: string;
  selected: boolean;
  isCorrect: boolean;
}

export interface UmlExerciseTextPart {
  text: string;
  selectableClass?: SelectableClass;
}

const capWordTextSplitRegex: RegExp = /([A-Z][\wäöü?&;]*)/g;

export function splitExerciseText(exerciseText: string): string[] {
  return exerciseText
    .replace('\n', ' ')
    .split(capWordTextSplitRegex)
    .filter((s) => s.length > 0);
}

export function replaceWithMapping(mappings: KeyValueObject[], str: string): string {
  const maybeMapping = mappings.find((m) => m.key === str);
  return maybeMapping ? maybeMapping.value : str;
}

export function isSelectable(toIgnore: string[], s: string): boolean {
  return s.match(capWordTextSplitRegex) && !toIgnore.includes(s);
}

export function getUmlExerciseTextParts(
  exercise: ExerciseSolveFieldsFragment,
  exerciseContent: UmlExerciseContentSolveFieldsFragment
): { selectableClasses: SelectableClass[], textParts: UmlExerciseTextPart[] } {

  const splitText = splitExerciseText(exercise.text);

  const allBaseForms = distinctStringArray(
    splitText
      .filter((s) => isSelectable(exerciseContent.toIgnore, s))
      .map((s) => replaceWithMapping(exerciseContent.mappings, s))
  );

  const sampleSolution = exerciseContent.umlSampleSolutions[0].sample;

  const selectableClasses = allBaseForms.map<SelectableClass>((name) => {
      return {
        name,
        selected: false,
        isCorrect: sampleSolution.classes.find((c) => c.name === name) !== undefined
      };
    }
  );

  const textParts = splitText.map<UmlExerciseTextPart>((text) => {
    if (isSelectable(exerciseContent.toIgnore, text)) {
      const selectableClass = selectableClasses.find(
        (c) => c.name === replaceWithMapping(exerciseContent.mappings, text)
      );
      return {text, selectableClass};
    } else {
      return {text};
    }
  });

  return {selectableClasses, textParts};
}

