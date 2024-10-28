package victorgponce.com.autisbornmod.Screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CustomMainScreen extends Screen {

    private final Minecraft minecraftInstance = Minecraft.getInstance();
    private static final int TITLE_Y_POS = 30; // Posición Y del título en pantalla
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("victorgponce:autisbornmod/textures/background.png");
    private static final ResourceLocation TITLE_TEXTURE = new ResourceLocation("victorgponce:autisbornmod/textures/title.png");


    public CustomMainScreen() {
        super(Component.literal("Custom Main Menu"));
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int buttonHeight = 20;
        int l = this.height / 4 + 48;
        // int p_96764_ = l;
        // int p_96765_ = 24;

        // Botón "AUTISBORN" - Conexión directa al servidor
        this.addRenderableWidget(new Button(this.width / 2 - 100, l + 24, buttonWidth, buttonHeight,
                Component.literal("AUTISBORN"), button ->
                connectToServer("node-marb.ponchisaohosting.xyz", 25566)));

        // Botón "Opciones" - Abre el menú de opciones
        this.addRenderableWidget(new Button(this.width / 2 - 100, l + 68, 98, 20,
                Component.translatable("menu.options"), button -> this.minecraftInstance.setScreen(new OptionsScreen(this, this.minecraftInstance.options))));

        // Botón "Quit Game" - Salir del juego
        this.addRenderableWidget(new Button(this.width / 2 + 2, l + 68, 98, 20,
                Component.translatable("menu.quit"), button -> this.minecraftInstance.stop()));

        // Botón "MODS" - Abre el menú de Mods de forge
        this.addRenderableWidget(new Button(this.width / 2 - 100, l + 23 * 2, buttonWidth, buttonHeight, Component.translatable("fml.menu.mods"), button -> {
            this.minecraft.setScreen(new net.minecraftforge.client.gui.ModListScreen(this));
        }));
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.enableTexture();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Dibuja el fondo
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        int width = this.width;
        int height = this.height;
        blit(poseStack, 0, 0, 0, 0, width, height, width, height);

        // Dibuja el título personalizado
        RenderSystem.setShaderTexture(0, TITLE_TEXTURE);
        int titleWidth = (int) Math.round(886 / 3.5);
        int titleHeight = (int) Math.round(253 / 3.5);
        int x = (this.width - titleWidth) / 2; // Centra el título en el ancho

        // Dibuja el título sin repetir la textura ni estirarla
        blit(poseStack, x, TITLE_Y_POS, 0, 0, titleWidth, titleHeight, titleWidth, titleHeight);

        // Renderiza los botones y demás elementos
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    public void connectToServer(String ip, int port) {
        ServerData serverData = new ServerData("Servidor", ip + ":" + port, false);
        assert Minecraft.getInstance().screen != null;
        ConnectScreen.startConnecting(
                Minecraft.getInstance().screen,
                Minecraft.getInstance(),
                ServerAddress.parseString(serverData.ip),
                serverData
        );
    }
}

