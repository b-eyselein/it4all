import {Component, Input, OnInit, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {IExercise, IExerciseFile, IWebCompleteResult, IWebExerciseContent} from '../../../../_interfaces/models';
import {ToolPart} from '../../../../_interfaces/tool';
import {ApiService} from '../../_services/api.service';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {TabsComponent} from '../../../../shared/tabs/tabs.component';
import {TabComponent} from '../../../../shared/tab/tab.component';
import {DexieService} from '../../../../_services/dexie.service';
import {DbSolution} from '../../../../_interfaces/exercise';

import 'codemirror/mode/htmlmixed/htmlmixed';

@Component({
  selector: 'it4all-web-exercise',
  templateUrl: './web-exercise.component.html'
})
export class WebExerciseComponent extends ComponentWithExercise<IWebCompleteResult> implements OnInit {

  @Input() exercise: IExercise;
  @Input() part: ToolPart;

  exerciseContent: IWebExerciseContent;
  exerciseFiles: IExerciseFile[] = [];

  @ViewChild(TabsComponent, {static: false}) tabsComponent: TabsComponent;
  @ViewChildren(TabComponent) tabComponents: QueryList<TabComponent>;

  constructor(private apiService: ApiService, private dexieService: DexieService) {
    super();
  }

  ngOnInit(): void {
    this.exerciseContent = this.exercise.content as IWebExerciseContent;
    this.exerciseFiles = this.exerciseContent.files;

    this.dexieService.getSolution<IExerciseFile[]>(this.exercise, this.part.id)
      .then((oldSolution: DbSolution<IExerciseFile[]> | undefined) => this.exerciseFiles = oldSolution ? oldSolution.solution : []);
  }

  correct(): void {
    const solution: IExerciseFile[] = this.exerciseFiles;

    this.isCorrecting = true;

    // noinspection JSIgnoredPromiseFromCall
    this.dexieService.upsertSolution<IExerciseFile[]>(this.exercise, this.part.id, solution);

    this.apiService.correctSolution<IExerciseFile[], IWebCompleteResult>(this.exercise, this.part.id, solution)
      .subscribe((result: IWebCompleteResult | undefined) => {
        this.result = result;

        console.info(JSON.stringify(this.result, null, 2));

        this.isCorrecting = false;

        this.activateCorrectionTab(this.tabsComponent, this.tabComponents);
      });
  }

  showSampleSolution(): void {
    console.error('TODO: show sample solution...');
  }

}
