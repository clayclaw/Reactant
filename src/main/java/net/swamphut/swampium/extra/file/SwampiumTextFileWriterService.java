package net.swamphut.swampium.extra.file;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import net.swamphut.swampium.core.swobject.container.SwObject;
import net.swamphut.swampium.core.swobject.dependency.ServiceProvider;
import net.swamphut.swampium.service.spec.file.text.TextFileWriterService;

import java.io.File;
import java.io.FileWriter;

@SwObject
@ServiceProvider(provide = TextFileWriterService.class)
public class SwampiumTextFileWriterService implements TextFileWriterService {
    @Override
    public Completable append(File file, String string) {
        return write(file, string, true);
    }

    @Override
    public Completable write(File file, String string) {
        return write(file, string, false);
    }

    protected Completable write(File file, String string, boolean append) {
        return Completable.fromAction(() -> {
            if (file.exists() && !file.isFile()) throw new IllegalArgumentException(file.getName() + " is not a file");
            file.getParentFile().mkdirs();
            file.createNewFile();

            FileWriter writer = new FileWriter(file, append);
            writer.write(string);
            writer.flush();

            writer.close();
        }).subscribeOn(Schedulers.io());
    }
}
