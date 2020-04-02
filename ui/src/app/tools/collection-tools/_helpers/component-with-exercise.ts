import {TabsComponent} from '../../../shared/tabs/tabs.component';
import {Directive, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {TabComponent} from '../../../shared/tab/tab.component';
import {DexieService} from '../../../_services/dexie.service';
import {ApiService} from '../_services/api.service';
import {ToolPart} from '../../../_interfaces/tool';
import * as Apollo from 'apollo-angular';

@Directive()
export abstract class ComponentWithExercise<SolutionType,
  SolutionInputType,
  MutationQueryType,
  PartType,
  MutationGQL extends Apollo.Mutation<MutationQueryType, { exId: number, collId: number, part: PartType, solution: SolutionInputType }>,
  ResultType> {

  readonly exerciseTextTabTitle = 'Aufgabenstellung';
  readonly correctionTabTitle = 'Korrektur';
  readonly sampleSolutionsTabTitle = 'Musterlösungen';


  isCorrecting = false;

  /**
   * @deprecated
   */
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

    const solution: SolutionInputType | undefined = this.getSolution();

    if (!solution) {
      alert('Ihre Lösung war nicht valide!');
      return;
    }

    const mutationQueryVars = {exId, collId, part, solution};

    // noinspection JSIgnoredPromiseFromCall
    this.dexieService.upsertSolution<SolutionInputType>(exId, collId, toolId, oldPart.id, solution);


    this.mutationGQL
      .mutate(mutationQueryVars)
      .subscribe(({data}) => {
        this.resultQuery = data;
        this.activateCorrectionTab();
        this.isCorrecting = false;
      });
  }

  protected loadOldSolutionAbstract(exId: number, collId: number, toolId: string, part: ToolPart): Promise<SolutionInputType | undefined> {
    return this.dexieService.getSolution<SolutionInputType>(exId, collId, toolId, part.id)
      .then((dbSol) => dbSol ? dbSol.solution : undefined);
  }

  protected abstract getSolution(): SolutionInputType | undefined;

  protected abstract get sampleSolutions(): SolutionType[];

}
