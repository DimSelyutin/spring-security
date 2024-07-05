package by.plamya.project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import by.plamya.project.entity.User;
import by.plamya.project.entity.UserPrincipal;
import by.plamya.project.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с таким именем не найден!" + username));
        return build(user);
    }

    // public User loadUserByEmail(String email) {
    //     User user = userRepository.findByEmail(email)
    //             .orElseThrow(() -> new UsernameNotFoundException("Пользователь с таким именем не найден!" + email));
    //     return build(user);
    // }

    public User loadUserById(Long id) {
        return userRepository.findUserById(id).orElse(null);
    }

    public static User build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());

        return new User(user.getId(), user.getEmail(), user.getUsername(), user.getPassword(), authorities);

    }

}
