import {Component, Input, OnInit} from '@angular/core';
import {IExercise} from '../../../../_interfaces/models';
import {ToolPart} from '../../../../_interfaces/tool';
import {ApiService} from '../../_services/api.service';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {DexieService} from '../../../../_services/dexie.service';
import {DbSolution} from '../../../../_interfaces/exercise';
import {IExerciseFile, IWebCompleteResult, IWebExerciseContent} from '../web-interfaces';

import 'codemirror/mode/htmlmixed/htmlmixed';

@Component({
  selector: 'it4all-web-exercise',
  templateUrl: './web-exercise.component.html'
})
export class WebExerciseComponent extends ComponentWithExercise<IExerciseFile[], IWebCompleteResult> implements OnInit {

  @Input() exercise: IExercise;
  @Input() part: ToolPart;

  exerciseContent: IWebExerciseContent;
  exerciseFiles: IExerciseFile[] = [];

  constructor(apiService: ApiService, dexieService: DexieService) {
    super(apiService, dexieService);
  }

  ngOnInit(): void {
    this.exerciseContent = this.exercise.content as IWebExerciseContent;
    this.exerciseFiles = this.exerciseContent.files;

    this.dexieService.getSolution<IExerciseFile[]>(this.exercise, this.part.id)
      .then((oldSolution: DbSolution<IExerciseFile[]> | undefined) => this.exerciseFiles = oldSolution ? oldSolution.solution : []);
  }

  correct(): void {
    this.correctAbstract(this.exercise, this.part);
  }

  protected getSolution(): IExerciseFile[] {
    return this.exerciseFiles;
  }

  get sampleSolutions(): IExerciseFile[][] {
    const exContent = this.exercise.content as IWebExerciseContent;
    return exContent.sampleSolutions.map((sample) => sample.sample);
  }

}
