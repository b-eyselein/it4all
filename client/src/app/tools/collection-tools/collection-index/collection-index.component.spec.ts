import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CollectionIndexComponent} from './collection-index.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {collectionToolRoutingComponents, CollectionToolRoutingModule} from '../collection-tools.routing';
import {ExerciseFilesEditorComponent} from '../_components/exercise-files-editor/exercise-files-editor.component';

describe('CollectionIndexComponent', () => {
  let component: CollectionIndexComponent;
  let fixture: ComponentFixture<CollectionIndexComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, CollectionToolRoutingModule],
      declarations: [CollectionIndexComponent,  ...collectionToolRoutingComponents, ExerciseFilesEditorComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CollectionIndexComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
