package se.ecutb.loffe.library.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import se.ecutb.loffe.library.entities.Book;

public interface BookRepository extends MongoRepository<Book, String> {
}
