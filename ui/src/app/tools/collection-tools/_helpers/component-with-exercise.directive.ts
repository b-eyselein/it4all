import {Mutation} from 'apollo-angular';
import {TabsComponent} from '../../../shared/tabs/tabs.component';
import {Directive, ViewChild} from '@angular/core';
import {DexieService} from '../../../_services/dexie.service';
import {ExerciseSolveFieldsFragment} from '../../../_services/apollo_services';

import {CorrectionHelpers} from "./correction-helpers";

@Directive()
export abstract class ComponentWithExerciseDirective<SolutionInputType, MutationQueryType, MutationVariablesType>
  extends CorrectionHelpers {

  isCorrecting = false;

  resultQuery: MutationQueryType | undefined;

  @ViewChild(TabsComponent) tabsComponent: TabsComponent | undefined;

  protected constructor(protected mutationGQL: Mutation<MutationQueryType, MutationVariablesType>, protected dexieService: DexieService) {
    super();
  }

  protected activateCorrectionTab(): void {
    if (this.tabsComponent) {
      this.tabsComponent.selectTabByTitle(this.correctionTabTitle);
    }
  }

  protected correctAbstract(exerciseFragment: ExerciseSolveFieldsFragment, partId: string, onComplete?: () => void): void {
    const solution: SolutionInputType | undefined = this.getSolution();

    if (!solution) {
      alert('Ihre Lösung war nicht valide!');
      return;
    }

    this.isCorrecting = true;

    // noinspection JSIgnoredPromiseFromCall
    this.dexieService.upsertSolution<SolutionInputType>(exerciseFragment, partId, solution);

    this.mutationGQL
      .mutate(this.getMutationQueryVariables())
      .subscribe(
        ({data}) => {
          this.resultQuery = data;
          this.activateCorrectionTab();
        },
        (error) => console.error('There has been an graphQL error:', error),
        () => {
          this.isCorrecting = false;
          if (onComplete) {
            onComplete();
          }
        }
      );
  }

  protected loadOldSolutionAbstract(
    exerciseFragment: ExerciseSolveFieldsFragment,
    partId: string,
    setOldSolution: (oldSol: SolutionInputType) => void,
    onNoSolution: () => void = () => void 0
  ) {
    return this.dexieService.getSolution<SolutionInputType>(exerciseFragment, partId)
      .then((dbSol) => {
        if (dbSol) {
          setOldSolution(dbSol.solution);
        } else {
          onNoSolution();
        }
      });
  }

  protected abstract getSolution(): SolutionInputType | undefined;

  protected abstract getMutationQueryVariables(): MutationVariablesType;

}
