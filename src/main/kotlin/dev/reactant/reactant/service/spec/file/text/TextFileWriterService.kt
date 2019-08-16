package dev.reactant.reactant.service.spec.file.text

import io.reactivex.Completable

import java.io.File

interface TextFileWriterService {
    /**
     * Append the text into the end of file
     *
     *
     * Create the file if not exist
     */
    fun append(file: File, string: String): Completable

    /**
     * Write the text into file
     *
     *
     * Overwrite if the file is not empty
     * Create the file if not exist
     */
    fun write(file: File, string: String): Completable
}
