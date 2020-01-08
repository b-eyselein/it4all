import {Component, Input, OnInit, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {DbProgrammingSolution, ProgrammingCorrectionResult} from '../programming-interfaces';
import {ApiService} from '../../_services/api.service';
import {DexieService} from '../../../../_services/dexie.service';
import {TabComponent} from '../../../../shared/tab/tab.component';
import {TabsComponent} from '../../../../shared/tabs/tabs.component';
import {ProgrammingImplementationToolPart, ProgrammingTool} from '../programming-tool';
import {ToolPart} from '../../../../_interfaces/tool';
import {IExercise, IExerciseFile, IProgSolution} from '../../../../_interfaces/models';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';

import 'codemirror/mode/python/python';

@Component({
  selector: 'it4all-programming-exercise',
  templateUrl: './programming-exercise.component.html',
  styleUrls: ['./programming-exercise.component.sass']
})
export class ProgrammingExerciseComponent extends ComponentWithExercise<ProgrammingCorrectionResult> implements OnInit {

  @Input() exercise: IExercise;
  @Input() part: ToolPart;

  // exerciseContent: IProgExerciseContent ;
  exerciseFiles: IExerciseFile[] = [];

  @ViewChild(TabsComponent, {static: false}) tabsComponent: TabsComponent;
  @ViewChildren(TabComponent) tabComponents: QueryList<TabComponent>;

  constructor(private apiService: ApiService, private dexieService: DexieService) {
    super();
  }

  get sampleSolutionFilesList(): IExerciseFile[][] {
    return this.exercise ? this.exercise.content.sampleSolutions.map((s) => s.sample.files) : [];
  }

  ngOnInit(): void {
    this.exerciseFiles = (this.part === ProgrammingImplementationToolPart) ?
      this.exercise.content.implementationPart.files :
      this.exercise.content.unitTestPart.unitTestFiles;

    this.loadOldSolution();
  }


  loadOldSolution(): void {
    // TODO: deactivated for now...
    // this.dexieService.programmingSolutions.get([this.collection.id, this.exercise.id])
    //   .then((oldSolution: DbProgrammingSolution | undefined) => {
    //     if (oldSolution) {
    //       // FIXME: editor does not update...
    //       this.exerciseFiles = oldSolution.solution.files;
    //     }
    //   });
  }

  correct(): void {
    const solution: DbProgrammingSolution = {
      toolId: ProgrammingTool.id,
      collId: this.exercise.collectionId,
      exId: this.exercise.id,
      partId: this.part.id,
      solution: {
        files: this.exerciseFiles, testData: []
      }
    };

    // noinspection JSIgnoredPromiseFromCall
    this.dexieService.upsertSolution<IProgSolution>(this.exercise, this.part.id, solution.solution);

    this.isCorrecting = true;

    this.apiService.correctSolution<IProgSolution, any>(this.exercise, this.part.id, solution.solution)
      .subscribe((result: ProgrammingCorrectionResult | undefined) => {
        // console.info(JSON.stringify(result, null, 2));

        this.result = result;

        this.isCorrecting = false;

        this.activateCorrectionTab(this.tabsComponent, this.tabComponents);
      });
  }


  showSampleSolution(): void {
    this.displaySampleSolutions = true;

    if (this.tabsComponent) {
      const sampleSolutionsTab = this.tabComponents.toArray().find((t) => t.title === this.sampleSolutionsTabTitle);
      if (sampleSolutionsTab) {
        sampleSolutionsTab.selectable = true;
        this.tabsComponent.selectTab(sampleSolutionsTab);
      }
    }
  }

}
