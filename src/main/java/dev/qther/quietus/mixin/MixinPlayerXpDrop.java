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
    double xpFromLevel(int level) {
        return (level <= 16)
                ? level * level + 6 * level
                : (level <= 32)
                ? (2.5 * level * level - 40.5 * level + 360)
                : (4.5 * level * level - 162.5 * level + 2220);
    }

    @Inject(at = @At("HEAD"), method = "getXpToDrop()I", cancellable = true)
    public void getXpToDrop(CallbackInfoReturnable<Integer> cir) {
        if (!Quietus.CONFIG.enabled) {
            return;
        }

        PlayerEntity player = (PlayerEntity) (Object) this;

        if (player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || player.isSpectator()) {
            cir.setReturnValue(0);
        }

        int xp = (int) (Quietus.CONFIG.percentage * (xpFromLevel(player.experienceLevel) + player.experienceProgress * player.getNextLevelExperience()));
        Quietus.LOGGER.info("Dropping " + xp + " XP.");

        if (Quietus.CONFIG.maxLevels != -1) {
            int max = (int) xpFromLevel(Quietus.CONFIG.maxLevels);
            xp = Math.min(xp, max);
        }
        Quietus.LOGGER.info("Final " + xp + " XP.");
        cir.setReturnValue(xp);
    }
}
