package Team4.TobeHonest.dto.login;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
public class KakaoProfileVo {

    private long id;
    private String connected_at;
    private Properties properties;
    private KakaoAccount kakao_account;
    @Data
    public static class Properties {
        private String nickname;
        private String profile_image;
        private String thumbnail_image;

        // getters and setters
    }
    @Data
    public static class KakaoAccount {
        @JsonProperty("profile_nickname_needs_agreement")
        private boolean profileNicknameNeedsAgreement;

        @JsonProperty("profile_image_needs_agreement")
        private boolean profileImageNeedsAgreement;

        private Profile profile;
        private boolean has_email;

        @JsonProperty("email_needs_agreement")
        private boolean emailNeedsAgreement;

        @JsonProperty("is_email_valid")
        private boolean isEmailValid;

        @JsonProperty("is_email_verified")
        private boolean isEmailVerified;

        private String email;
        private boolean has_birthday;

        @JsonProperty("birthday_needs_agreement")
        private boolean birthdayNeedsAgreement;

        private String birthday;
        private String birthday_type;

        // getters and setters
    }
    @Data
    public static class Profile {
        private String nickname;

        @JsonProperty("thumbnail_image_url")
        private String thumbnailImageUrl;

        @JsonProperty("profile_image_url")
        private String profileImageUrl;

        @JsonProperty("is_default_image")
        private boolean isDefaultImage;

        // getters and setters
    }

    // getters and setters
}