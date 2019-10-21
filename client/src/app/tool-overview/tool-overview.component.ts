import {Component} from '@angular/core';
import {collectionTools} from '../tools/collection-tools/collection-tools-list';
import {randomTools} from '../tools/random-tools/random-tools-list';
import {Tool} from '../_interfaces/tool';

@Component({
  template: `
      <div class="container">
          <h2 class="subtitle is-4">Tools mit zufälligen Aufgaben</h2>
          <div class="columns is-multiline">
              <div *ngFor="let randomTool of randomTools" class="column is-one-third">
                  <a [routerLink]="['/randomTools', randomTool.id]" class="button is-link is-fullwidth">{{randomTool.name}}</a>
              </div>
          </div>

          <h2 class="subtitle is-4">Tools mit Aufgaben in Sammlungen</h2>
          <div class="columns is-multiline">
              <div *ngFor="let colTool of collectionTools" class="column is-one-third">
                  <a [routerLink]="['/tools', colTool.id]" class="button is-link is-fullwidth">
                      {{colTool.name}}<sup *ngIf="colTool.status" [innerHTML]="'&nbsp;&' + colTool.status + ';'"></sup>
                  </a>
              </div>
          </div>
      </div>`,
  styles: []
})
export class ToolOverviewComponent {
  randomTools: Tool[] = randomTools;
  collectionTools: Tool[] = collectionTools;
}
