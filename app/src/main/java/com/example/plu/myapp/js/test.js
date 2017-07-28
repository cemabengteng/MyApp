function Point(x,y){
    this.xLocation = x;
    this.yLocation = y;
}

Point.prototype = {
    xTranslation:function(x){
        this.xLocation = this.xLocation + x;
    },

    yTranslation: function(y){
        this.yLocation = this.yLocation + y;
    },

    toString:function(){
        return "(" + this.xLocation + "," + this.yLocation + ")";
    }
}

var p = new Point(1,2);
console.log(Object.prototype.toString.call(p));
console.log(p.toString());