package de.rieckpil.courses.book.review;

import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.sql.DataSource;

import de.rieckpil.courses.book.management.Book;
import de.rieckpil.courses.book.management.User;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(
    properties = {
      "spring.flyway.enabled=false",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "spring.datasource.driver-class-name=com.p6spy.engine.spy.P6SpyDriver", // P6Spy
      "spring.datasource.url=jdbc:p6spy:h2:mem:testing;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false" // P6Spy
    })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReviewRepositoryTest {

  @Autowired private EntityManager entityManager;

  @Autowired private ReviewRepository reviewRepository;

  @Autowired private DataSource dataSource;

  @Autowired private TestEntityManager testEntityManager;

  @BeforeEach
  void setUp() {
    assertThat(reviewRepository.count()).isZero();
  }

  @Test
  void notNull() throws SQLException {
    assertThat(entityManager).isNotNull();
    assertThat(reviewRepository).isNotNull();
    assertThat(dataSource).isNotNull();
    assertThat(testEntityManager).isNotNull();
    System.out.println("using database: " + dataSource.getConnection().getMetaData().getDatabaseProductName());

    Book java = new Book();
    java.setTitle("Java Book");
    java.setIsbn("0-9445-7984-1");
    java.setAuthor("James Gosling");
    java.setGenre("Computer Technology");
    java.setDescription("A book on Java");
    java.setPages(880L);
    java.setPublisher("LeanPub");

    User user = new User();
    user.setCreatedAt(LocalDateTime.now());
    user.setEmail("user@gmail.com");
    user.setName("Buster Scruggs");

    Review goodReview = new Review();
    goodReview.setBook(java);
    goodReview.setTitle("Review of Java Book");
    goodReview.setContent("A great book for java developers at any skill level written by the founder of the language himself");
    goodReview.setRating(5);
    goodReview.setUser(user);
    goodReview.setCreatedAt(LocalDateTime.now());

    Review savedReview = testEntityManager.persist(goodReview);
    assertThat(savedReview.getId()).isNotNull();
  }

  @Test
  void transactionalSupportTest() {
    Book java = new Book();
    java.setTitle("Java Book");
    java.setIsbn("0-9445-7984-1");
    java.setAuthor("James Gosling");
    java.setGenre("Computer Technology");
    java.setDescription("A book on Java");
    java.setPages(880L);
    java.setPublisher("LeanPub");

    User user = new User();
    user.setCreatedAt(LocalDateTime.now());
    user.setEmail("user@gmail.com");
    user.setName("Buster Scruggs");

    Review goodReview = new Review();
    goodReview.setBook(java);
    goodReview.setTitle("Review of Java Book");
    goodReview.setContent("A great book for java developers at any skill level written by the founder of the language himself");
    goodReview.setRating(5);
    goodReview.setUser(user);
    goodReview.setCreatedAt(LocalDateTime.now());

    Review savedReview = reviewRepository.save(goodReview);
    assertThat(savedReview.getId()).isNotNull();

  }
}
