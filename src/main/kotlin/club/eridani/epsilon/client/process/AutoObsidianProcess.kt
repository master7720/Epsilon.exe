package club.eridani.epsilon.client.process

import baritone.api.process.IBaritoneProcess
import baritone.api.process.PathingCommand
import baritone.api.process.PathingCommandType
import club.eridani.epsilon.client.module.misc.AutoObsidian

object AutoObsidianProcess : IBaritoneProcess {

    override fun isTemporary(): Boolean {
        return true
    }

    override fun priority(): Double {
        return 3.0
    }

    override fun onLostControl() {}

    override fun displayName0(): String {
        return "AutoObsidian: ${AutoObsidian.getHudInfo()}"
    }

    override fun isActive(): Boolean {
        return AutoObsidian.isActive()
    }

    override fun onTick(p0: Boolean, p1: Boolean): PathingCommand {
        return AutoObsidian.goal?.let {
            PathingCommand(it, PathingCommandType.SET_GOAL_AND_PATH)
        } ?: PathingCommand(null, PathingCommandType.REQUEST_PAUSE)
    }
}