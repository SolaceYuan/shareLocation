package cn.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.gddisplaymap.R;

import org.json.JSONException;
import org.json.JSONObject;

import cn.server.loginSend;
import cn.tools.MyTools;

public class loginActivity extends AppCompatActivity {

    private Button loginBtn;
    private EditText userId;
    private EditText password;
    private Button register;
    public String sendId;
    public String sendPw;
    private loginSend sendLogin = new loginSend(loginActivity.this);
    private RadioButton yes;
    private RadioButton no;
    private RadioGroup mainGruop;
    private boolean isRemember=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_ui);
        userId = (EditText) this.findViewById(R.id.userId);
        password = (EditText) this.findViewById(R.id.password);
        loginBtn = (Button) this.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAction();
            }
        });
        register = (Button) this.findViewById(R.id.toRegister);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                registerUi();
            }
        });
        mainGruop = (RadioGroup) this.findViewById(R.id.radioGruop);
        yes = (RadioButton) this.findViewById(R.id.rememberRarioButton);
        no = (RadioButton) this.findViewById(R.id.dRememberRarioButton);
        //是否记录登录状态
        mainGruop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                switch (checkedId) {
                    case R.id.rememberRarioButton:
                        //点击执行逻辑
                        isRemember=true;
                        break;
                    case R.id.dRememberRarioButton:
                        isRemember=false;
                        break;
                    default:
                        break;
                }
            }
        });
        boolean isLogin = (Boolean) MyTools.getParam(this, MyTools.IS_LOGIN, false);
        if (isLogin) {
            //Toast.makeText(this, "已登录",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(loginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

    }


    /**
     * 登录动作
     */
    private void loginAction() {
        //Toast.makeText(this, "点击了登录按钮", Toast.LENGTH_SHORT).show();
        String id = userId.getText().toString();
        String pw = password.getText().toString();
        if (!MyTools.isMobile(id)) {
            Toast.makeText(this, "手机号码格式错误", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pw)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        sendId = id;
        sendPw = pw;
        new Thread(sendLogin).start();


    }

    /**
     * 切换注册界面
     */
    private void registerUi() {
        Intent register = new Intent(loginActivity.this, RegisterActivity.class);
        startActivity(register);
        overridePendingTransition(R.anim.leftin, R.anim.leftout);
    }

    public String getSendId() {
        return sendId;
    }

    public String getSendPw() {
        return sendPw;
    }

    public void isSu(JSONObject jsonObject) {
        int code = 0;
        String userName;
        try {
            code = jsonObject.getInt("isOk");
        } catch (JSONException e) {
            Toast.makeText(this, "手机号尚未注册", Toast.LENGTH_SHORT).show();
        }
        if (code == 1) {
            try {
                userName = jsonObject.getString("userName");
                Toast.makeText(this, "登录成功，用户名：" + userName, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(loginActivity.this, HomeActivity.class);
                //保存登录状态
                if(isRemember){
                    MyTools.setParam(this, "userName", userName);
                    MyTools.setParam(this,"phoneNumber",userId.getText().toString());
                    MyTools.setParam(this, MyTools.IS_LOGIN, true);
                }
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "获取用户名失败", Toast.LENGTH_SHORT).show();
            }
        }
        if (code == 100) {
            Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
        } else if (code == 2) {
            Toast.makeText(this, "手机号尚未注册", Toast.LENGTH_SHORT).show();

        }

    }
}
