package kr.doka.template.command.hello

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import net.kyori.adventure.text.Component

object HelloCommand {
    fun build() =
        Commands
            .literal("hello")
            .then(
                Commands
                    .argument("player", ArgumentTypes.player())
                    .executes(::execute),
            )

    private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        val resolver = ctx.getArgument("player", PlayerSelectorArgumentResolver::class.java)
        val target = resolver.resolve(ctx.source).first()
        val sender = ctx.source.sender

        target.sendMessage(Component.text("안녕하세요, ${target.name}님!"))
        sender.sendMessage(Component.text("${target.name}님께 인사를 전달했습니다."))

        return Command.SINGLE_SUCCESS
    }
}
