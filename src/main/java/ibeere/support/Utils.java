package ibeere.support;

import org.apache.commons.math3.util.Precision;

import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static java.lang.String.valueOf;
import static java.math.BigDecimal.ROUND_FLOOR;
import static java.time.temporal.ChronoUnit.DAYS;
import static ibeere.support.ClockProvider.STANDARD_CLOCK;
import static ibeere.support.Constants.STANDARD_ZONE;

public class Utils {

    public static final DecimalFormat REMOVE_TRAILING_ZEROS = new DecimalFormat("0.#");
    public static final DecimalFormat REMOVE_TRAILING_DIGITS = new DecimalFormat("###.#");

    public static String getDaysAgoToHuman(Instant submitDate) {
        int days = Math.toIntExact(DAYS.between(submitDate.atZone(STANDARD_ZONE).toLocalDate(),
                LocalDate.now(Clock.system(STANDARD_ZONE))));

        int years = Math.floorDiv(days, 365);
        int months = Math.floorDiv(days, 30);
        int weeks = Math.floorDiv(days, 7);

        if (years > 1)
            return years + " years ago";
        else if (years == 1)
            return "1 year ago";
        else if (months > 1)
            return months + " months ago";
        else if (months == 1)
            return "1 month ago ";
        else if (weeks > 0) {
            if (weeks == 1)
                return weeks + " week ago";
            return weeks + " weeks ago";
        } else if (days == 0)
            return " today";
        else if (days == 1)
            return " yesterday";
        else return days + " days ago";
    }

    public static Optional<String> getShortDateToHuman(Instant submitDate) {
        LocalDate localDate = submitDate.atZone(STANDARD_ZONE).toLocalDate();
        int days = Math.toIntExact(DAYS.between(localDate,
                LocalDate.now(STANDARD_CLOCK)));

        if (days == 0)
            return Optional.empty();
        else
            return Optional.of(localDate.format(DateTimeFormatter.ofPattern("dd MMM")));
    }

    public static String getActualDateHuman(Instant submitDate) {
        return ZonedDateTime.ofInstant(submitDate, STANDARD_ZONE).format(DateTimeFormatter.ofPattern("d MMM uuuu"));
    }

    public static String getCountsToHuman(long count) {

        final int digitCount = digitCount(count);

        if (digitCount > 3) {
            final double round = Precision.round((double) count, -(digitCount - 2), ROUND_FLOOR);
            final double d = round / 1000;
            return REMOVE_TRAILING_ZEROS.format(d) + "k";
        } else if (digitCount < 3 ) {
            return valueOf(count);
        } else {
            final double round = Precision.round((double) count, -(digitCount - 1), ROUND_FLOOR);
            return REMOVE_TRAILING_DIGITS.format(round) + "+";
        }
    }

    private static int digitCount(long count) {
        int digitCount = 0;
        while (count > 0) {
            count = count / 10;
            digitCount++;
        }

        return digitCount;
    }
}
