package cn.server;

import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;

import cn.ui.MessageActivity;

/**
 * Created by Solace on 2018/3/10.
 */

public class GetMessageRe implements Runnable {
    private InputStream inputStream;
    private MessageActivity messageActivity;
    private String text = "null";

    public GetMessageRe() {
    }

    public GetMessageRe(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setMainActivity(MessageActivity messageActivity) {
        this.messageActivity = messageActivity;
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
                messageActivity.isSu(jsonObject);
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
