package se.ecutb.loffe.library.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import se.ecutb.loffe.library.entities.Book;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {
}
