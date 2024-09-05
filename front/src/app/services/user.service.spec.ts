import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { UserService } from './user.service';
import { Observable } from 'rxjs';

describe('UserService', () => {
  let service: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientModule
      ]
    });
    service = TestBed.inject(UserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

});

describe('UserService with mocked http responses', () => {
  let service: UserService;
  let http: HttpClient;
  const now = new Date()
  let dummyUser = {
    id:1,
    email: "bob@test.com",
    lastName: "Le Bricoleur",
    firstName: "Bob",
    admin: true,
    createdAt: now,
    updatedAt: now
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers:[
        { provide: HttpClient, useValue: { get: jest.fn(), delete: jest.fn() } },
      ]
    });
    service = TestBed.inject(UserService);
    http = TestBed.inject(HttpClient);
  });


  it('getById should return an observable', (done) => {
    http.get = jest.fn(() => new Observable<any>((obs)=> obs.next(dummyUser)));
    service.getById("1").subscribe(user => {
      expect(user).toBe(dummyUser);
      done();
    })
  });

  it('delete should return an observable', (done) => {
    http.delete = jest.fn(() => new Observable<any>((obs)=> obs.next(dummyUser)));
    service.delete("1").subscribe(user => {
      expect(user).toBe(dummyUser);
      done();
    })
  })

});
