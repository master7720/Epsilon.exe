package club.eridani.epsilon.client.launch

import club.eridani.epsilon.client.Epsilon
import com.sun.org.apache.xpath.internal.operations.Mod

object InitManager {

    @JvmStatic
    fun onMinecraftInit() {

    }

    @JvmStatic
    fun onFinishingInit() {

    }

    @Suppress("UNUSED_PARAMETER")
    @JvmStatic
    fun preInitHook() {
        Epsilon.preInit()
    }

    @Suppress("UNUSED_PARAMETER")
    @JvmStatic
    fun postInitHook() {
        Epsilon.postInit()
    }

}