package me.srinjoyss01;

import org.json.simple.JSONObject;

public class property {
    public static JSONObject generate(String id, boolean avatar){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",id);
        jsonObject.put("avatar",avatar);
        return jsonObject;
    }
}
