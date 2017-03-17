# Mixnode WARC Reader for Java
This library makes reading WARC files in Java extremely easy. It is open source and compatible with the latest WARC standard (<a href="https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.0/">  WARC-1.1 </a>).

## Build the Project

This project uses <a href="https://maven.apache.org/">Maven</a> for building the code and managing thedependecies.

```
mvn clean dependency:copy-dependencies package
```

The output jar file (WarcReader.jar) is stored in <I>target/</I> folder.
All dependencies are stored in <I>target/dependency/</I> folder.
To use this library in your code you have to include WarcReader-1.jar and all jar files in <I>dependency/</I> folder into your classpath.
This command also compiles the test and store the class file in <I>target/test/</I> folder.


## Running the Test

By adding the WarcReader jar file and all dependent jar files in <I>dependency/</I> folder, into java classpath you can run the java test.

```
java -cp "target/warcreader-1.jar:target/dependency/*:target/test-classes" com.mixnode.test.warcreader.TestWarcReader
```

