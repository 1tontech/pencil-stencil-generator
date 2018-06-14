package in.oneton.pencil.stencil.generator;

import in.oneton.pencil.stencil.fontawesome.FontAwesomeDefinitionGenerator;
import in.oneton.pencil.stencil.generator.model.DefinitionMetadata;
import in.oneton.pencil.stencil.generator.model.ShapeMetadata;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static in.oneton.pencil.stencil.fontawesome.FontAwesomeDefinitionGenerator.FONT_AWESOME_FREE_METADATA_ICONS_JSON_URL;
import static in.oneton.pencil.stencil.generator.IconSetType.fontawesome;
import static java.util.Arrays.asList;
import static org.apache.commons.text.StringEscapeUtils.escapeXml11;

public class StencilDefinitionGenerator {

  private static final String XML_DEFINITION_PREFIX =
      "<Shapes xmlns=\"http://www.evolus.vn/Namespace/Pencil\" id=\"%s\"\n"
          + "        displayName=\"%s\" author=\"%s\" url=\"%s\"\n"
          + "        description=\"%s\">\n" + "    <Properties>\n"
          + "        <PropertyGroup name=\"Colors\">\n"
          + "            <Property name=\"color\" type=\"Color\" displayName=\"Color\">#000000FF</Property>\n"
          + "            <Property name=\"disabledColor\" type=\"Color\" displayName=\"Disabled Color\">#AAAAAAFF</Property>\n"
          + "        </PropertyGroup>\n" + "    </Properties>\n";

  private static final String XML_SHAPE =
      "    <Shape id=\"%s\" displayName=\"%s\" icon=\"data:image/svg+xml;charset=utf8,%s\">\n"
          + "        <Properties>\n" + "            <PropertyGroup>\n"
          + "                <Property name=\"box\" type=\"Dimension\" displayName=\"Size\">%.5f,%.5f</Property>\n"
          + "                <Property name=\"disabled\" displayName=\"Disabled\" type=\"Bool\">false</Property>\n"
          + "            </PropertyGroup>\n" + "            <PropertyGroup name=\"Colors\">\n"
          + "                <Property name=\"color\" type=\"Color\" displayName=\"Color\"><E>$$color</E></Property>\n"
          + "                <Property name=\"disabledColor\" type=\"Color\" displayName=\"Disabled Color\"><E>$$disabledColor</E></Property>\n"
          + "            </PropertyGroup>\n" + "        </Properties>\n" + "        <Behaviors>\n"
          + "            <For ref=\"glyph\">\n" + "                <Box>$box</Box>\n"
          + "                <Disabled></Disabled>\n"
          + "                <Fill>$disabled.value ? $disabledColor : $color</Fill>\n"
          + "            </For>\n" + "        </Behaviors>\n" + "        <Content>\n"
          + "            %s\n" + "        </Content>\n" + "    </Shape>\n";

  private static final String XML_DEFINITION_SUFFIX = "</Shapes>";

  public static void main(String[] args) throws IOException {
    OptionParser optionParser = new OptionParser();

    ArgumentAcceptingOptionSpec<IconSetType> typeOptionSpec =
        optionParser.acceptsAll(asList("t", "type"), "Icon set type. Eg. fontawesome")
            .withOptionalArg().ofType(IconSetType.class).defaultsTo(fontawesome);

    ArgumentAcceptingOptionSpec<String> inputOptionSpec = optionParser
        .acceptsAll(asList("i", "input"),
            "Input path to fetch icon definitions from. Can be local zip (or) file (or) can point to url")
        .withOptionalArg().defaultsTo(FONT_AWESOME_FREE_METADATA_ICONS_JSON_URL);

    ArgumentAcceptingOptionSpec<String> outputOptionSpec = optionParser
        .acceptsAll(asList("o", "outputFileName"),
            "Destination zip filename to save pencil stencil definition to. Can be name (or) absolute path")
        .withOptionalArg();

    AbstractOptionSpec<Void> helpOptionSpec =
        optionParser.acceptsAll(asList("h", "help"), "Shows help").forHelp();

    OptionSet parsedOptionSet = optionParser.parse(args);
    if (parsedOptionSet.has(helpOptionSpec)) {
      if (parsedOptionSet.has(helpOptionSpec)) {
        optionParser.printHelpOn(new PrintWriter(System.out));
      } else {
        optionParser.printHelpOn(new PrintWriter(System.err));
        System.exit(1);
      }
    } else {
      IconSetType iconSetType = parsedOptionSet.valueOf(typeOptionSpec);
      String iconMetadataSourcePath = parsedOptionSet.valueOf(inputOptionSpec);
      String stencilDestinationNameOrPath = parsedOptionSet.has(outputOptionSpec) ? parsedOptionSet.valueOf(outputOptionSpec) : null;
      System.out.println("Please wait while we fetch & parse metadata");
      try {
        fetchParseAndWriteMetadata(iconSetType, iconMetadataSourcePath,
            stencilDestinationNameOrPath);
      } catch (RuntimeException e) {
        System.err.println(
            "Error encountered while generating. Used the following options. Type: " + iconSetType
                + "; Input metadata path: " + iconMetadataSourcePath
                + "\nPlease make sure the passed in path contains required metadata & destination is valid");
        e.printStackTrace();
        System.exit(1);
       }
    }
  }

  private static void fetchParseAndWriteMetadata(IconSetType iconSetType,
      String iconMetadataSourcePath, String stencilDestinationNameOrPath) throws IOException {
    DefinitionMetadata definitionMetadata;
    switch (iconSetType) {
      case fontawesome:
      case fa:
      case fa5:
      case fontawesome5:
      default:
        definitionMetadata =
            new FontAwesomeDefinitionGenerator(iconMetadataSourcePath, stencilDestinationNameOrPath).generate();
    }
    System.out.println("Reading of source metadata complete. Will generate stencil");
    File stencilDestination = new File(definitionMetadata.getOutputFilePathOrName());
    writeStencil(definitionMetadata, stencilDestination);
    System.out
        .println("Pencil Stencil Definition file saved to " + stencilDestination.getAbsolutePath());
  }

  private static void writeStencil(DefinitionMetadata definitionMetadata, File stencilDestinationPath)
      throws IOException {
    try (ZipOutputStream stencilZipStream = new ZipOutputStream(
        new FileOutputStream(stencilDestinationPath))) {
      System.out.println(
          "Creating & adding Definition.xml to stencil zip");
      StringBuilder builder = new StringBuilder();
      builder.append(String.format(XML_DEFINITION_PREFIX, definitionMetadata.getId(), definitionMetadata.getDisplayName(), definitionMetadata.getAuthor(), definitionMetadata.getUrl(), definitionMetadata.getDescription()));
      definitionMetadata.getShapeMetadataList().forEach(shape -> builder.append(String
          .format(XML_SHAPE, shape.getId(), escapeXml11(shape.getDescription()),
              escapeXml11(shape.getSvgAsStr()), shape.getWidth(), shape.getHeight(),
              shape.getSvgAsStr())));
      builder.append(XML_DEFINITION_SUFFIX);

      ZipEntry entry = new ZipEntry("Definition.xml");
      entry.setCreationTime(FileTime.from(Instant.now()));
      entry.setComment("Stencil definition file");
      stencilZipStream.putNextEntry(entry);
      byte[] bytes = builder.toString().getBytes();

      stencilZipStream.write(bytes, 0, bytes.length);
    }
  }

}
