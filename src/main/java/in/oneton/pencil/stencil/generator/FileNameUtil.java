package in.oneton.pencil.stencil.generator;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

import static in.oneton.pencil.stencil.fontawesome.FontAwesomeDefinitionGenerator.FONT_AWESOME_FREE_METADATA_ICONS_JSON_URL;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@UtilityClass
public class FileNameUtil {

  public static String computeStencilZipPath(String iconMetadataSourcePath,
      @Nullable String stencilDestinationNameOrPath) {
    if (!isEmpty(stencilDestinationNameOrPath)) {
      return appendZipExtensionIfNotPresent(stencilDestinationNameOrPath);
    } else {
      if (iconMetadataSourcePath.equalsIgnoreCase(FONT_AWESOME_FREE_METADATA_ICONS_JSON_URL)) {
        return "pencil-stencils-font-awesome5-free.zip";
      } else {
        boolean isHttpUrl = iconMetadataSourcePath.toLowerCase().startsWith("http");
        if (isHttpUrl) {
          return "pencil-stencils.zip";
        } else {
          stencilDestinationNameOrPath =
              new File("pencil-stencils-" + new File(iconMetadataSourcePath).getName()).getName();
          return appendZipExtensionIfNotPresent(stencilDestinationNameOrPath);
        }
      }
    }
  }

  @NotNull
  private static String appendZipExtensionIfNotPresent(String stencilDestinationNameOrPath) {
    if (!stencilDestinationNameOrPath.toLowerCase().endsWith(".zip")) {
      int lastDotIndex = stencilDestinationNameOrPath.lastIndexOf(".");
      if (lastDotIndex == -1) {
        return stencilDestinationNameOrPath + ".zip";
      } else {
        return stencilDestinationNameOrPath.substring(0, lastDotIndex) + ".zip";
      }
    }
    return stencilDestinationNameOrPath;
  }

}
