# Stanford CoreNLP Schema & Annotation Catalog

## Overview

Stanford CoreNLP represents linguistic annotations via a type-safe map mechanism (`TypesafeMap` / `CoreMap`). Keys in this map are subclasses of `CoreAnnotation<V>`, where `V` is the value type associated with that key.

This document describes the schema hierarchy, standard annotation keys, and data contracts across all levels of analysis.

---

## 1. Schema Hierarchy

```
Document Level (edu.stanford.nlp.pipeline.Annotation)
  │
  ├── CoreAnnotations.TextAnnotation                 [String]
  ├── CoreAnnotations.DocIDAnnotation                [String]
  ├── CoreAnnotations.SentencesAnnotation            [List<CoreMap>]
  │     │
  │     ├── CoreAnnotations.SentenceIndexAnnotation  [Integer]
  │     ├── CoreAnnotations.TextAnnotation           [String]
  │     ├── TreeCoreAnnotations.TreeAnnotation        [Tree]
  │     ├── SemanticGraphCoreAnnotations.BasicDependenciesAnnotation [SemanticGraph]
  │     ├── SentimentCoreAnnotations.SentimentClass   [String]
  │     └── CoreAnnotations.TokensAnnotation         [List<CoreLabel>]
  │           │
  │           ├── CoreAnnotations.TextAnnotation               [String]
  │           ├── CoreAnnotations.CharacterOffsetBeginAnnotation [Integer]
  │           ├── CoreAnnotations.CharacterOffsetEndAnnotation   [Integer]
  │           ├── CoreAnnotations.PartOfSpeechAnnotation       [String]
  │           ├── CoreAnnotations.LemmaAnnotation              [String]
  │           ├── CoreAnnotations.NamedEntityTagAnnotation     [String]
  │           └── CoreAnnotations.NormalizedNamedEntityTagAnnotation [String]
  │
  ├── CoreAnnotations.MentionsAnnotation             [List<CoreMap>]
  ├── CorefCoreAnnotations.CorefChainAnnotation      [Map<Integer, CorefChain>]
  └── CoreAnnotations.QuotesAnnotation               [List<CoreMap>]
```

---

## 2. Standard CoreAnnotations Catalog

### Document-Level Keys

| Key Class | Java Value Type | Set By | Description |
|-----------|-----------------|--------|-------------|
| `CoreAnnotations.TextAnnotation` | `String` | Pipeline / User | Raw source text of the document. |
| `CoreAnnotations.DocIDAnnotation` | `String` | User / Reader | Unique identifier for the document. |
| `CoreAnnotations.SentencesAnnotation` | `List<CoreMap>` | `ssplit` | List of sentence structures in the document. |
| `CoreAnnotations.TokensAnnotation` | `List<CoreLabel>` | `tokenize` | Flat list of all tokens across the entire document. |
| `CoreAnnotations.MentionsAnnotation` | `List<CoreMap>` | `entitymentions` | Recognized multi-word entity mentions. |
| `CorefCoreAnnotations.CorefChainAnnotation` | `Map<Integer, CorefChain>` | `coref` / `dcoref` | Map from cluster ID to CorefChain representing entity coreference clusters. |
| `CoreAnnotations.QuotesAnnotation` | `List<CoreMap>` | `quote` | Extracted quotations and direct speeches. |

### Sentence-Level Keys

| Key Class | Java Value Type | Set By | Description |
|-----------|-----------------|--------|-------------|
| `CoreAnnotations.SentenceIndexAnnotation` | `Integer` | `ssplit` | 0-indexed position of sentence in document. |
| `CoreAnnotations.CharacterOffsetBeginAnnotation` | `Integer` | `ssplit` | Character start offset in document text. |
| `CoreAnnotations.CharacterOffsetEndAnnotation` | `Integer` | `ssplit` | Character end offset in document text. |
| `CoreAnnotations.TokensAnnotation` | `List<CoreLabel>` | `tokenize` / `ssplit` | Tokens belonging to this individual sentence. |
| `TreeCoreAnnotations.TreeAnnotation` | `Tree` | `parse` | Constituency phrase structure parse tree. |
| `TreeCoreAnnotations.BinarizedTreeAnnotation` | `Tree` | `parse` / `sentiment` | Binarized constituency parse tree. |
| `SemanticGraphCoreAnnotations.BasicDependenciesAnnotation` | `SemanticGraph` | `depparse` / `parse` | Basic Universal Dependencies graph. |
| `SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation` | `SemanticGraph` | `depparse` / `parse` | Enhanced++ Universal Dependencies graph. |
| `SentimentCoreAnnotations.SentimentClass` | `String` | `sentiment` | Sentence sentiment ("Very Negative", "Negative", "Neutral", "Positive", "Very Positive"). |
| `NaturalLogicAnnotations.RelationTriplesAnnotation` | `Collection<RelationTriple>` | `openie` | Open Information Extraction relational facts `(subject, relation, object)`. |

### Token-Level Keys (`CoreLabel`)

| Key Class | Java Value Type | Set By | Description |
|-----------|-----------------|--------|-------------|
| `CoreAnnotations.TextAnnotation` | `String` | `tokenize` | Token surface text. |
| `CoreAnnotations.OriginalTextAnnotation` | `String` | `tokenize` | Original text before normalization. |
| `CoreAnnotations.CharacterOffsetBeginAnnotation` | `Integer` | `tokenize` | Character start offset in document. |
| `CoreAnnotations.CharacterOffsetEndAnnotation` | `Integer` | `tokenize` | Character end offset in document. |
| `CoreAnnotations.IndexAnnotation` | `Integer` | `tokenize` | 1-indexed position of token in sentence. |
| `CoreAnnotations.PartOfSpeechAnnotation` | `String` | `pos` | Penn Treebank POS tag (e.g., `NNP`, `VBD`, `JJ`). |
| `CoreAnnotations.LemmaAnnotation` | `String` | `lemma` | Base morphological lemma form. |
| `CoreAnnotations.NamedEntityTagAnnotation` | `String` | `ner` | NER label (`PERSON`, `ORGANIZATION`, `LOCATION`, `DATE`, `MONEY`, etc., or `O`). |
| `CoreAnnotations.NormalizedNamedEntityTagAnnotation` | `String` | `ner` | Normalized value (e.g., ISO date `2024-01-01` for "January 1st, 2024"). |
| `CoreAnnotations.TrueCaseAnnotation` | `String` | `truecase` | Predicted canonical casing tag (`UPPER`, `LOWER`, `INIT_UPPER`). |

---

## 3. Serialization Formats & Protocols

Stanford CoreNLP provides serializers for multiple output representations:

1. **Protocol Buffers (`CoreNLP.proto`)**:
   - High-performance binary serialization using `ProtobufAnnotationSerializer`.
   - Compact representation preserving all token, sentence, dependency, parse, and coreference structures.

2. **JSON**:
   - Generated via `JSONOutputter`:
     ```json
     {
       "docId": "doc-01",
       "sentences": [
         {
           "index": 0,
           "tokens": [
             {
               "index": 1,
               "word": "Stanford",
               "originalText": "Stanford",
               "lemma": "Stanford",
               "characterOffsetBegin": 0,
               "characterOffsetEnd": 8,
               "pos": "NNP",
               "ner": "ORGANIZATION"
             }
           ],
           "basicDependencies": [ ... ]
         }
       ]
     }
     ```

3. **XML**:
   - Generated via `XMLOutputter` adhering to `CoreNLP-to-HTML.xsl` schemas.

4. **CoNLL / CoNLL-U**:
   - Tab-separated standard representation for Universal Dependencies evaluations.
