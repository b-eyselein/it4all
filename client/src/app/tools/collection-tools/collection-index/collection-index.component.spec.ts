import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CollectionIndexComponent} from './collection-index.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {AppRoutingModule, routingComponents} from '../../../app-routing.module';
import {TagComponent} from '../../_components/tag/tag.component';

describe('CollectionIndexComponent', () => {
  let component: CollectionIndexComponent;
  let fixture: ComponentFixture<CollectionIndexComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, AppRoutingModule],
      declarations: [CollectionIndexComponent, TagComponent, ...routingComponents]
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
