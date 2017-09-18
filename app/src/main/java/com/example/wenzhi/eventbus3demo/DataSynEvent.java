package com.example.wenzhi.eventbus3demo;

/**
 * 事件类
 * 其实就是一个bean类
 */
public class DataSynEvent {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
