package dev.koffi.mediastore;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Koffi
 */

public class MediaStore extends CordovaPlugin {

    private MusicLoader musicLoader;
    private JsonCoverter jsonCoverter;
    private Context context;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.i(this.toString(), action);
        if ("getList".equals(action)) {

            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {

                    context = cordova.getActivity().getApplicationContext();
                    PluginResult result = new PluginResult(PluginResult.Status.OK, getList());
                    result.setKeepCallback(true);
                    callbackContext.sendPluginResult(result);

                    Toast.makeText(context, "Permission denied to read your External storage",
                            Toast.LENGTH_LONG).show();
                    callbackContext.success();

                }
            });

            return true;
        } else {
            Toast.makeText(context, "AlertPlugin." + action + " not found !",
                    Toast.LENGTH_LONG).show();
            Log.i(this.toString(), "AlertPlugin." + action + " not found !");
            callbackContext.error("AlertPlugin." + action + " not found !");
            return false;
        }
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    private String getList() {
        musicLoader = new MusicLoader(context);
        List<MusicItem> musicItems = (List<MusicItem>) musicLoader.loadInBackground();
        jsonCoverter = new JsonCoverter(musicItems);

        return jsonCoverter.getJson();
    }

}
