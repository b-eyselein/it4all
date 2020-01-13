import {Component, Input, OnInit} from '@angular/core';
import {IExercise, IExerciseFile, IXmlCompleteResult, IXmlExerciseContent, IXmlSolution} from '../../../../_interfaces/models';
import {ToolPart} from '../../../../_interfaces/tool';
import {ApiService} from '../../_services/api.service';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {DexieService} from '../../../../_services/dexie.service';

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
      name: grammarFileName, content: `<!ELEMENT ${this.exerciseContent.rootNode} (EMPTY)>`,
      fileType: 'dtd', editable: this.isGrammarPart, resourcePath: grammarFileName
    };

    const documentFileName = `${this.exerciseContent.rootNode}.xml`;
    this.documentFile = {
      name: documentFileName, content: '', fileType: 'xml',
      editable: !this.isGrammarPart, resourcePath: documentFileName
    };

    this.exerciseFiles = [this.grammarFile, this.documentFile];
  }

  showSampleSolution(): void {
    console.error('TODO: show sample sol...');
  }

  correct(): void {
    this.correctAbstract(this.exercise, this.part, true);
  }

  protected getSolution(): IXmlSolution {
    return {
      grammar: this.grammarFile.content,
      document: this.documentFile.content
    };
  }

}
