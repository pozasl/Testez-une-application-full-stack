describe('Sessions spec', () => {
    const sessions = [{
      id: 1,
      name: 'Ceci n\'est pas une location',
      date: '2024-09-03T00:00:00.000+00:00',
      teacher_id: 1,
      description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non risus. Suspendisse lectus tortor, dignissim sit amet, adipiscing nec, ultricies sed, dolor. Cras elementum ultrices diam. Maecenas ligula massa, varius a, semper congue, euismod non, mi. Proin porttitor, orci nec nonummy molestie, enim est eleifend mi, non fermentum diam nisl sit amet erat.',
      users: [4,5,6],
      createdAt: '2024-09-02T07:23:36',
      updatedAt: '2024-09-03T07:23:57'
    },
    {
      id: 2,
      name: 'Session n°2',
      date: '2024-10-04T00:00:00.000+00:00',
      teacher_id: 1,
      description: 'Duis semper. Duis arcu massa, scelerisque vitae, consequat in, pretium a, enim. Pellentesque congue.',
      users: [],
      createdAt: '2024-09-02T07:23:36',
      updatedAt: '2024-09-03T07:23:57'
    }
    ];

    const sessionCreate = {
      id: 3 ,
      name: 'Session test',
      date: '2024-10-04T00:00:00.000+00:00',
      teacher_id: 2,
      description: 'Session de Yoga orientée renforcement musculaire',
      users: [],
      createdAt: '2024-09-04T07:23:36',
      updatedAt: '2024-09-04T07:23:57'
    }

    const sessionEdit = {
      id: 3 ,
      name: 'Session test (edit)',
      date: '2024-09-16T00:00:00.000+00:00',
      teacher_id: 3,
      description: 'Session de Yoga orientée asouplissment',
      users: [],
      createdAt: '2024-09-04T07:23:36',
      updatedAt: '2024-09-05T07:23:57'
    }

    describe('As admin', () => {
      before(()=> {
          cy.visit('/login')
    
          cy.intercept('POST', '/api/auth/login', {
            body: {
              id: 1,
              username: 'test@test.com',
              firstName: 'test',
              lastName: 'TEST',
              admin: true
            },
          })
      

          cy.intercept('GET', '/api/session', {
            body: sessions
          })
      
          cy.get('input[formControlName=email]').type("yoga@studio.com")
          cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
      });

      it('Display sessions', () => {
        cy.url().should('include', '/sessions')
        cy.contains('Create').should('have.length', 1)
        cy.contains('Rentals available').should('have.length', 1)
        cy.contains('Ceci n\'est pas une location').should('have.length', 1)
        cy.contains('Session n°2').should('have.length', 1)
        cy.contains('Session on September 3, 2024').should('have.length', 1)
        cy.contains('Session on October 4, 2024').should('have.length', 1)
        cy.get('img.picture').should('have.length', 2)
        cy.contains('Lorem ipsum dolor sit amet,').should('have.length', 1)
        cy.contains('Duis semper. Duis arcu massa,').should('have.length', 1)
        cy.get('.mat-card-actions > :nth-child(1)').should('have.length', 2).should('contain','Detail')
        cy.get('.mat-card-actions > :nth-child(2)').should('have.length', 2).should('contain','Edit')
      })

      it('Create session success', () => {
        cy.intercept('GET', '/api/teacher', {
          body: [{
            id: 2,
            lastName: "DELAHAYE",
            firstName: "Margot",
            createdAt: "2024-09-02T07:05:07",
            updatedAt: "2024-09-04T07:05:07"
          },
          {
            id: 3,
            lastName: "INDIA",
            firstName: "Dhalsim",
            createdAt: "2024-09-02T07:05:07",
            updatedAt: "2024-09-04T07:05:07"
          }]
        })
        cy.intercept('POST', '/api/session', {
          body: {message: 'Session created'}
        })
        cy.contains('Create').click()
        cy.url().should('include', '/sessions/create')
        cy.get('h1').contains('Create session').should('have.length', 1)
        cy.get('input[formControlName=name]').type("Session test")
        cy.get('input[formControlName=date]').type("2025-10-25")
        cy.get('.mat-form-field-type-mat-select').click()
        cy.get('mat-option').contains('Margot DELAHAYE').click()
        cy.get('textarea').type("Session de Yoga orientée renforcement musculaire")
        cy.contains('Save').click()
      })

      it('Create session input errors', () => {
        cy.intercept('GET', '/api/teacher', {
          body: [{
            id: 2,
            lastName: "DELAHAYE",
            firstName: "Margot",
            createdAt: "2024-09-02T07:05:07",
            updatedAt: "2024-09-04T07:05:07"
          },
          {
            id: 3,
            lastName: "INDIA",
            firstName: "Dhalsim",
            createdAt: "2024-09-02T07:05:07",
            updatedAt: "2024-09-04T07:05:07"
          }]
        })
        cy.intercept('POST', '/api/session', {
          body: {message: 'Session created'}
        })
        cy.contains('Create').click()
        cy.url().should('include', '/sessions/create')
        cy.get('h1').contains('Create session').should('have.length', 1)
        cy.get('button:disabled').contains('Save').should('exist')

        cy.get('input[formControlName=name]:invalid').should('have.length', 1)
        cy.get('input[formControlName=name]').type("Session test")
        cy.get('input[formControlName=name]:invalid').should('have.length', 0)

        cy.get('input[formControlName=date]:invalid').should('have.length', 1)
        cy.get('input[formControlName=date]').type("2025-10-25")
        cy.get('input[formControlName=date]:invalid').should('have.length', 0)

        cy.get('button:disabled').contains('Save').should('exist')

        cy.get('.mat-form-field-type-mat-select').click()
        cy.get('mat-option').contains('Margot DELAHAYE').click()

        cy.get('button:disabled').contains('Save').should('exist')

        cy.intercept('GET', '/api/session', {
          body: [...sessions, sessionCreate]
        })

        // Go back
        cy.get('button:first').click()

        // Bug cypress don't find textarea it
        // cy.get('form textearea:invalid').should('have.length', 1)
        // cy.get('form textearea').type("Session de Yoga orientée renforcement musculaire")
        // cy.get('form textearea:invalid').should('have.length', 0)

      })

      it('Edit session success', () => {
        cy.intercept('GET', '/api/teacher', {
          body: [{
            id: 2,
            lastName: "DELAHAYE",
            firstName: "Margot",
            createdAt: "2024-09-02T07:05:07",
            updatedAt: "2024-09-04T07:05:07"
          },
          {
            id: 3,
            lastName: "INDIA",
            firstName: "Dhalsim",
            createdAt: "2024-09-02T07:05:07",
            updatedAt: "2024-09-04T07:05:07"
          }]
        })
        cy.intercept('PUT', '/api/session/3', {
          body: {message: 'Session updated'}
        })
        cy.intercept('GET', '/api/session/3', {
          body: sessions[0]
        })
        cy.get('.mat-card-actions > :nth-child(2)').last().click();
        cy.url().should('include', '/sessions/update/3')
        cy.get('h1').contains('Update session').should('have.length', 1)
        cy.get('input[formControlName=name]').clear()
        cy.get('input[formControlName=name]').type("Session test (edit)")
        cy.get('input[formControlName=date]').type("2024-09-16")
        cy.get('.mat-form-field-type-mat-select').click()
        cy.get('mat-option').contains('Dhalsim INDIA').click()
        cy.get('textarea').clear()
        cy.get('textarea').type("Session de Yoga orientée assouplissment")
        cy.intercept('GET', '/api/session', {
          body: [...sessions, sessionEdit]
        })
        cy.contains('Save').click()
      })
      

      it('Session detail', () => {
        cy.intercept('GET', '/api/session/3', {
          body: sessionEdit
        })
        cy.intercept('GET', '/api/teacher/3', {
          body: {
            id: 3,
            lastName: "INDIA",
            firstName: "Dhalsim",
            createdAt: "2024-09-02T07:05:07",
            updatedAt: "2024-09-04T07:05:07"
          }
        })
        cy.get('.mat-card-actions > :nth-child(1)').last().click();
        cy.url().should('include', '/sessions/detail/3')
        cy.get('h1').contains('Session Test (Edit)').should('have.length', 1)
        cy.get('img.picture').should('have.length', 1)
        cy.contains('Session de Yoga orientée asouplissment').should('have.length', 1)
        cy.contains('0 attendees').should('have.length', 1)
        cy.contains('September 16, 2024').should('have.length', 1)
        cy.contains('Create at: September 4, 2024').should('have.length', 1)
        cy.contains('Last update: September 5, 2024')
        cy.contains('Delete').should('have.length', 1)
        
      })

      it('Delete session', () => {
        cy.intercept('DELETE', '/api/session/3', {
          body: {
            'message': 'Session deleted'
          }
        })
        cy.contains('Delete').click()
        cy.url().should('include', '/sessions')
      })

      after(() => {
        cy.get('span.link').contains('Logout').click()
      })
    });

    describe('As user', () => {
      const sessionsModed = [
        {
          id: 1,
          name: 'Ceci n\'est pas une location',
          date: '2024-09-03T00:00:00.000+00:00',
          teacher_id: 1,
          description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non risus. Suspendisse lectus tortor, dignissim sit amet, adipiscing nec, ultricies sed, dolor. Cras elementum ultrices diam. Maecenas ligula massa, varius a, semper congue, euismod non, mi. Proin porttitor, orci nec nonummy molestie, enim est eleifend mi, non fermentum diam nisl sit amet erat.',
          users: [2,4,5,6],
          createdAt: '2024-09-02T07:23:36',
          updatedAt: '2024-09-03T07:23:57'
        },
        sessions[1]
      ]
      before(()=> {
          cy.visit('/login')
    
          cy.intercept('POST', '/api/auth/login', {
            body: {
              id: 2,
              username: 'alice@test.com',
              firstName: 'alice',
              lastName: 'TEST',
              admin: false
            },
          })
      

          cy.intercept('GET', '/api/session', {
            body: sessions
          })
      
          cy.get('input[formControlName=email]').type("alice@test.com")
          cy.get('input[formControlName=password]').type(`${"pass1234"}{enter}{enter}`)
      });

      it('Display sessions', () => {
        cy.url().should('include', '/sessions')
        cy.contains('Rentals available').should('have.length', 1)
        cy.contains('Ceci n\'est pas une location').should('have.length', 1)
        cy.contains('Session n°2').should('have.length', 1)
        cy.contains('Session on September 3, 2024').should('have.length', 1)
        cy.contains('Session on October 4, 2024').should('have.length', 1)
        cy.get('img.picture').should('have.length', 2)
        cy.contains('Lorem ipsum dolor sit amet,').should('have.length', 1)
        cy.contains('Duis semper. Duis arcu massa,').should('have.length', 1)
        cy.get('.mat-card-actions > :nth-child(1)').should('have.length', 2).should('contain','Detail')
      })

      it('Session detail', () => {
        cy.intercept('GET', '/api/session/1', {
          body: sessions[0]
        })
        cy.intercept('GET', '/api/teacher/1', {
          body: {
            id: 1,
            lastName: "DELAHAYE",
            firstName: "Margot",
            createdAt: "2024-09-02T07:05:07",
            updatedAt: "2024-09-04T07:05:07"
          }
        })
        cy.get('.mat-card-actions > :nth-child(1)').first().click();
        cy.url().should('include', '/sessions/detail/1')
        cy.get('h1').contains('Ceci N\'est Pas Une Location').should('have.length', 1)
        cy.get('img.picture').should('have.length', 1)
        cy.contains('Lorem ipsum dolor sit amet,').should('have.length', 1)
        cy.contains('3 attendees').should('have.length', 1)
        cy.contains('September 3, 2024').should('have.length', 1)
        cy.contains('Create at: September 2, 2024').should('have.length', 1)
        cy.contains('Last update: September 3, 2024')
        cy.contains('Participate').should('have.length', 1)
      })

      it('Participate session', () => {
        cy.intercept('POST', '/api/session/1/participate/2', {
          body: {
            'message': 'participation created'
          }
        })
        cy.intercept('GET', '/api/session', {
          body: sessionsModed
        })
        cy.get('button:last').should('contain','Participate');
        cy.intercept('GET', '/api/session/1', {
          body: sessionsModed[0]
        })
        cy.get('button:last').click()
        cy.contains('Do not participate').should('have.length', 1)
      })

      it('Unparticipate session', () => {
        
        cy.intercept('DELETE', '/api/session/1/participate/2', {
          body: {
            'message': 'participation deleted'
          }
        })
        cy.get('button:last').should('contain','Do not participate')
        cy.intercept('GET', '/api/session/1', {
          body: sessions[0]
        })
        cy.get('button:last').click()
      })

      after(() => {
        cy.get('span.link').contains('Logout').click()
      })
    });

  });