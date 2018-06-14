package in.oneton.pencil.stencil.generator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class ShapeMetadata {
  private String id;
  private String description;
  private String svgAsStr;
  private double width;
  private double height;
}
