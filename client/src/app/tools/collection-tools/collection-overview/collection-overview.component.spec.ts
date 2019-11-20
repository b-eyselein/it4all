import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CollectionOverviewComponent} from './collection-overview.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {collectionToolRoutingComponents, CollectionToolRoutingModule} from '../collection-tools.routing';
import {ExerciseFilesEditorComponent} from '../_components/exercise-files-editor/exercise-files-editor.component';

describe('CollectionOverviewComponent', () => {
  let component: CollectionOverviewComponent;
  let fixture: ComponentFixture<CollectionOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, CollectionToolRoutingModule],
      declarations: [CollectionOverviewComponent,  ...collectionToolRoutingComponents, ExerciseFilesEditorComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CollectionOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
