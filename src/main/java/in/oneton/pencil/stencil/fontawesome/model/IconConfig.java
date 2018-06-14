package in.oneton.pencil.stencil.fontawesome.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IconConfig {
  @JsonProperty("search")
  private IconSearchTermContainer searchTermContainer;
  private String label;
  @JsonProperty("svg")
  private Map<String, IconSvgMetadata> typeToSvgMetadata;


  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class IconSearchTermContainer {
    @JsonProperty("terms")
    private List<String> searchTerms;
  }


  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class IconSvgMetadata {
    // NOTE: the explanation below is only useful from the point of view of this Stencil generator & does not match SVG spec
    // array of [<origin x axis>, <origin y axis>, <width of icon>, <height of icon>]
    @JsonProperty("viewBox")
    private double[] viewBoxCoordinates;
    @JsonProperty("raw")
    private String rawSvg;
    @JsonProperty("path")
    private String pathDescription;

    public double getWidth() {
      return viewBoxCoordinates[2];
    }

    public double getHeight() {
      return viewBoxCoordinates[3];
    }

    public double computeScaledWidthForHeight(double height) {
      return height * getWidth() / getHeight();
    }

    public String getViewBoxCoordinatesAsStr() {
      return viewBoxCoordinates[0] + " " + viewBoxCoordinates[1] + " " + viewBoxCoordinates[2] + " "
          + viewBoxCoordinates[3];
    }
  }
}
