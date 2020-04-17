import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ExerciseOverviewGQL, ExerciseOverviewQuery} from "../../../_services/apollo_services";
import {CollectionTool} from "../../../_interfaces/tool";
import {collectionTools} from "../collection-tools-list";

@Component({templateUrl: './exercise-overview.component.html'})
export class ExerciseOverviewComponent implements OnInit {

  private toolId: string;
  private collectionId: number;
  private exerciseId: number;

  tool: CollectionTool;

  exerciseOverviewQuery: ExerciseOverviewQuery;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private exerciseOverviewGQL: ExerciseOverviewGQL,
  ) {
  }

  ngOnInit() {
    this.route.paramMap.subscribe((paramMap) => {
      this.toolId = paramMap.get('toolId');
      this.collectionId = parseInt(paramMap.get('collId'), 10);
      this.exerciseId = parseInt(paramMap.get('exId'), 10);

      this.tool = collectionTools.find((t) => t.id === this.toolId);

      this.exerciseOverviewGQL
        .watch({toolId: this.tool.id, collId: this.collectionId, exId: this.exerciseId})
        .valueChanges
        .subscribe(({data}) => this.exerciseOverviewQuery = data);
    })
  }

}
