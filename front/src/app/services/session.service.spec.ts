import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { SessionInformation } from '../interfaces/sessionInformation.interface';
import { SessionService } from './session.service';


describe('SessionService', () => {
  let service: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy()
  });

  it('$isLogged should return an observable initialized', () => {
    expect(service.$isLogged()).toBeDefined();
  })

  it('$isLogged should return a boolean observable initialized at false', done => {
    service.$isLogged().subscribe((logged)=> {
      expect(logged).toBe(false);
      done();
    })
  })

  it('logIn should work', () => {
    const session = Object({
      token: "token",
      type: "type",
      username: "bob",
      firstName: "Bob",
      lastName: "Le Bricoleur",
      admin: true
    }) as SessionInformation;
    service.logIn(session);
  })

  it('logOut should work', () => {
    service.logOut();
  })

});
