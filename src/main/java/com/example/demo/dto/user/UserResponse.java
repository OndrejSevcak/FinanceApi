package com.example.demo.dto.user;

public class UserResponse {
    private Long userKey;
    private String email;
    private String nickName;
    private Integer level;

    public UserResponse(Long userKey, String email, String nickName, Integer level) {
        this.userKey = userKey;
        this.email = email;
        this.nickName = nickName;
        this.level = level;
    }

    public Long getUserKey() {
        return userKey;
    }

    public void setUserKey(Long userKey) {
        this.userKey = userKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
