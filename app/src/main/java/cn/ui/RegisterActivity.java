package cn.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gddisplaymap.R;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bean.MsgCode;
import cn.server.Register;
import cn.server.Send;
import cn.tools.MyTools;

public class RegisterActivity extends AppCompatActivity {
    private Button register;
    private Button backToLogin;
    private EditText userId;
    private EditText password;
    private EditText password2;
    private String sendId;
    private String sendPw;
    private Register sendRegister = new Register(RegisterActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_ui);
        userId = (EditText) this.findViewById(R.id.ruserId);
        password = (EditText) this.findViewById(R.id.rpassword); //密码
        password2 = (EditText) this.findViewById(R.id.rpassword2); //验证密码
        backToLogin = (Button) this.findViewById(R.id.backToLogin);
        register = (Button) this.findViewById(R.id.register);
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.leftin, R.anim.leftout);//切换动画
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    //物理按键返回动画
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        overridePendingTransition(R.anim.leftin, R.anim.leftout);
    }

    /**
     * 注册动作
     */
    private void register() {
        String text1 = userId.getText().toString();
        String text2 = password.getText().toString();
        String text3 = password2.getText().toString();
        if (!MyTools.isMobile(text1)) {
            Toast.makeText(this, "手机号码格式错误", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(text2)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else if (!(text2.equals(text3))) {
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        } else {
            sendId = text1;
            sendPw = text2;
            new Thread(sendRegister).start();
        }

    }

    public void isSu(JSONObject jsonObject) {
        int code = 0;
        String userName;
        try {
            code = jsonObject.getInt("isOk");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (code) {
            case 101:
                Toast.makeText(this, "注册成功，正在登陆", Toast.LENGTH_SHORT).show();
                try {
                    userName = jsonObject.getString("userName");
                    Toast.makeText(this, "登录成功，用户名：" + userName, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    //保存登录状态
                    MyTools.setParam(this, "userName", userName);
                    MyTools.setParam(this,"phoneNumber",userId.getText().toString());
                    MyTools.setParam(this, MyTools.IS_LOGIN, true);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(this, "获取用户名失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case 102:
                Toast.makeText(this, "抱歉，服务器发生错误！！！", Toast.LENGTH_SHORT).show();
                break;
            case 103:
                Toast.makeText(this, "该手机号已注册！", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    public String getId() {
        return sendId;
    }

    public String getPw() {
        return sendPw;
    }


}
