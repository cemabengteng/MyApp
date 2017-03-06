var spawn = require('child_process').spawn;
var ls = spawn('cmd.exe',['/c', 'dir']);
ls.stdout.on('data',function(data){
    console.log(data.toString);
})

ls.stderr.on('data',function(data){
    console.log('error:' + data.toString());
})

ls.on('close',function(code){
    console.log('exit: ' + code);
})

ls.on('error',function(err){
    console.log('error: ' + err.toString());
})

