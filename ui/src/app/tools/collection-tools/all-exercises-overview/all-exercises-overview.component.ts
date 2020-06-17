import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {distinctObjectArray, flatMapArray} from '../../../helpers';
import {
  AllExercisesOverviewGQL,
  AllExercisesOverviewQuery,
  FieldsForLinkFragment,
  TopicFragment,
  TopicWithLevelFragment
} from '../../../_services/apollo_services';
import {Subscription} from 'rxjs';
import {AuthenticationService} from '../../../_services/authentication.service';

@Component({templateUrl: './all-exercises-overview.component.html'})
export class AllExercisesOverviewComponent implements OnInit, OnDestroy {

  private sub: Subscription;
  allExercisesOverviewQuery: AllExercisesOverviewQuery;

  distinctTopicWithLevels: TopicWithLevelFragment[];
  filteredExercises: FieldsForLinkFragment[];
  filtersActivated: Map<TopicFragment, boolean> = new Map();

  constructor(
    private authenticationService: AuthenticationService,
    private route: ActivatedRoute,
    private allExercisesOverviewGQL: AllExercisesOverviewGQL
  ) {
  }

  ngOnInit() {
    const userJwt = this.authenticationService.currentUserValue.jwt;

    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');

      this.allExercisesOverviewGQL
        .watch({userJwt, toolId})
        .valueChanges
        .subscribe(({data}) => {
          this.allExercisesOverviewQuery = data;

          this.filteredExercises = this.allExercisesOverviewQuery.me.tool.allExercises;

          this.distinctTopicWithLevels = distinctObjectArray(
            flatMapArray(
              this.allExercisesOverviewQuery.me.tool.allExercises,
              (exercises) => exercises.topicsWithLevels
            ),
            (t) => t.topic.abbreviation
          );
        });
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  toggleFilter(topicWithLevel: TopicWithLevelFragment): void {
    this.filtersActivated.set(topicWithLevel.topic, !this.filtersActivated.get(topicWithLevel.topic));

    const activatedFilters: TopicFragment[] = Array.from(this.filtersActivated.entries())
      .filter(([_, activated]) => activated)
      .map(([t, _]) => t);

    if (activatedFilters.length > 0) {
      this.filteredExercises = this.allExercisesOverviewQuery.me.tool.allExercises
        .filter((metaData) => activatedFilters.every((t) => this.exerciseHasTag(metaData, t)));
    } else {
      this.filteredExercises = this.allExercisesOverviewQuery.me.tool.allExercises;
    }
  }

  exerciseHasTag(exercise: FieldsForLinkFragment, tag: TopicFragment): boolean {
    return exercise.topicsWithLevels
      .map((t) => t.topic.abbreviation)
      .includes(tag.abbreviation);
  }


}
