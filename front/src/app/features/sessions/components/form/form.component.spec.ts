import { HttpClientModule, HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';

import { FormComponent } from './form.component';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';

class MatSnackBarStub {
  open() {
    return {
      // Can't wait 3 seconds for a test
      onAction: () => new Observable((obs) => obs.next())
    };
  }
}

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let router: Router;

  describe('as User', () => {
    
    const mockSessionService = {
      sessionInformation: {
        admin: false
      },
      isLogged: true
    }

    beforeEach(async () => {
      await TestBed.configureTestingModule({

        imports: [
          RouterTestingModule.withRoutes(
            [{ path: 'sessions', redirectTo: '/' }],
          ),
          HttpClientModule,
          MatCardModule,
          MatIconModule,
          MatFormFieldModule,
          MatInputModule,
          ReactiveFormsModule,
          MatSnackBarModule,
          MatSelectModule,
          BrowserAnimationsModule
        ],
        providers: [
          { provide: SessionService, useValue: mockSessionService },
          SessionApiService
        ],
        declarations: [FormComponent]
      })
        .compileComponents();
      router = TestBed.inject(Router);
      fixture = TestBed.createComponent(FormComponent);

      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('initialization should redirect to /sessions if not admin', () => {
      const routerSpy = jest.spyOn(router, "navigate");
      component.ngOnInit();
      fixture.detectChanges();
      expect(routerSpy).toBeCalledWith(['/sessions']);
    });

  });

  describe('as Admin', () => {
    let sessionApiService: SessionApiService;
    let snackBar: MatSnackBar;
    const now = new Date();
    const mockSessionService = {
      sessionInformation: {
        admin: true
      },
      isLogged: true
    }

    const mockRoute = {
      snapshot: {
        paramMap: {
          get: jest.fn(() => '1')
        }
      }
    }

    const session1 = {
      id: '1',
      name: 'Session 9',
      description: 'Description',
      date: now,
      teacher_id: '1',
      users: [],
      createdAt: now,
      updatedAt: now,
    }

    const formSession = {
      name: "Session 9",
      date: now.toISOString().split('T')[0],
      teacher_id: '1',
      description: "Description"
    };

    beforeEach(async () => {
      await TestBed.configureTestingModule({

        imports: [
          RouterTestingModule.withRoutes(
            [{ path: 'sessions/update/:id', component: FormComponent },
            { path: 'sessions/create', component: FormComponent },
            { path: 'sessions', redirectTo: '/' },
            ],
          ),
          HttpClientModule,
          MatCardModule,
          MatIconModule,
          MatFormFieldModule,
          MatInputModule,
          ReactiveFormsModule,
          MatSnackBarModule,
          MatSelectModule,
          BrowserAnimationsModule,
        ],
        providers: [
          { provide: SessionService, useValue: mockSessionService },
          { provide: ActivatedRoute, useValue: mockRoute },
          { provide: MatSnackBar, useClass: MatSnackBarStub },
          SessionApiService
        ],
        declarations: [FormComponent]
      })
        .compileComponents();

      sessionApiService = TestBed.inject(SessionApiService);
      router = TestBed.inject(Router);
      snackBar = TestBed.inject(MatSnackBar);
      fixture = TestBed.createComponent(FormComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    // Impossible but Needed for 100% coverage
    const httpError = new HttpErrorResponse({ error: 'Bad Request', status: 400 });
    it('Submit with undefined should send undefined data', async () => {
      sessionApiService.create = jest.fn((session) => new Observable<any>(obs => obs.next(httpError)))
      const createSessionSpy = jest.spyOn(sessionApiService, "create");
      component.sessionForm = undefined;
      expect(component.sessionForm).toBeUndefined();
      component.onUpdate = false;
      component.submit();
      expect(createSessionSpy).toBeCalledWith(undefined);
    });

  });
});
