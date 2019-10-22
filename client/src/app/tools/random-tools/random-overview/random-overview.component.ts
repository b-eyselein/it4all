import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Tool} from '../../../_interfaces/tool';
import {randomTools} from '../random-tools-list';

@Component({templateUrl: './random-overview.component.html'})
export class RandomOverviewComponent implements OnInit {

   tool: Tool;

  constructor(private route: ActivatedRoute) {
    const toolId = route.snapshot.paramMap.get('toolId');
    this.tool = randomTools.find((t) => t.id === toolId);
  }

  ngOnInit() {
  }

}
