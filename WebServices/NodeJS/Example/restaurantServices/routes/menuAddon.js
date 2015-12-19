var async = require('async');
var db_conn = require('../db_conn');

function getAllMenuAddons (request, response) {
	async.waterfall([
		function (callback) {
			db_conn.getConnection(function (cb) {
				if (cb.error) {
					callback(cb.error);
				} else {
					callback(null, cb.connection);
				}
			});
		},
		function (conn, callback) {
			var queryStr = "SELECT * FROM restaurantschema.menuAddon ma ";
			var inserts = [];
			conn.query(queryStr, inserts, function(err, rows) {
				conn.release();
				if (err) {
					callback(err);
				} else {
					callback(null, rows);
				}
			});
		}
	],
	function (err, res) {
		response.setHeader('content-type', 'application/json');
		if (err) {
			response.status(500).send({error: err});
		} else {
			response.send({menuAddons: res});
		}
	});
}

function addNewMenuAddon (request, response) {
	async.waterfall([
		function (callback) {
			db_conn.getConnection(function (cb) {
				if (cb.error) {
					callback(cb.error);
				} else {
					callback(null, cb.connection);
				}
			});
		},
		function (conn, callback) {
			var queryStr = "INSERT INTO `menuAddon` (`menuAddonName`) VALUES ? ";
			var inserts = [request.body.addons];
			conn.query(queryStr, inserts, function(err, rows) {
				conn.release();
				if (err) {
					callback(err);
				} else {
					callback(null, rows);
				}
			});
		}
	],
	function (err, res) {
		response.setHeader('content-type', 'application/json');
		if (err) {
			response.status(500).send({error: err});
		} else {
			response.send({Result: "Success"});
		}
	});
}

module.exports = {
	getAllMenuAddons: getAllMenuAddons,
	addNewMenuAddon: addNewMenuAddon
}