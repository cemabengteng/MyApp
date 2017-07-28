var superagent = require('superagent');
var cheerio = require('cheerio');
/*
    1. load页面
    2. 载入cheerio
    3. 写入数据库
    用不同的异步库都体验一下
*/
var loadPromise = new Promise((resolve,reject)=>{
    superagent
        .get('www.baidu.com')
        .end((err,resulet)=>{
            if(err){
                reject(err);
            }else{
                var $ = cheerio.load(resulet);
                resolve($);
            }
        })
})


function* gen(x){
    var y = yield x + 2;
    return y;
}

var v= gen(10);
console.log(v.next());
console.log(v.next().value);