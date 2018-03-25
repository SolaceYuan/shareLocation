package cn.server;

import android.util.Log;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import cn.tools.MyTools;
import cn.ui.HomeActivity;

/**
 * 发送消息至服务器
 */
public class Send implements Runnable {
    private String Ip = MyTools.getIp();
    private Integer port = 8080;
    private JSONObject jsonObject;
    private Socket socket;
    private OutputStream outputStream;
    private Send_InputStream inputStream;
    private HomeActivity homeActivity;
    Boolean isClose = true;


    public Send(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
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
            SocketAddress socketAddress = new InetSocketAddress(Ip, port);
            socket.connect(socketAddress, 3000);
            Log.i("128", "已连接服务器");
            outputStream = socket.getOutputStream();
            inputStream = new Send_InputStream(socket.getInputStream());
            inputStream.setMainActivity(homeActivity);
            new Thread(inputStream).start();
        } catch (Exception e) {
            Log.i("128", "连接服务器报错" + e.getMessage());
        }

    }

    public void SetIsClose(Boolean isClose) {
        this.isClose = isClose;
    }

    @Override
    public void run() {
        try {
            startSocket();
            jsonObject = new JSONObject();
            while (socket != null && isClose != false) {

                Thread.sleep(3000);
                jsonObject.put("code", 111);
                jsonObject.put("userName", homeActivity.getUserName());
                jsonObject.put("phone", homeActivity.getPhoneNum());
                jsonObject.put("lat", HomeActivity.getLat());
                jsonObject.put("lng", HomeActivity.getLng());
                outputStream.write(jsonObject.toString().getBytes("UTF-8"));
                outputStream.flush();
                Log.i("128", "上传的数据：" + jsonObject.toString());
            }
            if (isClose == false) {
                jsonObject.put("code", 444);
                outputStream.write(jsonObject.toString().getBytes("UTF-8"));
                outputStream.flush();
                Log.i("128", "发送关闭指令");
                outputStream.close();
                socket.close();
            }
        } catch (Exception e) {
            Log.i("128", e.getMessage() + "上传出错");
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                    Log.i("128", "9999999999999outputStream已关闭");
                } catch (Exception e) {
                    Log.i("128", e.getMessage() + "关闭出错");
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                    Log.i("128", "9999999999999socket已关闭");
                } catch (Exception e) {
                    Log.i("128", e.getMessage() + "关闭出错");
                }
            }
        }


    }
}
