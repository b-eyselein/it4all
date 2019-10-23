import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CollToolAdminComponent } from './coll-tool-admin.component';

describe('CollToolAdminComponent', () => {
  let component: CollToolAdminComponent;
  let fixture: ComponentFixture<CollToolAdminComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CollToolAdminComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CollToolAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
