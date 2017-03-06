var cluster = require('cluster');
var http = require('http');
var cpuNums = require('os').cpus().length;

if(cluster.isMaster){
    console.log(`Master ${process.pid} is Running`);
    for(var i = 0; i < cpuNums; i++){
        cluster.fork();
    }

    cluster.on('exit',function(work,code,signal){
        console.log(`worker ${work.process.pid} died`);
    })
}else{
    http.createServer(function(req,res){
        res.writeHead(200);
        res.end('hello workd\n');
    }).listen(7777);
    console.log(`worker ${process.pid} started`);
}