package in.oneton.pencil.stencil.generator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class DefinitionMetadata {
  private String id;
  private String displayName;
  private String description;
  private String author;
  private String url;
  private List<ShapeMetadata> shapeMetadataList;
  private String outputFilePathOrName;
}
