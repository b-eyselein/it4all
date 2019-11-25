import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../../tools/collection-tools/_services/api.service';
import {DexieService} from '../../_services/dexie.service';
import {ExerciseComponentHelpers} from '../../tools/collection-tools/_helpers/ExerciseComponentHelpers';
import {IExerciseCollection} from '../../_interfaces/models';

@Component({templateUrl: './admin-edit-collection.component.html'})
export class AdminEditCollectionComponent extends ExerciseComponentHelpers implements OnInit {

  collection: IExerciseCollection;

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService, private dexieService: DexieService) {
    super(route);

    if (!this.tool) {
      this.router.navigate(['/admin']);
    }
  }

  private fetchCollection(collId: number): void {
    this.apiService.getCollection(this.tool.id, collId)
      .subscribe((collection: IExerciseCollection | undefined) => {
        if (this.collection) {
          this.collection = collection;
        } else {
          this.router.navigate(['/admin', this.tool.id]);
        }
      });
  }

  ngOnInit() {
    const collId = parseInt(this.route.snapshot.paramMap.get('collId'), 10);

    this.dexieService.collections.get([this.tool.id, collId])
      .then((maybeCollection) => {
        if (maybeCollection) {
          this.collection = maybeCollection;
        } else {
          this.fetchCollection(collId);
        }
      });
  }

}
