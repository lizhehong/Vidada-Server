package com.elderbyte.oauth.server;

import com.elderbyte.common.util.RandomUtil;
import com.elderbyte.vidada.security.SecurityUtils;
import com.elderbyte.oauth.server.authorities.Authority;
import com.elderbyte.oauth.server.authorities.AuthorityService;
import com.elderbyte.oauth.server.authorities.KnownAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthorityService authorityService;

    @Value("${security.oauth2.jwt.key}")
    private String secretKey;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, AuthorityService authorityService){
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authorityService = authorityService;
    }

    @PostConstruct
    public void init(){
        ensureKnownUsers();
    }


    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                userRepository.save(user);
                log.debug("Activated user: {}", user);
                return user;
            });
        return Optional.empty();
    }

    @Transactional
    public User createUserInformation(String login, String password, String firstName, String lastName, String email,
                                      String langKey) {
        User newUser = new User();

        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(login);
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setLangKey(langKey);
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        addKnownAuthorities(newUser, KnownAuthority.USER);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public void updateUserInformation(String firstName, String lastName, String email) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).ifPresent(u -> {
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setEmail(email);
            userRepository.save(u);
            log.debug("Changed Information for User: {}", u);
        });
    }

    public void changePassword(String password) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).ifPresent(u-> {
            String encryptedPassword = passwordEncoder.encode(password);
            u.setPassword(encryptedPassword);
            userRepository.save(u);
            log.debug("Changed password for User: {}", u);
        });
    }

    @Transactional(readOnly = true)
    public User getUser(String login){
        Optional<User> user = userRepository.findOneByLogin(login);
        return user.orElse(null);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithCredentials(String login, String password){
        return userRepository.findOneByLogin(login).flatMap(user -> {
            if(passwordEncoder.matches(password, user.getPassword())){
                return Optional.of(user);
            }
            log.warn("The provided password did not match for this user!");
            return Optional.empty();
        });
    }


    @Transactional
    private void ensureKnownUsers() {

        User system = getUser("system");
        User anonymousUser = getUser("anonymous");
        User admin = getUser("admin");

        if(admin == null){
            User u = createUserInformation("admin", "1337", "Administrator", "Administrator", "admin@vidada", "en");
            addKnownAuthorities(u, KnownAuthority.ADMIN);
            u.setActivated(true);
            userRepository.save(u);
        }

        if(system == null){
            User u = createUserInformation("system", "1337", "System", "", "system@vidada", "en");
            u.getAuthorities().add(authorityService.findByName(KnownAuthority.ADMIN));
            addKnownAuthorities(u, KnownAuthority.ADMIN);
            u.setActivated(true);
            userRepository.save(u);
        }

        if(anonymousUser == null){
            User u =  createUserInformation("anonymous", "", "Anonymous", "", "anon@vidada", "en");
            u.getAuthorities().clear();
            addKnownAuthorities(u, KnownAuthority.ANONYMOUS);
            u.setActivated(true);
            userRepository.save(u);
        }
    }

    private void addKnownAuthorities(User user, String... authorities){
        for (String authority : authorities) {
            Authority auth = authorityService.findByName(authority);
            if(auth != null){
                user.getAuthorities().add(auth);
            }
        }
    }
}
