import {TabsComponent} from '../../../shared/tabs/tabs.component';
import {Directive, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {TabComponent} from '../../../shared/tab/tab.component';
import {DexieService} from '../../../_services/dexie.service';
import {ExerciseSolveFieldsFragment} from '../../../_services/apollo_services';
import {Mutation} from 'apollo-angular';

@Directive()
export abstract class ComponentWithExercise<SolutionType, SolutionInputType, MutationQueryType, PartType,
  MutationGQL extends Mutation<MutationQueryType, { exId: number, collId: number, part: PartType, solution: SolutionInputType }>> {

  readonly exerciseTextTabTitle = 'Aufgabenstellung';
  readonly correctionTabTitle = 'Korrektur';
  readonly sampleSolutionsTabTitle = 'Musterlösungen';

  isCorrecting = false;

  resultQuery: MutationQueryType;

  displaySampleSolutions = false;

  @ViewChild(TabsComponent) tabsComponent: TabsComponent;
  @ViewChildren(TabComponent) tabComponents: QueryList<TabComponent>;

  protected constructor(protected mutationGQL: MutationGQL, protected dexieService: DexieService) {
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

  protected correctAbstract(exerciseFragment: ExerciseSolveFieldsFragment, part: PartType, partId: string): void {
    this.isCorrecting = true;

    const solution: SolutionInputType | undefined = this.getSolution();

    if (!solution) {
      alert('Ihre Lösung war nicht valide!');
      return;
    }

    const mutationQueryVars = {
      exId: exerciseFragment.exerciseId, collId: exerciseFragment.collectionId, part, solution
    };

    // noinspection JSIgnoredPromiseFromCall
    this.dexieService.upsertSolution<SolutionInputType>(exerciseFragment, partId, solution);

    this.mutationGQL
      .mutate(mutationQueryVars)
      .subscribe(
        ({data}) => {
          this.resultQuery = data;
          this.activateCorrectionTab();
          this.isCorrecting = false;
        },
        (error) => {
          console.error('There has been an graphQL error:', error);
          this.isCorrecting = false;
        }
      );
  }

  protected loadOldSolutionAbstract(
    exerciseFragment: ExerciseSolveFieldsFragment,
    partId: string,
    setOldSolution: (oldSol: SolutionInputType) => void
  ) {
    return this.dexieService.getSolution<SolutionInputType>(exerciseFragment, partId)
      .then((dbSol) => {
        if (dbSol) {
          setOldSolution(dbSol.solution);
        }
      });
  }

  protected abstract getSolution(): SolutionInputType | undefined;

  protected abstract get sampleSolutions(): SolutionType[];

}
