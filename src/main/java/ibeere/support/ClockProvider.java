package ibeere.support;

import org.springframework.stereotype.Component;

import java.time.Clock;

import static ibeere.support.Constants.STANDARD_ZONE;

@Component
public class ClockProvider {

    public static Clock STANDARD_CLOCK = Clock.system(STANDARD_ZONE);

    public Clock standardClock(){
        return STANDARD_CLOCK;
    }
}
