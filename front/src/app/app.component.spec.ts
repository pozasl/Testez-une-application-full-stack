import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';

import { AppComponent } from './app.component';
import { SessionService } from './services/session.service';
import { Router } from '@angular/router';
import { Observable, Subscription } from 'rxjs';


describe('AppComponent', () => {
  let app: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let router: Router;
  let sessionService: SessionService;
  let subs: Subscription[];
  beforeEach(async () => {
    subs = [];
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatToolbarModule
      ],
      declarations: [
        AppComponent
      ]

    }).compileComponents();
    router = TestBed.inject(Router);
    sessionService = TestBed.inject(SessionService);
    sessionService.logOut = jest.fn();
    router.navigate = jest.fn();
  });

  afterEach(() => {
    subs.forEach(sub=>sub.unsubscribe());
  });

  describe(' when not logged', () => {

    beforeEach(async () => {
      sessionService.$isLogged = jest.fn(()=> new Observable(obs => obs.next(false)));
      fixture = TestBed.createComponent(AppComponent);
      app = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should create the app', () => {
      expect(app).toBeTruthy();
    });
  
    it('$isLogged should return False as a boolean Observable', (done) => {
      subs.push(app.$isLogged().subscribe((logged) => {
        expect(logged).toBe(false);
        done();
      }))
    });

    it('logout should call sessionService logout and go back to site root', () => {
      const sessionServiceSpy = jest.spyOn(sessionService, 'logOut');
      const routerSpy = jest.spyOn(router, 'navigate');
      app.logout();
      expect(sessionServiceSpy).toBeCalled();
      expect(routerSpy).toBeCalledWith(['']);
    });

  });

  describe(' when logged', () => {

    beforeEach(async () => {
      sessionService.$isLogged = jest.fn(()=> new Observable(obs => obs.next(true)));
      fixture = TestBed.createComponent(AppComponent);
      app = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('$isLogged should return True as a boolean Observable', (done) => {
      subs.push(app.$isLogged().subscribe((logged) => {
        expect(logged).toBe(true);
        done();
      }))
    });

    it('logout should call sessionService logout and go back to site root', () => {
      const sessionServiceSpy = jest.spyOn(sessionService, 'logOut');
      const routerSpy = jest.spyOn(router, 'navigate');
      app.logout();
      expect(sessionServiceSpy).toBeCalled();
      expect(routerSpy).toBeCalledWith(['']);
    });

  });

});
