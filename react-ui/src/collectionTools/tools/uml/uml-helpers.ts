import {ExerciseSolveFieldsFragment, KeyValueObject, UmlExerciseContentFragment} from '../../../graphql';

/*
export function getIdForUmlExPart(umlExPart: UmlExPart): string {
  switch (umlExPart) {
    case UmlExPart.ClassSelection:
      return 'classSelection';
    case UmlExPart.DiagramDrawing:
      return 'diagramDrawing';
    case UmlExPart.DiagramDrawingHelp:
      return 'diagramDrawingHelp';
    case UmlExPart.MemberAllocation:
      return 'memberAllocation';
  }
}
 */

// Helper functions

export type UmlExClassSelectionTextPart = string | { text: string; className: string; }

const capWordTextSplitRegex = /([A-Z][\wäöü?&;]*)/g;

export function splitExerciseText(exerciseText: string): string[] {
  return exerciseText
    .replace('\n', ' ')
    .split(capWordTextSplitRegex)
    .filter((s) => s.length > 0);
}

/* export */
function replaceWithMapping(mappings: KeyValueObject[], str: string): string {
  const maybeMapping = mappings.find((m) => m.key === str);
  return maybeMapping ? maybeMapping.value : str;
}

/* export */
function isSelectable(toIgnore: string[], s: string): boolean {
  return !!s.match(capWordTextSplitRegex) && !toIgnore.includes(s);
}

export function getUmlExerciseTextParts(
  exercise: ExerciseSolveFieldsFragment,
  exerciseContent: UmlExerciseContentFragment,
): UmlExClassSelectionTextPart[] {

  const mappings = exerciseContent.mappings;

  const splitText = splitExerciseText(exercise.text);

  const allBaseForms: string[] = Array.from(
    new Set(
      splitText
        .filter((s) => isSelectable(exerciseContent.toIgnore, s))
        .map((s) => replaceWithMapping(mappings, s))
    )
  );

  return splitText.map<UmlExClassSelectionTextPart>((text) =>
    isSelectable(exerciseContent.toIgnore, text)
      ? {text, className: allBaseForms.find((name) => name === replaceWithMapping(mappings, text))!}
      : text
  );
}

