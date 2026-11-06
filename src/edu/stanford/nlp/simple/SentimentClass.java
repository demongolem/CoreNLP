package edu.stanford.nlp.simple;

import java.util.NoSuchElementException;

/**
 * An enum for the Simple CoreNLP API to represent a sentiment value.
 *
 * @author <a href="mailto:angeli@stanford.edu">Gabor Angeli</a>
 */
public enum SentimentClass {
  /**
   * Strongly positive sentiment polarity.
   */
  VERY_POSITIVE,

  /**
   * Positive sentiment polarity.
   */
  POSITIVE,

  /**
   * Neutral or objective sentiment polarity.
   */
  NEUTRAL,

  /**
   * Negative sentiment polarity.
   */
  NEGATIVE,

  /**
   * Strongly negative sentiment polarity.
   */
  VERY_NEGATIVE,
  ;

  /**
   * Checks whether this sentiment is either {@link #VERY_POSITIVE} or {@link #POSITIVE}.
   *
   * <p><b>Example usage:</b></p>
   * <pre>{@code
   * SentimentClass sentiment = SentimentClass.POSITIVE;
   * if (sentiment.isPositive()) {
   *   System.out.println("Positive response detected");
   * }
   * }</pre>
   *
   * @return {@code true} if positive or very positive; {@code false} otherwise.
   */
  public boolean isPositive() {
    return this == VERY_POSITIVE || this == POSITIVE;
  }

  /**
   * Checks whether this sentiment is either {@link #VERY_NEGATIVE} or {@link #NEGATIVE}.
   *
   * <p><b>Example usage:</b></p>
   * <pre>{@code
   * SentimentClass sentiment = SentimentClass.NEGATIVE;
   * if (sentiment.isNegative()) {
   *   System.out.println("Negative response detected");
   * }
   * }</pre>
   *
   * @return {@code true} if negative or very negative; {@code false} otherwise.
   */
  public boolean isNegative() {
    return this == VERY_NEGATIVE || this == NEGATIVE;
  }

  /**
   * Checks whether this sentiment represents an extreme emotional polarity (either {@link #VERY_NEGATIVE} or {@link #VERY_POSITIVE}).
   *
   * <p><b>Example usage:</b></p>
   * <pre>{@code
   * if (sentiment.isExtreme()) {
   *   System.out.println("Strong emotional valence");
   * }
   * }</pre>
   *
   * @return {@code true} if strongly positive or strongly negative; {@code false} otherwise.
   */
  public boolean isExtreme() {
    return this == VERY_NEGATIVE || this == VERY_POSITIVE;
  }

  /**
   * Checks whether this sentiment is mild (i.e. not {@link #VERY_NEGATIVE} and not {@link #VERY_POSITIVE}).
   *
   * <p><b>Example usage:</b></p>
   * <pre>{@code
   * if (sentiment.isMild()) {
   *   System.out.println("Mild sentiment");
   * }
   * }</pre>
   *
   * @return {@code true} if neutral, positive, or negative without extreme intensity; {@code false} otherwise.
   */
  public boolean isMild() {
    return !isExtreme();
  }

  /**
   * Checks whether this sentiment is {@link #NEUTRAL}.
   *
   * <p><b>Example usage:</b></p>
   * <pre>{@code
   * if (sentiment.isNeutral()) {
   *   System.out.println("Neutral tone");
   * }
   * }</pre>
   *
   * @return {@code true} if neutral; {@code false} otherwise.
   */
  public boolean isNeutral() {
    return this == NEUTRAL;
  }


  /**
   * Get the sentiment class from the Stanford Sentiment Treebank
   * integer encoding. That is, an integer between 0 and 4 (inclusive)
   *
   * @param sentiment The Integer representation of a sentiment.
   *
   * @return The sentiment class associated with that integer.
   */
  public static SentimentClass fromInt(int sentiment) {
    switch (sentiment) {
      case 0:
        return VERY_NEGATIVE;
      case 1:
        return NEGATIVE;
      case 2:
        return NEUTRAL;
      case 3:
        return POSITIVE;
      case 4:
        return VERY_POSITIVE;
      default:
        throw new NoSuchElementException("No sentiment value for integer: " + sentiment);
    }
  }
}
