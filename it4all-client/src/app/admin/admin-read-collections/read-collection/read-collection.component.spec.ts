import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ReadCollectionComponent} from './read-collection.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ApiService} from '../../../tools/collection-tools/_services/api.service';

describe('ReadCollectionComponent', () => {
  let component: ReadCollectionComponent;
  let fixture: ComponentFixture<ReadCollectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ReadCollectionComponent],
      providers: [ApiService]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReadCollectionComponent);
    component = fixture.componentInstance;
    component.collection = {id: 1, toolId: '', title: '', authors: [], text: '', shortName: '', exercises: []};
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
