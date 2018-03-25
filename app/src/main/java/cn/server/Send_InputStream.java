package cn.server;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.tools.MyApp;
import cn.tools.MyTools;
import cn.ui.HomeActivity;
import cn.ui.HomeActivity.MyHandler;

/**
 * 接收消息
 */
public class Send_InputStream implements Runnable {

    private InputStream inputStream;
    private HomeActivity homeActivity;
    private String text = "";
    private boolean isClose = false;
    BufferedReader mReader;
    private MyApp myApp = null;
    private MyHandler handler = null;

    public Send_InputStream() {
    }

    public Send_InputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setMainActivity(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    @Override
    public void run() {
        myApp = (MyApp) homeActivity.getApplication();
        handler = myApp.getHandler();
        int len = 0;
        byte[] buff = new byte[1024];

        try {
//                while(true){
//                    Log.i("129", "接收到信息了1111111111111111返回值");
//                    if(isClose){
//                        break;
//                    }
            Thread.sleep(3000);
//            mReader = new BufferedReader(new InputStreamReader(inputStream));
//            while (true) {
//                if (mReader.ready()) {
//                    while (mReader.readLine() != null) {
//                        String re = mReader.readLine().toString();
//                        Log.i("129", "22222222222222222222222返回值" + re);
//                        JSONObject jsonObject = new JSONObject(re);
//                        int errorCode = jsonObject.getInt("errorCode");
//                        if (errorCode == 200) {
//                            JSONArray jsonArray = jsonObject.getJSONArray("data");
//                            Log.i("129", "jsonArray" + jsonArray.toString());
//                            Message ms = new Message();
//                            ms = Message.obtain();
//                            ms.obj = jsonArray;
//                            handler.sendMessage(ms);
//
//                        }
//                    }
//                }
//            }

            while (!isClose) {
                while ((len = inputStream.read(buff)) != -1) {
                    text = new String(buff, 0, len);
                    Log.i("129", text + "返回值");
                    JSONObject jsonObject = new JSONObject(text);
                    int errorCode = jsonObject.getInt("errorCode");
                    if (errorCode == 200) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        Log.i("129", "jsonArray" + jsonArray.toString());
                        Message ms = new Message();
                        ms = Message.obtain();
                        ms.obj = jsonArray;
                        handler.sendMessage(ms);
                    }
                }
            }if(isClose){
                try{
                    inputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }


        } catch (Exception e) {
            Log.i("129", e.getMessage() + "接收返回值报错");
        }


    }

    /**
     * 关闭
     * @param isClose
     */
    public void setClose(Boolean isClose){
        this.isClose=isClose;
    }
}
