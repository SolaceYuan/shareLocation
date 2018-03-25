package cn.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.gddisplaymap.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import cn.bean.HelloAdapter;
import cn.bean.HelloBean;
import cn.server.Search;
import cn.tools.MyTools;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private Button searchBtn;
    private Search searchSend = new Search(SearchActivity.this);

    private ListView listView;
    private List<HelloBean> mdata = null;
    private HelloAdapter madapter = null;
    private Context mcontext = null;
    private Handler handler=null;
    private EditText etSearch;
    private String searchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchBtn=(Button) this.findViewById(R.id.btnSearch);
        searchBtn.setOnClickListener(this);
        etSearch=(EditText)findViewById(R.id.etSearch);
        handler=new Handler();
        mcontext = SearchActivity.this;
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
                final String tv=mdata.get(position).getTextView();
                Dialog alertDialog=new AlertDialog.Builder(SearchActivity.this).setTitle("提示")
                        .setMessage("申请添加好友？").setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //发送好友申请
                                addFriends(tv);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                alertDialog.show();
                //Toast.makeText(FriendsActivity.this, "id="  + tv.toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void addFriends(String reId){
        JSONObject sendJson=new JSONObject();
        try {
            sendJson.put("code", 006);//申请添加好友
            sendJson.put("recieveId",reId );
            sendJson.put("sendId",this.getUserId() );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Search.setJsonObject(sendJson);
        new Thread(searchSend).start();
    }

    public String getUserId(){
        String userId=null;
        userId= MyTools.getParam(this,"phoneNumber","String").toString();
        return userId;
    }

    public void isSu(JSONObject jsonObject){
        int code = 0;
        try {
            code = jsonObject.getInt("isOk");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (code) {
            case 101:
                Toast.makeText(this, "已发送好友申请", Toast.LENGTH_SHORT).show();
                break;
            case 102:
                Toast.makeText(this, "未知错误，申请好友失败", Toast.LENGTH_SHORT).show();
                break;
            case 103:
                Toast.makeText(this, "查无此人！", Toast.LENGTH_SHORT).show();
                break;
            case 104:
                //存在此人，更新列表
                new Thread(){
                    public void run(){
                        handler.post(runnableUi);
                    }
                }.start();
                break;
            default:
                break;
        }
    }

    //更新列表
    Runnable runnableUi=new Runnable() {
        @Override
        public void run() {
                madapter.add(new HelloBean(R.mipmap.ic_friends, searchId));
                    }
    };


    @Override
    public void onClick(View v) {//搜索好友

        searchId=etSearch.getText().toString();
        if(searchId.equals(this.getUserId())){
            Toast.makeText(this,"此人正是阁下",Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject sendJson=new JSONObject();
        try {
            sendJson.put("code", 007);//搜索好友
            sendJson.put("userId",searchId );

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Search.setJsonObject(sendJson);
        new Thread(searchSend).start();
    }
}
