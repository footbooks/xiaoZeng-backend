package com.yupaoBackend.entity.request;

import lombok.Data;

@Data
public class TeamJoinRequest {
    private Long teamId;
    private String password;
}
