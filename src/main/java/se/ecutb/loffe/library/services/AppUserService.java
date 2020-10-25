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
        var appUsers = appUserRepo.findAll();
        if (username != null) {
            appUsers = appUsers.stream()
                    .filter(appUser -> appUser.getUsername().contains(username))
                    .collect(Collectors.toList());

        }

        if (sort) {
            appUsers.sort(Comparator.comparing(AppUser::getUsername));
        }

        return appUsers;
    }

    public AppUser findById(String id) {
        return appUserRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Could not find the user by id '%s'.", id)));
    }

    public AppUser save(AppUser appUser) {
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return appUserRepo.save(appUser);
    }

    public void update(String id, AppUser appUser) {
        if (!appUserRepo.existsById(appUser.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Could not find the user by id '%s'.", id));
        }
        appUser.setId(id);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUserRepo.save(appUser);
    }

    public void delete(String id) {
        if (!appUserRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Could not find the user by id '%s'.", id));
        }
        appUserRepo.deleteById(id);
    }

    public AppUser findByUsername(String username) {
        return appUserRepo.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Could not find the user by name '%s'.", username)));
    }
}
