package cn.bean;

/**
 * Created by Solace on 2018/3/7.
 */
public class HelloBean {

    private int icon;
    private String textView;

    public HelloBean(int icon, String textView) {
        this.icon = icon;
        this.textView = textView;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTextView() {
        return textView;
    }

    public void setTextView(String textView) {
        this.textView = textView;
    }
}