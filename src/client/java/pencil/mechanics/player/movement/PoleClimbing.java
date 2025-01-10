package pencil.mechanics.player.movement;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import pencil.mechanics.ConfigValues;
import pencil.mechanics.RainworldMechanicsClient;
import pencil.mechanics.init.BlockInit;

import java.util.Arrays;

public class PoleClimbing {
    public static boolean climbing = false;

    public static double climbRotation = 0;
    public static Vec3d climbOffsetPos = new Vec3d(0, 0, 0);
    public static double climbOffset = 0.19;
    public static double axis = 1;

    public static float poleJumpX = ConfigValues.poleJumpXMultiplier;
    public static float poleJumpY = ConfigValues.poleJumpYMultiplier;

    private static boolean upperGrabbed = false;
    private static boolean verticalSet = false;
    private static boolean touchingPole = false;

    public static boolean set = false;
    public static boolean setPole = false;
    public static boolean jumped = false;

    public static Block[] nonColidables = { Blocks.AIR };

    public static BlockPos pole;

    public static void main(MinecraftClient client, KeyBinding grabKey) {
        HitResult hit1 = client.cameraEntity.raycast(3, 0, false);

        if (poleJumpX != ConfigValues.poleJumpXMultiplier || poleJumpY != ConfigValues.poleJumpYMultiplier) {
            poleJumpX = ConfigValues.poleJumpXMultiplier;
            poleJumpY = ConfigValues.poleJumpYMultiplier;
        }

        if (!setPole) {
            if (client.player.getWorld().getBlockState(client.player.getBlockPos()).getBlock() == BlockInit.POLE_X) {
                axis = 1;
                verticalSet = false;
                touchingPole = true;
                pole = client.player.getBlockPos();
                climbRotation = 90;
                setPole = true;
            } else if (client.player.getWorld().getBlockState(client.player.getBlockPos()).getBlock() == BlockInit.POLE_Z) {
                axis = 2;
                verticalSet = false;
                touchingPole = true;
                pole = client.player.getBlockPos();
                climbRotation = 90;
                setPole = true;
            } else if (client.player.getWorld().getBlockState(client.player.getBlockPos()).getBlock() == BlockInit.POLE_Y) {
                verticalSet = true;
                touchingPole = true;
                pole = client.player.getBlockPos();
                climbRotation = 0;
                setPole = true;
            } else if (client.player.getWorld().getBlockState(client.player.getBlockPos()).getBlock() == BlockInit.POLE_JOINT) {
                verticalSet = true;
                touchingPole = true;
                pole = client.player.getBlockPos();
                climbRotation = 0;
                setPole = true;
            }

            if (client.player.getWorld().getBlockState(client.player.getBlockPos().add(0, 1, 0)).getBlock() == BlockInit.POLE_X) {
                axis = 1;
                verticalSet = false;
                touchingPole = true;
                pole = client.player.getBlockPos().add(0, 1, 0);
                climbRotation = 90;
                setPole = true;
            } else if (client.player.getWorld().getBlockState(client.player.getBlockPos().add(0, 1, 0)).getBlock() == BlockInit.POLE_Y) {
                verticalSet = true;
                touchingPole = true;
                pole = client.player.getBlockPos().add(0, 1, 0);
                climbRotation = 0;
                setPole = true;
                upperGrabbed = true;
            } else if (client.player.getWorld().getBlockState(client.player.getBlockPos().add(0, 1, 0)).getBlock() == BlockInit.POLE_Z) {
                axis = 2;
                verticalSet = false;
                touchingPole = true;
                pole = client.player.getBlockPos().add(0, 1, 0);
                climbRotation = 90;
                setPole = true;
            } else if (client.player.getWorld().getBlockState(client.player.getBlockPos().add(0, 1, 0)).getBlock() == BlockInit.POLE_JOINT) {
                verticalSet = true;
                touchingPole = true;
                pole = client.player.getBlockPos().add(0, 1, 0);
                climbRotation = 0;
                setPole = true;
            }
        }

        if (setPole) {
            if (client.player.getWorld().getBlockState(pole).getBlock() == BlockInit.POLE_X) {
                if (setPole && client.player.getPos().getX() >= pole.toCenterPos().getX() + 0.5 ||
                        setPole && client.player.getPos().getX() <= pole.toCenterPos().getX() - 0.5) {
                    if (climbRotation == 0) {
                        pole = client.player.getBlockPos();
                        verticalSet = false;
                    } else if (climbRotation == 180) {
                        pole = new BlockPos(client.player.getBlockPos().getX(), pole.getY(), client.player.getBlockPos().getZ());
                        verticalSet = false;
                    }
                }
            } else if (client.player.getWorld().getBlockState(pole).getBlock() == BlockInit.POLE_Y) {
                if (setPole && !upperGrabbed && client.player.getPos().getY() >= pole.toCenterPos().getY() + 0.5 || setPole && !upperGrabbed && client.player.getPos().getY() <= pole.toCenterPos().getY() - 0.5) {
                    pole = new BlockPos(pole.getX(), client.player.getBlockPos().getY(), pole.getZ());
                    verticalSet = true;
                } else if (setPole && upperGrabbed && client.player.getPos().getY() >= pole.toCenterPos().getY() + 0.5) {
                    pole = new BlockPos(pole.getX(), client.player.getBlockPos().getY()+1, pole.getZ());
                    verticalSet = true;
                }
            } else if (client.player.getWorld().getBlockState(pole).getBlock() == BlockInit.POLE_Z) {
                if (setPole && client.player.getPos().getZ() >= pole.toCenterPos().getZ() + 0.5 ||
                        setPole && client.player.getPos().getZ() <= pole.toCenterPos().getZ() - 0.5) {
                    if (climbRotation == 0) {
                        pole = client.player.getBlockPos();
                        verticalSet = false;
                    } else if (climbRotation == 180) {
                        pole = new BlockPos(client.player.getBlockPos().getX(), pole.getY(), client.player.getBlockPos().getZ());
                        verticalSet = false;
                    }
                }
            } else if (client.player.getWorld().getBlockState(pole).getBlock() == BlockInit.POLE_JOINT) {
                verticalSet = true;
                if (client.player.getPos().getY() >= pole.toCenterPos().getY() + 0.5 || setPole && client.player.getPos().getY() <= pole.toCenterPos().getY() - 0.5) {
                    pole = new BlockPos(pole.getX(), client.player.getBlockPos().getY(), pole.getZ());
                    verticalSet = true;
                } else if (setPole && client.player.getPos().getX() >= pole.toCenterPos().getX() + 0.5 || setPole && client.player.getPos().getX() <= pole.toCenterPos().getX() - 0.5) {
                    pole = new BlockPos(client.player.getBlockPos().getX(), pole.getY(), pole.getZ());
                    verticalSet = false;
                    axis = 1;
                } else if (client.player.getPos().getZ() >= pole.toCenterPos().getZ() + 0.5 || setPole && client.player.getPos().getZ() <= pole.toCenterPos().getZ() - 0.5) {
                    pole = new BlockPos(pole.getX(), pole.getY(), client.player.getBlockPos().getZ());
                    verticalSet = false;
                    axis = 2;
                }
            }

            if (client.player.getWorld().getBlockState(client.player.getBlockPos()).getBlock() != BlockInit.POLE_X && client.player.getWorld().getBlockState(client.player.getBlockPos()).getBlock() != BlockInit.POLE_Y && client.player.getWorld().getBlockState(client.player.getBlockPos()).getBlock() != BlockInit.POLE_Z && client.player.getWorld().getBlockState(client.player.getBlockPos()).getBlock() != BlockInit.POLE_JOINT && client.player.getWorld().getBlockState(client.player.getBlockPos().add(0, 1, 0)).getBlock() != BlockInit.POLE_X && client.player.getWorld().getBlockState(client.player.getBlockPos().add(0, 1, 0)).getBlock() != BlockInit.POLE_Y && client.player.getWorld().getBlockState(client.player.getBlockPos().add(0, 1, 0)).getBlock() != BlockInit.POLE_Z && client.player.getWorld().getBlockState(client.player.getBlockPos().add(0, 1, 0)).getBlock() != BlockInit.POLE_JOINT) {
                touchingPole = false;
                climbing = false;
                set = false;
                verticalSet = false;
                setPole = false;
                pole = null;
            }
        }

        if (grabKey.isPressed() && !climbing && setPole && !jumped && touchingPole && pole != null && verticalSet) {
            jumped = true;
            climbing = true;
            client.player.setPos(pole.getX(), pole.getY(), pole.getZ());
        } else if (grabKey.isPressed() && !climbing && setPole && !jumped && touchingPole && pole != null) {
            jumped = true;
            climbing = true;
            client.player.setPos(pole.getX(), pole.getY(), pole.getZ());
        } else if (grabKey.isPressed() && !jumped && climbing) {
            jumped = true;
            climbing = false;
            set = false;
            setPole = false;
            touchingPole = false;
        } else if (!grabKey.isPressed() && jumped) {
            jumped = false;
        }

        if (climbing && client.player != null && touchingPole) {
            RainworldMechanicsClient.climbing = climbing;
            if (pole != null) {
                if (verticalSet) {
                    if (!set && pole != null) {
                        climbRotation = 0;
                        set = true;
                    }
                    if (client.options.leftKey.wasPressed() && climbing && touchingPole) {
                        if (climbRotation < 270) {
                            climbRotation += 90;
                        } else if (climbRotation >= 270) {
                            climbRotation = 0;
                        }
                    }
                    if (client.options.rightKey.wasPressed() && climbing && touchingPole) {
                        if (climbRotation > 0) {
                            climbRotation -= 90;
                        } else if (climbRotation <= 0) {
                            climbRotation = 270;
                        }
                    }
                    if (climbRotation == 0 && pole != null) {
                            climbOffsetPos = pole.toCenterPos().add(0, 0.25, climbOffset);
                    } else if (climbRotation == 180 && pole != null) {
                            climbOffsetPos = pole.toCenterPos().add(0, 0.25, climbOffset * -1);
                    } else if (climbRotation == 270 && pole != null) {
                            climbOffsetPos = pole.toCenterPos().add(climbOffset, 0.25, 0);
                    } else if (climbRotation == 90 && pole != null) {
                            climbOffsetPos = pole.toCenterPos().add(-1 * climbOffset, 0.25, 0);
                    }
                    if (client.options.sprintKey.isPressed() && client.player.getPos().getY() <= pole.toCenterPos().getY() + 0.5 && client.world.getBlockState(client.player.getBlockPos().add(0,1,0)).getBlock() == BlockInit.POLE_Y ||
                            client.options.sprintKey.isPressed() && client.player.getPos().getY() <= pole.toCenterPos().getY() + 0.5 && client.world.getBlockState(client.player.getBlockPos().add(0,1,0)).getBlock() == BlockInit.POLE_JOINT) {
                        client.player.setVelocity(0, 0.15, 0);
                        client.player.setPos(climbOffsetPos.getX(), client.player.getY(), climbOffsetPos.getZ());
                    } else if (client.options.sprintKey.isPressed()) {
                        client.player.setVelocity(0, 0, 0);
                        client.player.setPos(climbOffsetPos.getX(), pole.getY()-0.1, climbOffsetPos.getZ());
                    } else if (client.options.sneakKey.isPressed()) {
                        client.player.setVelocity(0, -0.15, 0);
                        client.player.setPos(climbOffsetPos.getX(), client.player.getY(), climbOffsetPos.getZ());
                    } else if (client.world.getBlockState(pole).getBlock() == BlockInit.POLE_JOINT) {
                            if (client.options.forwardKey.isPressed() && client.player.getHorizontalFacing() == Direction.NORTH && client.world.getBlockState(pole.add(new Vec3i(0, 0, -1))).getBlock() == BlockInit.POLE_Z) {
                                client.player.setVelocity(0, 0, -0.15);
                                client.player.setPos(climbOffsetPos.getX(), climbOffsetPos.getY(), client.player.getZ());
                            } else if (client.options.forwardKey.isPressed() && client.player.getHorizontalFacing() == Direction.SOUTH && client.world.getBlockState(pole.add(new Vec3i(0, 0, 1))).getBlock() == BlockInit.POLE_Z) {
                                client.player.setVelocity(0, 0, 0.15);
                                client.player.setPos(climbOffsetPos.getX(), climbOffsetPos.getY(), client.player.getZ());
                            } else if (client.options.forwardKey.isPressed() && client.player.getHorizontalFacing() == Direction.EAST && client.world.getBlockState(pole.add(new Vec3i(1, 0, 0))).getBlock() == BlockInit.POLE_X) {
                                client.player.setVelocity(0.15, 0, 0);
                                client.player.setPos(client.player.getPos().getX(), climbOffsetPos.getY(), climbOffsetPos.getZ());
                            } else if (client.options.forwardKey.isPressed() && client.player.getHorizontalFacing() == Direction.WEST && client.world.getBlockState(pole.add(new Vec3i(-1, 0, 0))).getBlock() == BlockInit.POLE_X) {
                                client.player.setVelocity(-0.15, 0, 0);
                                client.player.setPos(client.player.getPos().getX(), climbOffsetPos.getY(), climbOffsetPos.getZ());
                            } else {
                                client.player.setVelocity(0, 0, 0);
                                client.player.setPos(client.player.getX(), climbOffsetPos.getY(), client.player.getZ());
                            }
                    } else {
                        client.player.setVelocity(0, 0, 0);
                        client.player.setPos(climbOffsetPos.getX(), client.player.getY(), climbOffsetPos.getZ());
                    }
                    if (client.player.getPos().getY() >= pole.getY() && client.player.getWorld().getBlockState(client.player.getBlockPos()).getBlock() == BlockInit.POLE_Y) {
                        pole = client.player.getBlockPos().add(0, 1, 0);
                    }
                    if (client.world.getBlockState(pole.add(0, 1, 0)).getBlock() != BlockInit.POLE_Y && client.world.getBlockState(pole.add(0, 1, 0)).getBlock() != BlockInit.POLE_JOINT && client.player.getBlockPos().getY() > pole.getY()) {
                        touchingPole = false;
                        climbing = false;
                        set = false;
                        verticalSet = false;
                        setPole = false;
                    }
                    if (client.options.jumpKey.isPressed() && touchingPole) {
                        verticalSet = false;
                        setPole = false;
                        RainworldMechanicsClient.climbJumping = true;
                        client.player.setVelocity(client.player.getRotationVector().getX() * poleJumpX, poleJumpY, client.player.getRotationVector().getZ() * poleJumpX);
                        touchingPole = false;
                        climbing = false;
                        set = false;
                    }
                } else if (!verticalSet) {
                    if (client.options.sprintKey.wasPressed() && climbing && touchingPole) {
                        climbRotation = 0;
                        climbOffsetPos = pole.toCenterPos().add(0, 0.1, 0);
                    }
                    if (client.options.sneakKey.wasPressed() && climbing && touchingPole) {
                        climbRotation = 180;
                        climbOffsetPos = pole.toCenterPos().subtract(0, 1.3, 0);
                    }
                    if (!set) {
                        if (client.player.getWorld().getBlockState(pole.add(new Vec3i(0, 1, 0))).getBlock() == Blocks.AIR && client.player.getWorld().getBlockState(pole.add(new Vec3i(0, 2, 0))).getBlock() == Blocks.AIR) {
                            climbRotation = 0;
                            climbOffsetPos = pole.toCenterPos().add(0, 0.1, 0);
                            set = true;
                        } else {
                            touchingPole = false;
                            climbing = false;
                            set = false;
                            verticalSet = false;
                            setPole = false;
                            pole = null;
                        }
                    }
                    if (axis == 1) {
                        if (client.options.forwardKey.isPressed() && client.player.getHorizontalFacing() == Direction.EAST) {
                            client.player.setVelocity(0.15, 0, 0);
                            client.player.setPos(client.player.getPos().getX(), climbOffsetPos.getY(), climbOffsetPos.getZ());
                        } else if (client.options.forwardKey.isPressed() && client.player.getHorizontalFacing() == Direction.WEST) {
                            client.player.setVelocity(-0.15, 0, 0);
                            client.player.setPos(client.player.getPos().getX(), climbOffsetPos.getY(), climbOffsetPos.getZ());
                        } else {
                            client.player.setVelocity(0, 0, 0);
                            client.player.setPos(client.player.getPos().getX(), climbOffsetPos.getY(), climbOffsetPos.getZ());
                        }
                    }
                    if (axis == 2) {
                        if (client.options.forwardKey.isPressed() && client.player.getHorizontalFacing() == Direction.NORTH) {
                            client.player.setVelocity(0, 0, -0.15);
                            client.player.setPos(climbOffsetPos.getX(), climbOffsetPos.getY(), client.player.getZ());
                        } else if (client.options.forwardKey.isPressed() && client.player.getHorizontalFacing() == Direction.SOUTH) {
                            client.player.setVelocity(0, 0, 0.15);
                            client.player.setPos(climbOffsetPos.getX(), climbOffsetPos.getY(), client.player.getZ());
                        } else {
                            client.player.setVelocity(0, 0, 0);
                            client.player.setPos(climbOffsetPos.getX(), climbOffsetPos.getY(), client.player.getZ());
                        }
                    }
                    if (client.player.getPos().getX() > pole.toCenterPos().getX() + 0.6 && axis == 1
                            || client.player.getPos().getZ() > pole.toCenterPos().getZ() + 0.6 && axis == 2) {
                        touchingPole = false;
                        climbing = false;
                        set = false;
                        setPole = false;
                    } else if (client.player.getPos().getX() < pole.toCenterPos().getX() - 0.6 && axis == 1
                            || client.player.getPos().getZ() < pole.toCenterPos().getZ() - 0.6 && axis == 2) {
                        touchingPole = false;
                        climbing = false;
                        set = false;
                        setPole = false;
                    }
                    if (client.options.jumpKey.isPressed() && touchingPole) {
                        RainworldMechanicsClient.climbJumping = true;
                        climbing = false;
                        pole = null;
                        client.player.setVelocity(client.player.getRotationVector().getX() * poleJumpX, poleJumpY, client.player.getRotationVector().getZ() * poleJumpX);
                        touchingPole = false;
                        set = false;
                        setPole = false;
                    }
                }
            }
        } else {
            set = false;
            setPole = false;
            RainworldMechanicsClient.climbing = false;
        }
    }
}