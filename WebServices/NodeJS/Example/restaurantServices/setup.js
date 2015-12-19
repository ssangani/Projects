var mysql = require('mysql');
var fs = require('fs');
var readline = require('readline');
var async = require('async');
var db_conn = require('./db_conn');

async.waterfall([
	function (callback) {
		db_conn.init(function (cb) {
			if (cb.error) {
				callback(cb.error);
			}
			else {
				callback();
			}
		});
	},
	function (callback) {
		db_conn.getConnection(function(cb) {
			if (cb.error) {
				callback(cb.error);
			}
			else {
				callback(null, cb.connection);
			}
		});
	},
	function (conn, callback) {
		var rl = readline.createInterface({
			input: fs.createReadStream('./deployMin.sql'),
			terminal: false
		});
		rl.on('line', function(chunk){
			//console.log(chunk.toString('ascii'))
			conn.query(chunk.toString('ascii'), function(err, sets, fields){
				if(err) {
					callback(err);
				}
			});
		});
		rl.on('close', function(){
			console.log("Database setup finished...");
			callback(null, conn);
		});
	},
	function (conn, callback) {
		var rl = readline.createInterface({
			input: fs.createReadStream('./initDataMin.sql'),
			terminal: false
		});
		rl.on('line', function(chunk){
			//console.log(chunk.toString('ascii'))
			conn.query(chunk.toString('ascii'), function(err, sets, fields){
				if(err) {
					callback(err);
				}
			});
		});
		rl.on('close', function(){
			console.log("Inserting data finisheds...");
			conn.release();
			callback();
		});
	},
],
function (err, res) {
	if (err) {
		console.log("Error: ");
		console.log(err)
	}
	else {
		console.log("Setup finished...");
	}
});