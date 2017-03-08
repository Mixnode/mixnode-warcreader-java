<b>How to build the code:</b>

mvn clean dependency:copy-dependencies package

The output jar file is generated in target/ folder.
All dependencies are stored in target/dependency/ folder.
To use this library in your code you have to include WarcReader-1.jar and all jar files in dependency/ folder into your classpath.



<b>Run stand-alone test:</b>

java -cp "target/warcreader-1.jar:target/dependency/*:target/test-classes" com.mixnode.test.warcreader.TestWarcReader
