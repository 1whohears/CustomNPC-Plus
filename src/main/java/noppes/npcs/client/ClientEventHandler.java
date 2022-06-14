package noppes.npcs.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import noppes.npcs.client.gui.customoverlay.OverlayCustom;
import noppes.npcs.client.renderer.RenderCNPCPlayer;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientEventHandler {
    private final RenderCNPCPlayer renderCNPCPlayer = new RenderCNPCPlayer();
    public static HashMap<Integer,Long> disabledButtonTimes = new HashMap<>();

    @SubscribeEvent
    public void onMouse(MouseEvent event) {
        if (Minecraft.getMinecraft().currentScreen != null)
            return;

        ArrayList<Integer> removeList = new ArrayList<>();
        for (Map.Entry<Integer,Long> entry : disabledButtonTimes.entrySet()) {
            if (entry.getValue() > 0) {
                if (entry.getKey() == event.button || entry.getKey() == -1) {
                    event.setCanceled(true);
                }
                disabledButtonTimes.put(entry.getKey(), entry.getValue() - 1);
            } else {
                removeList.add(entry.getKey());
            }
        }

        for (int i : removeList) {
            disabledButtonTimes.remove(i);
        }
    }

    @SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent.Post event){
        if(event.type == RenderGameOverlayEvent.ElementType.ALL) {
            for (OverlayCustom overlayCustom : Client.customOverlays.values()) {
                overlayCustom.renderGameOverlay(event.partialTicks);
            }

            if (Client.questTrackingOverlay != null) {
                Client.questTrackingOverlay.renderGameOverlay(event.partialTicks);
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Post event) {
        if (!(event.renderer instanceof RenderCNPCPlayer)) {
            renderCNPCPlayer.tempRenderPartialTicks = event.partialRenderTick;
            renderCNPCPlayer.doRender(event.entityPlayer, 0, 0, 0, 0.0F, event.partialRenderTick);
        }
    }

    @SubscribeEvent
    public void cancelSpecials(RenderPlayerEvent.Specials.Pre event) {
        if (event.renderer instanceof RenderCNPCPlayer) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null && mc.theWorld != null && !mc.isGamePaused() && event.phase == TickEvent.Phase.END) {
            renderCNPCPlayer.itemRenderer.updateEquippedItem();
            renderCNPCPlayer.updateFovModifierHand();
        }
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if (Client.skinOverlays.containsKey(Minecraft.getMinecraft().thePlayer.getUniqueID()) && Client.skinOverlays.get(Minecraft.getMinecraft().thePlayer.getUniqueID()).values().size() > 0) {
            GL11.glPushMatrix();
                event.setCanceled(true);
                GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
                renderCNPCPlayer.renderHand(event.partialTicks, event.renderPass);
            GL11.glPopMatrix();
        } else {
            event.setCanceled(false);
        }
    }
}
