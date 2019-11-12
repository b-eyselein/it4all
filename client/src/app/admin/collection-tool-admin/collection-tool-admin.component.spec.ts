import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CollectionToolAdminComponent} from './collection-tool-admin.component';
import {AppRoutingModule} from '../../app-routing.module';

describe('CollToolAdminComponent', () => {
  let component: CollectionToolAdminComponent;
  let fixture: ComponentFixture<CollectionToolAdminComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CollectionToolAdminComponent],
      imports: [AppRoutingModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CollectionToolAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
