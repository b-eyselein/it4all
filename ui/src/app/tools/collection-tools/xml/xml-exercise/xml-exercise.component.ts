import {Component, Input, OnInit} from '@angular/core';
import {ToolPart} from '../../../../_interfaces/tool';
import {ApiService} from '../../_services/api.service';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {DexieService} from '../../../../_services/dexie.service';
import {
  ExerciseFile,
  ExerciseSolveFieldsFragment,
  XmlExerciseContentSolveFieldsFragment
} from "../../../../_services/apollo_services";

import {
  XmlCompleteResultFragment,
  XmlCorrectionGQL,
  XmlCorrectionMutation,
  XmlErrorFragment,
  XmlExPart,
  XmlGrammarResultFragment,
  XmlSolution,
  XmlSolutionInput
} from "../xml-apollo-service";

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
export class XmlExerciseComponent
  extends ComponentWithExercise<XmlSolution, XmlSolutionInput, XmlCorrectionMutation, XmlExPart, XmlCorrectionGQL, XmlCompleteResultFragment>
  implements OnInit {

  @Input() oldPart: ToolPart;
  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() xmlExerciseContent: XmlExerciseContentSolveFieldsFragment;

  isGrammarPart: boolean;

  grammarFile: ExerciseFile;
  documentFile: ExerciseFile;

  exerciseFiles: ExerciseFile[] = [];

  constructor(xmlCorrectionGQL: XmlCorrectionGQL, apiService: ApiService, dexieService: DexieService) {
    super(xmlCorrectionGQL, apiService, dexieService);
  }

  ngOnInit() {
    const rootNode = this.xmlExerciseContent.rootNode;

    this.isGrammarPart = this.oldPart.id === 'grammar';

    const grammarFileName = `${rootNode}.dtd`;
    this.grammarFile = {
      name: grammarFileName,
      content: this.isGrammarPart ? getXmlGrammarContent(rootNode) : (this.xmlExerciseContent.xmlSampleSolutions[0].sample).grammar,
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

    this.loadOldSolutionAbstract(this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, this.oldPart)
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
    const part: XmlExPart = this.isGrammarPart ? XmlExPart.GrammarCreationXmlPart : XmlExPart.DocumentCreationXmlPart;

    this.correctAbstract(this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, part, this.oldPart);
  }

  protected getSolution(): XmlSolutionInput {
    return {
      grammar: this.grammarFile.content,
      document: this.documentFile.content
    };
  }

  get sampleSolutions(): XmlSolution[] {
    return this.xmlExerciseContent.xmlSampleSolutions.map((sample) => sample.sample);
  }

  get grammarDescription(): string {
    return this.xmlExerciseContent.grammarDescription;
  }

  get grammarResult(): XmlGrammarResultFragment | null {
    return this.resultQuery?.correctXml.grammarResult;
  }

  get documentResult(): XmlErrorFragment[] | null {
    return this.resultQuery?.correctXml.documentResult;
  }

}
