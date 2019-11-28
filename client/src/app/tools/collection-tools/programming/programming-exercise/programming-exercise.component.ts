import {Component, Input, OnInit, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {DbProgrammingSolution, ProgrammingCorrectionResult} from '../programming-interfaces';
import {ApiService} from '../../_services/api.service';
import {DexieService} from '../../../../_services/dexie.service';
import {TabComponent} from '../../../../shared/tab/tab.component';
import {TabsComponent} from '../../../../shared/tabs/tabs.component';
import {ProgrammingImplementationToolPart, ProgrammingTool} from '../programming-tool';
import {ToolPart} from '../../../../_interfaces/tool';
import {IExercise, IExerciseFile, IProgSolution} from '../../../../_interfaces/models';

import 'codemirror/mode/python/python';
import {sample} from 'rxjs/operators';

@Component({
  selector: 'it4all-programming-exercise',
  templateUrl: './programming-exercise.component.html',
  styleUrls: ['./programming-exercise.component.sass']
})
export class ProgrammingExerciseComponent implements OnInit {

  @Input() exercise: IExercise;
  @Input() part: ToolPart;

  @ViewChild(TabsComponent, {static: false}) tabsComponent: TabsComponent;
  @ViewChildren(TabComponent) tabComponents: QueryList<TabComponent>;

  exerciseFiles: IExerciseFile[] = [];

  correctionRunning = false;
  result: ProgrammingCorrectionResult;

  displaySampleSolutions = false;

  correctionTabTitle = 'Korrektur';
  sampleSolutionsTabTitle = 'Musterlösungen';

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService, private dexieService: DexieService) {
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
    this.dexieService.programmingSolutions.put(solution);

    this.correctionRunning = true;

    this.apiService.correctSolution<IProgSolution, any>(this.exercise, this.part.id, solution.solution)
      .subscribe((result: ProgrammingCorrectionResult | undefined) => {
          // tslint:disable-next-line:no-console
          console.info(JSON.stringify(result, null, 2));

          this.result = result;

          this.correctionRunning = false;

          // Activate correction tab
          if (this.tabsComponent) {
            const correctionTab = this.tabComponents.toArray().find((v) => v.title === this.correctionTabTitle);
            if (correctionTab) {
              this.tabsComponent.selectTab(correctionTab);
            }
          }
        }
      );
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
