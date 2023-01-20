package dev.qther.quietus.mixin;

import dev.qther.quietus.Quietus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerXpDrop {
    @Inject(at = @At("HEAD"), method = "getXpToDrop()I", cancellable = true)
    public void getXpToDrop(CallbackInfoReturnable<Integer> cir) {
        if (!Quietus.CONFIG.enabled) {
            return;
        }

        PlayerEntity player = (PlayerEntity) (Object) this;

        if (player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || player.isSpectator()) {
            cir.setReturnValue(0);
        }

        double lvl = player.experienceLevel;

        int xp = (int) (Quietus.CONFIG.percentage *
                ((lvl <= 16)
                ? lvl * lvl + 6 * lvl
                : (lvl <= 32)
                ? (2.5 * lvl * lvl - 40.5 * lvl + 360)
                : (4.5 * lvl * lvl - 162.5 * lvl + 2220)));

        cir.setReturnValue(xp);
    }
}
