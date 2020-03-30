import {TabsComponent} from '../../../shared/tabs/tabs.component';
import {Directive, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {TabComponent} from '../../../shared/tab/tab.component';
import {DexieService} from '../../../_services/dexie.service';
import {ApiService} from '../_services/api.service';
import {ToolPart} from '../../../_interfaces/tool';
import * as Apollo from 'apollo-angular';

@Directive()
export abstract class ComponentWithExercise<SolutionType, MutationQueryType, PartType, MutationGQL extends Apollo.Mutation<MutationQueryType, { exId: number, collId: number, part: PartType, solution: SolutionType }>, ResultType> {

  readonly exerciseTextTabTitle = 'Aufgabenstellung';
  readonly correctionTabTitle = 'Korrektur';
  readonly sampleSolutionsTabTitle = 'Musterlösungen';


  isCorrecting = false;

  result: ResultType | undefined;
  resultQuery: MutationQueryType;

  displaySampleSolutions = false;

  @ViewChild(TabsComponent) tabsComponent: TabsComponent;
  @ViewChildren(TabComponent) tabComponents: QueryList<TabComponent>;


  protected constructor(protected mutationGQL: MutationGQL, protected apiService: ApiService, protected dexieService: DexieService) {
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

  protected correctAbstract(exId: number, collId: number, toolId: string, part: PartType, oldPart: ToolPart): void {
    this.isCorrecting = true;

    const solution: SolutionType = this.getSolution();

    const mutationQueryVars = {exId, collId, part, solution};

    // noinspection JSIgnoredPromiseFromCall
    this.dexieService.upsertSolution<SolutionType>(exId, collId, toolId, oldPart.id, solution);


    this.mutationGQL
      .mutate(mutationQueryVars)
      .subscribe(({data}) => {
        this.resultQuery = data;

        console.info(JSON.stringify(this.resultQuery, null, 2));
      });
  }

  protected loadOldSolutionAbstract(exId: number, collId: number, toolId: string, part: ToolPart): Promise<SolutionType | undefined> {
    return this.dexieService.getSolution<SolutionType>(exId, collId, toolId, part.id)
      .then((dbSol) => dbSol ? dbSol.solution : undefined);
  }

  protected abstract getSolution(): SolutionType;

  protected abstract get sampleSolutions(): SolutionType[];

}
