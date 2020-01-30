import {TabsComponent} from '../../../shared/tabs/tabs.component';
import {QueryList, ViewChild, ViewChildren} from '@angular/core';
import {TabComponent} from '../../../shared/tab/tab.component';
import {IExercise} from '../../../_interfaces/models';
import {DexieService} from '../../../_services/dexie.service';
import {ApiService} from '../_services/api.service';
import {ToolPart} from '../../../_interfaces/tool';

export abstract class ComponentWithExercise<SolutionType, ResultType> {

  // FIXME: Use?
  //  exercise: IExercise;
  //  part: ToolPart;

  isCorrecting = false;

  result: ResultType | undefined;

  displaySampleSolutions = false;

  @ViewChild(TabsComponent, {static: false}) tabsComponent: TabsComponent;
  @ViewChildren(TabComponent) tabComponents: QueryList<TabComponent>;


  readonly exerciseTextTabTitle = 'Aufgabenstellung';
  readonly correctionTabTitle = 'Korrektur';
  readonly sampleSolutionsTabTitle = 'Musterlösungen';

  protected constructor(protected apiService: ApiService, protected dexieService: DexieService) {
  }

  protected activateCorrectionTab(): void {
    const correctionTab = this.tabComponents.toArray().find((v) => v.title === this.correctionTabTitle);

    if (correctionTab) {
      this.tabsComponent.selectTab(correctionTab);
    }
  }

  protected correctAbstract(exercise: IExercise, part: ToolPart, logResult: boolean = false, logSolution: boolean = false): void {
    this.isCorrecting = true;

    const solution: SolutionType = this.getSolution();

    if (logSolution) {
      console.info(JSON.stringify(solution, null, 2));
    }

    // noinspection JSIgnoredPromiseFromCall
    this.dexieService.upsertSolution<SolutionType>(exercise, part.id, solution);

    this.apiService.correctSolution<SolutionType, ResultType>(exercise, part.id, solution)
      .subscribe((result: ResultType | undefined) => {
        this.isCorrecting = false;
        this.result = result;
        this.activateCorrectionTab();

        if (logResult) {
          console.log(JSON.stringify(result, null, 2));
        }
      });
  }

  protected loadOldSolutionAbstract(exercise: IExercise, part: ToolPart): Promise<SolutionType | undefined> {
    return this.dexieService.getSolution<SolutionType>(exercise, part.id)
      .then((dbSol) => dbSol ? dbSol.solution : undefined);
  }

  protected abstract getSolution(): SolutionType;

}
