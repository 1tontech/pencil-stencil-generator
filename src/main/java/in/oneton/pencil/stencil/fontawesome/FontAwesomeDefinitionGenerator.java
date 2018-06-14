package in.oneton.pencil.stencil.fontawesome;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import in.oneton.pencil.stencil.fontawesome.model.IconConfig;
import in.oneton.pencil.stencil.fontawesome.model.IconConfig.IconSvgMetadata;
import in.oneton.pencil.stencil.generator.DefinitionGenerator;
import in.oneton.pencil.stencil.generator.model.DefinitionMetadata;
import in.oneton.pencil.stencil.generator.model.DefinitionMetadata.DefinitionMetadataBuilder;
import in.oneton.pencil.stencil.generator.model.ShapeMetadata;
import in.oneton.pencil.stencil.generator.model.Svg;
import in.oneton.pencil.stencil.generator.model.Svg.Path;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static in.oneton.pencil.stencil.generator.FileNameUtil.computeStencilZipPath;
import static java.util.stream.Collectors.joining;

public class FontAwesomeDefinitionGenerator implements DefinitionGenerator {

  public static final String FONT_AWESOME_FREE_METADATA_ICONS_JSON_URL =
      "https://raw.githubusercontent.com/FortAwesome/Font-Awesome/master/advanced-options/metadata/icons.json";

  private final String iconMetadataSourcePath;
  private final String stencilDestinationNameOrPath;

  public FontAwesomeDefinitionGenerator(String iconMetadataSourcePath,
      @Nullable String stencilDestinationNameOrPath) {
    this.iconMetadataSourcePath = iconMetadataSourcePath;
    this.stencilDestinationNameOrPath = stencilDestinationNameOrPath;
  }

  @Override
  public DefinitionMetadata generate() throws IOException {
    boolean isHttpUrl = iconMetadataSourcePath.toLowerCase().startsWith("http");
    if (isHttpUrl) {
      HttpURLConnection urlConnection = null;
      try {
        urlConnection = (HttpURLConnection) new URL(iconMetadataSourcePath).openConnection();
        urlConnection.connect();
        try (InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream())) {
          return generate(inputStream);
        }
      } finally {
        if (urlConnection != null) {
          urlConnection.disconnect();
        }
      }
    } else {
      File iconsJsonFile = new File(iconMetadataSourcePath);
      if (iconsJsonFile.getName().toLowerCase().endsWith(".zip")) {
        try (ZipFile iconJsonZip = new ZipFile(iconsJsonFile)) {
          Enumeration<? extends ZipEntry> entries = iconJsonZip.entries();
          while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (!entry.isDirectory()) {
              if (entry.getName().toLowerCase().endsWith("/icons.json")) {
                return generate(iconJsonZip.getInputStream(entry));
              }
            }
          }
        }
        throw new RuntimeException(
            "Could not find icon.json file inside " + iconMetadataSourcePath);
      } else {
        try (InputStream stream = new BufferedInputStream(new FileInputStream(iconsJsonFile))) {
          return generate(stream);
        }
      }
    }
  }

  private DefinitionMetadata generate(InputStream iconsJsonInputStream) throws IOException {
    DefinitionMetadataBuilder builder =
        DefinitionMetadata.builder().id("fa5-icons").displayName("Font Awesome 5")
            .author("Font Awesome").url("https://fontawesome.com")
            .description("Icons from Font Awesome 5 set");
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
    TypeReference<Map<String, IconConfig>> iconNameToConfigTypeRef =
        new TypeReference<Map<String, IconConfig>>() {
        };

    try {
      Map<String, IconConfig> iconNameToConfig =
          mapper.readValue(iconsJsonInputStream, iconNameToConfigTypeRef);

      List<ShapeMetadata> shapeMetadataList = new ArrayList<>();

      //@formatter:off
      for(Entry<String, IconConfig> iconNameToConfigEntry : iconNameToConfig.entrySet()) {
        String name = iconNameToConfigEntry.getKey();
        IconConfig config = iconNameToConfigEntry.getValue();

        for(Entry<String, IconSvgMetadata> typeToSvgMetadataEntry : config.getTypeToSvgMetadata().entrySet()) {
          String type = typeToSvgMetadataEntry.getKey();
          IconSvgMetadata svgMetadata = typeToSvgMetadataEntry.getValue();
          double heightInPx = 30;
          double widthInPx = svgMetadata.computeScaledWidthForHeight(heightInPx);

          Svg svg = Svg.builder()
              .id("glyph")
              .width(widthInPx)
              .height(heightInPx)
              .viewBox(svgMetadata.getViewBoxCoordinatesAsStr())
              .path(Path.builder().description(svgMetadata.getPathDescription()).build())
              .build();

          StringWriter svgAsXmlCapturer = new StringWriter();
          new XmlMapper().writeValue(svgAsXmlCapturer, svg);

          String description;
          if (config.getSearchTermContainer().getSearchTerms().size() != 0) {
            description = config.getLabel() + " " + type + " " + config.getSearchTermContainer().getSearchTerms().stream().collect(joining(" "));
          } else {
            description = config.getLabel() + " " + type;
          }

          shapeMetadataList.add(ShapeMetadata.builder()
              .id("fa5-" + type + "-" + name)
              .description(description)
              .svgAsStr(svgAsXmlCapturer.toString())
              .width(widthInPx)
              .height(heightInPx)
              .build());
        }
      }
      //@formatter:on
      return builder.shapeMetadataList(shapeMetadataList).outputFilePathOrName(
          computeStencilZipPath(iconMetadataSourcePath, stencilDestinationNameOrPath)).build();
    } catch (RuntimeException e) {
      System.err.println(
          "Error while parsing icons.json file. Indicates that specified icons.json is not valid. Please retry with correct file (or) raise an issue");
      throw e;
    }
  }
}
