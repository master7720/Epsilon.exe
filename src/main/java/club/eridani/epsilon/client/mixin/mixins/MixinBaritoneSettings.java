package club.eridani.epsilon.client.mixin.mixins;

import baritone.api.Settings;
import club.eridani.epsilon.client.event.events.BaritoneSettingsInitEvent;
import club.eridani.epsilon.client.util.BaritoneUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Settings.class, remap = false)
public class MixinBaritoneSettings {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void baritoneSettingsInit(CallbackInfo ci) {
        BaritoneUtils.INSTANCE.setInitialized(true);
        BaritoneSettingsInitEvent.INSTANCE.post();
    }
}
