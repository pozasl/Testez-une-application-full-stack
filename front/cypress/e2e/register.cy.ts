describe('Register spec', () => {

  it('Invalid email register', () => {
    cy.visit('/register')

    cy.get('input[formControlName=firstName]').type("test")
    cy.get('input[formControlName=lastName]').type("TEST")
    cy.get('input[formControlName=email]').type("nobody-studio.com")
    cy.get('input:invalid').should('have.length', 1)
    cy.get('input[formControlName=password]').type(`${"test!234"}`)
    // This bypass form validation before sending request
    // cy.get('input[formControlName=password]').type(`${"test!234"}{enter}{enter}`)
    // cy.get('.error').contains('An error occurred').should('exist')

    cy.get('button[type=submit]:disabled').should("exist")
  })

  it('Invalid pasword register', () => {
    cy.visit('/register')

    cy.get('input[formControlName=firstName]').type("test")
    cy.get('input[formControlName=lastName]').type("TEST")
    cy.get('input[formControlName=email]').type("nobody@studio.com")
    cy.get('input[formControlName=password]').type(`${" "}`)
    cy.get('input[formControlName=password]').clear()
    cy.get('input:invalid').should('have.length', 1)

    cy.get('button[type=submit]:disabled').should("exist")

  })

  it('Register successfull', () => {
    cy.visit('/register')

    cy.intercept('POST', '/api/auth/register', {
      body: {
        message:'User registered successfully!'
      },
    })

    cy.get('input[formControlName=firstName]').type("test")
    cy.get('input[formControlName=lastName]').type("TEST")
    cy.get('input[formControlName=email]').type("test@test.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.url().should('include', '/login')
  })

  it('Login successfull', () => {

    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'test@test.com',
        firstName: 'test',
        lastName: 'TEST',
        admin: false
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
  })

  it('logout successfull', () => {
    cy.get('span.link').contains('Logout').click()
    cy.location("pathname").should("eq", "/")
  })

  it('Register failure', () => {
    cy.visit('/register')
    cy.intercept('POST', '/api/auth/register', {
      body: {
        message: 'email already exists'
      },
      statusCode: 409,
    })

    cy.intercept('POST', '/api/auth/register', {
      body: {
        message: 'User email already exist'
      },
      statusCode: 409,
    })

    cy.get('input[formControlName=firstName]').type("test")
    cy.get('input[formControlName=lastName]').type("TEST")
    cy.get('input[formControlName=email]').type("test@test.com")
    cy.get('input[formControlName=password]').type(`${"test!234"}{enter}{enter}`)

    cy.get('.error').contains('An error occurred').should('exist')
  })

});