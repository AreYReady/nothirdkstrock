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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

//交易标题viewpageadapter
public class TradeTitleAdapter extends PagerAdapter {
    private Context context;
    private List<BeanSymbolConfig.SymbolsBean> list;
    private int pencentPosition;
    private int symbolePosition;

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
        TextView tvSymbol, tvCommisionLevel;
        ImageView ivSymbol;
        tvSymbol = (TextView) view.findViewById(R.id.assetNameView11);
        tvCommisionLevel = (TextView) view.findViewById(R.id.profitRiskView1);
        if(position == symbolePosition){
            tvCommisionLevel.setText(realTimeData.getCycles().get(this.pencentPosition).getPercent()+"%");
        }else {
            tvCommisionLevel.setText(realTimeData.getCycles().get(0).getPercent()+ "%");
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
}
