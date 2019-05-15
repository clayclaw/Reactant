package net.swamphut.swampium.service.spec.file.text;

import io.reactivex.Completable;

import java.io.File;

public interface TextFileWriterService {
    /**
     * Append the text into the end of file
     * <p>
     * Create the file if not exist
     */
    Completable append(File file, String string);

    /**
     * Write the text into file
     * <p>
     * Overwrite if the file is not empty
     * Create the file if not exist
     */
    Completable write(File file, String string);
}
