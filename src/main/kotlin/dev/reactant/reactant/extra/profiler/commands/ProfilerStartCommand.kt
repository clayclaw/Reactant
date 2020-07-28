package dev.reactant.reactant.extra.profiler.commands

import com.google.common.net.UrlEscapers
import dev.reactant.reactant.core.commands.ReactantPermissions
import dev.reactant.reactant.extra.command.ReactantCommand
import dev.reactant.reactant.extra.file.FileIOUploadService
import dev.reactant.reactant.extra.profiler.ReactantProfilerService
import dev.reactant.reactant.service.spec.parser.JsonParserService
import dev.reactant.reactant.service.spec.profiler.ProfilerDataProvider
import io.reactivex.rxjava3.schedulers.Schedulers
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import picocli.CommandLine

@CommandLine.Command(
        name = "start",
        mixinStandardHelpOptions = true,
        description = ["Start the profiler"]
)
internal class ProfilerStartCommand(
        private val profilerService: ReactantProfilerService,
        private val fileIOUploadService: FileIOUploadService,
        private val jsonParser: JsonParserService
) : ReactantCommand() {
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

                        fileIOUploadService.upload("report.html", jsonParser.encode(data).blockingGet())
                                .subscribeOn(Schedulers.io())
                                .subscribe { res ->
                                    if (sender is Player) {
                                        val msg = TextComponent("Click here to open your profiler report")
                                        msg.clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, res.link)
                                        (sender as Player).spigot().sendMessage(msg)
                                    } else {
                                        sender.sendMessage("Profiler report: ${UrlEscapers.urlPathSegmentEscaper().escape(res.link)}")
                                    }
                                }
                        stdout.out("Uploading profiler report...")
                    }
            stdout.out("Started profiler successfully, use \"/reactant profiler stop ${profilerId}\" to stop profiling")
        }
    }

}
