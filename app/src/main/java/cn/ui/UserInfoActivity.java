package cn.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gddisplaymap.R;

import org.json.JSONException;
import org.json.JSONObject;

import cn.server.SaveInfo;
import cn.tools.MyTools;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText phoneNumber;
    private EditText userName;
    private Button saveBtn;
    public String sendName;
    private String sendId;
    private HomeActivity homeActivity = new HomeActivity();
    private SaveInfo saveInfo = new SaveInfo(UserInfoActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        phoneNumber = (EditText) findViewById(R.id.info_userId);
        userName = (EditText) findViewById(R.id.myUserName);
        saveBtn = (Button) findViewById(R.id.saveInfo);
        saveBtn.setOnClickListener(this);

        init();

    }

    private void init() {
        phoneNumber.setText(MyTools.getParam(this, "phoneNumber", "String").toString());
        userName.setText(MyTools.getParam(this, "userName", "String").toString());
    }

    @Override
    public void onClick(View v) {
        saveAction();
    }

    public String getName() {
        return sendName;
    }

    public String getId() {
        return sendId;
    }

    public void saveAction() {
        String name = userName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        sendName = name;
        sendId = phoneNumber.getText().toString();
        new Thread(saveInfo).start();
    }

    public void isSu(JSONObject jsonObject) {
        try {
            boolean isOk = jsonObject.getBoolean("isOk");
            if (isOk) {
                Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                MyTools.setParam(this, "userName", userName.getText());
            } else {
                Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
        }


    }
}
