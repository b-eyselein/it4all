import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Tool} from '../../_interfaces/tool';
import {collectionTools} from '../../tools/collection-tools/collection-tools-list';
import {randomTools} from '../../tools/random-tools/random-tools-list';
import {EXAMPLE_TUTORIALS, Lesson} from '../../_interfaces/lesson';

@Component({templateUrl: './tool-tutorials-overview.component.html'})
export class ToolTutorialsOverviewComponent implements OnInit {

  tool: Tool;
  lessons: Lesson[] = [];

  constructor(private route: ActivatedRoute, private router: Router) {
    const toolId = this.route.snapshot.paramMap.get('toolId');

    this.tool = collectionTools.concat(randomTools).find((t) => t.id === toolId);

    if (!this.tool) {
      this.router.navigate(['/']);
    }
  }

  ngOnInit() {
    // TODO: find tutorials...
    this.lessons = EXAMPLE_TUTORIALS.get(this.tool) || [];
  }

}
