import {Component, OnInit} from '@angular/core';
import {ExerciseCollection} from '../../../_interfaces/tool';
import {ApiService} from '../../../_services/api.service';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  template: `
      <div class="container">
          <h1 class="title is-3 has-text-centered">Sammlungen</h1>
          <div *ngIf="collections?.length > 0; else elseBlock" class="columns is-multiline">
              <div *ngFor="let collection of collections" class="column is-one-third">
                  <a [routerLink]="['/tools', this.toolId, 'collections', collection.id]"
                     class="button is-link is-fullwidth">{{collection.title}}</a>
              </div>
          </div>
          <ng-template #elseBlock>
              <div class="notification is-danger has-text-centered">
                  Es konnten keine Sammlungen gefunden werden!
              </div>
          </ng-template>
          <!-- FIXME: uncomment evaluation! -->
          <!-- <h1 class="title is-3 has-text-centered">Evaluation</h1>-->
      </div>`
})
export class ToolIndexComponent implements OnInit {

  toolId: string;
  collections: ExerciseCollection[] = [];

  constructor(private route: ActivatedRoute, private apiService: ApiService, private router: Router) {
    this.toolId = this.route.snapshot.paramMap.get('toolId');

    if (this.toolId === undefined) {
      this.router.navigate(['/']);
    }
  }

  ngOnInit() {
    this.apiService.getCollections(this.toolId)
      .subscribe(
        (collections: ExerciseCollection[]) => this.collections = collections,
        (error) => console.error('X:  ' + error)
      );
  }

}
