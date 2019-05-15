package net.swamphut.swampium.extra.file

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.dependency.ServiceProvider
import net.swamphut.swampium.service.spec.file.text.TextFileReaderService
import java.io.*

@SwObject
@ServiceProvider(provide = [TextFileReaderService::class])
class SwampiumTextFileReaderService : TextFileReaderService {

    override fun readAll(file: File): Single<List<String>> = read(file).toList()

    override fun readAll(fileReader: Reader): Single<List<String>> = read(fileReader).toList()

    override fun read(file: File): Flowable<String> {
        return Single.defer<File> { Single.just(file) }.doOnSuccess {
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
