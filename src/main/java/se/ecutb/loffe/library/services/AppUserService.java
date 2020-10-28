package se.ecutb.loffe.library.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.ecutb.loffe.library.entities.AppUser;
import se.ecutb.loffe.library.repositories.AppUserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepo;
    private final PasswordEncoder passwordEncoder;

    public List<AppUser> findAll(String username, boolean sort) {
        log.info("Retrieve all users");
        var appUsers = appUserRepo.findAll();
        if (username != null) {
            appUsers = appUsers.stream()
                    .filter(appUser -> appUser.getUsername().contains(username))
                    .collect(Collectors.toList());
        }

        if (sort) {
            appUsers.sort(Comparator.comparing(AppUser::getUsername));
        }
        log.info(appUsers.size() + " user(s) retrieved!");
        return appUsers;
    }

    public AppUser findById(String id) {
        log.info("Retrieve user by id " + id);
        return appUserRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Could not find the user by id '%s'.", id)));
    }

    public AppUser save(AppUser appUser) {
        log.info("Saving user " + appUser.getUsername());
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return appUserRepo.save(appUser);
    }

    public void update(String id, AppUser appUser) {
        log.info("Updating user...");
        if (!appUserRepo.existsById(id)) {
            log.warn("Could not find user by id " + id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Could not find the user by id '%s'.", id));
        }
        appUser.setId(id);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUserRepo.save(appUser);
        log.info("...user updated!");
    }

    public void delete(String id) {
        log.info("Deleting user by id " + id + "...");
        if (!appUserRepo.existsById(id)) {
            log.warn("Could not find user by id " + id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Could not find the user by id '%s'.", id));
        }
        // Om jag injectar LibraryService får jag "circular dependency injection"
        // returnAllBooks(id);
        appUserRepo.deleteById(id);
        log.info("...user deleted!");

    }

    public AppUser findByUsername(String username) {
        log.info("Retrieve user by username " + username);
        return appUserRepo.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Could not find the user by name '%s'.", username)));
    }

}
