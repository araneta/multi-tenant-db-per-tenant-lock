# Example App for setting one database per tenant and using lock
 
assumes more than 50 databases

## Dependencies
* Java
* Node
* NPM
* Yarn
* ngrok

## Auth0 Setup
* Create Auth0 Tenant (NOTE: This will create and delete a bunch of database connections that match example-... so you may want to create a fresh tenant for testing)  See below for instructions for creating a tenant.
* Create new client in Auth0.  Set the type to Regular Web App.
    * Open manage.auth0.com in chrome
    * make sure you select your new tenant
    * click `clients` in the left nav
    * click `create new client` button in upper right
    * enter a name: springApp
    * select Regular Web App
    * click `settings`
    * In the allowed callback urls, enter:
    ```
    http://*.springApp.local:8080/callback
    ```
    * In the allowed logout urls, enter:
    ```
    https://*.springApp.local:8080/logout
    ```
* Create a non-interactive client in Auth0 for genConnections:
    * Open manage.auth0.com in chrome
    * make sure you select your new tenant
    * click `clients` in the left nav
    * click `create new client` button in upper right
    * enter a name: e.g. genConnections
    * select Non-Interactive Client
* Grant the genConnections client access to the management API:
    * Click `apis` in the left nav
    * Click `Got it let's rock` if this is your first time to the apis section
    * Click `User Management API`
    * Click `Non-Interactive Clients`
    * Click `Authorize` next to your new client
    * Type `connections` in the scope filter
    * Choose all CRUD operations for connections: "read:connections update:connections create:connections delete:connections"
    * Click Save
* In your `/etc/hosts` file create an entry for example79-db.springApp.local:
```
127.0.0.1 example79-db.springApp.local
```
* Start ngrok (make note of the URL it creates you will need it later):
```
ngrok http 8080
```

## Install
* Clone the repo:
```
git clone git@github.com:/auth0-samples/multi-tenant-db-per-tenant-lock
```

* Customize the Hosted Login Page:
    * Open manage.auth0.com in chrome
    * make sure you select your new tenant
    * click `Hosted Pages` in the left nav
    * make sure `Login` is the tab you are on
    * Click the switch next to `Customize Login Page`
    * Delete all code in the login page
    * Add all code from multi-tenant-db-per-tenant-lock/hostedLoginPage.html
    * On line 44 of the login page code, replace `https://d5e7021b.ngrok.io` with the ngrok url from the output of when you started ngrok.  NOTE: you must use the https version of the URL

* Setup the genConnections app
```bash
cd multi-tenant-db-per-tenant-lock/genConnections

cp config.sample.json config.json

# Add your genConnections client ID and secret, your springApp client ID and your tenant domain to the config.json you just created

yarn install

node index.js
```

* Create a user
    * Open manage.auth0.com in chrome
    * make sure you select your new tenant
    * click `Users` in the left nav
    * click the `CREATE USER` button in the upper right
    * enter an email and password for the new user
    * Select example79-db for the connection

* Setup the springApp
    * Navigate to the directory:
    ```bash
    cd multi-tenant-db-per-tenant-lock/springApp
    ```
    * Set the client values in the `src/main/resources/auth0.properties` file. They are read by the `AppConfig` class:
    ```
    com.auth0.domain: {YOUR_AUTH0_DOMAIN}
    com.auth0.clientId: {YOUR_SPRING_APP_CLIENT_ID}
    com.auth0.clientSecret: {YOUR_SPRING_APP_CLIENT_SECRET}    
    ```
    * Start the app:
    ```
    ./gradlew clean bootRun
    ```
    
* Open Chrome to http://example79-db.springApp.local
    * You should be able to log in with your user that you created

## Issue Reporting

If you have found a bug or if you have a feature request, please report them at this repository issues section. Please do not report security vulnerabilities on the public GitHub issue tracker. The [Responsible Disclosure Program](https://auth0.com/whitehat) details the procedure for disclosing security issues.

## Author

[Auth0](auth0.com)

## What is Auth0?

Auth0 helps you to:

* Add authentication with [multiple authentication sources](https://docs.auth0.com/identityproviders), either social like **Google, Facebook, Microsoft Account, LinkedIn, GitHub, Twitter, Box, Salesforce, amont others**, or enterprise identity systems like **Windows Azure AD, Google Apps, Active Directory, ADFS or any SAML Identity Provider**.
* Add authentication through more traditional **[username/password databases](https://docs.auth0.com/mysql-connection-tutorial)**.
* Add support for **[linking different user accounts](https://docs.auth0.com/link-accounts)** with the same user.
* Support for generating signed [Json Web Tokens](https://docs.auth0.com/jwt) to call your APIs and **flow the user identity** securely.
* Analytics of how, when and where users are logging in.
* Pull data from other sources and add it to the user profile, through [JavaScript rules](https://docs.auth0.com/rules).

## Create a free Auth0 Account

1. Go to [Auth0](https://auth0.com) and click Sign Up.
2. Use Google, GitHub or Microsoft Account to login.

## License

This project is licensed under the MIT license. See the [LICENSE](LICENSE) file for more info.