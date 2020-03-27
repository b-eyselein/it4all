import {Component, Input, OnInit} from '@angular/core';
import {ToolPart} from '../../../../_interfaces/tool';
import {ApiService} from '../../_services/api.service';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {DexieService} from '../../../../_services/dexie.service';
import {IXmlCompleteResult, IXmlExerciseContent, IXmlSolution} from '../xml-interfaces';
import {IExercise} from '../../../../_interfaces/models';
import {IExerciseFile} from '../../web/web-interfaces';

import 'codemirror/mode/dtd/dtd';
import 'codemirror/mode/xml/xml';

function getXmlGrammarContent(rootNode: string): string {
  return `<!ELEMENT ${rootNode} (EMPTY)>`;
}

function getXmlDocumentContent(rootNode: string): string {
  return `
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE ${rootNode} SYSTEM "${rootNode}.dtd">
<${rootNode}>
</${rootNode}>`.trim();
}

@Component({
  selector: 'it4all-xml-exercise',
  templateUrl: './xml-exercise.component.html'
})
export class XmlExerciseComponent extends ComponentWithExercise<IXmlSolution, IXmlCompleteResult> implements OnInit {

  @Input() exercise: IExercise;
  @Input() part: ToolPart;

  isGrammarPart: boolean;

  grammarFile: IExerciseFile;
  documentFile: IExerciseFile;

  exerciseFiles: IExerciseFile[] = [];

  constructor(apiService: ApiService, dexieService: DexieService) {
    super(apiService, dexieService);
  }

  ngOnInit() {
    const exerciseContent = this.exercise.content as IXmlExerciseContent;

    this.isGrammarPart = this.part.id === 'grammar';

    const grammarFileName = `${exerciseContent.rootNode}.dtd`;
    this.grammarFile = {
      name: grammarFileName,
      content: this.isGrammarPart ?
        getXmlGrammarContent(exerciseContent.rootNode) : (exerciseContent.sampleSolutions[0].sample as IXmlSolution).grammar,
      fileType: 'dtd',
      editable: this.isGrammarPart,
      resourcePath: grammarFileName
    };

    const documentFileName = `${exerciseContent.rootNode}.xml`;
    this.documentFile = {
      name: documentFileName,
      content: getXmlDocumentContent(exerciseContent.rootNode),
      fileType: 'xml',
      editable: !this.isGrammarPart,
      resourcePath: documentFileName
    };

    this.exerciseFiles = [this.grammarFile, this.documentFile];

    this.loadOldSolutionAbstract(this.exercise, this.part)
      .then((oldSol) => {
        if (oldSol) {
          this.grammarFile.content = oldSol.grammar;
          this.documentFile.content = oldSol.document;

          // do not delete or else editor does not get updated...
          this.exerciseFiles = [this.grammarFile, this.documentFile];
        }
      });
  }

  correct(): void {
    this.correctAbstract(this.exercise.id, this.exercise.collectionId, this.exercise.toolId, this.part);
  }

  protected getSolution(): IXmlSolution {
    return {
      grammar: this.grammarFile.content,
      document: this.documentFile.content
    };
  }

  get sampleSolutions(): IXmlSolution[] {
    const exContent = this.exercise.content as IXmlExerciseContent;
    return exContent.sampleSolutions.map((sample) => sample.sample);
  }

  get grammarDescription(): string {
    return (this.exercise.content as IXmlExerciseContent).grammarDescription;
  }

}
