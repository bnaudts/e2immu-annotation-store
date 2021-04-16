# e2immu/annotation-store

## Start the annotation store

To start, execute
```
gradle run
```

To start the server on a different port, either execute

```
gradle run -De2immu-port=9999
```

or uncomment the `applicationDefaultJvmArgs` line in the `application` block in `build.gradle`.

## As an example project

This project serves, in this initial development phase of the analyser, as an example project to run the analyser on.
While it only consists of two classes, its code is non-trivial, and depends on external libraries.

To run the analyser, execute
```
./gradlew e2immu-analyser --info  
```

If all went well, the annotations have been uploaded to the annotation store, and 

```
curl "http://localhost:8281/v1/list/e2immu-annotation-store?nice"
```

shows them in your terminal.
