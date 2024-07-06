package by.plamya.project.security.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import by.plamya.project.entity.User;
import by.plamya.project.entity.UserPrincipal;
import by.plamya.project.exceptions.UserExistException;
import by.plamya.project.repository.UserRepository;
import by.plamya.project.service.AuthenticationService;
import by.plamya.project.service.UserService;
import by.plamya.project.utils.enums.ERole;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // User user = userService.getUserInfo(oAuth2UserInfo.getEmail());

        // if (user == null) {
        // user = authenticationService.registerOauth2User(provider, oAuth2UserInfo);
        // } else {
        // user = authenticationService.updateOauth2User(user, provider,
        // oAuth2UserInfo);
        // }
        // return UserPrincipal.create(user, oAuth2User.getAttributes());
        log.info("Provider:{}", provider);
        log.info("oAuth2User:{}", oAuth2User);

        return null;
    }

    @Transactional
    public User registerOauth2User(UserPrincipal userPrincipal) {
        User user = new User();
        user.setEmail(userPrincipal.getEmail());
        user.setFirstname(userPrincipal.getUsername());

        user.setRoles(Collections.singleton(ERole.ROLE_USER));
        try {
            log.info("Saving user with email: " + user.getEmail());
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("Error during registration", e);
            throw new UserExistException("User could not be saved. Please try again.");
        }
    }

}