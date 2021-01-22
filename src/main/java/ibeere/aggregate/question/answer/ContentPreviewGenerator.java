package ibeere.aggregate.question.answer;

public class ContentPreviewGenerator {
    public static String generate(String content) {

    return content.replaceAll("<img([\\w\\W]+?)>", "");
    }
}
