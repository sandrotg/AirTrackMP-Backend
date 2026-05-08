package com.airtrackmp.iot.airtrackmp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    private String name;
    private String email;
    private String password;
    private String role;
}
