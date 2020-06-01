package dev.reactant.reactant.extra.file

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.service.spec.file.text.TextFileReaderService
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.*

@Component
class ReactantTextFileReaderService : TextFileReaderService {

    override fun readAll(file: File): Single<List<String>> = read(file).toList()

    override fun readAll(fileReader: Reader): Single<List<String>> = read(fileReader).toList()

    override fun read(file: File): Flowable<String> {
        return Single.defer { Single.just(file) }.doOnSuccess {
            if (!file.exists()) throw FileNotFoundException(file.name)
            if (file.exists() && !file.isFile)
                throw IllegalArgumentException(file.name + " is not a file")
        }.map { FileReader(file) }.flatMapPublisher { read(it) }
    }

    override fun read(fileReader: Reader): Flowable<String> {
        return Flowable.using(
                { BufferedReader(fileReader) },
                { bufferedReader -> Flowable.fromIterable<String>(Iterable { bufferedReader.lines().iterator() }) },
                { it.close() }
        ).subscribeOn(Schedulers.io())
    }
}
