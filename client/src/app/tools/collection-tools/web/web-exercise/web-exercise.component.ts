import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {IExercise, IExerciseFile, IWebExerciseContent} from '../../../../_interfaces/models';
import {ToolPart} from '../../../../_interfaces/tool';
import {ApiService} from '../../_services/api.service';
import {ExerciseFilesEditorComponent} from '../../_components/exercise-files-editor/exercise-files-editor.component';
import {IdeWorkspace} from '../../../basics';

import 'codemirror/mode/htmlmixed/htmlmixed';


type SolType = IExerciseFile[];

@Component({
  selector: 'it4all-web-exercise',
  templateUrl: './web-exercise.component.html'
})
export class WebExerciseComponent implements OnInit {

  @Input() exercise: IExercise;
  @Input() part: ToolPart;

  exerciseContent: IWebExerciseContent;

  result: any;

  @ViewChild(ExerciseFilesEditorComponent, {static: true}) editor: ExerciseFilesEditorComponent;

  constructor(private apiService: ApiService) {
  }

  ngOnInit(): void {
    this.exerciseContent = this.exercise.content as IWebExerciseContent;
  }

  correct(): void {
    const files = this.editor.exerciseFiles;

    const solution: IdeWorkspace = {
      filesNum: files.length, files
    };

    this.apiService.correctSolution<IdeWorkspace, SolType>(this.exercise, this.part.id, solution)
      .subscribe((result) => this.result = result);
  }

  showSampleSolution(): void {
    console.error('TODO: show sample solution...');
  }

}
