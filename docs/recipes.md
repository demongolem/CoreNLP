# Stanford CoreNLP Practical Usage Recipes

This guide provides concrete, copy-pasteable code examples for common natural language processing tasks using Stanford CoreNLP.

---

## 1. Basic Pipeline Execution (Tokens, Sentences, POS, Lemmas)

```java
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.Properties;

public class BasicPipelineExample {
  public static void main(String[] args) {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize, ssplit, pos, lemma");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    String text = "Stanford CoreNLP provides robust language processing tools. It is widely used.";
    Annotation document = new Annotation(text);

    pipeline.annotate(document);

    for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
      System.out.println("Sentence: " + sentence.get(CoreAnnotations.TextAnnotation.class));
      for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
        String word = token.get(CoreAnnotations.TextAnnotation.class);
        String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
        String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
        System.out.printf("  %s [pos=%s, lemma=%s]%n", word, pos, lemma);
      }
    }
  }
}
```

---

## 2. Named Entity Recognition (NER) & Entity Mentions

```java
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.Properties;

public class NERExample {
  public static void main(String[] args) {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, entitymentions");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation doc = new Annotation("Apple Inc. was founded by Steve Jobs in Cupertino, California.");

    pipeline.annotate(doc);

    // Iterating over extracted entity mentions
    for (CoreMap mention : doc.get(CoreAnnotations.MentionsAnnotation.class)) {
      String name = mention.get(CoreAnnotations.TextAnnotation.class);
      String entityType = mention.get(CoreAnnotations.NamedEntityTagAnnotation.class);
      System.out.printf("Mention: %s -> %s%n", name, entityType);
    }
  }
}
```

---

## 3. Dependency Parsing & Constituency Parsing

```java
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.Properties;

public class ParsingExample {
  public static void main(String[] args) {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize, ssplit, pos, parse, depparse");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation doc = new Annotation("The golden eagle flew across the blue sky.");

    pipeline.annotate(doc);

    for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
      // Constituency tree
      Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
      System.out.println("Constituency Tree:");
      tree.pennPrint();

      // Universal Dependencies
      SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
      System.out.println("Dependency Graph:\n" + dependencies.toString(SemanticGraph.OutputFormat.LIST));
    }
  }
}
```

---

## 4. Simple CoreNLP API (Fluent / Lightweight)

```java
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

public class SimpleAPIExample {
  public static void main(String[] args) {
    Document doc = new Document("Albert Einstein received the 1921 Nobel Prize in Physics. He was born in Germany.");

    for (Sentence sent : doc.sentences()) {
      System.out.println("Sentence: " + sent.text());
      System.out.println("Lemmas: " + sent.lemmas());
      System.out.println("POS Tags: " + sent.posTags());
      System.out.println("NER Tags: " + sent.nerTags());
    }
  }
}
```

---

## 5. Custom Annotator Implementation

```java
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;

public class WordCountAnnotator implements Annotator {

  public static class WordCountAnnotation implements CoreAnnotation<Integer> {
    @Override
    public Class<Integer> getType() {
      return Integer.class;
    }
  }

  public WordCountAnnotator(String name, Properties props) {
    // Custom configuration parameters can be parsed from props
  }

  @Override
  public void annotate(Annotation annotation) {
    int wordCount = 0;
    if (annotation.has(CoreAnnotations.TokensAnnotation.class)) {
      wordCount = annotation.get(CoreAnnotations.TokensAnnotation.class).size();
    }
    annotation.set(WordCountAnnotation.class, wordCount);
  }

  @Override
  public Set<Class<? extends CoreAnnotation>> requires() {
    return Collections.singleton(CoreAnnotations.TokensAnnotation.class);
  }

  @Override
  public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() {
    return Collections.singleton(WordCountAnnotation.class);
  }
}
```
Register and use with:
```properties
customAnnotatorClass.wordcount = com.example.WordCountAnnotator
annotators = tokenize, ssplit, wordcount
```
