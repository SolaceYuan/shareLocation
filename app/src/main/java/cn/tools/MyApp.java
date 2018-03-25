package cn.tools;

import android.app.Application;
import cn.ui.HomeActivity.MyHandler;


/**
 * Created by Solace on 2018/3/13.
 */

public class MyApp extends Application{

    private MyHandler handler=null;

    public void setHandler(MyHandler handler){
        this.handler=handler;
    }

    public MyHandler getHandler(){
        return  handler;
    }
}
