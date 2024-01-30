package club.eridani.epsilon.client.launch

import club.eridani.epsilon.client.Epsilon
import com.sun.org.apache.xpath.internal.operations.Mod
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

object InitManager {

    @JvmStatic
    fun onMinecraftInit() {

    }

    @JvmStatic
    fun onFinishingInit() {

    }

    @Suppress("UNUSED_PARAMETER")
    @Mod.EventHandler
    @JvmStatic
    fun preInitHook(event: FMLPreInitializationEvent) {
        Epsilon.preInit()
    }

    @Suppress("UNUSED_PARAMETER")
    @Mod.EventHandler
    @JvmStatic
    fun postInitHook(event: FMLPostInitializationEvent) {
        Epsilon.postInit()
    }

}