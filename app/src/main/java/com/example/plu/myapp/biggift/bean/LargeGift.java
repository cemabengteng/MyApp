package com.example.plu.myapp.biggift.bean;

/**
 * Created by chengXing on 2016/12/28.
 * 文件中的每一个大额礼物
 */

public class LargeGift {
    private String path;
    private String name;
    private boolean isOk;
    private String errorMessage;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
