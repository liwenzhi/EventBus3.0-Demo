#EventBus3.0框架使用详细讲解


EventBus和RxJava、RxAndroid出现的时期是差不多的，而且功效也是类似，之前有用过RxJava和RxAndroid，其实很是比较麻烦的，那些类太多了，很多名词不用就不记得了，并且学起来耗费不少时间。


##EventBus概述


EventBus是针一款对Android的发布/订阅事件总线。它可以让我们很轻松的实现在Android各个组件之间传递消息，并且代码的可读性更好，耦合度更低。
现在最新版本是3.0，新版本比旧版本好用很多，无论是注册，发送，监听都是一句话！


##EventBus有多强大！

###1.Activity之间数据传递

我们之前要在不同页面发送数据，一般的做法是用Intent传递，但是如果是隔了很多个的Activity就要用动态广播的监听和接收了，写那么多广播的代码，想想都可怕！
而使用了EventBus框架，几句话完全搞定，并且逻辑层次清晰。


###2.Fragment和Activity数据传递

Fragment和Activity数据传递，如果对于页面比较熟的话应该是不难的，但是如果有子线程，并且有回调（在请求网络完成后才能显示数据），这就比较麻烦了。
如果你使用EventBus，传递数据不成问题，线程切换很方便，回调都是小事（事情完成再发送事件，另一边就会接收）。

###2.Fragment和Fragment数据传递

同上！

###3.同一个页面的子线程和主线程切换

子线程请求数据，主线程处理数据，是我们经常要用到的模式。我们可以用Handler，但是代码不少！
使用EvnetBus你会发现更加简单。


##EventBus的使用

EventBus的官方地址：https://github.com/greenrobot/EventBus


###1.build.gradle添加引用 

```
compile 'org.greenrobot:eventbus:3.0.0'
```

这个老司机应该都会了的，就不截图了。把这句话放在你的项目（不是工程）的build.gradle的dependencies的花括号里面就可以了。
如果不是Studio开发的就用相关的Jar包，这里不解释。


###2.定义一个事件类型

其实就是一个Bean类，里面定义用来传输的数据的类型，可以多个，多种。。。。

```

/**
 * 事件类
 * 其实就是一个bean类
 */
public class DataSynEvent {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

```


###3.订阅

```
EventBus.getDefault().register(this);//订阅
```
这里订阅既可以再Application的onCreate中，也是在Activity的onCreate中，或者你点击之后再订阅都是可以的，要求不高。


###4. 解除订阅

```
EventBus.getDefault().unregister(this);//解除订阅
```
如果只是在某一个页面上需要订阅信息，那么你可以在它的onDestroy方法里面解除订阅。

###5.发布事件

```
DataSynEvent  dataSynEvent = new DataSynEvent();//创建事件对象
dataSynEvent.setMessage("发送数据");//給类对象设置数据
EventBus.getDefault().post(dataSynEvent);//发送事件

```
###6.订阅事件处理

```
      /**
     * 事件接收
     * 这里可以在子线程或主线程操作，一般来说也只有在主线程才有实际意义吧。
     */
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(DataSynEvent event) {
        tv_account.append("\n" + event.getMessage());//把接收到的数据发到文本中
    }

```
####ThreadMode总共四个：
#####（1）NAIN，UI主线程
#####（2）BACKGROUND ，后台线程
#####（3）POSTING ，和发布者处在同一个线程
#####（4）ASYNC ，异步线程

主要都是用第一个和第二个！


####订阅事件的优先级
事件的优先级类似广播的优先级，优先级越高优先获得消息
```
 @Subscribe(threadMode = ThreadMode.MAIN,priority = 100) //在ui线程执行 优先级100
    public void onDataSynEvent(DataSynEvent event) {
        tv_account.append("\n" + event.getMessage());//把接收到的数据发到文本中
    }

```

####终止事件往下传递
发送有序广播可以终止广播的继续往下传递，EventBus也实现了此功能
```
 EventBus.getDefault().cancelEventDelivery(event) ;//优先级高的订阅者可以终止事件往下传递

```


###7.EventBus黏性事件
正常情况基本不用粘性事件，除非特殊情况。
EventBus除了普通事件也支持粘性事件，这个有点类似广播分类中的粘性广播。本身粘性广播用的就比较少，为了方便理解成订阅在发布事件之后，但同样可以收到事件。订阅/解除订阅和普通事件一样，但是处理订阅函数有所不同，需要注解中添加sticky = true

```
   @Subscribe(threadMode = ThreadMode.MAIN,sticky = true) //在ui线程执行
    public void onDataSynEvent(DataSynEvent event) {
        tv_account.append("\n" + event.getMessage());//把接收到的数据发到文本中
    }

```
####发送粘性事件

```
DataSynEvent  dataSynEvent = new DataSynEvent();//创建事件对象
dataSynEvent.setMessage("发送数据");//給类对象设置数据
 EventBus.getDefault().postSticky(dataSynEvent);//发送粘性事件
```
####清除粘性事件
粘性事件是可以清除的，普通的是不可以的，但是你可以通过逻辑判断，不处理那些数据也是可以的。
```
 EventBus.getDefault().postSticky(dataSynEvent);//清除某一个类的数据
 EventBus.getDefault().removeAllStickyEvents();//清除所有的数据
```



###Android广播的分类：
####（1） 普通广播：这种广播可以依次传递给各个处理器去处理
####（2） 有序广播：这种广播在处理器端的处理顺序是按照处理器的不同优先级来区分的，高优先级的处理器会优先截获这个消息，并且可以将这个消息删除
####（3） 粘性消息：粘性消息在发送后就一直存在于系统的消息容器里面，等待对应的处理器去处理，如果暂时没有处理器处理这个消息则一直在消息容器里面处于等待状态，粘性广播的Receiver如果被销毁，那么下次重建时会自动接收到消息数据。
注意：普通广播和粘性消息不同被截获，而有序广播是可以被截获的。
在Android系统粘性广播一般用来确保重要的状态改变后的信息被持久保存，并且能随时广播给新的广播接收器，比如电源的改变，防止因为没电某些数据没有及时保存。
 

##EvetnBus的一个示例程序

###动态图：

![1](https://i.imgur.com/uuZcGiJ.gif)

可以看到程序实现了不同线程的数据处理和不同页面的数据传递。

###代码
这里至提供MainActivity的代码，第二个页面的代码是很简单的啦。
```

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


```

程序中没有做阻断，拦截等操作，只是做了简单的注册，发送和接收，其他的操作根据上面的介绍来实现应该是不难的吧。

程序示例的全部代码：https://github.com/liwenzhi/EventBus3.0-Demo


##扩展
一个程序是可以存在多个订阅者和多个不同的发送者。。。。
如果一个Activity中有三四个Fragment，某一个Fragment数据改变，其他Fragment都改变数据，使用EventBus可以很方便实现。

##EventBus那么强大，它的底层是怎么实现的呢？
这里只是简单说一个原理，具体的代码我也是没有研究的。
里面有注解器的使用，还有很多线程处理的代码。
EventBus源码解析：http://www.jianshu.com/p/f057c460c77e

#共勉：生活如此多娇。
