package com.cdw.zhihutopnews.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cdw.zhihutopnews.MainActivity;
import com.cdw.zhihutopnews.R;
import com.cdw.zhihutopnews.activity.ZhihuDetailActivity;
import com.cdw.zhihutopnews.bean.ZhihuDailyItem;
import com.cdw.zhihutopnews.config.Config;
import com.cdw.zhihutopnews.uitls.DBUtils;
import com.cdw.zhihutopnews.uitls.DensityUtil;
import com.cdw.zhihutopnews.uitls.ObservableColorMatrix;

import java.util.ArrayList;

/**
 * Created by CDW on 2016/11/5.
 */

public class ZhihuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements MainActivity.LoadingMore {
    private static final int TYPE_LOADING_MORE = -1;
    private static final int NOMAL_ITEM = 1;
    private boolean showLoadingMore;
    private float width;
    private int widthPx;
    private int heighPx;
    private ArrayList<ZhihuDailyItem> zhihuDailyItems = new ArrayList<>();
    private Context context;
    private String imageUrl;

    public ZhihuAdapter(Context context) {
        this.context = context;
        width = this.context.getResources().getDimension(R.dimen.image_width);
        widthPx = DensityUtil.dip2px(this.context, width);
        heighPx = widthPx * 3 / 4;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case NOMAL_ITEM:
                return new ZhihuViewHolder(LayoutInflater.from(context).inflate(R.layout.zhihu_recycleview_item, parent, false));

            case TYPE_LOADING_MORE:
                return new LoadingMoreHolder(LayoutInflater.from(context).inflate(R.layout.infinite_loading, parent, false));

        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int type = getItemViewType(position);
        switch (type) {
            case NOMAL_ITEM:
                bindViewHolderNormal((ZhihuViewHolder) holder, position);
                break;
            case TYPE_LOADING_MORE:
                bindLoadingViewHold((LoadingMoreHolder) holder, position);
                break;
        }
    }

    private void bindLoadingViewHold(LoadingMoreHolder holder, int position) {
        holder.progressBar.setVisibility(showLoadingMore ? View.VISIBLE : View.INVISIBLE);
    }

    private void bindViewHolderNormal(final ZhihuViewHolder holder, final int position) {

        final ZhihuDailyItem zhihuDailyItem = zhihuDailyItems.get(holder.getAdapterPosition());

        if (DBUtils.getDB(context).isRead(Config.ZHIHU, zhihuDailyItem.getId(), 1))//判断是否已经阅读过
            holder.textView.setTextColor(Color.GRAY);
        else
            holder.textView.setTextColor(
                    Config.isNight ? context.getResources().getColor(R.color.text_primary_dark) : context.getResources().getColor(R.color.text_light));
        holder.cardView.setBackgroundColor(Config.isNight ? context.getResources().getColor(R.color.cardview_background_dark) : context.getResources().getColor(R.color.cardview_background_light));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goDescribeActivity(holder, zhihuDailyItem);
            }
        });
        holder.textView.setText(zhihuDailyItem.getTitle());

        holder.linearLayout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goDescribeActivity(holder, zhihuDailyItem);
                    }
                });


        Glide.with(context)
                .load(zhihuDailyItems.get(position).getImages()[0])
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (!zhihuDailyItem.hasFadedIn) {

                            holder.imageView.setHasTransientState(true);//告诉系统这个 View 应该尽可能的被保留，

                            // 直到setHasTransientState(false)被呼叫
                            final ObservableColorMatrix cm = new ObservableColorMatrix();
                            final ObjectAnimator animator = ObjectAnimator.ofFloat(cm, ObservableColorMatrix.SATURATION, 0f, 1f);
                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    holder.imageView.setColorFilter(new ColorMatrixColorFilter(cm));
                                }
                            });
                            animator.setDuration(2000L);
                            animator.setInterpolator(new AccelerateInterpolator());
                            animator.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    holder.imageView.clearColorFilter();

                                    holder.imageView.setHasTransientState(false);

                                    animator.start();
                                    zhihuDailyItem.hasFadedIn = true;

                                }
                            });
                        }

                        return false;
                    }
                }).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop().override(widthPx, heighPx)
                .into(holder.imageView);


    }

    private void goDescribeActivity(ZhihuViewHolder holder, ZhihuDailyItem zhihuDailyItem) {

        DBUtils.getDB(context).insertHasRead(Config.ZHIHU, zhihuDailyItem.getId(), 1);
        holder.textView.setTextColor(Color.GRAY);
        Intent intent = new Intent(context, ZhihuDetailActivity.class);
        intent.putExtra("id", zhihuDailyItem.getId());
        intent.putExtra("title", zhihuDailyItem.getTitle());
        intent.putExtra("image", imageUrl);
        context.startActivity(intent);

    }

    @Override
    public int getItemCount() {
        return zhihuDailyItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getDataItemCount()
                && getDataItemCount() > 0) {
            return NOMAL_ITEM;
        }
        return TYPE_LOADING_MORE;
    }

    private int getDataItemCount() {

        return zhihuDailyItems.size();
    }

    private int getLoadingMoreItemPosition() {
        return showLoadingMore ? getItemCount() - 1 : RecyclerView.NO_POSITION;
    }

    // List.add() 的含义就是：你往这个List 中添加对象，它就把自己当作一个对象，
    // 你往这个List中添加容器，它就把自己当成一个容器。
    //List.addAll()方法，就是规定了，自己的这个List 就是容器，往里面增加的List 实例，
    // 增加到里面后，都会被看成对象。
    public void addItems(ArrayList<ZhihuDailyItem> list) {
        zhihuDailyItems.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void loadingStart() {
        if (showLoadingMore) return;
        showLoadingMore = true;
        notifyItemInserted(getLoadingMoreItemPosition());//在position位置插入数据的时候更新
    }

    @Override
    public void loadingFinish() {
        if (!showLoadingMore) return;
        final int loadingPos = getLoadingMoreItemPosition();
        showLoadingMore = false;
        notifyItemRemoved(loadingPos);
    }

    public void clearData() {
        zhihuDailyItems.clear();
        notifyDataSetChanged();
    }

    /**
     * 正在加载的ViewHolder
     */
    public static class LoadingMoreHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingMoreHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView;
        }
    }

    /**
     * 正常加载完的ViewHolder
     */
    class ZhihuViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;
        final LinearLayout linearLayout;
        final ImageView imageView;
        final CardView cardView;

        ZhihuViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_image_id);
            textView = (TextView) itemView.findViewById(R.id.item_text_id);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.zhihu_item_layout);
            cardView = (CardView) itemView.findViewById(R.id.zhihu_item_carview);
        }
    }


}
