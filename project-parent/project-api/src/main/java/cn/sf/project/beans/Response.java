package cn.sf.project.beans;

import lombok.ToString;

import java.io.Serializable;

@ToString
public class Response<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean success;
    private T result;
    private String error;

    public Response() {
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getResult() {
        return this.result;
    }

    public void setResult(T result) {
        this.success = true;
        this.result = result;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.success = false;
        this.error = error;
    }

    public static <T> Response<T> ok(T data) {
        Response resp = new Response();
        resp.setResult(data);
        return resp;
    }

    public static <T> Response<T> ok() {
        return ok(null);
    }

    public static <T> Response<T> fail(String error) {
        Response resp = new Response();
        resp.setError(error);
        return resp;
    }
}