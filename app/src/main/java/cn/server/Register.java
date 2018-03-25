package cn.server;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import cn.tools.MyTools;
import cn.ui.RegisterActivity;

/**
 * Created by Solace on 2018/2/26.
 */

public class Register implements Runnable {

    private String Ip = MyTools.getIp();
    private Integer port = 8080;
    private JSONObject jsonObject;
    private Socket socket;
    private OutputStream outputStream;
    private registerRecive inputStream;
    private RegisterActivity registerActivity;

    public Register(RegisterActivity registerActivity) {
        this.registerActivity = registerActivity;
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
            inputStream = new registerRecive(socket.getInputStream());
            inputStream.setMainActivity(registerActivity);
            new Thread(inputStream).start();
        } catch (Exception e) {
            Log.i("128", e.getMessage() + "报错");
        }

    }

    @Override
    public void run() {
        try {
            startSocket();
            jsonObject = new JSONObject();
            jsonObject.put("code", 001);//001注册代号
            jsonObject.put("id", registerActivity.getId());
            jsonObject.put("pw", registerActivity.getPw());
            outputStream.write(jsonObject.toString().getBytes("UTF-8"));
            outputStream.flush();
            Log.i("128", "注册数据：" + jsonObject.toString());

        } catch (Exception e) {
            Log.i("128", e.getMessage() + "上传出错");
            Toast.makeText(registerActivity,"服务器连接超时",Toast.LENGTH_SHORT);
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
