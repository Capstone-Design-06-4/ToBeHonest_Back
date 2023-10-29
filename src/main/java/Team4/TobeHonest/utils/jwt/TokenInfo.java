package Team4.TobeHonest.utils.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
public class TokenInfo {

    private String grantType;
    private String accessToken;
    private Date accessExpire;
    private String refreshToken;
    private Date refreshExpire;
}