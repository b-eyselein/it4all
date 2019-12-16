import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ApiService} from '../_services/api.service';
import {IExerciseMetaData, IExTag} from '../../../_interfaces/models';
import {Tool} from '../../../_interfaces/tool';
import {collectionTools} from '../collection-tools-list';
import {distinctObjectArray, flatMapArray} from '../../../helpers';

@Component({templateUrl: './all-exercises-overview.component.html'})
export class AllExercisesOverviewComponent implements OnInit {

  tool: Tool;

  distinctTags: IExTag[];

  private exerciseMetaData: IExerciseMetaData[];

  filteredExerciseMetaData: IExerciseMetaData[];

  filtersActivated: Map<IExTag, boolean> = new Map<IExTag, boolean>();

  constructor(private route: ActivatedRoute, private apiService: ApiService) {
    const toolId = this.route.snapshot.paramMap.get('toolId');
    this.tool = collectionTools.find((t) => t.name === toolId);
  }

  ngOnInit() {
    this.apiService.getExerciseMetaDataForTool(this.tool.id)
      .subscribe((exerciseMetaData) => {
        this.distinctTags = distinctObjectArray(
          flatMapArray(exerciseMetaData, (md) => md.tags),
          (tag) => tag.abbreviation
        );

        for (const tag of this.distinctTags) {
          this.filtersActivated.set(tag, false);
        }

        this.exerciseMetaData = exerciseMetaData.sort((ex1, ex2) => ex1.collectionId - ex2.collectionId);
        this.filteredExerciseMetaData = this.exerciseMetaData;
      });
  }

  updateFilters(tag: IExTag): void {
    this.filtersActivated.set(tag, !this.filtersActivated.get(tag));

    const activatedFilters: IExTag[] = Array.from(this.filtersActivated.entries())
      .filter(([_, activated]) => activated)
      .map(([t, _]) => t);

    if (activatedFilters.length > 0) {
      this.filteredExerciseMetaData = this.exerciseMetaData
        .filter((metaData) =>
          activatedFilters.every((t) => this.exerciseHasTag(metaData, t))
        );
    } else {
      this.filteredExerciseMetaData = this.exerciseMetaData;
    }

  }

  exerciseHasTag(exercise: IExerciseMetaData, tag: IExTag): boolean {
    return exercise.tags
      .map((t) => t.abbreviation)
      .includes(tag.abbreviation);
  }


}
