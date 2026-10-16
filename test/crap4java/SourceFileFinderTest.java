package crap4java;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SourceFileFinderTest {

    @TempDir
    Path tempDir;

    @Test
    void findsAllJavaFilesUnderSrcOnly() throws Exception {
        Path src = tempDir.resolve("src/main/java/demo");
        Files.createDirectories(src);
        Path inSrc = src.resolve("Sample.java");
        Files.writeString(inSrc, "class Sample {}\n");

        Path outOfSrc = tempDir.resolve("other/Elsewhere.java");
        Files.createDirectories(outOfSrc.getParent());
        Files.writeString(outOfSrc, "class Elsewhere {}\n");

        List<Path> files = SourceFileFinder.findAllJavaFilesUnderSrc(tempDir);

        assertEquals(List.of(inSrc), files);
    }

    @Test
    void skipsTestSourcesButNotATestPackageUnderMain() throws Exception {
        Path main = tempDir.resolve("src/main/java/demo");
        Files.createDirectories(main);
        Path inMain = main.resolve("Sample.java");
        Files.writeString(inMain, "class Sample {}\n");

        // A package called test, under main: still production source, still scored.
        Path mainTestPackage = tempDir.resolve("src/main/java/demo/test");
        Files.createDirectories(mainTestPackage);
        Path packagedAsTest = mainTestPackage.resolve("Helper.java");
        Files.writeString(packagedAsTest, "class Helper {}\n");

        Path test = tempDir.resolve("src/test/java/demo");
        Files.createDirectories(test);
        Files.writeString(test.resolve("SampleTest.java"), "class SampleTest {}\n");

        Path fixtures = tempDir.resolve("src/testFixtures/java/demo");
        Files.createDirectories(fixtures);
        Files.writeString(fixtures.resolve("SharedFixture.java"), "class SharedFixture {}\n");

        List<Path> files = SourceFileFinder.findAllJavaFilesUnderSrc(tempDir);

        assertEquals(List.of(inMain, packagedAsTest), files);
    }
}
