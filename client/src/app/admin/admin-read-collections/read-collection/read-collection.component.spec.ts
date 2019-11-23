import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ReadCollectionComponent} from './read-collection.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('ReadCollectionComponent', () => {
  let component: ReadCollectionComponent;
  let fixture: ComponentFixture<ReadCollectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ReadCollectionComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReadCollectionComponent);
    component = fixture.componentInstance;
    component.collection = {id: 1, toolId: '', title: '', author: '', text: '', state: '', shortName: '', exercises: []};
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
