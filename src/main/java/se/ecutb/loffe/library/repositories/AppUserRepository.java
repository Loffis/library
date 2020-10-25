package se.ecutb.loffe.library.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import se.ecutb.loffe.library.entities.AppUser;

public interface AppUserRepository extends MongoRepository<AppUser, String> {

}
