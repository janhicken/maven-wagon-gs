package io.github.janhicken.maven.wagon.gs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import javax.annotation.Nullable;

/** An {@link Iterator} for all parent directories of a given {@link Path} */
public class ParentsIterator implements Iterator<Path> {

  @Nullable Path parent;
  Path path;

  public ParentsIterator(final String pathStr) {
    this(Paths.get(pathStr));
  }

  public ParentsIterator(final Path path) {
    this.path = path;
    this.parent = this.path.getParent();
  }

  @Override
  public boolean hasNext() {
    return parent != null;
  }

  @Override
  public synchronized Path next() {
    path = parent;
    if (path != null) parent = path.getParent();
    return Optional.ofNullable(path).orElseThrow();
  }
}
