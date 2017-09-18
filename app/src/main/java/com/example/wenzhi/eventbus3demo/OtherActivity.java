package com.example.wenzhi.eventbus3demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

/**
 * 发送事件的一个页面
 */

public class OtherActivity extends AppCompatActivity {
    DataSynEvent dataSynEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        dataSynEvent = new DataSynEvent();//创建事件对象
    }

    /**
     * 发送事件
     * 这里可以在子线程，也可以在主线程
     */
    public void addCount(View view) {
        dataSynEvent.setMessage("其他页面发送数据");
        EventBus.getDefault().post(dataSynEvent);//发送事件
    }

    /**
     * 关闭页面
     */
    public void finish(View view) {
        finish();
    }
}
