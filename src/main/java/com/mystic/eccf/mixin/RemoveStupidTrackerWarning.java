package com.mystic.eccf.mixin;

//This is to remove a warning for the Entity Tracker Entries for when it despawns an entity from a chunk due to it being to full!

import net.minecraft.entity.EntityTrackerEntry;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityTrackerEntry.class)
public class RemoveStupidTrackerWarning {

    @Redirect(method = "Lnet/minecraft/entity/EntityTrackerEntry;createSpawnPacket()Lnet/minecraft/network/Packet;", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;)V"))
    public void removeWarning(Logger logger, String string) {}
}
