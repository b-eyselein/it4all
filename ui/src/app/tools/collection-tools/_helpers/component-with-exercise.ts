import {TabsComponent} from '../../../shared/tabs/tabs.component';
import {Directive, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {TabComponent} from '../../../shared/tab/tab.component';
import {DexieService} from '../../../_services/dexie.service';
import {ApiService} from '../_services/api.service';
import {ToolPart} from '../../../_interfaces/tool';

@Directive()
export abstract class ComponentWithExercise<SolutionType, ResultType> {

  isCorrecting = false;

  result: ResultType | undefined;

  displaySampleSolutions = false;

  @ViewChild(TabsComponent) tabsComponent: TabsComponent;
  @ViewChildren(TabComponent) tabComponents: QueryList<TabComponent>;

  readonly exerciseTextTabTitle = 'Aufgabenstellung';
  readonly correctionTabTitle = 'Korrektur';
  readonly sampleSolutionsTabTitle = 'Musterlösungen';

  protected constructor(protected apiService: ApiService, protected dexieService: DexieService) {
  }

  toggleSampleSolutions(): void {
    this.displaySampleSolutions = !this.displaySampleSolutions;
  }

  protected activateCorrectionTab(): void {
    const correctionTab = this.tabComponents.toArray().find((tab) => tab.title === this.correctionTabTitle);

    if (correctionTab) {
      this.tabsComponent.selectTab(correctionTab);
    }
  }

  protected correctAbstract(exId: number, collId: number, toolId: string, part: ToolPart, logResult: boolean = false, logSolution: boolean = false): void {
    this.isCorrecting = true;

    const solution: SolutionType = this.getSolution();

    if (logSolution) {
      console.info(JSON.stringify(solution, null, 2));
    }

    // noinspection JSIgnoredPromiseFromCall
    this.dexieService.upsertSolution<SolutionType>(exId, collId, toolId, part.id, solution);

    this.apiService.correctSolution<SolutionType, ResultType>(exId, collId, toolId, part.id, solution)
      .subscribe((result: ResultType | undefined) => {
        this.isCorrecting = false;
        this.result = result;
        this.activateCorrectionTab();

        if (logResult) {
          console.log(JSON.stringify(result, null, 2));
        }
      });
  }

  protected loadOldSolutionAbstract(exId: number, collId: number, toolId: string, part: ToolPart): Promise<SolutionType | undefined> {
    return this.dexieService.getSolution<SolutionType>(exId, collId, toolId, part.id)
      .then((dbSol) => dbSol ? dbSol.solution : undefined);
  }

  protected abstract getSolution(): SolutionType;

  abstract get sampleSolutions(): SolutionType[];

}
