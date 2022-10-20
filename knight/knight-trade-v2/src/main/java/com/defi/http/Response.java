package com.defi.http;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiang
 */
@Data
@NoArgsConstructor
public class Response implements java.io.Serializable {

    private int errcode = 0;
    private String errmsg = "ok";
    private Object data;

    public static Response ok(Object data) {
        return new Response(data);
    }

    public static Response ok() {
        return new Response();
    }

    public static Response err(int errcode, String errmsg) {
        return new Response(errcode, errmsg);
    }

    private Response(Object data) {
        this.data = data;
    }

    private Response(int errcode, String errmsg) {
        this.errcode = errcode;
        this.errmsg = errmsg;
    }
}