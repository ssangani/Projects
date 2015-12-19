var async = require('async');
var db_conn = require('../db_conn');

function getAllMenuCategories (request, response) {
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
			var queryStr = "SELECT * FROM restaurantschema.menuCategory mc ";
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
			response.send({menuCategories: res});
		}
	});
}

function addNewCategory (request, response) {
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
			var queryStr = "INSERT INTO `menuCategory` (`menuCategoryName`) VALUES ? ";
			var inserts = [request.body.categories];
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
	getAllMenuCategories: getAllMenuCategories,
	addNewCategory: addNewCategory
}