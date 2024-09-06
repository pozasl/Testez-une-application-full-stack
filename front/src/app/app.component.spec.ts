import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';

import { AppComponent } from './app.component';
import { SessionService } from './services/session.service';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';


describe('AppComponent', () => {
  let app: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
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

  describe(' when not logged', () => {

    beforeEach(async () => {
      fixture = TestBed.createComponent(AppComponent);
      app = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should create the app', () => {
      expect(app).toBeTruthy();
    });
  
    it('$isLogged should return a boolean Observable', (done) => {
      app.$isLogged().subscribe((logged) => {
        expect(logged).toBe(false);
        done();
      })
    });

    it('should display Login and Register links', () => {
      const links: HTMLElement[] = fixture.nativeElement.querySelectorAll('span.link');
      expect(links[0].textContent).toBe('Login');
      expect(links[1].textContent).toBe('Register');
    });

  });

  describe(' when logged', () => {

    beforeEach(async () => {
      sessionService.$isLogged = jest.fn(()=> new Observable(obs => obs.next(true)));
      fixture = TestBed.createComponent(AppComponent);
      app = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should display Session, Account and Logout links', () => {
      const links: HTMLElement[] = fixture.nativeElement.querySelectorAll('span.link');
      expect(links[0].textContent).toBe('Sessions');
      expect(links[1].textContent).toBe('Account');
      expect(links[2].textContent).toBe('Logout');
    });

    it('Click on logout Link should log user out and go back to site root', () => {
      const sessionServiceSpy = jest.spyOn(sessionService, 'logOut');
      const routerSpy = jest.spyOn(router, 'navigate');
      const logoutLink:HTMLElement = fixture.nativeElement.querySelectorAll('span.link')[2]
      logoutLink.click();
      expect(sessionServiceSpy).toBeCalled();
      expect(routerSpy).toBeCalledWith(['']);
    });

  });

});
