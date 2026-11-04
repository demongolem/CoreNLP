# Stanford CoreNLP API Reference

## Table of Contents
1. [Core Pipeline API (`edu.stanford.nlp.pipeline`)](#1-core-pipeline-api)
2. [Simple CoreNLP API (`edu.stanford.nlp.simple`)](#2-simple-corenlp-api)
3. [Core Data Structures (`edu.stanford.nlp.ling`)](#3-core-data-structures)
4. [Syntactic & Semantic Representations](#4-syntactic--semantic-representations)

---

## 1. Core Pipeline API

### `StanfordCoreNLP`
The primary orchestration engine.

#### Constructors
```java
public StanfordCoreNLP(Properties props)
public StanfordCoreNLP(Properties props, boolean enforceRequirements)
public StanfordCoreNLP(String propsFile)
```
- **Parameters**:
  - `props`: Key-value properties controlling annotator selection and configuration.
  - `enforceRequirements`: If `true` (default), verifies all dependency prerequisites.
- **Exceptions**:
  - `IllegalArgumentException`: If prerequisites are unsatisfied or unknown annotators are requested.

#### Core Methods
```java
public void annotate(Annotation annotation)
public void annotate(CoreDocument document)
public void annotate(Annotation annotation, Consumer<Annotation> callback)
```
- **`annotate(Annotation annotation)`**:
  - Runs all configured annotators sequentially on the input annotation in-place.
- **`annotate(CoreDocument document)`**:
  - Runs the pipeline over the document and populates high-level sentence and entity mention objects.
- **`annotate(Annotation annotation, Consumer<Annotation> callback)`**:
  - Runs annotation asynchronously across the background thread pool, calling `callback.accept(annotation)` on completion.

---

## 2. Simple CoreNLP API

The `edu.stanford.nlp.simple` package offers a convenient, Pythonic interface that loads models lazily on demand.

### `Document`
Represents an entire document.

#### Constructors
```java
public Document(String text)
public Document(Properties props, String text)
```

#### Key Methods
- `List<Sentence> sentences()`: Returns the sentences in this document.
- `Map<Integer, CorefChain> coref()`: Returns the document's coreference clusters.
- `String json()`: Serializes the document to JSON string.
- `void serialize(OutputStream out)`: Serializes the document into protobuf format.

### `Sentence`
Represents an individual sentence with lazy annotator execution.

#### Constructors
```java
public Sentence(String text)
public Sentence(List<String> tokens)
```

#### Key Methods
- `List<String> words()`: Surface tokens.
- `List<String> lemmas()`: Base lemmas for each word.
- `List<String> posTags()`: Part-of-speech tags.
- `List<String> nerTags()`: Named entity recognition tags.
- `Tree parse()`: Constituency parse tree.
- `SemanticGraph dependencyGraph()`: Universal Dependencies graph.
- `List<RelationTriple> openieTriples()`: OpenIE relational triples `(subject, relation, object)`.
- `String sentiment()`: Sentiment classification string.
- `List<String> mentions()`: Named entity mentions in the sentence.

---

## 3. Core Data Structures

### `Annotation`
- An instance of `ArrayCoreMap` acting as the root container for document metadata and sentence sequences.
- Access via typed keys:
  ```java
  List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  ```

### `CoreLabel`
- Extends `ArrayCoreMap` to represent token-level linguistic features:
  ```java
  String word = token.get(CoreAnnotations.TextAnnotation.class);
  String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
  String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
  int begin = token.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
  ```

---

## 4. Syntactic & Semantic Representations

### `Tree` (`edu.stanford.nlp.trees.Tree`)
- Represents phrase structure syntax trees.
- Methods:
  - `pennPrint()`: Prints the tree in standard Penn Treebank bracketed notation.
  - `depth()`: Computes tree depth.
  - `getLeaves()`: Returns terminal leaves.

### `SemanticGraph` (`edu.stanford.nlp.semgraph.SemanticGraph`)
- Directed graph representing Universal Dependencies.
- Methods:
  - `getRoots()`: Returns root nodes of the sentence.
  - `edgeListSorted()`: Returns typed dependency edges `(governor -> dependent, relation)`.
  - `toCompactString()`: Formats graph as compact dependency strings.
