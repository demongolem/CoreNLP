package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;

import java.util.*;

/**
 * This is an interface for adding annotations to a partially annotated
 * Annotation.  In some ways, it is just a glorified function, except
 * that it explicitly operates in-place on Annotation objects.  Annotators
 * should be given to an AnnotationPipeline in order to make
 * annotation pipelines (the whole motivation of this package), and
 * therefore implementers of this interface should be designed to play
 * well with other Annotators and in their javadocs they should
 * explicitly state what annotations they are assuming already exist
 * in the annotation (like parse, POS tag, etc), what keys they are
 * expecting them under (see, for instance, the ones in CoreAnnotations),
 * and what annotations they will add (or modify) and the keys
 * for them as well.  If you would like to look at the code for a
 * relatively simple Annotator, I recommend NERAnnotator.  For a lot
 * of code you could just add the implements directly, but I recommend
 * wrapping instead because I believe that it will help to keep the
 * pipeline code more manageable.
 *
 * An Annotator should also provide a description of what it produces and
 * a description of what it requires to have been produced by using Sets
 * of requirements.
 * The StanfordCoreNLP version of the AnnotationPipeline can
 * enforce requirements, throwing an exception if an annotator does
 * not have all of its prerequisites met.  An Annotator which does not
 * participate in this system can simply return Collections.emptySet()
 * for both requires() and requirementsSatisfied().
 *
 * <h2>Properties</h2>
 *
 * We extensively use Properties objects to configure each Annotator.
 * In particular, CoreNLP has most of its properties in an informal
 * namespace with properties names like "parse.maxlen" to specify that
 * a property only applies to a parser annotator. There can also be
 * global properties; they should not have any periods in their names.
 * Each Annotator knows its own name; we assume these don't collide badly,
 * though possibly two parsers could share the "parse.*" namespace.
 * An Annotator should have a constructor that simply takes a Properties
 * object. At this point, the Annotator should expect to be getting
 * properties in namespaces. The classes that annotators call (like
 * a concrete parser, tagger, or whatever) mainly expect properties
 * not in namespaces. In general the annotator should subset the
 * passed in properties to keep only global properties and ones in
 * its own namespace, and then strip the namespace prefix from the
 * latter properties.
 *
 * @author Jenny Finkel
 */
public interface Annotator {

  /**
   * Given an {@link Annotation}, performs this annotator's task in-place.
   *
   * <p><b>Example usage:</b></p>
   * <pre>{@code
   * Annotator annotator = new POSTaggerAnnotator("pos", new Properties());
   * Annotation document = new Annotation("Stanford University is located in California.");
   * // First tokenize and sentence split
   * new TokenizerAnnotator().annotate(document);
   * new WordsToSentencesAnnotator().annotate(document);
   * // Annotate with part-of-speech tags
   * annotator.annotate(document);
   * }</pre>
   *
   * @param annotation The {@link Annotation} object representing the document to be processed and modified in-place. Must not be null.
   * @throws IllegalArgumentException If the provided annotation is null or missing prerequisite annotations.
   * @throws RuntimeException If an unrecoverable error occurs during processing.
   */
  void annotate(Annotation annotation);

  /**
   * Hook called when this annotator unmounts or is removed from the
   * {@link AnnotatorPool}.
   * By default, nothing is done. Implementations should override this to release
   * allocated resources, model handles, or background thread pools.
   *
   * <p><b>Example usage:</b></p>
   * <pre>{@code
   * annotator.unmount();
   * }</pre>
   */
  default void unmount() { }


  /**
   * Returns a set of requirement keys for annotations that this annotator provides or satisfies.
   * For example, the POS annotator returns {@code Collections.singleton(CoreAnnotations.PartOfSpeechAnnotation.class)}.
   *
   * <p><b>Example usage:</b></p>
   * <pre>{@code
   * Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
   * if (satisfied.contains(CoreAnnotations.NamedEntityTagAnnotation.class)) {
   *   System.out.println("Produces NER tags");
   * }
   * }</pre>
   *
   * @return A {@link Set} of {@link CoreAnnotation} class types satisfied by this annotator; never null (may be empty).
   */
  Set<Class<? extends CoreAnnotation>> requirementsSatisfied();

  /**
   * Returns the set of requirement keys which this annotator requires to be present
   * in the {@link Annotation} prior to {@link #annotate(Annotation)} execution.
   * For example, the POS annotator requires tokenization and sentence splitting.
   *
   * <p><b>Example usage:</b></p>
   * <pre>{@code
   * Set<Class<? extends CoreAnnotation>> reqs = annotator.requires();
   * if (reqs.contains(CoreAnnotations.TokensAnnotation.class)) {
   *   System.out.println("Requires tokenized input");
   * }
   * }</pre>
   *
   * @return A {@link Set} of {@link CoreAnnotation} class types required by this annotator; never null (may be empty).
   */
  Set<Class<? extends CoreAnnotation>> requires();

  /**
   * Tokenizer annotator key ("tokenize"). Splits raw text into tokens.
   */
  String STANFORD_TOKENIZE = "tokenize";

  /**
   * Clean XML annotator key ("cleanxml"). Removes or escapes XML/HTML tags.
   */
  String STANFORD_CLEAN_XML = "cleanxml";

  /**
   * Sentence splitter annotator key ("ssplit"). Splits token sequences into sentences.
   */
  String STANFORD_SSPLIT = "ssplit";

  /**
   * Part-of-speech tagger annotator key ("pos"). Assigns Penn Treebank POS tags to tokens.
   */
  String STANFORD_POS = "pos";

  /**
   * Lemmatizer annotator key ("lemma"). Generates base morphological lemma forms for tokens.
   */
  String STANFORD_LEMMA = "lemma";

  /**
   * Named entity recognizer annotator key ("ner"). Identifies entities such as PERSON, LOCATION, ORGANIZATION.
   */
  String STANFORD_NER = "ner";

  /**
   * Regular expression NER annotator key ("regexner"). Matches rule-based regex patterns to named entity tags.
   */
  String STANFORD_REGEXNER = "regexner";

  /**
   * TokensRegex annotator key ("tokensregex"). Executes pattern expressions over token sequences.
   */
  String STANFORD_TOKENSREGEX = "tokensregex";

  /**
   * Entity mentions annotator key ("entitymentions"). Extracts multi-word entity mentions.
   */
  String STANFORD_ENTITY_MENTIONS = "entitymentions";

  /**
   * Gender annotator key ("gender"). Assigns gender labels to person names.
   */
  String STANFORD_GENDER = "gender";

  /**
   * Truecase annotator key ("truecase"). Determines the canonical casing of words in text.
   */
  String STANFORD_TRUECASE = "truecase";

  /**
   * Constituent parse annotator key ("parse"). Generates full syntactic phrase structure trees.
   */
  String STANFORD_PARSE = "parse";

  /**
   * Deterministic coreference annotator key ("dcoref"). Rule-based coreference resolution system.
   */
  String STANFORD_DETERMINISTIC_COREF = "dcoref";

  /**
   * Statistical/neural coreference annotator key ("coref"). Resolves coreference mentions across documents.
   */
  String STANFORD_COREF = "coref";

  /**
   * Coreference mention detector key ("coref.mention"). Identifies mentions to link in coreference chains.
   */
  String STANFORD_COREF_MENTION = "coref.mention";

  /**
   * Relation extraction annotator key ("relation"). Identifies semantic relations between entity pairs.
   */
  String STANFORD_RELATION = "relation";

  /**
   * Sentiment analysis annotator key ("sentiment"). Predicts 5-class sentiment on sentence parse trees.
   */
  String STANFORD_SENTIMENT = "sentiment";

  /**
   * Column data classifier annotator key ("cdc"). Classifies tabular column-formatted data.
   */
  String STANFORD_COLUMN_DATA_CLASSIFIER = "cdc";

  /**
   * Neural dependency parser annotator key ("depparse"). Builds Universal Dependencies graphs.
   */
  String STANFORD_DEPENDENCIES = "depparse";

  /**
   * Natural logic annotator key ("natlog"). Annotates sentence tokens with natural logic operator scopes.
   */
  String STANFORD_NATLOG = "natlog";

  /**
   * Open Information Extraction annotator key ("openie"). Extracts open-domain relational triples (subject, relation, object).
   */
  String STANFORD_OPENIE = "openie";

  /**
   * Quotation annotator key ("quote"). Detects direct speech and quotes in prose.
   */
  String STANFORD_QUOTE = "quote";

  /**
   * Quotation attribution annotator key ("quote.attribution"). Links quotes to speaker entities.
   */
  String STANFORD_QUOTE_ATTRIBUTION = "quote.attribution";

  /**
   * Universal Dependencies morphological features annotator key ("udfeats"). Assigns morphological feature maps.
   */
  String STANFORD_UD_FEATURES = "udfeats";

  /**
   * Entity linker annotator key ("entitylink"). Links entity mentions to external knowledge bases like Wikipedia/Wikidata.
   */
  String STANFORD_LINK = "entitylink";

  /**
   * Knowledge Base Population annotator key ("kbp"). Extracts slot-filling knowledge base facts.
   */
  String STANFORD_KBP = "kbp";


  /**
   * A mapping from an annotator to a its default transitive dependencies.
   * Note that this is not guaranteed to be accurate, as properties set in the annotator
   * can change the annotator's dependencies; but, it's a reasonable guess if you're using
   * things out-of-the-box.
   */
  @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
  Map<String, Set<String>> DEFAULT_REQUIREMENTS = new HashMap<String, Set<String>>(){{
    put(STANFORD_TOKENIZE,                 new LinkedHashSet<>(Arrays.asList()));
    put(STANFORD_CLEAN_XML,                new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE)));
    put(STANFORD_SSPLIT,                   new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE)));
    put(STANFORD_POS,                      new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT)));
    put(STANFORD_LEMMA,                    new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT, STANFORD_POS)));
    put(STANFORD_NER,                      new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT, STANFORD_POS, STANFORD_LEMMA)));
    put(STANFORD_TOKENSREGEX,              new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE)));
    put(STANFORD_REGEXNER,                 new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT)));
    put(STANFORD_ENTITY_MENTIONS,          new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT, STANFORD_POS, STANFORD_LEMMA, STANFORD_NER)));
    put(STANFORD_GENDER,                   new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT, STANFORD_POS, STANFORD_LEMMA, STANFORD_NER)));
    put(STANFORD_TRUECASE,                 new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT)));
    put(STANFORD_PARSE,                    new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT, STANFORD_POS)));
    put(STANFORD_DETERMINISTIC_COREF,      new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT, STANFORD_POS, STANFORD_LEMMA, STANFORD_NER, STANFORD_PARSE)));
    put(STANFORD_COREF,                    new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT, STANFORD_POS, STANFORD_LEMMA, STANFORD_NER, STANFORD_DEPENDENCIES)));
    put(STANFORD_COREF_MENTION,                  new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT, STANFORD_POS, STANFORD_LEMMA, STANFORD_NER, STANFORD_DEPENDENCIES)));
    put(STANFORD_RELATION,                 new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT, STANFORD_POS, STANFORD_LEMMA, STANFORD_NER, STANFORD_PARSE, STANFORD_DEPENDENCIES)));
    put(STANFORD_SENTIMENT,                new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT, STANFORD_POS, STANFORD_PARSE)));
    put(STANFORD_COLUMN_DATA_CLASSIFIER,   new LinkedHashSet<>(Arrays.asList()));
    put(STANFORD_DEPENDENCIES,             new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT, STANFORD_POS)));
    put(STANFORD_NATLOG,                   new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT, STANFORD_POS, STANFORD_LEMMA, STANFORD_DEPENDENCIES)));
    put(STANFORD_OPENIE,                   new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT, STANFORD_POS, STANFORD_LEMMA, STANFORD_DEPENDENCIES, STANFORD_NATLOG)));
    put(STANFORD_QUOTE,                    new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT, STANFORD_POS, STANFORD_LEMMA, STANFORD_NER)));
    put(STANFORD_QUOTE_ATTRIBUTION,        new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT, STANFORD_POS, STANFORD_LEMMA, STANFORD_NER, STANFORD_COREF_MENTION, STANFORD_DEPENDENCIES, STANFORD_QUOTE)));
    put(STANFORD_UD_FEATURES,              new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT, STANFORD_POS, STANFORD_DEPENDENCIES)));
    put(STANFORD_LINK,                     new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT, STANFORD_POS, STANFORD_DEPENDENCIES, STANFORD_LEMMA, STANFORD_NER, STANFORD_ENTITY_MENTIONS)));
    put(STANFORD_KBP,                      new LinkedHashSet<>(Arrays.asList(STANFORD_TOKENIZE, STANFORD_SSPLIT, STANFORD_POS, STANFORD_DEPENDENCIES, STANFORD_LEMMA, STANFORD_NER, STANFORD_COREF, STANFORD_REGEXNER)));
  }};

}
