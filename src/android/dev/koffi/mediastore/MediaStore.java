package dev.koffi.mediastore;


import android.content.Context;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;


public class MediaStore extends CordovaPlugin {

    private MusicLoader musicLoader;
    private JsonCoverter jsonCoverter;
    private Context context;

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getList")) {
            //final String content = args.getString(0);

            context = this.cordova.getActivity().getApplicationContext();

            PluginResult result = new PluginResult(PluginResult.Status.OK, getList());
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);

            Toast.makeText(context, "Permission denied to read your External storage", Toast.LENGTH_LONG).show();

            callbackContext.success();
            return true;
        } else {
            callbackContext.error("AlertPlugin." + action + " not found !");
            return false;
        }
    }

    private String getList() {
        musicLoader = new MusicLoader(context);
        List<MusicItem> musicItems = (List<MusicItem>) musicLoader.loadInBackground();
        jsonCoverter = new JsonCoverter(musicItems);

        return jsonCoverter.getJson();
    }

}
