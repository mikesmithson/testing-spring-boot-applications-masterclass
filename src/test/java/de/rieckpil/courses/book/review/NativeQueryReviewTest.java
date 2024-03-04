package de.rieckpil.courses.book.review;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NativeQueryReviewTest {
  @Autowired
  ReviewRepository reviewRepository;

  @Container
  private static PostgreSQLContainer postgresContainer = new PostgreSQLContainer("postgres:12.18")
    .withDatabaseName("mytestdb")
    .withUsername("duke")
    .withPassword("s3cret");

  @DynamicPropertySource
  private static void propertyOverride(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgresContainer::getUsername);
    registry.add("spring.datasource.password", postgresContainer::getPassword);
  }

  @Test
  @DisplayName("Tests the native query for get review statistics")
  @Sql(scripts = "/scripts/INIT_REVIEW_EACH_BOOK.sql")
  void testReviewStatistics() {
    List<ReviewStatistic> reviewStatistics = reviewRepository.getReviewStatistics();
    assertThat(reviewStatistics).hasSize(2);
  }
}
