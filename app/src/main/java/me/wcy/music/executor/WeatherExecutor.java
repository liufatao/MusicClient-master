package me.wcy.music.executor;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import me.wcy.music.R;
import me.wcy.music.utils.binding.Bind;
import me.wcy.music.utils.binding.ViewBinder;


public class WeatherExecutor implements IExecutor {
    private static final String TAG = "WeatherExecutor";
    private Context mContext;
    @Bind(R.id.ll_weather)
    private LinearLayout llWeather;

    public WeatherExecutor(Context context, View navigationHeader) {
        mContext = context.getApplicationContext();
        ViewBinder.bind(this, navigationHeader);
    }

    @Override
    public void execute() {

    }

    @Override
    public void onPrepare() {
    }

    @Override
    public void onExecuteSuccess(Object o) {
    }

    @Override
    public void onExecuteFail(Exception e) {
    }
}
