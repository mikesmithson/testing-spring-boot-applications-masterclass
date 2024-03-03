package de.rieckpil.courses.book.review;

import de.rieckpil.courses.book.management.Book;
import de.rieckpil.courses.book.management.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ReviewRepositoryReuseTestContainersTest {
  private static PostgreSQLContainer postgresContainer = (PostgreSQLContainer) new PostgreSQLContainer("postgres:12.18")
    .withDatabaseName("mytestdb")
    .withUsername("duke")
    .withPassword("s3cret")
    .withReuse(true);

  @DynamicPropertySource
  private static void propertyOverride(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgresContainer::getUsername);
    registry.add("spring.datasource.password", postgresContainer::getPassword);
  }

  static {
    postgresContainer.start();
  }

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
    System.out.println(
        "using database: " + dataSource.getConnection().getMetaData().getDatabaseProductName());

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
    goodReview.setContent(
        "A great book for java developers at any skill level written by the founder of the language himself");
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
    goodReview.setContent(
        "A great book for java developers at any skill level written by the founder of the language himself");
    goodReview.setRating(5);
    goodReview.setUser(user);
    goodReview.setCreatedAt(LocalDateTime.now());

    Review savedReview = reviewRepository.save(goodReview);
    assertThat(savedReview.getId()).isNotNull();
  }
}
