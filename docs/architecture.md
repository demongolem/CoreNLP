# Stanford CoreNLP Architecture & Execution Model

## Overview

Stanford CoreNLP is an extensible natural language analysis framework built as a directed processing pipeline. It provides a unified Java API for running natural language processing tools over text, producing structured linguistic annotations stored inside an `Annotation` or modern `CoreDocument` wrapper.

---

## 1. Pipeline Architecture

The CoreNLP processing model centers on three key abstractions:

```
[Raw Text / Input]
        │
        ▼
   Annotation (CoreMap / Typesafe Map)
        │
        ▼
   Annotator 1 (e.g. tokenize) ──► Mutates Annotation in-place
        │
        ▼
   Annotator 2 (e.g. ssplit)   ──► Adds sentence segmentation
        │
        ▼
   Annotator 3 (e.g. pos, ner) ──► Adds token POS & entity labels
        │
        ▼
   Annotator N (e.g. parse)    ──► Adds syntactic parse trees
        │
        ▼
[Output Serializer / CoreDocument API]
```

### Core Abstractions

1. **`Annotation` (`edu.stanford.nlp.pipeline.Annotation`)**:
   - Implements `ArrayCoreMap`, an efficient type-safe heterogenous map keying annotation types to arbitrary objects.
   - All annotations (tokens, sentences, coreference graphs, sentiment labels) are attached as keys to this map.

2. **`Annotator` (`edu.stanford.nlp.pipeline.Annotator`)**:
   - The functional interface for pipeline components:
     ```java
     void annotate(Annotation annotation);
     Set<Class<? extends CoreAnnotation>> requires();
     Set<Class<? extends CoreAnnotation>> requirementsSatisfied();
     ```
   - Operates strictly in-place on the passed `Annotation` instance.

3. **`StanfordCoreNLP` (`edu.stanford.nlp.pipeline.StanfordCoreNLP`)**:
   - The orchestrating pipeline runner extending `AnnotationPipeline`.
   - Resolves dependencies among annotators, initializes required models, manages thread pools, and drives sequential or multi-threaded execution.

4. **`AnnotatorPool` (`edu.stanford.nlp.pipeline.AnnotatorPool`) & Caching**:
   - Heavyweight model weights (e.g., neural parsers, CRF models) are cached in `StanfordCoreNLP.GLOBAL_ANNOTATOR_CACHE` indexed by `(AnnotatorName, Signature)`.
   - Pipelines sharing the same configuration reuse model weights across instances without redundant initialization.

---

## 2. Annotator Dependency Resolution

Every annotator declares:
- **`requires()`**: The prerequisites that must already be populated in the `Annotation` before execution.
- **`requirementsSatisfied()`**: The annotation types guaranteed to be populated upon completion.

When initializing `new StanfordCoreNLP(props)`, the constructor validates the ordering of the requested `annotators` list against declared prerequisites. If a prerequisite annotator is missing, an `IllegalArgumentException` is thrown detailing the missing requirement.

### Standard Dependency Graph

```
tokenize
   │
   ├──► cleanxml (optional)
   │
   └──► ssplit
           │
           ├──► pos
           │     │
           │     ├──► lemma
           │     │       │
           │     │       ├──► ner
           │     │       │     │
           │     │       │     ├──► entitymentions
           │     │       │     └──► quote ──► quote.attribution
           │     │       │
           │     │       └──► depparse
           │     │               │
           │     │               ├──► natlog ──► openie
           │     │               └──► coref (statistical)
           │     │
           │     └──► parse (constituency)
           │             │
           │             ├──► sentiment
           │             └──► dcoref (rule-based)
           │
           └──► truecase
```

---

## 3. Concurrency & Threading Model

Stanford CoreNLP supports high-throughput parallel execution across documents:

- **`threads` Property**:
  ```properties
  annotators = tokenize, ssplit, pos, lemma, ner, parse
  threads = 8
  ```
- **Execution**:
  - `StanfordCoreNLP` allocates a semaphore matching the configured thread count.
  - Multi-threaded processing can be run using:
    ```java
    pipeline.annotate(annotation, completedAnn -> {
        // Callback invoked when processing finishes
    });
    ```
- **Thread Safety**:
  - `Annotator` implementations are required to be thread-safe for inference across distinct `Annotation` instances, or provide synchronized wrapping internally.
  - Model weights (such as CRF weights and neural network matrices) are read-only during inference and safely shared across threads.
