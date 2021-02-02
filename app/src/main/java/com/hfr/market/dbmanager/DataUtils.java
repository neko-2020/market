package com.hfr.market.dbmanager;


import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hfr.market.model.Food;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据处理工具类
 * 缓存与数据库数据之间的配合使用
 * 数据的增删由缓存处理比较方便。然后将数据提供给数据库工具类，进行储存
 */

public class DataUtils {
    private DaoManager daoManager;

    /**
     * 初始化数据库工具
     */
    public DataUtils(Context context) {
        daoManager = DaoManager.getInstance();
        daoManager.init(context);
    }

    /**
     * 关闭数据库
     */
    public void close() {
        daoManager.closeConnection();
    }


    /********************食物列表**************************/
    /**
     * 更新食物列表<BR/>参数由服务端提供，将会覆盖原来的列表数据
     *
     * @param json
     */
    public void updateFoodList(JSONArray json) {
        CacheUtils.updateFoodList(json);
        List<Food> fruitList = CacheUtils.getFoodList("F");
        insertMultiStudent(fruitList);
        List<Food> vegList = CacheUtils.getFoodList("V");
        insertMultiStudent(vegList);
    }

    /**
     * 同时插入多条记录，需要开辟新的线程
     *
     * @param foodList
     * @return
     */
    public boolean insertMultiStudent(final List<Food> foodList) {
        boolean flag = false;
        try {
            daoManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (Food food : foodList) {
                        daoManager.getDaoSession().insertOrReplace(food);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 从数据库获取数据
     */
    public List<Food> getFoodListFromDao(String type) {
        List<Food> foodList = daoManager.getDaoSession().loadAll(Food.class);
        List<Food> fruitList = new ArrayList<>();
        List<Food> vegList = new ArrayList<>();
        for (Food food : foodList) {
            if (food.getType().equals("V")) {
                vegList.add(food);
            } else if (food.getType().equals("F")) {
                fruitList.add(food);
            }
        }
        if (type.equals("V")) {
            return vegList;
        } else if (type.equals("F")) {
            return fruitList;
        } else {
            return null;
        }
    }

    /**
     * 获取列表
     *
     * @return
     */
    public List<Food> getFoodList(String type) {
        List<Food> foods = CacheUtils.getFoodList(type);
        if (foods == null) {
            foods = getFoodListFromDao(type);
            CacheUtils.updateFruitList(foods, type);
        }
        return foods;
    }

    /**
     * 获取jsonArray
     * @param foodList
     * @return
     */
    public JsonArray getFoodJsonArray(List<Food> foodList) {
        JsonArray jsonArray = new JsonArray();
        for (Food food : foodList) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", food.getName());
            obj.addProperty("price", food.getPrice());
            obj.addProperty("type", food.getType());
            jsonArray.add(obj);
        }
        return jsonArray;
    }
}


