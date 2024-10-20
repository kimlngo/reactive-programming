package repository;

import domain.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PersonRepositoryImpl implements PersonRepository {

    Person thomas = new Person(1, "Thomas", "Ngo");
    Person john = new Person(2, "John", "Nguyen");
    Person josie = new Person(3, "Josie", "Le");
    Person mary = new Person(4, "Mary", "Tran");

    @Override
    public Mono<Person> findById(Integer id) {
        return this.findAll()
                   .filter(person -> person.getId() == id)
                   .next();
    }

    @Override
    public Flux<Person> findAll() {
        return Flux.just(thomas, john, josie, mary);
    }
}
