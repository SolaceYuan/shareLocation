package cn.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gddisplaymap.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import cn.bean.HelloAdapter;
import cn.bean.HelloBean;
import cn.server.MyFriends;
import cn.tools.MyTools;


public class FriendsActivity extends AppCompatActivity {

    private ListView listView;
    private List<HelloBean> mdata = null;
    private HelloAdapter madapter = null;
    private Context mcontext = null;
    private int i = 1;
    private Handler handler=null;
    String[] friendsList;
    private MyFriends sendId = new MyFriends(FriendsActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        handler=new Handler();
        mcontext = FriendsActivity.this;
        listView = (ListView) findViewById(R.id.list_item);
        mdata = new LinkedList<HelloBean>();
        madapter = new HelloAdapter((LinkedList<HelloBean>) mdata, mcontext);
        listView.setAdapter(madapter);
        //列表点击事件，获取ID添加好友
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = listView.getItemAtPosition(position) + "";
                //获取点击的手机号
                String tv=mdata.get(position).getTextView();
                Toast.makeText(FriendsActivity.this, "id="  + tv.toString(), Toast.LENGTH_SHORT).show();

            }
        });
        final FloatingActionButton button = (FloatingActionButton) findViewById(R.id.add);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FriendsActivity.this, SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.leftin, R.anim.leftout);
            }
        });
        init();
    }
    private void init(){
        new Thread(sendId).start();
    }
    public String getUserId(){
        String userId=null;
        userId= MyTools.getParam(this,"phoneNumber","String").toString();
        return userId;
    }


    /**
     * 处理查询的好友，更新好友列表
     * @param jsonObject
     */
    public void isSu(JSONObject jsonObject){
        String friends="";
        try {
            friends=jsonObject.getString("friends");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(friends.length()>0){
            friendsList=friends.split(",");
            new Thread(){
                public void run(){
                    handler.post(runnableUi);
                }
            }.start();
        }



    }
    Runnable runnableUi=new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < friendsList.length; i++) {
                //System.out.println(friendsList[i]);
                madapter.add(new HelloBean(R.mipmap.ic_friends, friendsList[i]));
            }        }
    };
    //物理按键返回动画
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        overridePendingTransition(R.anim.leftin, R.anim.leftout);
    }
}
