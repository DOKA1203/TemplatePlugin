package kr.doka.template.command

import io.papermc.paper.command.brigadier.Commands
import kr.doka.template.command.hello.HelloCommand

object TemplateCommand {
    fun create() =
        Commands
            .literal("template")
            .then(HelloCommand.build())
}
