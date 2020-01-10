import {Component, Input, OnInit} from '@angular/core';
import {IExercise, IExerciseFile, IXmlCompleteResult, IXmlExerciseContent, IXmlSolution} from '../../../../_interfaces/models';
import {ToolPart} from '../../../../_interfaces/tool';

import 'codemirror/mode/dtd/dtd';
import 'codemirror/mode/xml/xml';
import {ApiService} from '../../_services/api.service';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';


@Component({
  selector: 'it4all-xml-exercise',
  templateUrl: './xml-exercise.component.html'
})
export class XmlExerciseComponent extends ComponentWithExercise<IXmlCompleteResult> implements OnInit {

  @Input() exercise: IExercise;
  @Input() part: ToolPart;

  exerciseContent: IXmlExerciseContent;

  grammarFile: IExerciseFile;
  documentFile: IExerciseFile;

  exerciseFiles: IExerciseFile[] = [];


  constructor(private apiService: ApiService) {
    super();
  }

  ngOnInit() {
    this.exerciseContent = this.exercise.content as IXmlExerciseContent;

    const isGrammarPart = this.part.id === 'grammar';

    const grammarFileName = `${this.exerciseContent.rootNode}.dtd`;
    this.grammarFile = {
      name: grammarFileName, content: `<!ELEMENT ${this.exerciseContent.rootNode} (EMPTY)>`,
      fileType: 'dtd', editable: isGrammarPart, resourcePath: grammarFileName
    };

    const documentFileName = `${this.exerciseContent.rootNode}.xml`;
    this.documentFile = {
      name: documentFileName, content: '', fileType: 'xml',
      editable: !isGrammarPart, resourcePath: documentFileName
    };

    this.exerciseFiles = [this.grammarFile, this.documentFile];
  }

  showSampleSolution(): void {
    console.error('TODO: show sample sol...');
  }

  correct(): void {
    this.isCorrecting = true;

    console.error('TODO: correct!');
    const solution: IXmlSolution = {grammar: this.grammarFile.content, document: this.documentFile.content};

    this.apiService.correctSolution<IXmlSolution, IXmlCompleteResult>(this.exercise, this.part.id, solution)
      .subscribe((result) => {
        this.isCorrecting = false;

        this.result = result;

        console.info(JSON.stringify(result, null, 2));
      });
  }

}
