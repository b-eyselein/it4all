import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CollectionIndexComponent} from './collection-index.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {AppRoutingModule} from '../../../app-routing.module';
import {TagComponent} from '../../_components/tag/tag.component';

describe('CollectionIndexComponent', () => {
  let component: CollectionIndexComponent;
  let fixture: ComponentFixture<CollectionIndexComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CollectionIndexComponent, TagComponent],
      imports: [HttpClientTestingModule, AppRoutingModule]
    })
      .compileComponents();
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
