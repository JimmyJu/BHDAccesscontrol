package com.bhd.accesscontrol.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bhd.accesscontrol.R;
import com.bhd.accesscontrol.bean.InfoBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.Holder> {

    private List<InfoBean> mList;
    private RecyclerView recyclerView;

    public GridAdapter(List<InfoBean> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //负责承载每个子项的布局。
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, null);
        final Holder holder = new Holder(view);
        //对加载的子项注册监听事件
        holder.fruitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                String ip = mList.get(position).getIp();
                String door = mList.get(position).getDoor();
                String doorInfo = mList.get(position).getDoorName();
                onItemClickListener.onItemClick(view, ip, door, doorInfo);
            }
        });

        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        //负责将每个子项holder绑定数据
//        holder.mControllerName.setText(mList.get(position).getIp().substring(0,mList.get(position).getIp().indexOf(":"))+ "\n"+mList.get(position).getDoor());
        if (mList.get(position).getDoorName() != null) {
//            holder.mImageView.setVisibility(View.GONE);
            holder.mControllerName.setText(mList.get(position).getDoorName());//+ "（" + mList.get(position).getDoor() + "）");
            holder.mdoorNumber.setText(mList.get(position).getDoor().substring(mList.get(position).getDoor().length() - 1));
        } else {
//            holder.mImageView.setVisibility(View.GONE);
            holder.mControllerName.setText(mList.get(position).getDoor());
        }
    }


    @Override
    public int getItemCount() {
        //返回子项列表数目。
        return mList == null ? 0 : mList.size();
    }


    //设置子项列表监听
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    //OnItemClickListener()接口，定义RecyclerView选项单击事件的回调接口。
    public interface OnItemClickListener {

        /**
         * @param view   当前单击的View
         * @param ipPort 单击的View的ip
         */
        void onItemClick(View view, String ipPort, String door, String doorInfo);
    }

    class Holder extends RecyclerView.ViewHolder {
        //控制器名称
        private TextView mControllerName;

        private ImageView mImageView;

        //门号
        private TextView mdoorNumber;

        private View fruitView;  //表示自定义的控件的视图

        public Holder(@NonNull View itemView) {
            super(itemView);
            fruitView = itemView;
            mControllerName = itemView.findViewById(R.id.tv_controller);
            mImageView = itemView.findViewById(R.id.iv_image);
            mdoorNumber = itemView.findViewById(R.id.tv_num);
        }
    }

    public void clear() {
        if (mList != null) {
            mList.clear();
            notifyDataSetChanged();
        }
    }


    /**
     * 判断position对应的Item是否是组的第一项
     *
     * @param position
     * @return
     */
    public boolean isItemHeader(int position) {
        if (position == 0) {
            return true;
        } else {
            String lastGroupName = mList.get(position - 1).getGroupname();
            String currentGroupName = mList.get(position).getGroupname();
            //判断上一个数据的组别和下一个数据的组别是否一致，如果不一致则是不同组，也就是为第一项（头部）
            if (lastGroupName.equals(currentGroupName)) {
                return false;
            } else {
                return true;
            }
        }
    }


    /**
     * 获取position对应的Item组名
     *
     * @param position
     * @return
     */
    public String getGroupName(int position) {
        return mList.get(position).getGroupname();
    }


}
