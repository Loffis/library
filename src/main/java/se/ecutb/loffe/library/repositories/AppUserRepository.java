package se.ecutb.loffe.library.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import se.ecutb.loffe.library.entities.AppUser;

import java.util.Optional;

@Repository
public interface AppUserRepository extends MongoRepository<AppUser, String> {
        Optional<AppUser> findByUsername(String username);
}
