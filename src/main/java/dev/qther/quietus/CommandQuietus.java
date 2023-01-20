package dev.qther.quietus;

import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static net.minecraft.server.command.CommandManager.*;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CommandQuietus {
    private static MutableText msg(String s) {
        return Text.empty().append(f("Quietus ⟫ ", Formatting.GOLD, Formatting.BOLD)).append(r(s));
    }

    private static MutableText t(String s) {
        return Text.literal(s);
    }

    private static MutableText f(String s, Formatting... f) {
        return Text.literal(s).formatted(f);
    }

    private static MutableText r(String s) {
        return f(s, Formatting.RESET);
    }

    private static MutableText b(String s) {
        return f(s, Formatting.BOLD);
    }

    private static MutableText help() {
        return msg("Help")
                .append(r("\n- ").append(f("toggle", Formatting.GOLD)).append(r(": "))).append(f("\n    Toggles the mod on or off.", Formatting.GRAY))
                .append(r("\n- ").append(f("percent", Formatting.GOLD)).append(r(": "))).append(f("\n    Changes the percentage of XP dropped.", Formatting.GRAY))
                .append(r("\n- ").append(f("settings", Formatting.GOLD)).append(r(": "))).append(f("\n    Shows the mod's current settings.", Formatting.GRAY))
                .append(r("\n- ").append(f("reload", Formatting.GOLD)).append(r(": "))).append(f("\n    Reloads the config file.", Formatting.GRAY));
    }

    private static MutableText settings() {
        return r("\n- ").append(f("Enabled", Formatting.GOLD)).append(r(": ")).append(f(String.valueOf(Quietus.CONFIG.enabled), Formatting.GRAY))
                .append("\n- ").append(f("Percent", Formatting.GOLD)).append(r(": ")).append(f(String.format("%.2f", Quietus.CONFIG.percentage * 100) + "%", Formatting.GRAY));
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("quietus")
                .executes(context -> {
                    context.getSource().sendMessage(help());

                    return 1;
                })
                .then(literal("toggle")
                        .executes(context -> {
                            Quietus.CONFIG.enabled = !Quietus.CONFIG.enabled;
                            Quietus.CONFIG.save();
                            context.getSource().sendMessage(msg("Quietus is now " + (Quietus.CONFIG.enabled ? "enabled" : "disabled") + "."));

                            return 1;
                        }))
                .then(literal("percent")
                        .executes(context -> {
                            context.getSource().sendMessage(msg("Percentage is currently " + Quietus.CONFIG.percentage + "."));
                            return 1;
                        })
                        .then(argument("percent", floatArg(0, 100))
                                .executes(context -> {
                                    Quietus.CONFIG.percentage = Math.min(1f, context.getArgument("percent", Float.class) / 100f);
                                    Quietus.CONFIG.save();
                                    context.getSource().sendMessage(msg("Quietus percentage set to " + String.format("%.2f", Quietus.CONFIG.percentage * 100) + "%."));

                                    return 1;
                                })))
                .then(literal("settings")
                        .executes(context -> {
                            context.getSource().sendMessage(msg("Current settings:").append(settings()));

                            return 1;
                        }))
                .then(literal("reload")
                        .executes(context -> {
                            Quietus.CONFIG.load();
                            context.getSource().sendMessage(msg("Config reloaded. Settings are now:").append(settings()));

                            return 1;
                        }))
        ));
    }
}
