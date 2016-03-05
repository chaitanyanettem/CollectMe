package chaitanya.im.collectme.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import chaitanya.im.collectme.AssetListDataModel;
import chaitanya.im.collectme.R;

public class AssetListAdapter extends RecyclerView.Adapter<AssetListAdapter.ViewHolder>{
    private ArrayList<AssetListDataModel> _dataSet;
    static Context _context;
    private int lastPosition = -1;
    private final OnItemClickListener _listener;
    private final String TAG = this.getClass().getName();

    public interface OnItemClickListener {
        void onItemClick(AssetListDataModel asset);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_asset_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        setAnimation(holder.itemView, listPosition);
        holder.bind(_dataSet.get(listPosition), _listener);
    }

    @Override
    public int getItemCount() {
        return _dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView _ItemName;
        public TextView _ExtraInfo;
        public ImageView _CategoryLogo;

        public ViewHolder(View itemView) {
            super(itemView);
            _ItemName = (TextView) itemView.findViewById(R.id.item_name);
            _ExtraInfo = (TextView) itemView.findViewById(R.id.extra_info);
            _CategoryLogo = (ImageView) itemView.findViewById(R.id.category_logo);
        }

        public void bind(final AssetListDataModel asset, final OnItemClickListener listener) {
            _ItemName.setText(asset.getItemName());
            _ExtraInfo.setText(asset.getExtraInfo());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(asset);
                }
            });
        }

    }

    public AssetListAdapter(ArrayList<AssetListDataModel> data, Context context, OnItemClickListener listener) {
        _dataSet = data;
        _context = context;
        _listener = listener;

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


}
