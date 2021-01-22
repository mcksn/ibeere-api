package ibeere.aggregate.question.answer.vote;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ibeere.ddd.ValueObject;

/**
 * Aggregated votes. up and down counts are accumulated but a non trivial aggregation of both is also stated
 */
@Getter
@RequiredArgsConstructor
public class Votes implements ValueObject {
    public final long down;
    public final long up;
    public final long aggregated;
}
