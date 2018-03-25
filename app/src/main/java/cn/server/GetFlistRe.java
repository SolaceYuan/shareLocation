package cn.server;

import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;

import cn.ui.FriendsActivity;
import cn.ui.HomeActivity;

/**
 * Created by Solace on 2018/3/14.
 */

public class GetFlistRe implements Runnable {
    private InputStream inputStream;
    private HomeActivity homeActivity;
    private String text = "null";

    public GetFlistRe() {
    }

    public GetFlistRe(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setMainActivity(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    public void run() {
        int len = 0;
        byte[] buff = new byte[1024];
        try {
            while ((len = inputStream.read(buff)) != -1) {
                text = new String(buff, 0, len);
                Log.i("129", text + "返回值");
                JSONObject jsonObject = new JSONObject(text);
                //int errorCode = jsonObject.getInt("errorCode");
                Looper.prepare();
                homeActivity.setFriends(jsonObject);
                Looper.loop();
            }
        } catch (Exception e) {
            Log.i("129", "接收返回值报错" + e.getMessage());
        }
    }
}