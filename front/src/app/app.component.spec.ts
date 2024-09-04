import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';

import { AppComponent } from './app.component';
import { SessionService } from './services/session.service';
import { Router } from '@angular/router';


describe('AppComponent', () => {
  let router: Router;
  let sessionService: SessionService;
  beforeEach(async () => {
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
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('$isLogged should return a boolean Observable', (done) => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    app.$isLogged().subscribe((logged) => {
      expect(logged).toBe(false);
      done();
    })
  });

  it('logout should log user out and go back to site root', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    const sessionServiceSpy = jest.spyOn(sessionService, 'logOut');
    const routerSpy = jest.spyOn(router, 'navigate');
    app.logout();
    expect(sessionServiceSpy).toHaveBeenCalled();
    expect(routerSpy).toHaveBeenCalledWith(['']);
  });
});
