package in.oneton.pencil.stencil.generator;

import in.oneton.pencil.stencil.generator.model.DefinitionMetadata;
import in.oneton.pencil.stencil.generator.model.ShapeMetadata;

import java.io.IOException;
import java.util.List;

public interface DefinitionGenerator {
  DefinitionMetadata generate() throws IOException;
}
