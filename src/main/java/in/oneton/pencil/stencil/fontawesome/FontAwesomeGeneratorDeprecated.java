package in.oneton.pencil.stencil.fontawesome;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import in.oneton.pencil.stencil.fontawesome.model.IconConfig;
import in.oneton.pencil.stencil.fontawesome.model.IconConfig.IconSvgMetadata;
import in.oneton.pencil.stencil.generator.model.StencilDefinitionDeprecated;
import in.oneton.pencil.stencil.generator.model.StencilDefinitionDeprecated.Properties;
import in.oneton.pencil.stencil.generator.model.StencilDefinitionDeprecated.Properties.PropertyGroup;
import in.oneton.pencil.stencil.generator.model.StencilDefinitionDeprecated.Properties.PropertyGroup.Property;
import in.oneton.pencil.stencil.generator.model.StencilDefinitionDeprecated.Properties.PropertyGroup.Property.VariableHolder;
import in.oneton.pencil.stencil.generator.model.StencilDefinitionDeprecated.Shape;
import in.oneton.pencil.stencil.generator.model.StencilDefinitionDeprecated.Shape.Behaviors;
import in.oneton.pencil.stencil.generator.model.StencilDefinitionDeprecated.Shape.Behaviors.ForBehavior;
import in.oneton.pencil.stencil.generator.model.StencilDefinitionDeprecated.Shape.Behaviors.ForBehavior.Box;
import in.oneton.pencil.stencil.generator.model.StencilDefinitionDeprecated.Shape.Behaviors.ForBehavior.Disabled;
import in.oneton.pencil.stencil.generator.model.StencilDefinitionDeprecated.Shape.Behaviors.ForBehavior.Fill;
import in.oneton.pencil.stencil.generator.model.StencilDefinitionDeprecated.Shape.Content;
import in.oneton.pencil.stencil.generator.model.StencilDefinitionDeprecated.Shape.Content.Svg;
import in.oneton.pencil.stencil.generator.model.StencilDefinitionDeprecated.Shape.Content.Svg.Path;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static in.oneton.pencil.stencil.generator.model.StencilDefinitionDeprecated.Shape.withDataSvgImagePrefix;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;

/**
 * @Deprecated as pencil canot handle xml properly within its definition file. It always expects the child svg to not have any namespace
 */
@Deprecated
public class FontAwesomeGeneratorDeprecated {

//  public static void main(String[] args) throws IOException {
//    new FontAwesomeGeneratorDeprecated().generate();
//  }

  public void generate() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
    TypeReference<Map<String, IconConfig>> iconNameToConfigTypeRef =
        new TypeReference<Map<String, IconConfig>>() {
        };
    Map<String, IconConfig> iconNameToConfig = mapper
        .readValue(new File("/home/thekalinga/Desktop/font-awesome-icons.json"),
            iconNameToConfigTypeRef);

    //@formatter:off
    Property defaultActiveColorProperty = Property.builder().name("color").type("Color").displayName("Color").value("#333333FF").build();
    Property defaultDisabledColorProperty = Property.builder().name("disabledColor").type("Color").displayName("Disabled Color").value("#AAAAAAFF").build();

    Property activeColorReplacementProperty = Property.builder().name("color").type("Color").displayName("Color").variableHolder(VariableHolder.builder().value("$$color").build()).build();
    Property defaultDisabledColorReplacementProperty = Property.builder().name("disabledColor").type("Color").displayName("Disabled Color").variableHolder(VariableHolder.builder().value("$$disabledColor").build()).build();

    Property disabledStatusProperty = Property.builder().name("disabled").type("Bool").displayName("Disabled").value("false").build();

    XmlMapper svgSerializer = new XmlMapper();

    List<Shape> shapes = new ArrayList<>();
    for(Map.Entry<String, IconConfig> iconNameToConfigEntry : iconNameToConfig.entrySet()) {
      String name = iconNameToConfigEntry.getKey();
      IconConfig config = iconNameToConfigEntry.getValue();

      for(Map.Entry<String, IconSvgMetadata> typeToSvgMetadataEntry : config.getTypeToSvgMetadata().entrySet()) {
        String type = typeToSvgMetadataEntry.getKey();
        IconSvgMetadata svgMetadata = typeToSvgMetadataEntry.getValue();
        double heightInPx = 30;
        double widthInPx = svgMetadata.computeScaledWidthForHeight(heightInPx);

        Property boxDimensionProperty = Property.builder().name("box").type("Dimension").displayName("Size").value(widthInPx + "," + heightInPx).build();
        Svg svg = Svg.builder()
              .id("glyph")
              .width(widthInPx)
              .height(heightInPx)
              .viewBox(svgMetadata.getViewBoxCoordinatesAsStr())
              .path(Path.builder().description(svgMetadata.getPathDescription()).build())
              .build();
        StringWriter svgAsXmlCapturer = new StringWriter();
        svgSerializer.writeValue(svgAsXmlCapturer, svg);

        Shape shape = Shape.builder().id("fa5-" + type + "-" + name)
          .description(config.getLabel() + "; " + config.getSearchTermContainer().getSearchTerms().stream().collect(joining("; ")))
          .icon(withDataSvgImagePrefix(svgAsXmlCapturer.toString()))
          .properties(
              Properties.builder()
                .groups(
                  asList(
                      PropertyGroup.builder().groupProperties(asList(boxDimensionProperty, disabledStatusProperty)).build(),
                      PropertyGroup.builder().name("Colors").groupProperties(asList(activeColorReplacementProperty, defaultDisabledColorReplacementProperty)).build()
                  )
                )
                .build()
          )
          .behaviors(
              Behaviors.builder()
                  .forBehaviours(
                      singletonList(
                          ForBehavior.builder().ref("glyph")
                          .box(Box.builder().value("$box").build())
                          .disabled(new Disabled())
                          .fill(Fill.builder().value("$disabled.value ? $disabledColor : $color").build())
                          .build()
                      )
                  )
                  .build()
          )
          .content(Content.builder().svg(svg).build())
          .build();
        shapes.add(shape);
      }
    }

    StencilDefinitionDeprecated definition =
        StencilDefinitionDeprecated.builder()
            .id("fa5-icons")
            .displayName("Font Awesome 5 Icons")
            .description("Icons from Font Awesome 5")
            .author("Font Awesome")
            .url("fontawesome.com")
            .properties(
                Properties.builder()
                  .groups(
                    singletonList(
                      PropertyGroup.builder()
                          .name("Colors")
                          .groupProperties(asList(defaultActiveColorProperty, defaultDisabledColorProperty))
                          .build()
                    )
                  ).build()
            )
            .shapes(shapes)
            .build();
    //@formatter:on

    XmlMapper xmlMapper = new XmlMapper();
    xmlMapper.configure(FAIL_ON_EMPTY_BEANS, false);
    xmlMapper.writeValue(new File("/home/thekalinga/Desktop/font-awesome-icon-definition.xml"),
        definition);
  }
}
