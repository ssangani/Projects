var async = require('async');
var db_conn = require('../db_conn');
var menuAddonOptions = require('./menuAddonOptions');

function getAllMenuItems (request, response) {
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
			var queryStr = "SELECT * FROM restaurantschema.menuItem mi "+
				"INNER JOIN restaurantschema.menuFamily mf "+
				"ON mf.menuFamilyId = mi.refFamilyId "+
				"INNER JOIN restaurantschema.menuCategory mc "+
				"ON mc.menuCategoryId = mi.refCategoryId ";
			var inserts = [];
			conn.query(queryStr, inserts, function(err, rows) {
				if (err) {
					conn.release();
					callback(err);
				} else {
					callback(null, conn, rows);
				}
			});
		},
		function (conn, menuItems, callback) {
			async.each(menuItems, 
				function(menuItem, loopCB) {
					menuAddonOptions.getOptionsForItem({
						menuItemId: menuItem.menuItemId
					}, function (fn_cb) {
						if(fn_cb.error) {
							loopCB(fn_cb.error);
						} else {
							menuItem.menuItemAddons = fn_cb.data;
							loopCB();
						}
					})
				},
				function (err) {
					if(err) {
						console.log("Error while fetching addons for menu item...")
						console.log(err);
					}
					conn.release();
					callback(null, menuItems);
				}
			);
		}
	],
	function (err, res) {
		response.setHeader('content-type', 'application/json');
		if (err) {
			response.status(500).send({error: err});
		} else {
			response.send({menuItems: res});
		}
	});
}

function addNewMenuItem (request, response) {
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
			var queryStr = "INSERT INTO `menuItem` (`menuItemName`, `menuItemDesc`, `menuItemPrice`, `refCategoryId`, "+
				"`refFamilyId`, `menuItemStartTime`, `menuItemEndTime`, `hasAddonOptions`, `maxLimitedAddons`) VALUES "+
				"(?, ?, ?, (SELECT menuCategoryId FROM menuCategory WHERE menuCategoryName = ? LIMIT 1), "+
				"(SELECT menuFamilyId FROM menuFamily WHERE menuFamilyName = ? LIMIT 1), "+
				"?, ?, ?, ?)";
			var inserts = [request.body.menuItemName, 
				request.body.menuItemDesc,
				request.body.menuItemPrice,
				request.body.menuCategoryName,
				request.body.menuFamilyName,
				request.body.menuItemStartTime,
				request.body.menuItemEndTime,
				request.body.hasAddonOptions,
				request.body.maxLimitedAddons];
			//mysql.format(queryStr, inserts);
			conn.query(queryStr, inserts, function(err, rows) {
				if (err) {
					conn.release();
					callback(err);
				} else {
					callback(null, conn, rows);
				}
			});
		},
		function (conn, insertRes, callback) {
			async.each(request.body.addons, 
				function (addon, loopCB) {
					var queryStr = "INSERT INTO `menuAddonOptions` (`menuItemId`, `menuAddonId`, `menuAddonPrice`, `isLimited`) VALUES "+
						"(?, ?, ?, ?)";
					var inserts = [insertRes.insertId, 
						addon.menuAddonId,
						addon.menuAddonPrice,
						addon.isLimited];
					conn.query(queryStr, inserts, function(err, rows) {
						if (err) {
							loopCB(err);
						} else {
							loopCB(null, conn, rows);
						}
					});
				},
				function (err) {
					if(err) {
						console.log("Error inserting addon for menuitem");
						console.log(err);
					}
					conn.release();
					callback();
				}
			)
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
	getAllMenuItems: getAllMenuItems,
	addNewMenuItem: addNewMenuItem
}