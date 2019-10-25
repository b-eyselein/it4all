import {Exercise, Tool, ToolPart} from '../../_interfaces/tool';
import {isProgrammingExercise} from './programming/programming';

// Programming

const ProgrammingTestCreationPart: ToolPart = {id: 'testCreation', name: 'Erstellen der Unittests'};

export const ProgrammingImplementationToolPart: ToolPart = {id: 'implementation', name: 'Implementierung'};

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
        } else if (part === ProgrammingImplementationToolPart) {
          return true;
        }
      }

      return false;
    }
  }
)();

// Regex

export const RegexTool: Tool = new Tool('regex', 'Reguläre Ausdrücke', [{id: 'regex', name: 'Regulären Ausdruck erstellen'}]);

// Rose

export const RoseTool: Tool = new Tool('rose', 'ROSE', [], 'alpha');

// SQL

export const SqlCreateQueryPart: ToolPart = {id: 'createQuery', name: 'Abfrage erstellen'};

export const SqlTool: Tool = new Tool('sql', 'SQL', [SqlCreateQueryPart]);

// Uml

export const UmlTool: Tool = new Tool('uml', 'UML-Klassendiagramme', [], 'beta');

// Web

export const WebTool: Tool = new Tool('web', 'Web', [], null, true, true);

// XML

export const XmlTool: Tool = new Tool('xml', 'XML', []);

// All Tools

export const collectionTools: Tool[] = [
  ProgrammingTool,
  RegexTool,
  RoseTool,
  SqlTool,
  UmlTool,
  WebTool,
  XmlTool
];

