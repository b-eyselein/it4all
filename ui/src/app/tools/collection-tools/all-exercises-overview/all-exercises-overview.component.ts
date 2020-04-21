import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {distinctObjectArray, flatMapArray} from '../../../helpers';
import {
  AllExercisesOverviewGQL,
  AllExercisesOverviewQuery,
  FieldsForLinkFragment,
  TopicFragment
} from "../../../_services/apollo_services";
import {Subscription} from "rxjs";

@Component({templateUrl: './all-exercises-overview.component.html'})
export class AllExercisesOverviewComponent implements OnInit, OnDestroy {

  private sub: Subscription;
  allExercisesOverviewQuery: AllExercisesOverviewQuery;

  distinctTags: TopicFragment[];
  filteredExercises: FieldsForLinkFragment[];
  filtersActivated: Map<TopicFragment, boolean> = new Map();


  constructor(private route: ActivatedRoute, private allExercisesOverviewGQL: AllExercisesOverviewGQL) {
  }

  ngOnInit() {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');

      this.allExercisesOverviewGQL
        .watch({toolId})
        .valueChanges
        .subscribe(({data}) => {
          this.allExercisesOverviewQuery = data;

          this.filteredExercises = this.allExercisesOverviewQuery.tool.allExercises;

          this.distinctTags = distinctObjectArray(
            flatMapArray(this.allExercisesOverviewQuery.tool.allExercises, (exercises) => exercises.topics),
            (t) => t.abbreviation
          );
        });
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  toggleFilter(tag: TopicFragment): void {
    this.filtersActivated.set(tag, !this.filtersActivated.get(tag));

    const activatedFilters: TopicFragment[] = Array.from(this.filtersActivated.entries())
      .filter(([_, activated]) => activated)
      .map(([t, _]) => t);

    if (activatedFilters.length > 0) {
      this.filteredExercises = this.allExercisesOverviewQuery.tool.allExercises
        .filter((metaData) => activatedFilters.every((t) => this.exerciseHasTag(metaData, t)));
    } else {
      this.filteredExercises = this.allExercisesOverviewQuery.tool.allExercises;
    }
  }

  exerciseHasTag(exercise: FieldsForLinkFragment, tag: TopicFragment): boolean {
    return exercise.topics
      .map((t) => t.abbreviation)
      .includes(tag.abbreviation);
  }


}
