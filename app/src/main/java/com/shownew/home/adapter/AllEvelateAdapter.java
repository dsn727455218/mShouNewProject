package com.shownew.home.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.shownew.home.R;
import com.shownew.home.ShouNewApplication;
import com.shownew.home.fragment.AllTalkFragment;
import com.shownew.home.module.entity.AllEvelateEntity;
import com.shownew.home.utils.PreviewImgUtils;
import com.shownew.home.utils.widget.WordWrapLayoutiml;
import com.wp.baselib.widget.CustomShapeImageView;

import java.util.ArrayList;

/**评价的适配
 * Created by WP on 2017/8/7.
 */

public class AllEvelateAdapter extends RecyclerView.Adapter<AllEvelateAdapter.AllEvelateViewHolder> {

    private ArrayList<AllEvelateEntity> allEvelateEntities;
    private AllTalkFragment evalueteActivity;
    private ShouNewApplication shouNewApplication;
    private LayoutInflater inflater;

    public AllEvelateAdapter(AllTalkFragment evalueteActivity, ShouNewApplication shouNewApplication, ArrayList<AllEvelateEntity> allEvelateEntities) {
        this.allEvelateEntities = allEvelateEntities;
        this.evalueteActivity = evalueteActivity;
        this.shouNewApplication = shouNewApplication;
        inflater = LayoutInflater.from(evalueteActivity.getActivity());
    }

    @Override
    public AllEvelateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AllEvelateViewHolder(inflater.inflate(R.layout.layout_evelate_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final AllEvelateViewHolder holder, final int position) {
        final AllEvelateEntity allEvelateEntity = allEvelateEntities.get(position);

        if (allEvelateEntity == null) {
            return;
        }
        holder.evelute_content.setText(allEvelateEntity.getDText());
        holder.nicheng_tv.setText(TextUtils.isEmpty(allEvelateEntity.getDUname())?"首牛用户":allEvelateEntity.getDUname());
        holder.fabulous.setText(String.valueOf(allEvelateEntity.getDNicecount() > 9999 ? "9999+" : allEvelateEntity.getDNicecount()));
        holder.fabulous.setCompoundDrawablesWithIntrinsicBounds(allEvelateEntity.getDIsnice() == 1
                ? R.drawable.fabulous_orange
                : R.drawable.fabulous_gray, 0, 0, 0);
        holder.evelute_time.setText(allEvelateEntity.getDDate());
        String url = allEvelateEntity.getDUicon();
        holder.my_info_header_scv.setTag(url);
        if (!TextUtils.isEmpty(url) && url.equals(holder.my_info_header_scv.getTag())) {
            Glide.with(evalueteActivity).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.square_seize).error(R.drawable.square_seize).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    holder.my_info_header_scv.setImageBitmap(resource);
                }
            });
        }
        final String dimg = allEvelateEntity.getDImg();
        if (!TextUtils.isEmpty(dimg)) {
            ArrayList<ImageView> imageViews;
            holder.evelute_img.setShow(true);
            holder.evelute_img.setLineShowCount(3);
            holder.evelute_img.setWidth((int) (shouNewApplication.terminalWidth * 0.7));
            if (dimg.contains(",")) {
                final String dims[] = dimg.split(",");
                int length = dims.length;
                imageViews = holder.evelute_img.getImage(length);
                for (int i = 0; i < length; i++) {
                    final ImageView imageView = imageViews.get(i);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    String urlImg = dims[i];
                    imageViews.get(i).setTag(urlImg);
                    if (!TextUtils.isEmpty(urlImg) && urlImg.equals(imageView.getTag())) {
                        shouNewApplication.loadImg(urlImg, imageView);
                    }
                    PreviewImgUtils.previewImg(imageView, dims, i, shouNewApplication);
                }
            } else {
                imageViews = holder.evelute_img.getImage(1);
                ImageView imageView = imageViews.get(0);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setTag(dimg);
                if (!TextUtils.isEmpty(dimg) && dimg.equals(imageView.getTag())) {
                    shouNewApplication.loadImg(dimg, imageView);
                }
                PreviewImgUtils.previewImg(imageView, new String[]{dimg}, 0, shouNewApplication);
            }

        }
        holder.fabulous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evalueteActivity.fabulous(allEvelateEntity);
            }
        });
        ArrayList<AllEvelateEntity.SubListBean> subList = allEvelateEntity.getSubList();
        holder.reply_ll.setTag(subList);
        if (null != subList && subList.equals(holder.reply_ll.getTag())) {
            handleSubList(holder.reply_ll, allEvelateEntity.getSubList());
        }
    }

    private void handleSubList(LinearLayout reply_ll, ArrayList<AllEvelateEntity.SubListBean> subList) {
        reply_ll.removeAllViews();
        for (AllEvelateEntity.SubListBean subListBean : subList
                ) {
            //sdType：0-追评 1-管理员回复
            long sdType = subListBean.getSdType();
            if (sdType == 1) {
                reply_ll.addView(getLine());
                TextView layout_reply_item = (TextView) inflater.inflate(R.layout.layout_reply_item, null);
                layout_reply_item.setText(Html.fromHtml(String.format("<font color=#ff8a29><big>回复: </big></font>%s", subListBean.getSdText())));
                reply_ll.addView(layout_reply_item);
            } else if (sdType == 0) {
                View layout_zhuiping = inflater.inflate(R.layout.layout_zhuiping_item, null);
                TextView again_evelaute_tv = (TextView) layout_zhuiping.findViewById(R.id.again_evelaute_tv);
                again_evelaute_tv.setText(Html.fromHtml(String.format("<font color=#ff8a29><big>%s天后追加评论:</big></font><br/>%s", subListBean.getSdDaynum(), subListBean.getSdText())));
                WordWrapLayoutiml zhuiping_showImg = (WordWrapLayoutiml) layout_zhuiping.findViewById(R.id.zhuiping_showImg);
                zhuiping_showImg.setShow(true);
                zhuiping_showImg.setWidth((int) (shouNewApplication.terminalWidth * 0.7));
                zhuiping_showImg.setLineShowCount(3);
                String dimg = subListBean.getSdImg();
                if (!TextUtils.isEmpty(dimg)) {
                    ArrayList<ImageView> imageViews;
                    if (dimg.contains(",")) {
                        String dims[] = dimg.split(",");
                        int length = dims.length;
                        imageViews = zhuiping_showImg.getImage(length);
                        for (int i = 0; i < length; i++) {
                            ImageView imageView = imageViews.get(i);
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            String urlImg = dims[i];
                            imageView.setTag(urlImg);
                            if (!TextUtils.isEmpty(urlImg) && urlImg.equals(imageView.getTag())) {
                                shouNewApplication.loadImg(urlImg, imageView);
                            }
                            PreviewImgUtils.previewImg(imageView, dims, i, shouNewApplication);
                        }
                    } else {
                        imageViews = zhuiping_showImg.getImage(1);
                        ImageView imageView = imageViews.get(0);
                        imageView.setTag(dimg);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        if (!TextUtils.isEmpty(dimg) && dimg.equals(imageView.getTag())) {
                            shouNewApplication.loadImg(dimg, imageView);
                        }
                        PreviewImgUtils.previewImg(imageView, new String[]{dimg}, 0, shouNewApplication);
                    }

                }
                reply_ll.addView(layout_zhuiping);
            }

        }


    }

    private View getLine() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 25;
        params.rightMargin = 25;
        params.height = 1;
        View view = new View(evalueteActivity.getActivity());
        view.setBackgroundColor(0xffdadee5);
        view.setLayoutParams(params);
        return view;
    }

    @Override
    public int getItemCount() {
        return allEvelateEntities.size();
    }

    class AllEvelateViewHolder extends RecyclerView.ViewHolder {

        CustomShapeImageView my_info_header_scv;
        TextView nicheng_tv;
        TextView evelute_time;
        TextView evelute_content;
        TextView fabulous;
        WordWrapLayoutiml evelute_img;
        LinearLayout reply_ll;

        public AllEvelateViewHolder(View itemView) {
            super(itemView);
            my_info_header_scv = (CustomShapeImageView) itemView.findViewById(R.id.my_info_header_scv);
            evelute_time = (TextView) itemView.findViewById(R.id.evelute_time);
            evelute_content = (TextView) itemView.findViewById(R.id.evelute_content);
            fabulous = (TextView) itemView.findViewById(R.id.fabulous);
            nicheng_tv = (TextView) itemView.findViewById(R.id.nicheng_tv);
            evelute_img = (WordWrapLayoutiml) itemView.findViewById(R.id.evelute_img);
            reply_ll = (LinearLayout) itemView.findViewById(R.id.reply_ll);
        }
    }
}
