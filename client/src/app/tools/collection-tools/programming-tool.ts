import {Exercise, ExerciseTag, Tool, ToolPart} from '../../_interfaces/tool';
import {isProgrammingExercise} from './programming/programming-interfaces';

// Exercise Parts

const ProgrammingTestCreationPart: ToolPart = {id: 'testCreation', name: 'Erstellen der Unittests'};

export const ProgrammingImplementationToolPart: ToolPart = {id: 'implementation', name: 'Implementierung'};

// Exercise Tags

const programmingExerciseTags: Map<string, ExerciseTag> = new Map([
  ['ForLoops', {label: 'FL', title: 'For-Schleifen'}],
  ['WhileLoops', {label: 'WL', title: 'While-Schleifen'}],
  ['Conditions', {label: 'C', title: 'Bedingungen'}],
  ['Lists', {label: 'L', title: 'Listen'}],
  ['Tuples', {label: 'T', title: 'Tuples'}],
  ['Dictionaries', {label: 'D', title: 'Dictionaries'}],
  ['Classes', {label: 'C', title: 'Klassen'}],
  ['Exceptions', {label: 'E', title: 'Exceptions'}],
  ['Math', {label: 'M', title: 'Mathematik'}],
  ['Strings', {label: 'S', title: 'Strings'}],
  ['Slicing', {label: 'SL', title: 'Slicing'}],
  ['Recursion', {label: 'R', title: 'Rekursion'}]
]);

// Tool

export const ProgrammingTool: Tool = new (
  class ProgrammingToolClass extends Tool {
    constructor() {
      super('programming', 'Programmierung', [ProgrammingTestCreationPart, ProgrammingImplementationToolPart], 'beta');
    }

    exerciseHasPart(exercise: Exercise, part: ToolPart): boolean {
      if (isProgrammingExercise(exercise)) {
        if (part === ProgrammingTestCreationPart) {
          // FIXME: simplified test execution is disabled...
          return exercise.unitTestPart.unitTestType === 'Normal';
        } else {
          return true;
        }
      }

      return false;
    }

    processTagString(tag: string): ExerciseTag {
      return programmingExerciseTags.get(tag);
    }
  }
)();
