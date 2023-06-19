package com.mystic.eccf.mixin;

//This is to remove a warning for the Entity Tracker Entries for when it despawns an entity from a chunk due to it being to full!

import net.minecraft.entity.EntityTrackerEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTrackerEntry.class)
public class RemoveStupidTrackerWarning {

    @Inject(method = "Lnet/minecraft/entity/EntityTrackerEntry;createSpawnPacket()Lnet/minecraft/network/Packet;", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;)V"), cancellable = true)
    private void removeWarning(CallbackInfo ci) {
        ci.cancel();
    }
}
