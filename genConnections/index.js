#! /usr/bin/env node

var _ = require('lodash');
var auth0 = require('auth0');
var Promise = require('bluebird');
var nconf = require('nconf');

nconf
  .env()
  .file({ file: 'config.json'});

var config = {
  clientId: nconf.get('GEN_CONNECTIONS_CLIENT_ID'),
  clientSecret: nconf.get('GEN_CONNECTIONS_CLIENT_SECRET'),
  domain: nconf.get('AUTH0_DOMAIN')
};

var audience = `https://${config.domain}/api/v2/`;

var authClient = new auth0.AuthenticationClient(config);

var defaultConnection = {
  "options": {
    "mfa": {
      "active": true,
      "return_enroll_settings": true
    },
    "brute_force_protection": true
  },
  "strategy": "auth0",
  "enabled_clients": [
    nconf.get('SPRING_APP_CLIENT_ID')
  ]
};

function doRange(startValue, times) {
  console.log('start: ', startValue);
  var connectionNames = _.times(times, function(i) {
    var val = parseInt(i, 10) + parseInt(startValue, 10);
    return `example${val}-db`;
  });

  console.log('connection names and start: ', connectionNames, startValue);

  return authClient.oauth.clientCredentialsGrant({ audience: audience }, function(err, tokens) {
    console.log("err: ", err);
    console.log("tokens: ", tokens);

    if (!tokens) {
      console.log('bad tokens');
      return;
    }

    var options = {
      domain: config.domain,
      token: tokens.access_token
    };

    var mgmtClient = new auth0.ManagementClient(options);

    var deletes = [];

    // Pre-delete the connections
    return mgmtClient.connections.getAll(function(err, connections) {
      if (err) console.log("get error: ", err);
      connections.forEach(function(connection) {
        if (connectionNames.indexOf(connection.name) >= 0) {
          var deleteMethod = Promise.promisify(mgmtClient.connections.delete, { context: mgmtClient.connections });
          deletes.push(deleteMethod({ id: connection.id }).catch(function(err) {
            console.log("delete err: ", err);
          }));
        }
      });

      return Promise.all(deletes)
        .then(function() {
          connectionNames.forEach(function(connectionName) {
            var connectionOptions = JSON.parse(JSON.stringify(defaultConnection));
            var parts = connectionName.split('-');
            connectionOptions.name = connectionName;
            connectionOptions.options.domain_aliases = [`${parts[0]}.com`];

            mgmtClient.connections.create(connectionOptions, function(err, result) {
              if (err) {
                console.log("err: ", err);
                process.exit(5);
              }
            });
          });
        });
    });
  });
}


var times = 20;
var last = 0;
var max = 100;
doRange(last,times);
setInterval(function() {
  last += times;
  if (last >= max) process.exit(0);
  console.log("Calling doRange: ", last, ", ", times);
  doRange(last,times);
},10000);
