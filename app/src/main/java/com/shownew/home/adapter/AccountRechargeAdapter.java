package com.shownew.home.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shownew.home.R;
import com.shownew.home.activity.AccountRechargeActivity;
import com.shownew.home.module.entity.SelectAccountMoneyEntity;
import com.wp.baselib.BaseObjectAdapter;
import com.wp.baselib.utils.StringUtil;

import java.util.ArrayList;

/**
 * 选择金额适配器
 * @author Jason
 * @version 1.0
 * @date 2017/4/21 0021
 */

public class AccountRechargeAdapter extends BaseObjectAdapter {

    private ArrayList<SelectAccountMoneyEntity> datas;
    private Context context;

    public AccountRechargeAdapter(Context context, ArrayList<SelectAccountMoneyEntity> datas) {
        super(context, datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AccountRechargeViewHolder rechargeViewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.textview, parent, false);
            rechargeViewHolder = new AccountRechargeViewHolder(convertView);
            convertView.setTag(rechargeViewHolder);

        } else {
            rechargeViewHolder = (AccountRechargeViewHolder) convertView.getTag();
        }
        rechargeViewHolder.setValues(datas, position);

        return convertView;
    }

    class AccountRechargeViewHolder extends BaseViewHolder<ArrayList<SelectAccountMoneyEntity>> {
        TextView mTextView;

        public AccountRechargeViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.account_text);
        }

        @Override
        public void setValues(ArrayList<SelectAccountMoneyEntity> object, final int position) {
            if (datas.get(position).isSelect()) {
                mTextView.setBackgroundResource(R.drawable.shape_account_money_press);
                mTextView.setTextColor(context.getResources().getColor(R.color.color_orgin_select));
            } else {
                mTextView.setBackgroundResource(R.drawable.select_account_money);
                mTextView.setTextColor(context.getResources().getColor(R.color.select_color_orgin));
            }
            final String money = datas.get(position).getMoney();
            mTextView.setText(StringUtil.formatMoney(money));
            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int size = getCount();
                    for (int i = 0; i < size; i++) {
                        SelectAccountMoneyEntity accountMoneyEntity = datas.get(i);
                        if (i == position) {
                            accountMoneyEntity.setSelect(!accountMoneyEntity.isSelect());
                            if (context instanceof AccountRechargeActivity) {
                                AccountRechargeActivity rechargeActivity = (AccountRechargeActivity) context;
                                rechargeActivity.itemClick(accountMoneyEntity.isSelect() ? money : "");
                            }
                        } else {
                            accountMoneyEntity.setSelect(false);
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }
}
