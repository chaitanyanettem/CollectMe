package chaitanya.im.collectme.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import chaitanya.im.collectme.DataModel.AssetListDataModel;
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
        public TextView _otherDetails;
        AssetListDataModel clicked;

        public ViewHolder(View itemView) {
            super(itemView);
            _ItemName = (TextView) itemView.findViewById(R.id.item_name);
            _ExtraInfo = (TextView) itemView.findViewById(R.id.extra_info);
            _CategoryLogo = (ImageView) itemView.findViewById(R.id.category_logo);
            _otherDetails = (TextView) itemView.findViewById(R.id.other_details);
        }

        public void bind(final AssetListDataModel asset, final OnItemClickListener listener) {
            clicked = asset;
            _ItemName.setText(asset.getItemName());
            if (!asset.getExtraInfo().equals(""))
                _ExtraInfo.setText(asset.getExtraInfo());
            else
                _ExtraInfo.setText("--");
            String textForOtherDetails = asset.getDateCreated() +
                    " | <b>" + asset.getCategory() + "</b>";
            _otherDetails.setText(Html.fromHtml(textForOtherDetails));
            setCategoryLogo();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(asset);
                }
            });
        }

        void setCategoryLogo(){
            if (clicked.getCategory().equals(_context.getResources().getString(R.string.radio_pump))) {
                _CategoryLogo.setImageResource(R.drawable.motor);
            }
            else if (clicked.getCategory().equals(_context.getResources().getString(R.string.radio_computer))) {
                _CategoryLogo.setImageResource(R.drawable.ic_computer);
            }
            else if (clicked.getCategory().equals(_context.getResources().getString(R.string.radio_electric_pole))) {
                _CategoryLogo.setImageResource(R.drawable.ic_pole);
            }
            else if (clicked.getCategory().equals(_context.getResources().getString(R.string.radio_meter))) {
                _CategoryLogo.setImageResource(R.drawable.ic_meter);
            }
            else if (clicked.getCategory().equals(_context.getResources().getString(R.string.radio_other))) {
                _CategoryLogo.setImageResource(R.drawable.ic_others);
            }
            else if (clicked.getCategory().equals(_context.getResources().getString(R.string.radio_bulb))) {
                _CategoryLogo.setImageResource(R.drawable.ic_bulb);
            }
            else if (clicked.getCategory().equals(_context.getResources().getString(R.string.radio_petrol))) {
                _CategoryLogo.setImageResource(R.drawable.ic_petrol);
            }
            else if (clicked.getCategory().equals(_context.getResources().getString(R.string.radio_signage))) {
                _CategoryLogo.setImageResource(R.drawable.ic_signage);
            }
            else if (clicked.getCategory().equals(_context.getResources().getString(R.string.radio_sofa))) {
                _CategoryLogo.setImageResource(R.drawable.ic_sofa);
            }
            else if (clicked.getCategory().equals(_context.getResources().getString(R.string.radio_tap))) {
                _CategoryLogo.setImageResource(R.drawable.ic_tap);
            }
            else if (clicked.getCategory().equals(_context.getResources().getString(R.string.radio_tree))) {
                _CategoryLogo.setImageResource(R.drawable.ic_tree);
            }


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
