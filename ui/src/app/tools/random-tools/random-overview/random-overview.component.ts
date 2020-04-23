import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {RandomTool} from '../../../_interfaces/tool';
import {randomTools} from '../random-tools-list';
import {Subscription} from "rxjs";

@Component({templateUrl: './random-overview.component.html'})
export class RandomOverviewComponent implements OnInit {

  private sub: Subscription;

  tool: RandomTool;

  constructor(private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      this.tool = randomTools.find((t) => t.id === paramMap.get('toolId'));
    })
  }

}
