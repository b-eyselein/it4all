import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Tool} from '../../../_interfaces/tool';
import {collectionTools} from '../collection-tools-list';
import {distinctObjectArray, flatMapArray} from '../../../helpers';
import {
  AllExercisesOverviewGQL,
  AllExercisesOverviewQuery,
  FieldsForLinkFragment,
  TagFragment
} from "../../../_services/apollo_services";

@Component({templateUrl: './all-exercises-overview.component.html'})
export class AllExercisesOverviewComponent implements OnInit {

  tool: Tool;

  allExercisesOverviewQuery: AllExercisesOverviewQuery;

  distinctTags: TagFragment[];
  filteredExerciseMetaData: FieldsForLinkFragment[];
  filtersActivated: Map<TagFragment, boolean> = new Map();

  constructor(private route: ActivatedRoute, private allExercisesOverviewGQL: AllExercisesOverviewGQL) {
  }

  ngOnInit() {
    this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');

      this.tool = collectionTools.find((t) => t.id === toolId);

      this.allExercisesOverviewGQL
        .watch({toolId: this.tool.id})
        .valueChanges
        .subscribe(({data}) => {
          this.allExercisesOverviewQuery = data;

          this.filteredExerciseMetaData = this.allExercisesOverviewQuery.tool.allExerciseMetaData;

          this.distinctTags = distinctObjectArray(
            flatMapArray(this.allExercisesOverviewQuery.tool.allExerciseMetaData, (exerciseMetaData) => exerciseMetaData.tags),
            (t) => t.abbreviation
          );
        });
    });
  }


  toggleFilter(tag: TagFragment): void {
    this.filtersActivated.set(tag, !this.filtersActivated.get(tag));

    const activatedFilters: TagFragment[] = Array.from(this.filtersActivated.entries())
      .filter(([_, activated]) => activated)
      .map(([t, _]) => t);

    if (activatedFilters.length > 0) {
      this.filteredExerciseMetaData = this.allExercisesOverviewQuery.tool.allExerciseMetaData
        .filter((metaData) => activatedFilters.every((t) => this.exerciseHasTag(metaData, t)));
    } else {
      this.filteredExerciseMetaData = this.allExercisesOverviewQuery.tool.allExerciseMetaData;
    }
  }

  exerciseHasTag(exercise: FieldsForLinkFragment, tag: TagFragment): boolean {
    return exercise.tags
      .map((t) => t.abbreviation)
      .includes(tag.abbreviation);
  }


}
