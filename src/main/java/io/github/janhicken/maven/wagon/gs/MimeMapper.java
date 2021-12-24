package io.github.janhicken.maven.wagon.gs;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class MimeMapper {

  static final String PROPERTIES_NAME = "mime.properties";

  final Properties properties = new Properties();

  public MimeMapper() {
    try (final var in = getClass().getResourceAsStream(PROPERTIES_NAME)) {
      properties.load(in);
    } catch (final IOException e) {
      throw new RuntimeException("Error loading " + PROPERTIES_NAME, e);
    }
  }

  public Optional<String> getMimeTypeForFileName(final String fileName) {
    final var extension = fileName.substring(fileName.lastIndexOf('.') + 1);
    return getMimeTypeForExtension(extension);
  }

  public Optional<String> getMimeTypeForExtension(final String extension) {
    return Optional.ofNullable(properties.getProperty(extension));
  }
}
