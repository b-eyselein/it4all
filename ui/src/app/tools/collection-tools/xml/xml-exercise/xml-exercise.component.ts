import {Component, Input, OnInit} from '@angular/core';
import {ToolPart} from '../../../../_interfaces/tool';
import {ApiService} from '../../_services/api.service';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {DexieService} from '../../../../_services/dexie.service';
import {IXmlCompleteResult, IXmlSolution} from '../xml-interfaces';
import {IExerciseFile} from '../../web/web-interfaces';

import 'codemirror/mode/dtd/dtd';
import 'codemirror/mode/xml/xml';
import {
  ExerciseSolveFieldsFragment,
  XmlExerciseContentSolveFieldsFragment
} from "../../../../_services/apollo_services";

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


  @Input() part: ToolPart;
  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() xmlExerciseContent: XmlExerciseContentSolveFieldsFragment;

  isGrammarPart: boolean;

  grammarFile: IExerciseFile;
  documentFile: IExerciseFile;

  exerciseFiles: IExerciseFile[] = [];

  constructor(apiService: ApiService, dexieService: DexieService) {
    super(apiService, dexieService);
  }

  ngOnInit() {
    const rootNode = this.xmlExerciseContent.rootNode;

    this.isGrammarPart = this.part.id === 'grammar';

    const grammarFileName = `${rootNode}.dtd`;
    this.grammarFile = {
      name: grammarFileName,
      content: this.isGrammarPart ? getXmlGrammarContent(rootNode) : (this.xmlExerciseContent.xmlSampleSolutions[0].sample as IXmlSolution).grammar,
      fileType: 'dtd',
      editable: this.isGrammarPart,
      resourcePath: grammarFileName
    };

    const documentFileName = `${rootNode}.xml`;
    this.documentFile = {
      name: documentFileName,
      content: getXmlDocumentContent(rootNode),
      fileType: 'xml',
      editable: !this.isGrammarPart,
      resourcePath: documentFileName
    };

    this.exerciseFiles = [this.grammarFile, this.documentFile];

    this.loadOldSolutionAbstract(this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, this.part)
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
    this.correctAbstract(this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, this.part);
  }

  protected getSolution(): IXmlSolution {
    return {
      grammar: this.grammarFile.content,
      document: this.documentFile.content
    };
  }

  get sampleSolutions(): IXmlSolution[] {
    return this.xmlExerciseContent.xmlSampleSolutions.map((sample) => sample.sample);
  }

  get grammarDescription(): string {
    return this.xmlExerciseContent.grammarDescription;
  }

}
