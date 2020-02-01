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

@Component({
  selector: 'it4all-xml-exercise',
  templateUrl: './xml-exercise.component.html'
})
export class XmlExerciseComponent extends ComponentWithExercise<IXmlSolution, IXmlCompleteResult> implements OnInit {

  @Input() exercise: IExercise;
  @Input() part: ToolPart;

  isGrammarPart: boolean;

  exerciseContent: IXmlExerciseContent;

  grammarFile: IExerciseFile;
  documentFile: IExerciseFile;

  exerciseFiles: IExerciseFile[] = [];

  constructor(apiService: ApiService, dexieService: DexieService) {
    super(apiService, dexieService);
  }

  ngOnInit() {
    this.exerciseContent = this.exercise.content as IXmlExerciseContent;

    this.isGrammarPart = this.part.id === 'grammar';

    const grammarFileName = `${this.exerciseContent.rootNode}.dtd`;
    this.grammarFile = {
      name: grammarFileName,
      content: this.isGrammarPart ?
        `<!ELEMENT ${this.exerciseContent.rootNode} (EMPTY)>`
        : (this.exerciseContent.sampleSolutions[0].sample as IXmlSolution).grammar,
      fileType: 'dtd',
      editable: this.isGrammarPart,
      resourcePath: grammarFileName
    };

    const documentFileName = `${this.exerciseContent.rootNode}.xml`;
    this.documentFile = {
      name: documentFileName,
      content: `
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE ${this.exerciseContent.rootNode} SYSTEM "${this.exerciseContent.rootNode}.dtd">
<${this.exerciseContent.rootNode}>
</${this.exerciseContent.rootNode}>`.trim(),
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

  showSampleSolution(): void {
    console.error('TODO: show sample sol...');
  }

  correct(): void {
    this.correctAbstract(this.exercise, this.part);
  }

  protected getSolution(): IXmlSolution {
    return {
      grammar: this.grammarFile.content,
      document: this.documentFile.content
    };
  }

}
