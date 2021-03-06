package se.ecutb.loffe.library.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import se.ecutb.loffe.library.entities.AppUser;
import se.ecutb.loffe.library.services.AppUserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @Secured({"ROLE_LIBRARIAN", "ROLE_ADMIN"})
    @GetMapping
    public ResponseEntity<List<AppUser>> findAllUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) boolean sort) {
        return ResponseEntity.ok(appUserService.findAll(username, sort));
    }

    @Secured({"ROLE_LIBRARIAN", "ROLE_ADMIN"})
    @GetMapping("/{id}")
    public ResponseEntity<AppUser> findAppUserById(@PathVariable String id) {
        return ResponseEntity.ok(appUserService.findById(id));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<AppUser> save(@Validated @RequestBody AppUser appUser) {
        return ResponseEntity.ok(appUserService.save(appUser));
    }

    @Secured({"ROLE_LIBRARIAN", "ROLE_ADMIN"})
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable String id, @Validated @RequestBody AppUser appUser) {
        appUserService.update(id, appUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        appUserService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
