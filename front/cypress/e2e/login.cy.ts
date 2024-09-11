describe('Login spec', () => {
  it('Login successfull', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      },
    })

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session')

    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.url().should('include', '/sessions')
    cy.contains('Yoga app').should('exist')
    cy.get('span.link').contains('Sessions').should('exist')
    cy.get('span.link').contains('Account').should('exist')
    cy.get('span.link').contains('Logout').should('exist')

  })

  it('logout successfull', () => {
    cy.get('span.link').contains('Logout').click()
    cy.location("pathname").should("eq", "/")
    cy.get('span.link').contains('Login').should('exist')
    cy.get('span.link').contains('Register').should('exist')
  })

  it('Login failure', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: {
        message: 'Couldn\'t authenticate'
      },
      statusCode: 401,
    })

    cy.get('input[formControlName=email]').type("nobody@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!234"}{enter}{enter}`)

    cy.get('.error').contains('An error occurred').should('exist')
  })

  it('Invalid email login', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: {
        message: 'Bad parameters'
      },
      statusCode: 400,
    })

    cy.get('input[formControlName=email]').type("nobody-studio.com")
    cy.get('input:invalid').should('have.length', 1)
    cy.get('input[formControlName=password]').type(`${"test!234"}`)

    // This bypass form validation before sending request
    // cy.get('input[formControlName=password]').type(`${"test!234"}{enter}{enter}`)
    // cy.get('.error').contains('An error occurred').should('exist')
    
    cy.get('button[type=submit]:disabled').should("exist")

      
  })

  it('Invalid pasword login', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: {
        message: 'Bad parameters'
      },
      statusCode: 400,
    })

    cy.get('input[formControlName=email]').type("nobody@studio.com")
    cy.get('input[formControlName=password]').type(`${" "}`)
    cy.get('input[formControlName=password]').clear()
    cy.get('input:invalid').should('have.length', 1)

    cy.get('button[type=submit]:disabled').should("exist")
    
  })
});