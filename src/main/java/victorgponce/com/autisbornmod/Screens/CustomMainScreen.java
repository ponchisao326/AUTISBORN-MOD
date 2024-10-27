package victorgponce.com.autisbornmod.Screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CustomMainScreen extends Screen {

    public CustomMainScreen() {
        super(Component.literal("Custom Main Menu"));
    }

    @Override
    protected void init() {
        // Buttons
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack); // Renderiza el fondo
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 15, 0xFFFFFF); // Título
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}

