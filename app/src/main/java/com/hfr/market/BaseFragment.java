package com.hfr.market;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hfr.market.adpter.FruitAdapter;
import com.hfr.market.adpter.VegetableAdapter;
import com.hfr.market.connect.ConnectUtils;
import com.hfr.market.dbmanager.DataUtils;
import com.hfr.market.model.Food;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BaseFragment extends Fragment {
    private final String TAG = "BaseFragment";
    private SwipeRefreshLayout fruitRefresh;
    private SwipeRefreshLayout vegRefresh;
    private VegetableAdapter vegAdapter;
    private FruitAdapter fruitAdapter;
    private RecyclerView fruitView;
    private RecyclerView vegView;
    private ConnectUtils connectUtils;
    public static final String PRODUCT_URL = "https://www.baidu.com/";
    private List<Food> fruitList = new ArrayList<>();
    private List<Food> vegList = new ArrayList<>();
    private boolean isFruitFresh;
    private boolean isVegFresh;
    private boolean isOnClickKeepData;
    private DataUtils dataUtils;
    private Dialog dialog;

    public BaseFragment() {
        // Required empty public constructor
    }

    public static BaseFragment newInstance() {
        BaseFragment fragment = new BaseFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_base, container, false);
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        isFruitFresh = false;
        isVegFresh = false;
        isOnClickKeepData = false;
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);//让导航按钮显示出来
        }

        fruitRefresh = root.findViewById(R.id.fruit_refresh);
        fruitRefresh.setColorSchemeResources(R.color.colorPrimary);
        fruitRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isFruitFresh = true;
                isVegFresh = false;
                updateData();
            }
        });

        vegRefresh = root.findViewById(R.id.veg_refresh);
        vegRefresh.setColorSchemeResources(R.color.colorPrimary);
        vegRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isFruitFresh = false;
                isVegFresh = true;
                updateData();
            }
        });

        dataUtils = new DataUtils(getContext());
        connectUtils = new ConnectUtils(getContext());

        fruitView = root.findViewById(R.id.fruit_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        fruitView.setLayoutManager(layoutManager);

        vegView = root.findViewById(R.id.veg_recycler_view);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
        vegView.setLayoutManager(layoutManager2);

        vegList = dataUtils.getFoodList("V");
        vegAdapter = new VegetableAdapter(vegList);
        vegView.setAdapter(vegAdapter);
        fruitList = dataUtils.getFoodList("F");
        fruitAdapter = new FruitAdapter(fruitList);
        fruitView.setAdapter(fruitAdapter);

        return root;
    }

    private void showWaitDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.wait_layout, null);
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        Log.d(TAG, "加载菜单文件");
        menu.clear();
        menuInflater.inflate(R.menu.toolbar, menu);//加载菜单文件
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirm:
                Log.e(TAG, "confirm");
                saveData();
                break;
            //HomeAsUp按钮的id永远是：android.R.id.home
            case android.R.id.home:
                showChoiceDialog();
                break;
            default:
        }
        return true;
    }

    private void saveData() {
        List<Food> foods = new ArrayList<>();
        foods.addAll(dataUtils.getFoodList("F"));
        foods.addAll(dataUtils.getFoodList("V"));
        Log.d(TAG, "postData foods size = " + foods.size());
        String jsonData = dataUtils.getFoodJsonArray(foods).toString();
        showWaitDialog();
        postData(jsonData);
    }

    private AlertDialog alertDialog;

    private void showChoiceDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.choice_dialog, null);
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        Button keepBtn = view.findViewById(R.id.keep_data);
        Button notKeepBtn = view.findViewById(R.id.not_keep_data);
        keepBtn.setOnClickListener(mClick);
        notKeepBtn.setOnClickListener(mClick);
    }

    private View.OnClickListener mClick = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            switch (arg0.getId()) {
                case R.id.keep_data:
                    Log.e(TAG, "keep_data");
                    alertDialog.dismiss();
                    isOnClickKeepData = true;
                    saveData();
                    break;
                case R.id.not_keep_data:
                    Log.e(TAG, "not_keep_data");
                    alertDialog.dismiss();
                    fruitList.clear();
                    vegList.clear();
                    isOnClickKeepData = false;
                    getFragmentManager().popBackStack();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 把修改的值传给服务器
     */
    private void postData(String jsonData) {
        Log.i(TAG, "jsonData = " + jsonData);
        connectUtils.postData(PRODUCT_URL, jsonData).subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "保留成功", Toast.LENGTH_SHORT).show();
                        if (isOnClickKeepData) {
                            getFragmentManager().popBackStack();
                        }
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e.getMessage());
                //错误的话，用默认的List展示
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
                        if (isOnClickKeepData) {
                            getFragmentManager().popBackStack();
                        }
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onNext(final String msg) {
                Log.d(TAG, "onNext msg = " + msg);
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        dataUtils.close();
        super.onDetach();
    }

    /**
     * 更新服务端数据
     */
    private void updateData() {
        connectUtils.updateData(PRODUCT_URL).subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e.getMessage());
                //错误的话，用默认的List展示
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNext(String response) {
                parseJSON(response);
            }
        });
    }

    /**
     * 解析服务端数据为food list
     *
     * @param jsonData
     */
    private void parseJSON(final String jsonData) {
        Log.i(TAG, "parseJSON");
        JSONArray json = null;
        try {
            json = new JSONArray(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dataUtils.updateFoodList(json);
        fruitList.clear();
        vegList.clear();
        fruitList.addAll(dataUtils.getFoodList("F"));
        vegList.addAll(dataUtils.getFoodList("V"));
        updateView();

    }

    /**
     * 接收到服务端数据后，更新界面
     */
    private void updateView() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fruitAdapter.notifyDataSetChanged();
                vegAdapter.notifyDataSetChanged();
                if (isFruitFresh) {
                    fruitRefresh.setRefreshing(false);
                } else if (isVegFresh) {
                    vegRefresh.setRefreshing(false);
                }
            }
        });
    }

}
