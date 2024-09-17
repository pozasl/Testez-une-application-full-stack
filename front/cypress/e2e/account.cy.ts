describe('Account spec', () => {

  const baseUrl = Cypress.env('API_PREFIX')
  
  describe('as User', () => {
    const userInfo = {
      id: 3,
      email: 'bob@test.com',
      lastName: 'LE BRICOLEUR',
      firstName: 'Bob',
      admin: false,
      createdAt: '2024-09-05T15:39:23',
      updatedAt: '2024-09-06T15:39:23'
    }
    before(() => {
      cy.visit('/login')

      cy.intercept('POST', `${baseUrl}/api/auth/login`, {
        body: {
          id: 3,
          username: 'bob@test.com',
          firstName: 'Bob',
          lastName: 'LE BRICOLEUR',
          admin: false
        },
      })

      cy.intercept('GET', `${baseUrl}/api/session`, {
        body: []
      })

      cy.intercept('GET', `${baseUrl}/api/user/3`, {
        body: userInfo
      })

      cy.get('input[formControlName=email]').type("bob@test.com")
      cy.get('input[formControlName=password]').type(`${"pass1234"}{enter}{enter}`)
    });

    it('Display account info', () => {
      cy.location("pathname").should("eq", "/sessions")
      cy.get('span.link').contains('Account').click()
      cy.location("pathname").should("eq", "/me")
      cy.get('h1').should('contain', 'User information')
      cy.contains('Name: Bob LE BRICOLEUR').should('exist')
      cy.contains('Email: bob@test.com').should('exist')
      // cy.contains('Create at: September 5, 2024').should('exist')
      // cy.contains('Last update: September 6, 2024').should('exist')
      cy.get('button').contains('Detail').should('exist')
    })

    it('Delete should delete account and logout', () => {

      cy.intercept('DELETE', `${baseUrl}/api/user/3`, {
        body: { message: 'User deleted' }
      })

      cy.get('button').contains('Detail').click()
      cy.location("pathname").should("eq", "/")
    })
  });

  describe('as Admin', () => {
    const userInfo = {
      id: 1,
      email: 'yoga@studio.com',
      lastName: 'Admin',
      firstName: 'Admin',
      admin: true,
      createdAt: '2024-09-05T15:39:23',
      updatedAt: '2024-09-06T15:39:23'
    }
    before(() => {
      cy.visit('/login')

      cy.intercept('POST', `${baseUrl}/api/auth/login`, {
        body: {
          id: 1,
          username: 'yoga@studio.com ',
          firstName: 'Admin',
          lastName: 'Admin',
          admin: true
        },
      })

      cy.intercept('GET', `${baseUrl}/api/session`, {
        body: []
      })

      cy.intercept('GET', `${baseUrl}/api/user/1`, {
        body: userInfo
      })

      cy.get('input[formControlName=email]').type("yoga@studio.com")
      cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
    });

    it('Display account info', () => {
      cy.location("pathname").should("eq", "/sessions")
      cy.get('span.link').contains('Account').click()
      cy.location("pathname").should("eq", "/me")
      cy.get('h1').should('contain', 'User information')
      cy.contains('Name: Admin ADMIN').should('exist')
      cy.contains('Email: yoga@studio.com').should('exist')
      cy.contains('You are admin').should('exist')
      // cy.contains('Create at: September 5, 2024').should('exist')
      // cy.contains('Last update: September 6, 2024').should('exist')
      cy.get('button').contains('Detail').should('not.exist');
    })

  });

});