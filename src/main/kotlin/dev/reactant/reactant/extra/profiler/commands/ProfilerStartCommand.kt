package dev.reactant.reactant.extra.profiler.commands

import com.google.common.net.UrlEscapers
import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.commands.ReactantPermissions
import dev.reactant.reactant.extra.command.ReactantCommand
import dev.reactant.reactant.extra.parser.GsonJsonParserService
import dev.reactant.reactant.extra.profiler.ReactantProfilerService
import dev.reactant.reactant.service.spec.profiler.ProfilerDataProvider
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import picocli.CommandLine
import java.io.File
import java.time.LocalDateTime

@CommandLine.Command(
    name = "start",
    mixinStandardHelpOptions = true,
    description = ["Start the profiler"]
)
internal class ProfilerStartCommand(
    private val profilerService: ReactantProfilerService,
    private val jsonParser: GsonJsonParserService
) : ReactantCommand(ReactantPermissions.ADMIN.DEV.PROFILER.toString()) {
    data class TimingData(val time: Long, val tick: Int)

    override fun execute() {
        requirePermission(ReactantPermissions.ADMIN.DEV.PROFILER)
        profilerService.startMeasure().let { (profilerId, measuredData) ->
            val startMeasureTime = System.currentTimeMillis()
            measuredData
                .toList()
                .subscribe { list: MutableList<Pair<ProfilerDataProvider, ProfilerDataProvider.ProfilerData>> ->
                    val data = list.groupBy { it.first }
                        .mapValues {
                            it.value.map { it.second }.groupBy { it.path }
                                .mapValues {
                                    it.value.groupBy { it.target }
                                        .mapValues { it.value.map { TimingData(it.time, it.tick) } }
                                }
                        }

                    val folder = File("${ReactantCore.configDirPath}/profiler")
                    if(!folder.exists()) folder.createNewFile()

                    val reportName = "report-${LocalDateTime.now()}"

                    sender.sendMessage("Saving report...")

                    Completable.create { emitter ->
                        val file = File(folder, "$reportName.html")
                        if(!file.exists()) {
                            file.writeText(jsonParser.encode(data).blockingGet())
                        } else {
                            sender.sendMessage("Oh no! the file is already exist.")
                        }
                        emitter.onComplete()
                    }.subscribeOn(Schedulers.io()).subscribe {
                        sender.sendMessage("Profiler report saved: $reportName")
                    }

                }
            stdout.out("Started profiler successfully, use \"/reactant profiler stop ${profilerId}\" to stop profiling")
        }
    }
}
