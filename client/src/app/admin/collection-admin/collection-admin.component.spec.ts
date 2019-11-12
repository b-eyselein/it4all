import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CollectionAdminComponent} from './collection-admin.component';
import {AppRoutingModule} from '../../app-routing.module';

describe('CollectionAdminComponent', () => {
  let component: CollectionAdminComponent;
  let fixture: ComponentFixture<CollectionAdminComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CollectionAdminComponent],
      imports: [AppRoutingModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CollectionAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
