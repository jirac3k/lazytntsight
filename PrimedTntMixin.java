package com.example.lazytntsight.mixin;

import com.example.lazytntsight.LazyTntSightClient;
import com.example.lazytntsight.TntSyncAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * While the server ticks a TNT it sends its fuse every tick, so the client lets vanilla
 * prediction run. In a lazy chunk the server sends nothing, so after a short grace period
 * the client stops predicting and just keeps the TNT visible where the server left it.
 */
@Mixin(PrimedTnt.class)
public abstract class PrimedTntMixin implements TntSyncAccess {
    @Unique
    private int lazytntsight$lastSync = 0;

    @Override
    public void lazytntsight$markSynced() {
        this.lazytntsight$lastSync = ((Entity) (Object) this).tickCount;
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void lazytntsight$freezeIfServerStopped(CallbackInfo ci) {
        PrimedTnt self = (PrimedTnt) (Object) this;
        if (!LazyTntSightClient.enabled || !self.level().isClientSide()) {
            return;
        }
        if (self.tickCount - this.lazytntsight$lastSync > 3) {
            self.baseTick();
            ci.cancel();
        }
    }
}
