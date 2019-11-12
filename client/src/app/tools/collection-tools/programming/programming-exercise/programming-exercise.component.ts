import {Component, OnInit, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {ExerciseCollection, Tool, ToolPart} from '../../../../_interfaces/tool';
import {ActivatedRoute, Router} from '@angular/router';
import {DbProgrammingSolution, ProgrammingCorrectionResult, ProgrammingExercise} from '../programming-interfaces';
import {ApiService} from '../../../../_services/api.service';

import 'codemirror/mode/python/python';
import {ExerciseFile, IdeWorkspace} from '../../../basics';
import {DexieService} from '../../../../_services/dexie.service';
import {TabComponent} from '../../../../_component_helpers/tab/tab.component';
import {TabsComponent} from '../../../../_component_helpers/tabs/tabs.component';
import {ProgrammingImplementationToolPart, ProgrammingTool} from '../../programming-tool';

@Component({templateUrl: './programming-exercise.component.html', styleUrls: ['./programming-exercise.component.sass']})
export class ProgrammingExerciseComponent implements OnInit {

  @ViewChild(TabsComponent, {static: false}) tabsComponent: TabsComponent;
  @ViewChildren(TabComponent) tabComponents: QueryList<TabComponent>;

  readonly tool: Tool = ProgrammingTool;
  collection: ExerciseCollection;
  exercise: ProgrammingExercise;
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
    return this.exercise ? this.exercise.sampleSolutions.map((s) => s.sample.files) : [];
  }

  ngOnInit() {
    const collId: number = parseInt(this.route.snapshot.paramMap.get('collId'), 10);
    const exId: number = parseInt(this.route.snapshot.paramMap.get('exId'), 10);
    const partStr: string = this.route.snapshot.paramMap.get('partId');

    this.part = this.tool.parts.find((p) => p.id === partStr);

    if (!this.part) {
      // noinspection JSIgnoredPromiseFromCall
      this.router.navigate(['/tools', this.tool.id, 'collections', collId]);
      return;
    }

    this.apiService.getCollection(this.tool.id, collId)
      .subscribe((coll: ExerciseCollection | undefined) => {
        if (coll) {
          this.collection = coll;
          this.updateExercise(exId);
        } else {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['/tools', this.tool.id]);
        }
      });
  }

  updateExercise(exId: number) {
    this.apiService.getExercise<ProgrammingExercise | undefined>(this.tool.id, this.collection.id, exId)
      .subscribe((ex: ProgrammingExercise | undefined) => {
        if (ex) {
          this.exercise = ex;
          this.exerciseFiles = (this.part === ProgrammingImplementationToolPart) ?
            ex.implementationPart.files : ex.unitTestPart.unitTestFiles;
          this.loadOldSolution();
        } else {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['/tools', this.tool.id, 'collections', this.collection.id]);
        }
      });
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
      collId: this.collection.id,
      exId: this.exercise.id,
      solution: {
        filesNum: this.exerciseFiles.length,
        files: this.exerciseFiles
      }
    };

    // noinspection JSIgnoredPromiseFromCall
    this.dexieService.programmingSolutions.put(solution);

    this.correctionRunning = true;

    this.apiService.correctSolution<IdeWorkspace, any>(this.tool.id, this.collection.id, this.exercise.id, this.part.id, solution.solution)
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
