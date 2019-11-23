import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {adminRoutingComponents, AdminRoutingModule} from './admin.routing';
import {ReadCollectionComponent} from './admin-read-collections/read-collection/read-collection.component';
import {ReadExerciseComponent} from './admin-read-exercises/read-exercise/read-exercise.component';


@NgModule({
  declarations: [
    ReadCollectionComponent,
    ReadExerciseComponent,

    ...adminRoutingComponents
  ],
  imports: [
    CommonModule,
    AdminRoutingModule
  ]
})
export class AdminModule {
}
