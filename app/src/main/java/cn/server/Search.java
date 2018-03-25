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
import cn.ui.SearchActivity;

/**
 * Created by Solace on 2018/3/10.
 */

public class Search implements Runnable {

    private String Ip = MyTools.getIp();
    private Integer port = 8080;
    private static JSONObject jsonObject;
    private Socket socket;
    private OutputStream outputStream;
    private SearchRe inputStream;
    private SearchActivity searchActivity;

    public Search(SearchActivity searchActivity) {
        this.searchActivity = searchActivity;
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

    public static void setJsonObject(JSONObject jsonObject) {
        Search.jsonObject = jsonObject;
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
            inputStream = new SearchRe(socket.getInputStream());
            inputStream.setMainActivity(searchActivity);
            new Thread(inputStream).start();
        } catch (Exception e) {
            Log.i("128", e.getMessage() + "报错");
        }

    }

    @Override
    public void run() {
        try {
            startSocket();
            //jsonObject = new JSONObject();
//            jsonObject.put("code", 004);//004查询好友
//            jsonObject.put("id", searchActivity.getUserId());

            JSONObject sendJson=new JSONObject();
            sendJson=this.getJsonObject();
            outputStream.write(sendJson.toString().getBytes("UTF-8"));
            outputStream.flush();
            Log.i("128", "数据：" + sendJson.toString());

        } catch (Exception e) {
            Log.i("128", e.getMessage() + "上传出错");
            Looper.prepare();
            Toast.makeText(searchActivity,"服务器连接超时",Toast.LENGTH_SHORT);
            Looper.loop();
        }

       }



}