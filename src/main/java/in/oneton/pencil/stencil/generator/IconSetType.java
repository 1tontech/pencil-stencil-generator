package in.oneton.pencil.stencil.generator;

/**
 * Specifies the types of icon sets
 */
public enum IconSetType {
  fontawesome("Represents font awesome as icon source type. Aliases include fontawesome5, fa, fa5"), fontawesome5("Represents font awesome as icon source type. Aliases include fontawesome, fa, fa5"), fa("Represents font awesome as icon source type. Aliases include fontawesome, fontawesome5, fa5"), fa5("Represents font awesome as icon source type. Aliases include fontawesome, fontawesome5, fa");

  private final String description;

  IconSetType(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
