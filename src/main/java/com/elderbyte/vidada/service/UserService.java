package com.elderbyte.vidada.service;

import com.elderbyte.vidada.domain.security.Authority;
import com.elderbyte.vidada.domain.User;
import com.elderbyte.vidada.domain.security.KnownAuthority;
import com.elderbyte.vidada.repository.UserRepository;
import com.elderbyte.vidada.security.SecurityUtils;
import com.elderbyte.vidada.service.util.RandomUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
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

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, AuthorityService authorityService){
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authorityService = authorityService;

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
        Authority authority = authorityService.get(KnownAuthority.USER);
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
        newUser.getAuthorities().add(authority);
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

    /**
     * Returns the current user
     * @return
     */
    @Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        User currentUser = userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).get();
        currentUser.getAuthorities().size(); // eagerly load the association
        return currentUser;
    }

    @Transactional(readOnly = true)
    public User getUser(String login){
        Optional<User> user = userRepository.findOneByLogin(login);
        return user.orElse(null);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        DateTime now = new DateTime();
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
        for (User user : users) {
            log.debug("Deleting not activated user {}", user.getLogin());
            userRepository.delete(user);
        }
    }



    @Transactional
    private void ensureKnownUsers() {

        User system = getUser("system");
        User anonymousUser = getUser("anonymous");
        User admin = getUser("admin");

        if(admin == null){
            User u = createUserInformation("admin", "1337", "Administrator", "Administrator", "admin@vidada", "en");
            u.getAuthorities().add(authorityService.get(KnownAuthority.ADMIN));
            u.setActivated(true);
            userRepository.save(u);
        }

        if(system == null){
            User u = createUserInformation("system", "1337", "System", "", "system@vidada", "en");
            u.getAuthorities().add(authorityService.get(KnownAuthority.ADMIN));
            u.setActivated(true);
            userRepository.save(u);
        }

        if(anonymousUser == null){
            User u =  createUserInformation("anonymous", "", "Anonymous", "", "anon@vidada", "en");
            u.getAuthorities().clear();
            u.getAuthorities().add(authorityService.get(KnownAuthority.ANONYMOUS));
            u.setActivated(true);
            userRepository.save(u);
        }

    }
}
