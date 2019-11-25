import {Component, Input, OnInit, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {DbProgrammingSolution, ProgrammingCorrectionResult} from '../programming-interfaces';
import {ApiService} from '../../_services/api.service';
import {ExerciseFile, IdeWorkspace} from '../../../basics';
import {DexieService} from '../../../../_services/dexie.service';
import {TabComponent} from '../../../../shared/tab/tab.component';
import {TabsComponent} from '../../../../shared/tabs/tabs.component';
import {ProgrammingImplementationToolPart, ProgrammingTool} from '../programming-tool';
import {ToolPart} from '../../../../_interfaces/tool';

import 'codemirror/mode/python/python';
import {IExercise, IExerciseCollection} from '../../../../_interfaces/models';

@Component({
  selector: 'it4all-programming-exercise',
  templateUrl: './programming-exercise.component.html',
  styleUrls: ['./programming-exercise.component.sass']
})
export class ProgrammingExerciseComponent implements OnInit {

  @Input() collection: IExerciseCollection;
  @Input() exercise: IExercise;

  @ViewChild(TabsComponent, {static: false}) tabsComponent: TabsComponent;
  @ViewChildren(TabComponent) tabComponents: QueryList<TabComponent>;

  part: ToolPart;

  exerciseFiles: ExerciseFile[] = [];

  correctionRunning = false;
  result: ProgrammingCorrectionResult;

  displaySampleSolutions = false;

  correctionTabTitle = 'Korrektur';
  sampleSolutionsTabTitle = 'Musterlösungen';

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService, private dexieService: DexieService) {
  }

  get sampleSolutionFilesList(): ExerciseFile[][] {
    return this.exercise ? this.exercise.content.sampleSolutions.map((s) => s.sample.files) : [];
  }

  ngOnInit(): void {
    console.info(JSON.stringify(this.exercise, null, 2));

    this.part = ProgrammingTool.parts.find((p) => p.id === this.route.snapshot.paramMap.get('partId'));

    if (!this.part) {
      // noinspection JSIgnoredPromiseFromCall
      this.router.navigate(['../..']);
      return;
    }

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
      collId: this.collection.id,
      exId: this.exercise.id,
      partId: this.part.id,
      solution: {
        filesNum: this.exerciseFiles.length,
        files: this.exerciseFiles
      }
    };

    // noinspection JSIgnoredPromiseFromCall
    this.dexieService.programmingSolutions.put(solution);

    this.correctionRunning = true;

    this.apiService.correctSolution<IdeWorkspace, any>(
      ProgrammingTool.id, this.collection.id, this.exercise.id, this.part.id, solution.solution)
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
        this.tabsComponent.selectTab(sampleSolutionsTab);
      }
    }
  }

}
