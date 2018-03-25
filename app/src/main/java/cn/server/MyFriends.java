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
import cn.ui.FriendsActivity;
import cn.ui.HomeActivity;

/**
 * Created by Solace on 2018/3/9.
 */

public class MyFriends implements Runnable {

    private String Ip = MyTools.getIp();
    private Integer port = 8080;
    private JSONObject jsonObject;
    private Socket socket;
    private OutputStream outputStream;
    private MyFriendsRecive inputStream;
    private FriendsActivity friendsActivity;

    public MyFriends(FriendsActivity friendsActivity) {
        this.friendsActivity = friendsActivity;
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
            inputStream = new MyFriendsRecive(socket.getInputStream());
            inputStream.setMainActivity(friendsActivity);
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
            jsonObject.put("code", 004);//004查询好友
            jsonObject.put("id", friendsActivity.getUserId());
            outputStream.write(jsonObject.toString().getBytes("UTF-8"));
            outputStream.flush();
            Log.i("128", "数据：" + jsonObject.toString());

        } catch (Exception e) {
            Log.i("128", e.getMessage() + "上传出错");
            Looper.prepare();
            Toast.makeText(friendsActivity,"服务器连接超时",Toast.LENGTH_SHORT);
            Looper.loop();
        }

    }
}