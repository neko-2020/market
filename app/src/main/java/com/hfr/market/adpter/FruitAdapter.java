package com.hfr.market.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hfr.market.R;
import com.hfr.market.model.Food;
import java.util.List;


public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.ViewHolder> {
    private Context mContext;
    private List<Food> mFoodList;

    //构造函数：把要展示的数据源传进来，并赋值给全局变量mItemList
    public FruitAdapter(List<Food> foodList) {
//        System.out.println("FruitAdapter size = " + foodList.size());
        mFoodList = foodList;
    }

    //构建内部类：ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        EditText price;
        Button plusBtn;
        Button minusBtn;

        public ViewHolder(View view) { //传入一个view,通常是recyclerView子项的最外层布局
            super(view);
            //获取布局中的控件
            itemName = view.findViewById(R.id.name);
            price = view.findViewById(R.id.price);
            plusBtn = view.findViewById(R.id.plus_btn);
            minusBtn = view.findViewById(R.id.minus_btn);
        }
    }

    private int price;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { //将_item布局加载进来，然后创建ViewHolder实例，把加载出来的布局传入到构造函数中，最后将ViewHolder的实例返回
//        System.out.println("onCreateViewHolder ");
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.price_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        final EditText editText = holder.price;
        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                final Food food = mFoodList.get(position);
                price = food.getPrice();
                price -= 1;
                editText.setText(price+"");
                food.setPrice(price);
            }
        });
        holder.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                final Food food = mFoodList.get(position);
                price = food.getPrice();
                price += 1;
                editText.setText(price+"");
                food.setPrice(price);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        System.out.println("onBindViewHolder ");
        //对_item子项进行赋值，在每个子项滚动到屏幕内的时候执行
        Food food = mFoodList.get(position);
        //通过Position得到当前项的item实例，再将数据设置到textView中。
        holder.itemName.setText(food.getName());
        holder.price.setText(food.getPrice()+"");
    }

    @Override
    public int getItemCount() { //告诉一共有多少子项，返回数据源的长度就可以。
        return mFoodList.size();
    }


}
