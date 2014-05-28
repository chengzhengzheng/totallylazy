package com.googlecode.totallylazy;

import org.junit.Test;

import java.io.File;

import static com.googlecode.totallylazy.Files.TEMP_DIR;
import static com.googlecode.totallylazy.Files.ancestors;
import static com.googlecode.totallylazy.Files.ancestorsAndSelf;
import static com.googlecode.totallylazy.Files.append;
import static com.googlecode.totallylazy.Files.directory;
import static com.googlecode.totallylazy.Files.emptyVMDirectory;
import static com.googlecode.totallylazy.Files.emptyTemporaryDirectory;
import static com.googlecode.totallylazy.Files.file;
import static com.googlecode.totallylazy.Files.files;
import static com.googlecode.totallylazy.Files.hasSuffix;
import static com.googlecode.totallylazy.Files.name;
import static com.googlecode.totallylazy.Files.path;
import static com.googlecode.totallylazy.Files.randomFilename;
import static com.googlecode.totallylazy.Files.recursiveFiles;
import static com.googlecode.totallylazy.Files.temporaryFile;
import static com.googlecode.totallylazy.Files.workingDirectory;
import static com.googlecode.totallylazy.Files.write;
import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Predicates.contains;
import static com.googlecode.totallylazy.Predicates.equalTo;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Strings.bytes;
import static com.googlecode.totallylazy.Strings.endsWith;
import static com.googlecode.totallylazy.matchers.IterablePredicates.hasExactly;
import static com.googlecode.totallylazy.Predicates.notNullValue;
import static com.googlecode.totallylazy.PredicateAssert.assertThat;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.PredicateAssert.assertFalse;
import static com.googlecode.totallylazy.PredicateAssert.assertTrue;

public class FilesTest {

    @Test
    public void shouldHaveSuffix(){
        assertThat(hasSuffix("xml").matches(new File("doesNotExist.xml")), is(true));
    }

    @Test
    public void shouldNotHaveSuffix(){
        assertThat(hasSuffix("xml").matches(new File("doesNotExist")), is(false));
    }

    @Test
    public void canDeleteNonEmptyDirectory() throws Exception {
        File parent = directory(TEMP_DIR, randomFilename());
        file(parent, "a");
        File childB = directory(parent, "b");
        file(parent, "c");
        file(childB, "c");
        assertThat(Files.delete(parent), is(true));
        assertThat(parent.exists(), is(false));
    }

    @Test
    public void shouldDeleteDirectoryContents() throws Exception {
        File parentDir = emptyVMDirectory("aTempDir");
        File file = Files.file(parentDir, "aFile");
        assertThat(file.exists(), is(true));
        emptyTemporaryDirectory(parentDir.getName());
        assertThat(file.exists(), is(false));
    }

    @Test
    public void supportsFiltering() throws Exception {
        File directory = emptyVMDirectory("filtering-test");
        File aFile = temporaryFile(directory);
        File anOtherFile = temporaryFile(directory);
        Sequence<File> files = files(directory);
        assertThat(files, contains(aFile, anOtherFile));
        assertThat(files.filter(where(name(), is(equalTo(aFile.getName())))), hasExactly(aFile));
    }

    @Test
    public void supportsRecursiveSequenceOfFiles() {
        assertThat(recursiveFiles(workingDirectory()).find(where(path(), endsWith("FilesTest.java"))).get(), notNullValue());
    }

    @Test
    public void handlesDirectoriesThatReturnANullArray() {
        assertThat(recursiveFiles(new File("doesNotExist")).find(where(name(), is("FilesTest.java"))), is(none(File.class)));
    }

    @Test
    public void listsAncestors() {
        File fileInWorkingDirectory = recursiveFiles(workingDirectory()).find(where(path(), endsWith("FilesTest.java"))).get();
        assertFalse(ancestors(fileInWorkingDirectory).exists(is(fileInWorkingDirectory)));

        assertTrue(ancestors(fileInWorkingDirectory).exists(is(fileInWorkingDirectory.getParentFile())));
        assertTrue(ancestors(fileInWorkingDirectory).exists(is(fileInWorkingDirectory.getParentFile().getParentFile())));
        assertTrue(ancestors(fileInWorkingDirectory).exists(is(workingDirectory())));
    }

    @Test
    public void listsAncestorsAndSelf() {
        File fileInWorkingDirectory = recursiveFiles(workingDirectory()).find(where(path(), endsWith("FilesTest.java"))).get();

        assertTrue(ancestorsAndSelf(fileInWorkingDirectory).exists(is(fileInWorkingDirectory)));
        assertTrue(ancestorsAndSelf(fileInWorkingDirectory).exists(is(fileInWorkingDirectory.getParentFile())));
        assertTrue(ancestorsAndSelf(fileInWorkingDirectory).exists(is(fileInWorkingDirectory.getParentFile().getParentFile())));
        assertTrue(ancestorsAndSelf(fileInWorkingDirectory).exists(is(workingDirectory())));
    }

    @Test
    public void shouldNotFindAFileThatDoesNotExist() {
        assertThat(recursiveFiles(workingDirectory()).find(where(name(), endsWith("doesNotExist"))), is(none(File.class)));
    }

    @Test
    public void appendToAFile() throws Exception {
       File aFile = temporaryFile();
       write(bytes("a"), aFile);
       append(bytes("b"), aFile);

       assertThat(Strings.lines(aFile).first(), is("ab"));
    }
}
