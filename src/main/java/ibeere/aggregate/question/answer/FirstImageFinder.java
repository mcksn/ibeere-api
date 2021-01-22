package ibeere.aggregate.question.answer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Optional.empty;
import static java.util.Optional.of;


public class FirstImageFinder {
    private static Pattern PATTERN = Pattern.compile("src=\"(.*?)\"");

    /**
     * Parses the given answer content (assuming a very specific format) to find the first (positionally higher) image.
     * Fetches the image to determine height and width.
     */
    public static Optional<Image> findFirstImage(String content) {
        final Matcher matcher = PATTERN.matcher(content);


        Optional<String> url = matcher.find() ? of(matcher.group(0)
                .replaceAll("\\\"", "")
                .replaceAll("src=", "")) : empty();


        if (url.isPresent()) {
            final BufferedImage bufferedImage;
            try {

                final URL input = new URL(url.get());
                URLConnection urlConnection = input.openConnection();
                urlConnection.addRequestProperty("User-Agent",
                        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");

                bufferedImage = ImageIO.read(urlConnection.getInputStream());

                return Optional.ofNullable( bufferedImage)
                        .map(bufferedImage1 -> new Image(url.get(), bufferedImage.getWidth(), bufferedImage.getHeight()));

            } catch (Exception e) {
                e.printStackTrace(); // TODO cleanup
                return empty();
            }
        } else {
            return empty();

        }
    }
}
