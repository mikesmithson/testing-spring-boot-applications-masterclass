package de.rieckpil.courses.book.management;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookSynchronizationListenerTest {

  private static final String VALID_ISBN = "1234567891234";
  private static final String INVALID_ISBN = "01234567891234";
  private static final String NON_EXISTENT_VALID_ISBN = new StringBuilder("1234567891234").reverse().toString();
  private static final BookSynchronization VALID_BOOK_ISBN = new BookSynchronization(VALID_ISBN);
  private static final BookSynchronization INVALID_BOOK_ISBN = new BookSynchronization(INVALID_ISBN);
  private static final BookSynchronization NON_EXISTENT_VALID_BOOK_ISBN = new BookSynchronization(NON_EXISTENT_VALID_ISBN);

  private static final Book existingBook = new Book();
  @Mock
  private BookRepository bookRepository;

  @Mock
  private OpenLibraryApiClient openLibraryApiClient;
  @InjectMocks
  private BookSynchronizationListener cut;

  @Captor
  private ArgumentCaptor<Book> bookArgumentCaptor;

  @Test
  void shouldRejectBookWhenIsbnIsMalformed() {

    cut.consumeBookUpdates(INVALID_BOOK_ISBN);

    verifyNoInteractions(bookRepository, openLibraryApiClient);
  }

  @Test
  void shouldNotOverrideWhenBookAlreadyExists() {
    existingBook.setIsbn(VALID_ISBN);
    when(bookRepository.findByIsbn(VALID_ISBN)).thenReturn(existingBook);

    cut.consumeBookUpdates(VALID_BOOK_ISBN);

    verify(bookRepository, atMostOnce()).findByIsbn(VALID_ISBN);
    verify(bookRepository, never()).save(any(Book.class));
    verifyNoInteractions(openLibraryApiClient);
  }

  @Test
  void shouldThrowExceptionWhenProcessingFails() {
    when(bookRepository.findByIsbn(VALID_ISBN)).thenReturn(null);
    when(openLibraryApiClient.fetchMetadataForBook(VALID_ISBN)).thenThrow(new RuntimeException("Network timeout"));

    assertThrows(RuntimeException.class, () -> cut.consumeBookUpdates(VALID_BOOK_ISBN));
    verify(bookRepository, atMostOnce()).findByIsbn(VALID_ISBN);
    verify(bookRepository, never()).save(any(Book.class));
  }

  @Test
  void shouldStoreBookWhenNewAndCorrectIsbn() {

    String expectedTitle = "Java Spring Boot Testing Complete";
    Book newBook = new Book();
    newBook.setIsbn(NON_EXISTENT_VALID_ISBN);
    newBook.setTitle(expectedTitle);

    when(bookRepository.findByIsbn(NON_EXISTENT_VALID_ISBN)).thenReturn(null);
    when(openLibraryApiClient.fetchMetadataForBook(NON_EXISTENT_VALID_ISBN)).thenReturn(newBook);
    when(bookRepository.save(newBook)).then(invocation -> {
      Book argument = invocation.getArgument(0, Book.class);
      argument.setId(42L);
      return argument;
    });

    cut.consumeBookUpdates(NON_EXISTENT_VALID_BOOK_ISBN);

    verify(bookRepository).findByIsbn(NON_EXISTENT_VALID_ISBN);
    verify(openLibraryApiClient).fetchMetadataForBook(NON_EXISTENT_VALID_ISBN);
    verify(bookRepository).save(bookArgumentCaptor.capture());
    Book savedBook = bookArgumentCaptor.getValue();
    assertEquals(savedBook.getIsbn(),  NON_EXISTENT_VALID_ISBN);
    assertEquals(savedBook.getTitle(),  expectedTitle);
    assertEquals(savedBook.getId(),  42L);
  }
}
