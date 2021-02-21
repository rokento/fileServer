package com.lxd.web.dto;

import lombok.Data;

@Data
public class ReturnModel<T> {

    private String msg;

    private int code;

    private T data;

    private boolean isSuccess;

    public void success(){
        this.code = 200;
        this.msg = "成功";
        this.isSuccess = true;
    }

    public void success(T data){
        this.code = 200;
        this.msg = "成功";
        this.data = data;
        this.isSuccess = true;
    }

    public void fail(){
        this.code = 500;
        this.msg = "ERROR";
        this.isSuccess = false;
    }

    public void fail(String errmsg){
        this.code = 500;
        this.msg = errmsg;
        this.isSuccess = false;
    }

}
