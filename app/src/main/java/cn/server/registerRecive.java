package cn.server;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;

import cn.bean.MsgCode;
import cn.ui.RegisterActivity;

/**
 * Created by Solace on 2018/2/26.
 */

public class registerRecive implements Runnable {
    private InputStream inputStream;
    private RegisterActivity registerActivity;
    private String text = "null";

    public registerRecive() {
    }

    public registerRecive(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setMainActivity(RegisterActivity registerActivity) {
        this.registerActivity = registerActivity;
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
                registerActivity.isSu(jsonObject);
                Looper.loop();
//                if (errorCode == 200) {
//                    //JSONArray jsonArray = jsonObject.getJSONArray("date");
//                    //  registerActivity.allLatLng(jsonArray);
////                    MsgCode code=new MsgCode();
////                    code.setCode(200);
////                    Log.i("写入返回代号",text+"返回值");
//                    Looper.prepare();
//                    registerActivity.isSu(200);
//                    Looper.loop();
//                }
//                if (errorCode == 201) {
//                    Looper.prepare();
//                    registerActivity.isSu(201);
//                    Looper.loop();
//                }
            }
        } catch (Exception e) {
            Log.i("129", "接收返回值报错" + e.getMessage());
        }
    }
}
