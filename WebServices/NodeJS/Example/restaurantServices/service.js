var http = require('http');
var express = require('express');
var app = express();
var cors = require('cors');
var bodyParser = require('body-parser');
app.set('port', process.env.PORT || 3112);
app.use(bodyParser.urlencoded({
	extended: true
}));
app.use(bodyParser.json({type: 'application/json'}));
app.use(cors());

//	Import routes
var mi = require('./routes/menuItem');
var ma = require('./routes/menuAddon');
var mc = require('./routes/menuCategory');
var mf = require('./routes/menuFamily');

app.get('/hello', function (request, response) {
	response.setHeader('content-type', 'application/json');
	response.send({msg: 'Hello World'});
});


app.get('/menuItem/all', mi.getAllMenuItems);
app.post('/menuItem/insert', mi.addNewMenuItem);

app.get('/menuFamily/all', mf.getAllMenuFamilies);
app.post('/menuFamily/insert', mf.addNewFamily);

app.get('/menuCategory/all', mc.getAllMenuCategories);
app.post('/menuCategory/insert', mc.addNewCategory);

app.get('/menuAddon/all', ma.getAllMenuAddons);
app.post('/menuAddon/insert', ma.addNewMenuAddon);


http.createServer(app).listen(app.get('port'), function(){
	console.log("Express server listening on port " + app.get('port'));
});