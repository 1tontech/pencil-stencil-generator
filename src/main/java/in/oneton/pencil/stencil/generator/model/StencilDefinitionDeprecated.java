package in.oneton.pencil.stencil.generator.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static in.oneton.pencil.stencil.generator.model.StencilDefinitionDeprecated.PENCIL_NS;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "Shapes", namespace = PENCIL_NS)
/**
 * @Deprecated as pencil canot handle xml properly within its definition file. It always expects the child svg to not have any namespace
 */
@Deprecated
public class StencilDefinitionDeprecated {
  public static final String PENCIL_NS = "http://www.evolus.vn/Namespace/Pencil";
  public static final String SVG_NS = "http://www.w3.org/2000/svg";

  @JacksonXmlProperty(isAttribute = true)
  private String id;
  @JacksonXmlProperty(isAttribute = true)
  private String displayName;
  @JacksonXmlProperty(isAttribute = true)
  private String author;
  @JacksonXmlProperty(isAttribute = true)
  private String url;
  @JacksonXmlProperty(isAttribute = true)
  private String description;

  @JacksonXmlProperty(localName = "Properties", namespace = PENCIL_NS)
  @Builder.Default
  private Properties properties = new Properties();
  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "Shape", namespace = PENCIL_NS)
  @Builder.Default
  private List<Shape> shapes = new ArrayList<>();


  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Properties {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "PropertyGroup", namespace = PENCIL_NS)
    @Builder.Default
    private List<PropertyGroup> groups = new ArrayList<>();

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PropertyGroup {
      @JacksonXmlProperty(isAttribute = true)
      private String name;
      @JacksonXmlElementWrapper(useWrapping = false)
      @JacksonXmlProperty(localName = "Property", namespace = PENCIL_NS)
      @Builder.Default
      private List<Property> groupProperties = new ArrayList<>();


      @Getter
      @Setter
      @Builder
      @NoArgsConstructor
      @AllArgsConstructor
      public static class Property {
        @JacksonXmlProperty(isAttribute = true)
        private String name;
        @JacksonXmlProperty(isAttribute = true)
        private String type;
        @JacksonXmlProperty(isAttribute = true)
        private String displayName;
        @JacksonXmlText
        private String value;
        @JacksonXmlProperty(localName = "E", namespace = PENCIL_NS)
        private VariableHolder variableHolder;


        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class VariableHolder {
          @JacksonXmlText
          private String value;
        }
      }
    }
  }


  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Shape {
    @JacksonXmlProperty(isAttribute = true)
    private String id;
    @JacksonXmlProperty(localName = "displayName", isAttribute = true)
    private String description;
    @JacksonXmlProperty(isAttribute = true)
    private String icon;

    @JacksonXmlProperty(localName = "Properties", namespace = PENCIL_NS)
    @Builder.Default
    private Properties properties = new Properties();
    @JacksonXmlProperty(localName = "Behaviors", namespace = PENCIL_NS)
    @Builder.Default
    private Behaviors behaviors = new Behaviors();
    @JacksonXmlProperty(localName = "Content", namespace = PENCIL_NS)
    @Builder.Default
    private Content content = new Content();

    public static String withDataSvgImagePrefix(String svgAsStr) {
      return "data:image/svg+xml;charset=utf8," + svgAsStr;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Behaviors {
      @JacksonXmlElementWrapper(useWrapping = false)
      @JacksonXmlProperty(localName = "For", namespace = PENCIL_NS)
      @Builder.Default
      private List<ForBehavior> forBehaviours = new ArrayList<>();


      @Getter
      @Setter
      @Builder
      @NoArgsConstructor
      @AllArgsConstructor
      public static class ForBehavior {
        @JacksonXmlProperty(isAttribute = true)
        private String ref;
        @JacksonXmlProperty(localName = "Box", namespace = PENCIL_NS)
        private Box box;
        @JacksonXmlProperty(localName = "Disabled", namespace = PENCIL_NS)
        private Disabled disabled;
        @JacksonXmlProperty(localName = "Fill", namespace = PENCIL_NS)
        private Fill fill;


        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Box {
          @JacksonXmlText
          private String value;
        }


        public static class Disabled {
        }


        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Fill {
          @JacksonXmlText
          private String value;
        }
      }
    }


    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
      @JacksonXmlProperty(namespace = SVG_NS)
      private Svg svg;


      @Getter
      @Setter
      @Builder
      @NoArgsConstructor
      @AllArgsConstructor
      @JacksonXmlRootElement(localName = "svg", namespace = SVG_NS)
      public static class Svg {
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
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Path {
          @JacksonXmlProperty(localName = "d", isAttribute = true)
          private String description;
        }
      }
    }
  }

}
