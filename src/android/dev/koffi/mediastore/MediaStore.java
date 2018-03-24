package dev.koffi.mediastore;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;


public class MediaStore extends CordovaPlugin {

    private static final int MUSIC_LOADER_ID = 1;
    private static final int OVERLAY_PERMISSION_REQ_CODE = 1;
    private static final int EXT_STORAGE_PERMISSION_REQ_CODE = 2;
    private MusicLoader musicLoader;
    private JsonCoverter jsonCoverter;
    private Context context;

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("getList".equals(action)) {
            //final String content = args.getString(0);

            context = this.cordova.getActivity().getApplicationContext();
            PluginResult result = new PluginResult(PluginResult.Status.OK, getList());
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            Toast.makeText(context, getList(), Toast.LENGTH_LONG).show();

            callbackContext.success();
            return true;
        } else {
            callbackContext.error("AlertPlugin." + action + " not found !");
            return false;
        }
    }


    private void requestDialog() {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
    }

    private String getList() {
        musicLoader = new MusicLoader(context);
        List<MusicItem> musicItems = (List<MusicItem>) musicLoader.loadInBackground();
        jsonCoverter = new JsonCoverter(musicItems);

        return jsonCoverter.getJson();
    }


}
