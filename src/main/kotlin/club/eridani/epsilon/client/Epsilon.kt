package club.eridani.epsilon.client

import club.eridani.epsilon.client.Epsilon.MOD_ID
import club.eridani.epsilon.client.Epsilon.MOD_NAME
import club.eridani.epsilon.client.Epsilon.VERSION
import club.eridani.epsilon.client.config.ConfigManager
import club.eridani.epsilon.client.event.EventBus
import club.eridani.epsilon.client.event.ForgeAccessor
import club.eridani.epsilon.client.gui.def.AsyncRenderEngine
import club.eridani.epsilon.client.gui.def.ThemeContainer
import club.eridani.epsilon.client.language.TextManager
import club.eridani.epsilon.client.management.*
import club.eridani.epsilon.client.module.client.HUDEditor
import club.eridani.epsilon.client.module.client.RootGUI
import club.eridani.epsilon.client.notification.NotificationManager
import club.eridani.epsilon.client.util.Logger
import club.eridani.epsilon.client.util.ScaleHelper
import club.eridani.epsilon.client.util.TpsCalculator
import club.eridani.epsilon.client.util.Wrapper
import club.eridani.epsilon.client.util.graphics.ProjectionUtils
import club.eridani.epsilon.client.util.graphics.RenderUtils3D
import club.eridani.epsilon.client.util.graphics.ResolutionHelper
import club.eridani.epsilon.client.util.graphics.font.renderer.IconRenderer
import club.eridani.epsilon.client.util.graphics.font.renderer.MainFontRenderer
import club.eridani.epsilon.client.util.graphics.shaders.WindowBlurShader
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.lwjgl.opengl.Display
import net.minecraftforge.fml.common.Mod

@Mod(
    modid = MOD_ID,
    name = MOD_NAME,
    version = VERSION
)
object Epsilon {

    const val MOD_NAME = "Epsilon"
    const val MOD_ID = "epsilon"
    const val VERSION = "4.0u220529"

    const val INFO = "$MOD_NAME Build $VERSION - Epsilon Release"

    const val DEFAULT_COMMAND_PREFIX = "."
    const val DEFAULT_CONFIG_PATH = "Epsilon/"

    val mainThread: Thread = Thread.currentThread().also {
        it.priority = Thread.MAX_PRIORITY
    }

    var isReady = false

    @Suppress("UNUSED_PARAMETER")
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent, value: Int) {
        Logger.info("Pre initializing Epsilon with value: $value")
        Display.setTitle("$MOD_NAME $VERSION")
        ModuleManager
        CommandManager
        TextManager.readText()
        TextManager.setText()
        MainFontRenderer
        IconRenderer
        Fonts
    }

    @Suppress("UNUSED_PARAMETER")
    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent, value: String) {
        Logger.info("Post initializing Epsilon with value: $value")
        ConfigManager.loadAll(true)
        RootGUI.disable(notification = false, silent = true)
        HUDEditor.disable(notification = false, silent = true)
        ThemeContainer

        register(WindowBlurShader)
        register(SpartanCore)
        register(TpsCalculator)
        register(ResolutionHelper)
        register(ScaleHelper)
        register(ProjectionUtils)
        register(RenderUtils3D)

        register(ChatMessageManager)
        register(CombatManager)
        register(CommandManager)
        register(DisplayManager)
        register(EntityManager)
        register(FriendManager)
        register(GUIManager)
        register(HoleManager)
        register(HotbarManager)
        register(InputManager)
        register(InventoryTaskManager)
        register(PlayerPacketManager)
        register(ModuleManager)
        register(NotificationManager)
        register(TimerManager)
        register(TotemPopManager)

        ForgeAccessor.subscribe()

        DisplayManager.setIcon(DisplayManager.Icon.IE)

        AsyncRenderEngine.init()

        isReady = true
    }

    private fun register(obj: Any) {
        EventBus.subscribe(obj)
    }

}