package cn.server;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import cn.tools.MyTools;
import cn.ui.UserInfoActivity;

/**
 * Created by Solace on 2018/3/8.
 */

public class SaveInfo  implements Runnable{
    private String Ip = MyTools.getIp();
    private Integer port = 8080;
    private JSONObject jsonObject;
    private Socket socket;
    private OutputStream outputStream;
    private SaveReceive inputStream;
    private UserInfoActivity userInfoActivity;

    public SaveInfo(UserInfoActivity userInfoActivity) {
        this.userInfoActivity = userInfoActivity;
    }

    public String getIp() {
        return Ip;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public Socket getSocket() {
        return socket;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setIp(String ip) {
        Ip = ip;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    private void startSocket() {
        try {
            socket = new Socket();
            SocketAddress socketAddress=new InetSocketAddress(Ip,port);
            socket.connect(socketAddress,3000);
            Log.i("128", "已连接服务器");
            outputStream = socket.getOutputStream();
            inputStream = new SaveReceive(socket.getInputStream());
            inputStream.setMainActivity(userInfoActivity);
            new Thread(inputStream).start();
        } catch (Exception e) {
            Toast.makeText(userInfoActivity, "连接超时", Toast.LENGTH_SHORT).show();

        }

    }

    public void run() {
        try {
            startSocket();
            jsonObject = new JSONObject();
            jsonObject.put("code", 003);//003修改昵称代号
            jsonObject.put("userId",userInfoActivity.getId());
            jsonObject.put("userName", userInfoActivity.getName());
            outputStream.write(jsonObject.toString().getBytes("UTF-8"));
            outputStream.flush();
            Log.i("128", "修改数据：" + jsonObject.toString());

        } catch (Exception e) {
            Looper.prepare();
            Log.i("128", e.getMessage() + "上传出错");
            Toast.makeText(userInfoActivity, "连接超时code02", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
// finally {
//            if (outputStream != null) {
//                try {
//                    outputStream.close();
//                } catch (Exception e) {
//                    Log.i("128", e.getMessage() + "关闭出错");
//                }
//            }
//            if (socket != null) {
//                try {
//                    socket.close();
//                } catch (Exception e) {
//                    Log.i("128", e.getMessage() + "关闭出错");
//                }
//            }
//        }
    }
}