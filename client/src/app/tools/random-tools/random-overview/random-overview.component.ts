import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Tool} from '../../../_interfaces/tool';
import {randomTools} from '../random-tools-list';

@Component({
  // selector: 'app-random-overview',
  template: `
      <div class="container">
          <h1 class="title is-3 has-text-centered">Übungsaufgaben</h1>
          <div class="buttons">
              <ng-container *ngFor="let part of tool.parts">
                  <a *ngIf="!part.disabled" [routerLink]="['/randomTools', tool.id, part.id]"
                     class="button is-link is-fullwidth">{{part.name}}</a>
              </ng-container>
          </div>

          <h1 *ngIf="tool.hasPlayground" class="title is-3 has-text-centered">Playground</h1>

          <h1 *ngIf="false" class="title is-3 has-text-centered">Evaluation</h1>
      </div>`,
})
export class RandomOverviewComponent implements OnInit {

   tool: Tool;

  constructor(private route: ActivatedRoute) {
    const toolId = route.snapshot.paramMap.get('toolId');
    this.tool = randomTools.find((t) => t.id === toolId);
  }

  ngOnInit() {
  }

}
