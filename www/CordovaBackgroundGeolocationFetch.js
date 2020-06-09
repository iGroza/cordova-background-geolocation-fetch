var exec = require('cordova/exec');

exports.startTracking = function () {
    exec(null, null, 'CDVBackgroundGeolocationFetch', 'startTracking', [null]);
};

exports.stopTracking = function () {
    exec(null, null, 'CDVBackgroundGeolocationFetch', 'stopTracking', [null]);
};

exports.ready = function (config, callback) {
    exec(callback, null, 'CDVBackgroundGeolocationFetch', 'ready', [{ uuid: device.uuid, ...config }]);
};
