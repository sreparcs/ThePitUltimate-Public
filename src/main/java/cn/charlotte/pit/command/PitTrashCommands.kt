package cn.charlotte.pit.command

import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import cn.charlotte.pit.trash.TrashManager
import cn.charlotte.pit.util.chat.CC
import org.bukkit.entity.Player


@Command(name = "lj", aliases = ["trash", "垃圾桶"])
class PitTrashCommands {

    @Execute
    fun open(@Context player: Player) {
        TrashManager.openTrash(player)
    }

    @Execute(name = "open")
    fun openExplicit(@Context player: Player) {
        TrashManager.openTrash(player)
    }

    @Execute(name = "back")
    fun back(@Context player: Player): String {
        return CC.translate(TrashManager.restore(player))
    }

    @Execute(name = "time")
    fun time(@Context player: Player): String {
        return CC.translate("&7距离垃圾桶自动清理: &e" + TrashManager.getFormattedRemainingTime())
    }
}
