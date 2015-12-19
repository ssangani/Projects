var async = require('async');
var db_conn = require('../db_conn');

function getOptionsForItem (options, return_cb) {
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
			var queryStr = "SELECT * FROM restaurantschema.menuAddonOptions mao "+
				"INNER JOIN restaurantschema.menuAddon ma "+
				"ON ma.menuAddonId = mao.menuAddonId "+
				"WHERE mao.menuItemId = ? ";
			var inserts = [options.menuItemId];
			conn.query(queryStr, inserts, function(err, rows) {
				conn.release();
				if (err) {
					callback(err);
				} else {
					callback(null, rows);
				}
			});
		},
	],
	function (err, res) {
		if (err) {
			return_cb({error: err});
		} else {
			return_cb({data: res});
		}
	});
}

module.exports = {
	getOptionsForItem: getOptionsForItem,
	
}