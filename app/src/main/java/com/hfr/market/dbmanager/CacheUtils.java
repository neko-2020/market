package com.hfr.market.dbmanager;

import android.util.Log;

import com.hfr.market.model.Food;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * hp
 */

public class CacheUtils {
    private static final String TAG = "CacheUtils";

    /********************FoodListCache**************************/
    private static List<Food> mFruitList;
    private static List<Food> mVegList;

    public static void updateFoodList(JSONArray jsonArray) {
        //type == F ,水果
        //type == V,蔬菜
        mFruitList = new ArrayList<>();
        mVegList = new ArrayList();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String type = jsonObject.getString("type");
                if (type.equals("F")) {
                    Food fruit = new Food();
                    fruit.setName(jsonObject.getString("name"));
                    fruit.setPrice(Integer.parseInt(jsonObject.getString("price")));
                    fruit.setType(jsonObject.getString("type"));
                    mFruitList.add(fruit);
                } else if (type.equals("V")) {
                    Food veg = new Food();
                    veg.setName(jsonObject.getString("name"));
                    veg.setPrice(Integer.parseInt(jsonObject.getString("price")));
                    veg.setType(jsonObject.getString("type"));
                    mVegList.add(veg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateFruitList(List<Food> foodList, String type) {
        if (type.equals("F")) {
            mFruitList = foodList;
        } else if (type.equals("V")) {
            mVegList = foodList;
        }else {
            Log.e(TAG,"type error = " + type);
        }
    }

    public static List<Food> getFoodList(String type) {
        if (type.equals("F")) {
            return mFruitList;
        }else if (type.equals("V")){
            return mVegList;
        }else {
            Log.e(TAG,"type error = " + type);
            return null;
        }
    }

}
