var hello = {
    world: function(str, callback) {
        cordova.exec(callback, function(err) {
        	callback('Nothing to hello.');
    	}, "Backgeoonalarm", "hello", [str]);
    },
    initbackgroundgeo: function(str, callback) {
        cordova.exec(callback, function(err) {
        	callback('ERROR : Nothing to do.');
    	}, "Backgeoonalarm", "initbackgroundgeo", [str]);
    }
}

module.exports = hello;