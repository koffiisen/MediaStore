var KMediaStore = {

    getAudioList: function (successCallback, errorCallback) {

        cordova.exec(successCallback, errorCallback, "MediaStore", "getList", null);

    }
};


module.exports = KMediaStore;

/*var exec = require('cordova/exec');

exports.Audio = function (arg0, success, error) {
    exec(success, error, 'MediaStore', 'get', [arg0]);
};*/

//plugman -d install --platform android --project platforms\android --plugin C:\Users\Koffi\Documents\Dev\Cordova\plugin\MediaStore
//plugman install --platform android --project C:\Users\Koffi\Documents\Dev\Cordova\TestReact --plugin C:\Users\Koffi\Documents\Dev\Cordova\plugin\MediaStore
// cordova plugin add --link C:\Users\Koffi\Documents\Dev\Cordova\plugin\MediaStore
// plugman createpackagejson C:\Users\Koffi\Documents\Dev\Cordova\plugin\MediaStore
// plugman create --name MediaStore --plugin_id koffi.cordova.MediaStore --plugin_version 0.0.1 --path .
//