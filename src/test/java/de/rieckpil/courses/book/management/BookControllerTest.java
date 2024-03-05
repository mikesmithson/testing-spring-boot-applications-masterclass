package de.rieckpil.courses.book.management;

import de.rieckpil.courses.config.WebSecurityConfig;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
// see
// https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.7-Release-Notes#migrating-from-websecurityconfigureradapter-to-securityfilterchain
@Import(WebSecurityConfig.class)
class BookControllerTest {

  @MockBean
  private BookManagementService bookManagementService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldGetEmptyArrayWhenNoBooksExists() throws Exception {
    mockMvc.perform(get("/api/books")
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("[]"));
  }

  @Test
  void shouldNotReturnXML() throws Exception {
    mockMvc.perform(get("/api/books")
        .accept(MediaType.APPLICATION_XML))
      .andExpect(status().isNotAcceptable());
  }

  @Test
  void shouldGetBooksWhenServiceReturnsBooks() throws Exception {
    Book createdBook = createBook(1L, "12345", "Test",
      "Test Author", "A Test Book", "Fiction",
      356L, "Random House", "http://thumbnails.org/pic");
    List<Book> bookList = List.of(createdBook);
    when(bookManagementService.getAllBooks()).thenReturn(bookList);

    mockMvc.perform(get("/api/books")
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].isbn", equalTo("12345")))
      .andExpect(jsonPath("$[0].title", equalTo("Test")))
      .andExpect(jsonPath("$[0].author", equalTo("Test Author")))
      .andExpect(jsonPath("$[0].description", equalTo("A Test Book")))
      .andExpect(jsonPath("$[0].genre", equalTo("Fiction")))
      .andExpect(jsonPath("$[0].pages", equalTo(356)))
      .andExpect(jsonPath("$[0].publisher", equalTo("Random House")))
      .andExpect(jsonPath("$[0].thumbnailUrl", equalTo("http://thumbnails.org/pic")));
  }

  private Book createBook(
    Long id,
    String isbn,
    String title,
    String author,
    String description,
    String genre,
    Long pages,
    String publisher,
    String thumbnailUrl) {
    Book result = new Book();
    result.setId(id);
    result.setIsbn(isbn);
    result.setTitle(title);
    result.setAuthor(author);
    result.setDescription(description);
    result.setGenre(genre);
    result.setPages(pages);
    result.setPublisher(publisher);
    result.setThumbnailUrl(thumbnailUrl);
    return result;
  }
}
