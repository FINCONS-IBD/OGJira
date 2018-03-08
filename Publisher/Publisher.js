//#!/usr/bin/env node
//var env=process.env.NODEJS_PATH;
var requiredStr = './node_modules/express/lib/express';
console.log(requiredStr);
module.exports = require('./node_modules/express/lib/express');//(env + 'node_modules/express/lib/express');
var express = require('./node_modules/express');//(env + 'node_modules/express');
var app = express();

var amqp = require('./node_modules/amqplib/callback_api');//(env + 'node_modules/amqplib/callback_api');

var bodyParser = require('./node_modules/body-parser');//(env + 'node_modules/body-parser');

app.use(bodyParser.json()); // support json encoded bodies
app.use(bodyParser.urlencoded({ extended: true }));

var PropertiesReader = require('./node_modules/properties-reader');//(env+'node_modules/properties-reader');
var configPath=""+process.argv.slice(2);
var properties="";
try{
	properties = PropertiesReader(configPath);
}catch(e){
	console.error("configuration File not valid");
	return;
}
var listen = properties.get('listenPort');

if(configPath==null || configPath==""){
	configPath='./config.properties';
}


var rabbitMqUrl="amqp://"+properties.get('rabbitMqUser')+":"+properties.get('rabbitMqPsw')+"@"+properties.get('rabbitMQServer')+":"+properties.get('rabbitMQPort')+"/"+properties.get('rabbitMqvHost');

app.post('/modifyReq/', function (req, res){
	var bug = req.body;
	
	bug = JSON.stringify(bug, null, 2);
	
	try{
	amqp.connect(rabbitMqUrl, function(err, conn) {
		try{
		conn.createChannel(function(err, ch) {
			var ex = properties.get('rabbitMqExchange');
			//var queue=properties.get('rabbitMqQueue');
			var msg = bug;

			ch.publish(ex, '', new Buffer(msg), {persistent: false}, {noAck: false});
			console.log(" [x] Sent %s", msg + "\n");
			
		});
		} catch(err){
			console.log("could not create channel, reason: " + err);
		}

		setTimeout(function() { conn.close()}, 500);
		res.end();
	});
	} catch(err) {
		console.log("could not connect, reason: " + err);
	}
});

app.listen(listen, function () {
  console.log('Server started on port ' + listen + '...\n');
});