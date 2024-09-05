import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import {  ReactiveFormsModule } from '@angular/forms';
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

class MatSnackBarStub{
  open(){
    return {
      // Can't wait 3 seconds for a test
      onAction: () => new Observable((obs) => obs.next())
    };
  }
}

class RouterStub {
  url = '';
  navigate(commands: any[], extras?: any) { }
}

describe('FormComponent when not admin', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let router:Router;

  const mockSessionService = {
    sessionInformation: {
      admin: false
    },
    isLogged: true
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({

      imports: [
        RouterTestingModule,
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
        // { provide: Router, useClass: RouterStub },
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
    const routerSpy = jest.spyOn(router,"navigate");
    component.ngOnInit();
    fixture.detectChanges();
    expect(routerSpy).toBeCalledWith(['/sessions']);
  });

});

describe('FormComponent when admin', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let router: Router;
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
    id: 1,
    name: "Session 9",
    description: "Description",
    date: now,
    teacher_id: 1,
    users: [],
    createdAt: now,
    updatedAt: now,
  }

  const formSession = {
    name: session1.name,
    date: session1.date.toISOString().split('T')[0],
    teacher_id: session1.teacher_id,
    description: session1.description
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({

      imports: [
        RouterTestingModule.withRoutes(
          [{path: 'sessions/update/:id', component: FormComponent},
           {path: 'sessions/create', component: FormComponent},
           {path: 'sessions', redirectTo : '/'},
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

  it('initialization should fetch session data if update mod', async () => {
    sessionApiService.detail = jest.fn(() => new Observable<any>(obs => obs.next(session1)))
    const sessionApiServiceSpy = jest.spyOn(sessionApiService,"detail");
    await router.navigate(['sessions','update','1']);
    component.ngOnInit();
    fixture.detectChanges();
    expect(component.onUpdate).toBe(true)
    expect(sessionApiServiceSpy).toBeCalledWith('1');
    expect(component.sessionForm?.value).toEqual(formSession);
  });

  it('initialization should create empty session form if admin', async () => {
    await router.navigate(['/sessions/create'])
    component.ngOnInit();
    fixture.detectChanges();
    expect(component.onUpdate).toBe(false)
    expect(component.sessionForm?.value).toEqual({
      name:'',
      date: '',
      teacher_id: '',
      description: ''
    });
  });

  it('submit should create session if not update mod then exit to /sessions', async() => {
    sessionApiService.create = jest.fn(() => new Observable<any>(obs => obs.next(session1)));
    const sessionApiServiceSpy = jest.spyOn(sessionApiService,"create");
    const matSnackBarSpy = jest.spyOn(snackBar,"open");
    const routerSpy = jest.spyOn(router,"navigate")
    await router.navigate(['/sessions/create']);
    component.ngOnInit();
    fixture.detectChanges();
    component.sessionForm?.setValue(formSession);
    component.submit();
    expect(component.onUpdate).toBe(false)
    expect(sessionApiServiceSpy).toBeCalledWith(formSession);
    expect(matSnackBarSpy).toBeCalledWith('Session created !', 'Close', { duration: 3000 });
    expect(routerSpy).toBeCalledWith(['sessions']);
  });

  it('submit should update session if update mod then exit to /sessions', async () => {
    sessionApiService.detail = jest.fn(() => new Observable<any>(obs => obs.next(sessionApiService)))
    sessionApiService.update = jest.fn(() => new Observable<any>(obs => obs.next(session1)));
    const sessionApiServiceSpy = jest.spyOn(sessionApiService,"update");
    const matSnackBarSpy = jest.spyOn(snackBar,"open");
    const routerSpy = jest.spyOn(router,"navigate");
    await router.navigate(['sessions','update','1']);
    component.ngOnInit();
    fixture.detectChanges();
    await  fixture.whenStable()
    expect(component.sessionForm?.value).toEqual(formSession);
    expect(component.onUpdate).toBe(false)
    component.submit();
    expect(sessionApiServiceSpy).toBeCalledWith('1', formSession);
    expect(matSnackBarSpy).toBeCalledWith('Session updated !', 'Close', { duration: 3000 });
    expect(routerSpy).toBeCalledWith(['sessions']);
  });

});
