package net.permutated.exmachinis.events;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.compat.exnihilo.ExNihiloAPI;
import net.permutated.exmachinis.machines.base.AbstractMachineBlock;
import net.permutated.exmachinis.util.Constants;

import java.util.OptionalDouble;

@EventBusSubscriber(modid = ExMachinis.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientEventHandler {

    private ClientEventHandler() {
        // nothing to do
    }

    @SubscribeEvent
    public static void onClientLoggedOutEvent(final ClientPlayerNetworkEvent.LoggingOut event) {
        ExMachinis.LOGGER.debug("Clearing recipe cache after logging out");
        ModRegistry.COMPACTING_REGISTRY.clearRecipes();
    }
    @SubscribeEvent
    public static void onRenderHighlightEvent(final RenderHighlightEvent.Block event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ItemStack inHand = player.getMainHandItem();
        if (inHand.isEmpty() || !ExNihiloAPI.isHammerItem(inHand)) {
            return;
        }

        BlockPos pos = event.getTarget().getBlockPos();
        BlockState state = player.getCommandSenderWorld().getBlockState(pos);

        if (!state.isAir() && state.getBlock() instanceof AbstractMachineBlock) {
            Direction output = state.getValue(AbstractMachineBlock.OUTPUT);
            Vec3 offset = Vec3.atLowerCornerOf(pos).subtract(event.getCamera().getPosition());
            PoseStack poseStack = event.getPoseStack();

            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

            VertexConsumer consumer = bufferSource.getBuffer(OutputRenderType.OVERLAY_LINES);
            poseStack.pushPose();

            poseStack.translate(offset.x, offset.y, offset.z);
            poseStack.translate(.5, .5, .5);
            poseStack.mulPose(Constants.ROTATIONS.get(output));
            poseStack.translate(-.5, -.5, -.5);

            drawLine(consumer, poseStack, 0, 0.25f, 0.25f, 0, 0.25f, 0.75f); // z0 to z1
            drawLine(consumer, poseStack, 0, 0.75f, 0.25f, 0, 0.75f, 0.75f); // z0 to z1 at y1
            drawLine(consumer, poseStack, 0, 0.25f, 0.25f, 0, 0.75f, 0.25f); // y0 to y1
            drawLine(consumer, poseStack, 0, 0.25f, 0.75f, 0, 0.75f, 0.75f); // y0 to y1 at z1

            poseStack.popPose();
            bufferSource.endBatch(OutputRenderType.OVERLAY_LINES);
        }
    }

    public static final int BLUE = FastColor.ARGB32.color(255, 30, 136, 229);
    static void drawLine(VertexConsumer consumer, PoseStack stack, float x1, float y1, float z1, float x2, float y2, float z2)
    {
        float nX = x2 - x1;
        float nY = y2 - y1;
        float nZ = z2 - z1;
        float nLen = Mth.sqrt(nX * nX + nY * nY + nZ * nZ);

        nX = nX / nLen;
        nY = nY / nLen;
        nZ = nZ / nLen;

        consumer.addVertex(stack.last().pose(), x1, y1, z1)
            .setColor(BLUE)
            .setNormal(nX, nY, nZ);
        consumer.addVertex(stack.last().pose(), x2, y2, z2)
            .setColor(BLUE)
            .setNormal(nX, nY, nZ);
    }

    static class OutputRenderType extends RenderType {
        public static final RenderType OVERLAY_LINES = create(
            "overlay_lines",
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            VertexFormat.Mode.LINES,
            TRANSIENT_BUFFER_SIZE,
            false,
            false,
            CompositeState.builder()
                .setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
                .setLineState(new LineStateShard(OptionalDouble.of(3.0D)))
                .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                .setWriteMaskState(COLOR_DEPTH_WRITE)
                .setCullState(NO_CULL)
                .createCompositeState(false));

        public OutputRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
            super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
        }
    }
}
