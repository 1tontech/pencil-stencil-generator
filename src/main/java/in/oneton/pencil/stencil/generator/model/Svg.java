package in.oneton.pencil.stencil.generator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static in.oneton.pencil.stencil.generator.model.Svg.SVG_NS;

@Getter
@Builder
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
@JacksonXmlRootElement(localName = "svg", namespace = SVG_NS)
public class Svg {
  public static final String SVG_NS = "http://www.w3.org/2000/svg";

  @JacksonXmlProperty(isAttribute = true)
  private String id;
  @JacksonXmlProperty(isAttribute = true)
  private double width;
  @JacksonXmlProperty(isAttribute = true)
  private double height;
  @JacksonXmlProperty(isAttribute = true)
  private String viewBox;
  @JacksonXmlProperty(namespace = SVG_NS)
  private Path path;


  @Getter
  @Builder
  @AllArgsConstructor(onConstructor = @__(@JsonCreator))
  public static class Path {
    @JacksonXmlProperty(localName = "d", isAttribute = true)
    private String description;
  }
}
