package org.finalproject.tmeroom.auth.data.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class LoginResponseDto {

    private String accessToken;
    private String refreshToken;

    @Builder
    public LoginResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
