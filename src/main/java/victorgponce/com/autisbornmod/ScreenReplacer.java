package victorgponce.com.autisbornmod;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import com.mojang.blaze3d.platform.Window;
import victorgponce.com.autisbornmod.Screens.CustomMainScreen;

@EventBusSubscriber(modid = AUTISBORN_MOD.MODID, bus = Bus.FORGE, value = Dist.CLIENT)
public class ScreenReplacer {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Window mcWindow = mc.getWindow();

        mcWindow.setTitle("AUTISBORN - Powered by PonchisaoHosting");
        mcWindow.changeFullscreenVideoMode();

        if (event.phase == TickEvent.Phase.START) {
            // Verifica si la pantalla actual es la pantalla de título
            if (mc.screen instanceof TitleScreen) {
                // Reemplaza la pantalla con tu pantalla personalizada
                mc.setScreen(new CustomMainScreen());
            }
        }
    }
}
