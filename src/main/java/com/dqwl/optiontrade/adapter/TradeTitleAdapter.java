package com.dqwl.optiontrade.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dqwl.optiontrade.R;
import com.dqwl.optiontrade.bean.BeanSymbolConfig;
import com.dqwl.optiontrade.mvp.trade_index.TradeIndexActivity;
import com.dqwl.optiontrade.util.TimeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

//交易标题viewpageadapter
public class TradeTitleAdapter extends PagerAdapter {
    private Context context;
    private List<BeanSymbolConfig.SymbolsBean> list;
    private int pencentPosition;
    private int symbolePosition;
    TextView tvSymbol, tvCommisionLevel;
    ImageView ivSymbol;
    public TradeTitleAdapter(Context context, List<BeanSymbolConfig.SymbolsBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {

        if (list != null && list.size() > 0) {
            return list.size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BeanSymbolConfig.SymbolsBean realTimeData = list.get(position);
        View view = LayoutInflater.from(context).inflate(R.layout.view_pager_trade, null);

        tvSymbol = (TextView) view.findViewById(R.id.assetNameView11);
        tvCommisionLevel = (TextView) view.findViewById(R.id.profitRiskView1);
//        if(isZero){
//            tvCommisionLevel.setText("0%");
//        }
//        else
        if(position == symbolePosition){
            //如果不在时间里,即为0
            tvCommisionLevel.setText(realTimeData.getCycles().get(this.pencentPosition).getPercent()+"%");
            if(!setPercent(realTimeData)){
                tvCommisionLevel.setText("0%");
            }
        }else {
           if(!setPercent(realTimeData)){
               tvCommisionLevel.setText("0%");
           }
        }
        ivSymbol = (ImageView) view.findViewById(R.id.labelAssetIcon1);
//        ivSymbol.setImageResource(context.getResources().getIdentifier(realTimeData.getSymbol().toLowerCase(), "mipmap-xhdpi", context.getPackageName()));
        AssetManager assetManager = context.getAssets();
        InputStream is = null;
        try {
            is = assetManager.open(realTimeData.getSymbol().toLowerCase()+".png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable image = Drawable.createFromStream(is, "src");
        ivSymbol.setImageDrawable(image);
        tvSymbol.setText(realTimeData.getDesc());
        container.addView(view);
        return view;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setPercentPostion(int symbolPosion, int pencentPosion){
        this.pencentPosition = pencentPosion;
        this.symbolePosition = symbolPosion;
        notifyDataSetChanged();
    }

    public void initPercnet(){
        this.pencentPosition = 0;
    }
    private boolean setPercent(BeanSymbolConfig.SymbolsBean realTimeData){
        long offsetTimeSS = TradeIndexActivity.tz_detla * 60 * 60 * 1000;
        if(realTimeData.getCycles()!=null) {
            for (BeanSymbolConfig.SymbolsBean.CyclesBean cyclesBean : realTimeData.getCycles()) {
                //根结束时间比,如果小于,就跟开始时间比.判断是否在这个时间内,如果大于,直接跳过,在遍历
                try {
                    if (cyclesBean.getTimes() == null) {
                        tvCommisionLevel.setText(cyclesBean.getPercent() + "%");
                        return true;
                    } else if (TimeUtils.getCurrentTimeHHMMNoS() - TimeUtils.stringToLong(cyclesBean.getTimes().get(0).getE(), "HH:mm") - offsetTimeSS <= 0) {
                        if (TimeUtils.getCurrentTimeHHMMNoS() - TimeUtils.stringToLong(cyclesBean.getTimes().get(0).getB(), "HH:mm") - offsetTimeSS >= 0) {
                            tvCommisionLevel.setText(cyclesBean.getPercent() + "%");
                            return true;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

}
