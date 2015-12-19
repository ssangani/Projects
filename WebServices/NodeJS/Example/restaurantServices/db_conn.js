var env = require('./env');
var db = require('mysql');

exports.init = function (callback) {
	db.createConnection({
		host: env.settings.db_host,
		port: env.settings.db_port,
		user: env.settings.db_user,
		password: env.settings.db_password,
	}).query('CREATE DATABASE IF NOT EXISTS restaurantschema', function (err, rows) {
		if(err) {
			console.log("error creating schema");
			callback({
				error: err
			});
		} else {
			callback({
				error: false
			});
		}
		
	});
}

var db_pool = db.createPool({
	connectionLimit: 140,
	host: env.settings.db_host,
	port: env.settings.db_port,
	database: env.settings.db_schema,
	user: env.settings.db_user,
	password: env.settings.db_password,
	multipleStatements: true
});

exports.getConnection = function(callback) {
	db_pool.getConnection(function(err, conn) {
		callback({
			error: err,
			connection: conn
		});
	});
};