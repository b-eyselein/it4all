import {Component, OnInit} from '@angular/core';
import {Exercise, Tool} from '../../../_interfaces/tool';
import {ActivatedRoute, Router} from '@angular/router';
import {collectionTools} from '../collection-tools-list';
import {ApiService} from '../_services/api.service';

@Component({templateUrl: './exercise-overview.component.html'})
export class ExerciseOverviewComponent implements OnInit {

  tool: Tool;
  collectionId: number;
  exercise: Exercise;

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService) {
    const toolId = this.route.snapshot.paramMap.get('toolId');
    this.collectionId = parseInt(this.route.snapshot.paramMap.get('collId'), 10);


    this.tool = collectionTools.find((t) => t.id === toolId);

    if (!this.tool) {
      this.router.navigate(['../..']);
    }
  }

  ngOnInit() {
    const exerciseId = parseInt(this.route.snapshot.paramMap.get('exId'), 10);

    this.apiService.getExercise(this.tool.id, this.collectionId, exerciseId)
      .subscribe((exercise) => {
        console.info(JSON.stringify(exercise, null, 2));

        this.exercise = exercise;
      });
  }

}
