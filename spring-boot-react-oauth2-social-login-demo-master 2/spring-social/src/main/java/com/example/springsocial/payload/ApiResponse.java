package com.example.springsocial.payload;

import com.example.springsocial.model.ChatList;

import java.util.List;

public class ApiResponse {
    private boolean success;
    private String message;

    private List<ChatList> Data;

    private long chatId;

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    public ApiResponse(boolean success,String message,long chatId){
        this.success=success;
        this.message=message;
        this.chatId=chatId;
    }
    public ApiResponse(){

    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(List<ChatList> data) {
        Data = data;
    }

    public List<ChatList> getData() {
        return Data;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }
}
