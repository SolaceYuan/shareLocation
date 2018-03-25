package cn.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.gddisplaymap.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.bean.HelloAdapter;
import cn.bean.HelloBean;
import cn.server.GetMessage;
import cn.server.MyFriends;
import cn.tools.MyTools;

public class MessageActivity extends AppCompatActivity {

    private ListView listView;
    private List<HelloBean> mdata = null;
    private HelloAdapter madapter = null;
    private Context mcontext = null;
    private int i = 1;
    private Handler handler = null;
    String[] friendsList;
    JSONArray messageList;
    private GetMessage sendId = new GetMessage(MessageActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        handler = new Handler();
        mcontext = MessageActivity.this;
        listView = (ListView) findViewById(R.id.list_item);
        mdata = new LinkedList<HelloBean>();
        madapter = new HelloAdapter((LinkedList<HelloBean>) mdata, mcontext);
        listView.setAdapter(madapter);
        //列表点击事件，获取ID同意好友申请
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String text = listView.getItemAtPosition(position) + "";
                //获取点击的手机号
                final String tv = mdata.get(position).getTextView();
                //Toast.makeText(FriendsActivity.this, "id="  + tv.toString(), Toast.LENGTH_SHORT).show();
                Dialog alertDialog=new AlertDialog.Builder(MessageActivity.this).setTitle("提示")
                        .setMessage("同意申请？").setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //同意好友申请
                                isAgree(tv,true);
                                ((LinkedList<HelloBean>) mdata).remove(position);
                                madapter.notifyDataSetChanged();
                                listView.invalidate();
                                dialog.dismiss();
                            }
                        }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isAgree(tv,false);
                                ((LinkedList<HelloBean>) mdata).remove(position);
                                madapter.notifyDataSetChanged();
                                listView.invalidate();
                                dialog.dismiss();
                            }
                        }).create();
                alertDialog.show();

            }
        });

        init();
    }

    public void isAgree(String id,boolean isA){
        JSONObject jsonObject=new JSONObject();
        id=id.substring(0,11);
        try {
            jsonObject.put("code",108);
            jsonObject.put("isAgree",isA);
            jsonObject.put("recieveId",this.getUserId());
            jsonObject.put("sendId",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GetMessage.setJsonObject(jsonObject);
        new Thread(sendId).start();
    }


    private void init() {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("code", 005);//005查询消息
            jsonObject.put("recieve", this.getUserId());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        GetMessage.setJsonObject(jsonObject);
        new Thread(sendId).start();
    }

    public String getUserId() {
        String userId = null;
        userId = MyTools.getParam(this, "phoneNumber", "String").toString();
        return userId;
    }
    public void isSu(JSONObject jsonObject){
        int code=0;
        try {
            code=jsonObject.getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (code){
            //消息查询返回
            case 101:
                try {
                    messageList=jsonObject.getJSONArray("sends");
                    if(messageList.length()>=1){
                        messageList=new JSONArray();
                        messageList=jsonObject.getJSONArray("sends");
                    }else{
                        Toast.makeText(this,"没有消息",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this,"消息获取错误",Toast.LENGTH_SHORT).show();
                }
                new Thread(){
                    public void run(){
                        handler.post(runnableUi);
                    }
                }.start();
                break;
                //处理好友申请
            case 102:
                boolean isOk=false;
                try{
                   isOk=jsonObject.getBoolean("isOk");
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                if(!isOk){
                    Toast.makeText(this,"服务器错误",Toast.LENGTH_SHORT).show();
                }
                break;
            default:break;
        }



    }

    Runnable runnableUi=new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < messageList.length(); i++) {
                try {
                    madapter.add(new HelloBean(R.mipmap.ic_friends, messageList.getString(i)+"申请添加好友"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MessageActivity.this,"数据解析错误",Toast.LENGTH_SHORT).show();
                }
            }        }
    };

}
