package uk.ac.ebi.uniprot.taxonomyservice.imports.utils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an util class that will execute utility methods that are commonly used in integration tests.
 *
 * Created by lgonzales on 10/08/16.
 */
public class TaxonomyImportTestUtils {

    private static final Logger logger = LoggerFactory.getLogger(TaxonomyImportTestUtils.class);

    public static void cleanNeo4JTemporaryData(Path neo4JPath) {
        try {
            Files.walkFileTree(neo4JPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            logger.error("unable to clean temporary neo4J filtes",e);
        }

    }
}
