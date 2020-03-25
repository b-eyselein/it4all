import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ComponentWithCollectionTool} from '../_helpers/ComponentWithCollectionTool';
import {ExerciseOverviewGQL, ExerciseOverviewQuery} from "../../../_services/apollo_services";

@Component({templateUrl: './exercise-overview.component.html'})
export class ExerciseOverviewComponent extends ComponentWithCollectionTool implements OnInit {

  collectionId: number;
  exerciseId: number;

  exerciseOverviewQuery: ExerciseOverviewQuery;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private exerciseOverviewGQL: ExerciseOverviewGQL,
  ) {
    super(route);

    if (!this.tool) {
      this.router.navigate(['../..']);
    }

    this.collectionId = parseInt(this.route.snapshot.paramMap.get('collId'), 10);
    this.exerciseId = parseInt(this.route.snapshot.paramMap.get('exId'), 10);
  }

  ngOnInit() {
    this.exerciseOverviewGQL
      .watch({toolId: this.tool.id, collId: this.collectionId, exId: this.exerciseId})
      .valueChanges
      .subscribe(({data}) => this.exerciseOverviewQuery = data);
  }

}
