import {Component, Input, OnInit, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {ProgrammingCorrectionResult} from '../programming-interfaces';
import {ApiService} from '../../_services/api.service';
import {DexieService} from '../../../../_services/dexie.service';
import {TabComponent} from '../../../../shared/tab/tab.component';
import {TabsComponent} from '../../../../shared/tabs/tabs.component';
import {ProgrammingImplementationToolPart} from '../programming-tool';
import {ToolPart} from '../../../../_interfaces/tool';
import {IExercise, IExerciseFile, IProgSolution} from '../../../../_interfaces/models';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';

import 'codemirror/mode/python/python';

@Component({
  selector: 'it4all-programming-exercise',
  templateUrl: './programming-exercise.component.html',
  styleUrls: ['./programming-exercise.component.sass']
})
export class ProgrammingExerciseComponent extends ComponentWithExercise<IProgSolution, ProgrammingCorrectionResult> implements OnInit {

  @Input() exercise: IExercise;
  @Input() part: ToolPart;

  // exerciseContent: IProgExerciseContent ;
  exerciseFiles: IExerciseFile[] = [];

  @ViewChild(TabsComponent, {static: false}) tabsComponent: TabsComponent;
  @ViewChildren(TabComponent) tabComponents: QueryList<TabComponent>;

  constructor(apiService: ApiService, dexieService: DexieService) {
    super(apiService, dexieService);
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
    const maybeOldSol: Promise<IProgSolution | undefined> = this.loadOldSolutionAbstract(this.exercise, this.part);

    // TODO: deactivated for now...
    // this.dexieService.programmingSolutions.get([this.collection.id, this.exercise.id])
    //   .then((oldSolution: DbProgrammingSolution | undefined) => {
    //     if (oldSolution) {
    //       // FIXME: editor does not update...
    //       this.exerciseFiles = oldSolution.solution.files;
    //     }
    //   });
  }

  protected getSolution(): IProgSolution {
    return {
      files: this.exerciseFiles,
      testData: []
    };
  }

  correct(): void {
    this.correctAbstract(this.exercise, this.part);
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
