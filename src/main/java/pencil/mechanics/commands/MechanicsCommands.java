package pencil.mechanics.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;
import pencil.mechanics.ConfigValues;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MechanicsCommands {

    public static void main() {
        // Commands for config of variables
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("movement")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    context.getSource().sendFeedback(() -> Text.literal("Current Movement Config"), false);
                    context.getSource().sendFeedback(() -> Text.literal("ChargeJump X Multiplier: "+ ConfigValues.crawlJumpXMultiplier+" (Default 1.3)"), false);
                    context.getSource().sendFeedback(() -> Text.literal("ChargeJump Y Multiplier: "+ConfigValues.crawlJumpYMultiplier+" (Default 0.6)"), false);
                    context.getSource().sendFeedback(() -> Text.literal("PoleJump X Multiplier: "+ConfigValues.poleJumpXMultiplier+" (Default 0.5)"), false);
                    context.getSource().sendFeedback(() -> Text.literal("PoleJump Y Multiplier: "+ConfigValues.poleJumpYMultiplier+" (Default 0.6)"), false);
                    return 1;
                })
                .then(literal("chargeJumpX")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            context.getSource().sendFeedback(() -> Text.literal("the Charged Jump X Multiplier is currently set to "+ConfigValues.crawlJumpXMultiplier), false);
                            return 1;
                        }).then(literal("set").then(argument("value", FloatArgumentType.floatArg())
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> {
                                    final float newValue = FloatArgumentType.getFloat(context, "value");
                                    if (newValue != 0) {
                                        context.getSource().sendFeedback(() -> Text.literal("Set the Charged jump X multiplier to "+newValue), false);
                                        ConfigValues.crawlJumpXMultiplier = newValue;
                                    }
                                    return 1;
                                })
                        ))
                ).then(literal("chargeJumpY")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            context.getSource().sendFeedback(() -> Text.literal("the Charged Jump Y Multiplier is currently set to "+ConfigValues.crawlJumpYMultiplier), false);
                            return 1;
                        }).then(literal("set").then(argument("value", FloatArgumentType.floatArg())
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> {
                                    final float newValue = FloatArgumentType.getFloat(context, "value");
                                    if (newValue != 0) {
                                        context.getSource().sendFeedback(() -> Text.literal("Set the Charged jump Y multiplier to "+newValue), false);
                                        ConfigValues.crawlJumpYMultiplier = newValue;
                                    }
                                    return 1;
                                })
                        ))
                ).then(literal("poleJumpX")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            context.getSource().sendFeedback(() -> Text.literal("the Pole jump X Multiplier is currently set to "+ConfigValues.poleJumpXMultiplier), false);
                            return 1;
                        }).then(literal("set").then(argument("value", FloatArgumentType.floatArg())
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> {
                                    final float newValue = FloatArgumentType.getFloat(context, "value");
                                    if (newValue != 0) {
                                        context.getSource().sendFeedback(() -> Text.literal("Set the Pole jump X multiplier to "+newValue), false);
                                        ConfigValues.poleJumpXMultiplier = newValue;
                                    }
                                    return 1;
                                })
                        ))
                ).then(literal("poleJumpY")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            context.getSource().sendFeedback(() -> Text.literal("the Pole Jump Y Multiplier is currently set to "+ConfigValues.poleJumpYMultiplier), false);
                            return 1;
                        }).then(literal("set").then(argument("value", FloatArgumentType.floatArg())
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> {
                                    final float newValue = FloatArgumentType.getFloat(context, "value");
                                    if (newValue != 0) {
                                        context.getSource().sendFeedback(() -> Text.literal("Set the Pole jump Y multiplier to "+newValue), false);
                                        ConfigValues.poleJumpYMultiplier = newValue;
                                    }
                                    return 1;
                                })
                        ))
                ))
        );
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("rain")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    context.getSource().sendFeedback(() -> Text.literal("Current Rain Settings"), false);
                    context.getSource().sendFeedback(() -> Text.literal("Rain Active: "+ConfigValues.rainEnabled), false);
                    context.getSource().sendFeedback(() -> Text.literal("Lethality Timer (in ticks): "+ConfigValues.rainTimer+" (Default 500)"), false);
                    return 1;
                })
                .then(literal("setStatus").then(argument("value", BoolArgumentType.bool())
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            final boolean newValue = BoolArgumentType.getBool(context, "value");
                            context.getSource().sendFeedback(() -> Text.literal("rain is now "+newValue), false);
                            ConfigValues.rainEnabled = newValue;
                            return 1;
                        })
                ).then(literal("setTimer").then(argument("value", FloatArgumentType.floatArg())
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            final float newValue = FloatArgumentType.getFloat(context, "value");
                            context.getSource().sendFeedback(() -> Text.literal("rain timer is now "+newValue+" ticks"), false);
                            ConfigValues.rainTimer = newValue;
                            return 1;
                        }))))));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("items")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    context.getSource().sendFeedback(() -> Text.literal("Current Items Settings"), false);
                    context.getSource().sendFeedback(() -> Text.literal("Spear Base Speed: "+ConfigValues.spearSpeed), false);
                    return 1;
                })
                .then(literal("spear").then(argument("value", BoolArgumentType.bool())
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            context.getSource().sendFeedback(() -> Text.literal("Spear Base Speed: "+ConfigValues.spearSpeed), false);
                            return 1;
                        })
                        .then(literal("setSpeed").then(argument("value", FloatArgumentType.floatArg())
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> {
                                    final float newValue = FloatArgumentType.getFloat(context, "value");
                                    context.getSource().sendFeedback(() -> Text.literal("Spear Base Speed is now: "+newValue), false);
                                    return 1;
                                }
                                ))))
                        )));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("items")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    context.getSource().sendFeedback(() -> Text.literal("Current Items Settings"), false);
                    context.getSource().sendFeedback(() -> Text.literal("Spear Base Speed: "+ConfigValues.spearSpeed), false);
                    return 1;
                })
                .then(literal("spear").then(argument("value", BoolArgumentType.bool())
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            context.getSource().sendFeedback(() -> Text.literal("Spear Base Speed: "+ConfigValues.spearSpeed), false);
                            return 1;
                        })
                        .then(literal("setSpeed").then(argument("value", FloatArgumentType.floatArg())
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> {
                                            final float newValue = FloatArgumentType.getFloat(context, "value");
                                            context.getSource().sendFeedback(() -> Text.literal("Spear Base Speed is now: "+newValue), false);
                                            return 1;
                                        }
                                ))))
                )));
    }

}
