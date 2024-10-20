package repository;

import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

class PersonRepositoryImplTest {
    PersonRepositoryImpl personRepository;

    void printPerson(Person person) {
        System.out.println(person);
    }

    @BeforeEach
    void setup() {
        personRepository = new PersonRepositoryImpl();
    }

    @Test
    void testGetByIdBlock() {
        Mono<Person> personMono = personRepository.findById(1);
        StepVerifier.create(personMono)
                    .expectNextCount(1)
                    .verifyComplete();
        printPerson(personMono.block());
    }

    @Test
    void testGetByIdSubscribe() {
        Mono<Person> personMono = personRepository.findById(1);
        personMono.subscribe(this::printPerson);
    }

    @Test
    void testGetByIdWithMap() {
        Mono<Person> personMono = personRepository.findById(1);
        personMono.map(Person::getFirstName)
                  .subscribe(name -> {
                      System.out.println("Name from map: " + name);
                  });
    }

    @Test
    void testFindAllBlockFirst() {
        Flux<Person> allPersons = personRepository.findAll();
        printPerson(allPersons.blockFirst());
    }

    @Test
    void testFindAllSubscribe() {
        Flux<Person> allPersons = personRepository.findAll();
        allPersons.subscribe(this::printPerson);
    }

    @Test
    void testFluxToMonoConversion() {
        Flux<Person> allPersons = personRepository.findAll();

        //Convert a Flux[person...] to Mono[List of persons]
        Mono<List<Person>> monoListPerson = allPersons.collectList();
        monoListPerson.subscribe(list -> {
            list.forEach(this::printPerson);
        });
    }

    @Test
    void testFluxFiltering() {
        filterPerson(3);
    }

    @Test
    void testFluxFilterIdNotFound() {
        filterPerson(8);
        //next() handles NOT FOUND situation gracefully, no exception thrown
    }

    void filterPerson(Integer id) {
        Flux<Person> personFlux = personRepository.findAll();

        Mono<Person> monoPerson = personFlux.filter(person -> person.getId() == id)
                                            .next();
        monoPerson.subscribe(this::printPerson);
    }

    @Test
    void testFilterWithException() {
        Integer id = 5;
        Flux<Person> personFlux = personRepository.findAll();

        Mono<Person> monoPerson = personFlux
                .filter(person -> person.getId() > id)
                .single();
        monoPerson
                .doOnError(throwable -> {
                    System.out.println(throwable.getMessage());
                })
                .onErrorReturn(Person.builder()
                                     .id(id)
                                     .build())
                .subscribe(this::printPerson);
    }

    @Test
    void testFindById_Found() {
        Integer testId = 1;
        Mono<Person> personMono = personRepository.findById(testId);

        StepVerifier.create(personMono)
                    .expectNextCount(1)
                    .verifyComplete();
        personMono.subscribe(this::printPerson);

    }

    @Test
    void testFindById_NotFound() {
        Integer testId = 5;
        Mono<Person> personMono = personRepository.findById(testId);
        StepVerifier.create(personMono)
                    .expectNextCount(0)
                    .verifyComplete();

        personMono.subscribe(this::printPerson);

    }
}