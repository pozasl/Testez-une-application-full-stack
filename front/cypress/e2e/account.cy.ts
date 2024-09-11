describe('Account spec', () => {
  describe('as User', () => {
    const userInfo = {
      id: 2,
      email: 'alice@test.com',
      lastName: 'TEST',
      firstName: 'Alice',
      admin: false,
      createdAt: '2024-09-05T15:39:23',
      updatedAt: '2024-09-06T15:39:23'
    }
    before(() => {
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
        body: []
      })

      cy.intercept('GET', '/api/user/2', {
        body: userInfo
      })

      cy.get('input[formControlName=email]').type("alice@test.com")
      cy.get('input[formControlName=password]').type(`${"pass1234"}{enter}{enter}`)
    });

    it('Display account info', () => {
      cy.get('span.link').contains('Account').click()
      cy.url().should('include', '/me')
      cy.get('h1').should('contain', 'User information')
      cy.contains('Name: Alice TEST').should('exist')
      cy.contains('Email: alice@test.com').should('exist')
      cy.contains('Create at: September 5, 2024').should('exist')
      cy.contains('Last update: September 6, 2024').should('exist')
      cy.get('button').contains('Detail').should('exist')
    })

    it('Delete should delete account and logout', () => {
      cy.intercept('DELETE', '/api/user/2', {
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
      lastName: 'ADMIN',
      firstName: 'Admin',
      admin: true,
      createdAt: '2024-09-05T15:39:23',
      updatedAt: '2024-09-06T15:39:23'
    }
    before(() => {
      cy.visit('/login')

      cy.intercept('POST', '/api/auth/login', {
        body: {
          id: 1,
          username: 'yoga@studio.com ',
          firstName: 'Admin',
          lastName: 'ADMIN',
          admin: true
        },
      })

      cy.intercept('GET', '/api/session', {
        body: []
      })

      cy.intercept('GET', '/api/user/1', {
        body: userInfo
      })

      cy.get('input[formControlName=email]').type("yoga@studio.com")
      cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
    });

    it('Display account info', () => {
      cy.get('span.link').contains('Account').click()
      cy.url().should('include', '/me')
      cy.get('h1').should('contain', 'User information')
      cy.contains('Name: Admin ADMIN').should('exist')
      cy.contains('Email: yoga@studio.com').should('exist')
      cy.contains('You are admin').should('exist')
      cy.contains('Create at: September 5, 2024').should('exist')
      cy.contains('Last update: September 6, 2024').should('exist')
      cy.get('button').contains('Detail').should('not.exist');
    })

  });

});