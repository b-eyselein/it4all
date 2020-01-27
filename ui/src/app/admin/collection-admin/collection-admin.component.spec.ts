import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CollectionAdminComponent} from './collection-admin.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterTestingModule} from '@angular/router/testing';
import {ApiService} from '../../tools/collection-tools/_services/api.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRoute} from '@angular/router';

describe('CollectionAdminComponent', () => {
  let component: CollectionAdminComponent;
  let fixture: ComponentFixture<CollectionAdminComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, RouterTestingModule, HttpClientTestingModule],
      declarations: [CollectionAdminComponent],
      providers: [
        ApiService,
        {
          provide: ActivatedRoute,
          useValue: {snapshot: {paramMap: new Map<string, string>([['toolId', 'web']])}}
        }
      ]
    }).compileComponents();
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
