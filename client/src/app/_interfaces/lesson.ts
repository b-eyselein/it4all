import {Tool} from './tool';
import {BoolTool} from '../tools/random-tools/random-tools-list';

export interface TutorialContent {
  content: string;
}

export interface Lesson {
  id: number;
  title: string;
  description: string;
  content: TutorialContent[];
  dependsOn: number[];
}

export const EXAMPLE_TUTORIALS: Map<Tool, Lesson[]> = new Map<Tool, Lesson[]>([
  [
    BoolTool,
    [
      {
        id: 1,
        title: 'Grundlagen der Boolesche Algebra',
        description: `In dieser Lektion lernen Sie die Grundlagen der Booleschen Algebra wie Variablen und Operatoren kennen.`,
        content: [],
        dependsOn: []
      },
      {
        id: 2,
        title: 'Weitere Operatoren',
        description: `Aufbauend auf der Lektion 'Grundlagen der Booleschen Algebra' lernen Sie in dieser Lektion weitere Operatoren wie
        Nand und Nor kennen.`,
        content: [],
        dependsOn: [1]
      }
    ]
  ]
]);
