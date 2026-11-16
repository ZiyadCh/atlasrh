package ma.atlasrh.atlasrh;

import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

/**
 * Unit test for the application bootstrap method. {@link SpringApplication} is mocked so the test
 * proves the delegation without starting a real server or connecting to a database.
 */
class AtlasrhApplicationMainTest {

  @Test
  void main_shouldDelegateToSpringApplication() {
    String[] args = { "--spring.main.banner-mode=off" };

    try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
      springApplication.when(() -> SpringApplication.run(AtlasrhApplication.class, args))
          .thenReturn(null);

      AtlasrhApplication.main(args);

      springApplication.verify(() -> SpringApplication.run(AtlasrhApplication.class, args));
    }
  }
}
