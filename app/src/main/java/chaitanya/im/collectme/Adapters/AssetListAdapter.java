package chaitanya.im.collectme.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;

import chaitanya.im.collectme.AssetDetailActivity;
import chaitanya.im.collectme.AssetListDataModel;
import chaitanya.im.collectme.R;

public class AssetListAdapter extends RecyclerView.Adapter<AssetListAdapter.ViewHolder>{
    private ArrayList<AssetListDataModel> _dataSet;
    static Context _context;
    public static View.OnClickListener myOnClickListener = new MyOnClickListener();
    private int lastPosition = -1;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView _ItemName;
        public TextView _ExtraInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            _ItemName = (TextView) itemView.findViewById(R.id.item_name);
            _ExtraInfo = (TextView) itemView.findViewById(R.id.extra_info);
        }

    }

    public AssetListAdapter(ArrayList<AssetListDataModel> data, Context context) {
        _dataSet = data;
        _context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_asset_card, parent, false);

        view.setOnClickListener(myOnClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {

        TextView textViewMovieTitle = holder._ItemName;
        TextView textViewExtraInfo = holder._ExtraInfo;

        textViewMovieTitle.setText(_dataSet.get(listPosition).getItemName());
        textViewExtraInfo.setText(_dataSet.get(listPosition).getExtraInfo());
        setAnimation(holder.itemView, listPosition);
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(_context, R.anim.up_from_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder)
    {
        holder.itemView.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return _dataSet.size();
    }

    private static class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.d("TAG", "clicked");
            Intent intent = new Intent(_context, AssetDetailActivity.class);
            _context.startActivity(intent);
        }

    }
}
