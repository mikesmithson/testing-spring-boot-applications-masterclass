package de.rieckpil.courses.book.review;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static de.rieckpil.courses.book.review.RandomReviewParameterResolverExtension.RandomReview;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(RandomReviewParameterResolverExtension.class)
class ReviewVerifierTest {

  private ReviewVerifier reviewVerifier;

  @BeforeEach
  void setup() {
    reviewVerifier = new ReviewVerifier();
  }

  @Test
  @DisplayName("Fails when there are four-letter naughty words")
  void shouldFailWhenReviewContainsSwearWord() {
    String review = "This book is shit";
    boolean meetsQualityStandards = reviewVerifier.doesMeetQualityStandards(review);
    assertFalse(meetsQualityStandards, "ReviewVerifier did not detect swear word");
  }

  @Test
  @DisplayName("Should fail when review contains 'lorem ipsum'")
  void testLoremIpsum() {
    var review =  "Hi there and lorem ipsum";
    boolean meetsQualityStandards = reviewVerifier.doesMeetQualityStandards(review);
    assertFalse(meetsQualityStandards, "Review should contain lorem ipsum");

  }

  @ParameterizedTest
  @CsvFileSource(resources = "/badReview.csv")
  void shouldFailWhenReviewIsOfBadQuality(String review) {
    boolean meetsQualityStandards = reviewVerifier.doesMeetQualityStandards(review);
    assertFalse(meetsQualityStandards, "Review did meet quality standards");
  }

  @RepeatedTest(5)
  void shouldFailWhenRandomReviewQualityIsBad(@RandomReview String review) {
    boolean meetsQualityStandards = reviewVerifier.doesMeetQualityStandards(review);
    assertFalse(meetsQualityStandards, "Review did meet quality standards");
  }

  @Test
  void shouldPassWhenReviewIsGood() {
    String goodReview  = "I would recommend this book for all levels interested in learning Java";
    boolean meetsQualityStandards = reviewVerifier.doesMeetQualityStandards(goodReview);
    assertTrue(meetsQualityStandards, "Review did not meet quality standards");
  }

  @Test
  void shouldPassWhenReviewIsGoodHamcrest() {
  }

  @Test
  void shouldPassWhenReviewIsGoodAssertJ() {
    String goodReview  = "I would recommend this book for all levels interested in learning Java";
    boolean meetsQualityStandards = reviewVerifier.doesMeetQualityStandards(goodReview);
    assertThat(meetsQualityStandards).describedAs("Review meets quality standards").isTrue();
  }
}
