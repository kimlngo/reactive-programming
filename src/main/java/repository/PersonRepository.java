package repository;

import domain.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonRepository {

    Mono<Person> findById(Integer id);

    Flux<Person> findAll();
}
