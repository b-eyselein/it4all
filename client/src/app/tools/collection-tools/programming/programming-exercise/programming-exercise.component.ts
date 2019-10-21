import {Component, OnInit} from '@angular/core';
import {ExerciseCollection, Tool, ToolPart} from '../../../../_interfaces/tool';
import {ProgrammingTool} from '../../collection-tools-list';
import {ActivatedRoute, Router} from '@angular/router';
import {ProgrammingExercise} from '../programming';
import {ApiService} from '../../../../_services/api.service';

import 'codemirror/mode/python/python';

@Component({templateUrl: './programming-exercise.component.html'})
export class ProgrammingExerciseComponent implements OnInit {

  readonly tool: Tool = ProgrammingTool;
  collection: ExerciseCollection;
  exercise: ProgrammingExercise;
  part: ToolPart;

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService) {
  }

  ngOnInit() {
    const collId: number = parseInt(this.route.snapshot.paramMap.get('collId'), 10);
    const exId: number = parseInt(this.route.snapshot.paramMap.get('exId'), 10);

    const partStr: string | null = this.route.snapshot.paramMap.get('partId');

    const maybePart: ToolPart | undefined = this.tool.parts.find((p) => p.id === partStr);

    if (maybePart) {
      this.part = maybePart;
    } else {
      this.router.navigate(['/tools', this.tool.id, 'collections', collId]);
    }

    this.apiService.getCollection(this.tool.id, collId)
      .subscribe((coll) => this.collection = coll);

    this.apiService.getExercise<ProgrammingExercise>(this.tool.id, collId, exId)
      .subscribe((ex) => this.exercise = ex);
  }

  correct(): void {
    console.error('TODO: correct!');
    console.info(JSON.stringify(this.exercise, null, 2));
  }

  showSampleSolution(): void {
    console.error('TODO: show sample solution...');
  }

}
