package dev.koffi.mediastore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Koffi on 24/03/2018.
 */

public class JsonCoverter {


    private List<MusicItem> kModelList;
    private String json;

    public JsonCoverter(List<MusicItem> kModelList) {
        this.kModelList = kModelList;

        json = "";
        convert();
    }

    private void convert() {

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < this.kModelList.size(); i++) {
            JSONObject myJsonObject = new JSONObject();
            try {
                myJsonObject.put("title", this.kModelList.get(i).title());
                myJsonObject.put("album", this.kModelList.get(i).album());
                myJsonObject.put("artist", this.kModelList.get(i).artist());
                myJsonObject.put("duration", this.kModelList.get(i).duration());
                myJsonObject.put("coverArtPath", this.kModelList.get(i).coverArtPath());
                myJsonObject.put("albumArtUri", this.kModelList.get(i).albumArtUri());
                myJsonObject.put("fileUri", this.kModelList.get(i).fileUri());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(myJsonObject);
        }

        this.json = jsonArray.toString();
    }

    public String getJson() {
        return json;
    }
}
