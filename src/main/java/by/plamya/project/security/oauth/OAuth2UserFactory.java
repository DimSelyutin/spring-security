package by.plamya.project.security.oauth;

import java.util.Map;

import javax.security.sasl.AuthenticationException;

import lombok.SneakyThrows;

public class OAuth2UserFactory {
    @SneakyThrows
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase("GOOGLE")) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase("GITHUB")) {
            return new GithubOAuth2UserInfo(attributes);
        } else {
            throw new AuthenticationException("Error on authentication");
        }
    }
}
