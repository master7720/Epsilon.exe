package club.eridani.epsilon.client

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin
import org.spongepowered.asm.launch.MixinBootstrap
import org.spongepowered.asm.mixin.MixinEnvironment
import org.spongepowered.asm.mixin.Mixins
import org.apache.logging.log4j.LogManager

@IFMLLoadingPlugin.Name("EpsilonCoreMod")
@IFMLLoadingPlugin.MCVersion("1.12.2")
class EpsilonCoreMod : IFMLLoadingPlugin {
    init
    {
        MixinBootstrap.init()
        Mixins.addConfigurations("mixins.Epsilon.json")
        MixinEnvironment.getDefaultEnvironment().obfuscationContext = "searge"
        LogManager.getLogger("Epsilon").info("Epsilon mixins initialised.")
    }

    override fun getASMTransformerClass(): Array<String>
    {
        return emptyArray()
    }

    override fun getModContainerClass(): String?
    {
        return null
    }

    override fun getSetupClass(): String?
    {
        return null
    }

    override fun injectData(data: Map<String, Any>)
    {
    }

    override fun getAccessTransformerClass(): String?
    {
        return null
    }
}