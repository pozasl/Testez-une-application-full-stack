describe('Login spec', () => {

  const baseUrl = Cypress.env('API_PREFIX')

  console.log("==============> ", baseUrl)
  it('Login successfull', () => {
    cy.visit('/login')

    cy.intercept('POST', `${baseUrl}/api/auth/login`, {
      body: {
        id: 1,
        username: 'yoga@studio.com',
        firstName: 'Admin',
        lastName: 'Admin',
        admin: true
      },
    })

    cy.intercept(
      {
        method: 'GET',
        url: `${baseUrl}/api/session`,
      },
      []).as('session')

    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.location("pathname").should("eq", "/sessions")
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

    cy.intercept('POST', `${baseUrl}/api/auth/login`, {
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

    cy.intercept('POST', `${baseUrl}/api/auth/login`, {
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

    cy.intercept('POST', `${baseUrl}/api/auth/login`, {
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