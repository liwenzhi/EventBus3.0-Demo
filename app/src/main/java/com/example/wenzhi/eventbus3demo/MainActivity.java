package com.example.wenzhi.eventbus3demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    TextView     tv_account;
    DataSynEvent dataSynEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_account = (TextView) findViewById(R.id.tv_count);
        EventBus.getDefault().register(this);//订阅
        dataSynEvent = new DataSynEvent();//创建事件对象
    }

    /**
     * 发送事件
     * 这里可以在子线程，也可以在主线程
     */
    public void addCount(View view) {
        dataSynEvent.setMessage("本页面发送数据");
        EventBus.getDefault().post(dataSynEvent);//发送事件
    }

    /**
     * 发送事件
     * 这里在子线程
     */
    public void addCount2(View view) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);//睡两秒
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        dataSynEvent.setMessage("两秒后子线程发送数据");
                        EventBus.getDefault().post(dataSynEvent);//发送事件
                    }
                }
        ).start();
    }


    /**
     * 事件接收
     * 这里可以在子线程或主线程操作，一般来说也只有在主线程才有实际意义吧。
     */
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(DataSynEvent event) {
        tv_account.append("\n" + event.getMessage());
    }

    /**
     * 跳转到其他页面
     */
    public void jumpOthers(View view) {
        startActivity(new Intent(this, OtherActivity.class));
    }
}
