package cm.aptoide.pt.codelabs;

public class JavaPresentation {

  private final int numberOfSlides;
  private final String title;
  private final JavaCreator javaCreator;
  private final KotlinCreator kotlinCreator;

  public JavaPresentation(int numberOfSlides, String title, JavaCreator javaCreator, KotlinCreator kotlinCreator) {

    this.numberOfSlides = numberOfSlides;
    this.title = title;
    this.javaCreator = javaCreator;
    this.kotlinCreator = kotlinCreator;
  }

  public int getNumberOfSlides() {
    return numberOfSlides;
  }

  public String getTitle() {
    return title;
  }

  public JavaCreator getJavaCreator() {
    return javaCreator;
  }

  public static void main(String[] args) {

    JavaPresentation myJavaPresentation =
        new JavaPresentation(5, "title", new JavaCreator("myname", "id"), new KotlinCreator("name","id"));

    JavaPresentation hisJavaPresentation =
        new JavaPresentation(5, "title", new JavaCreator("hisname", "id"), new KotlinCreator("nae","id"));

    if (myJavaPresentation != hisJavaPresentation) {
      System.out.print("They're different");
    }













  }













  public KotlinCreator getKotlinCreator() {
    return kotlinCreator;
  }
}
