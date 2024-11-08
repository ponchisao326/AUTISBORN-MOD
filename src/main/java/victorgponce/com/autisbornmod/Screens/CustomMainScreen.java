package victorgponce.com.autisbornmod.Screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomMainScreen extends Screen {

    private Minecraft minecraftInstance = Minecraft.getInstance();
    private static final int TITLE_Y_POS = 30;
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("victorgponce:autisbornmod/textures/background.png");
    private static final ResourceLocation TITLE_TEXTURE = new ResourceLocation("victorgponce:autisbornmod/textures/title.png");
    private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation("victorgponce:autisbornmod/textures/buttoncobble.png");
    private String status = "El servidor se encuentra OFFLINE";
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private static final String SERVER_ADDRESS = "node-marb.ponchisaohosting.xyz";
    private static final int SERVER_PORT = 25566;
    private static final Component COPYRIGHT_TEXT = Component.literal("Menu creado por PonchisaoHosting");
    private static boolean sounding = false;
    private static boolean first = true;
    private static boolean hoverServer = false;

    public CustomMainScreen() {
        super(Component.literal("Custom Main Menu"));
        // Iniciamos el SubProceso que hace ping al servidor
        startPingThread();
    }

    @Override
    protected void init() {

        playBackgroundMusic();


        // Seteamos variables de los botones
        this.minecraftInstance = Minecraft.getInstance();
        int buttonWidth = 200;
        int buttonHeight = 20;
        int l = this.height / 4 + 48;
        int copyrightWidth = this.font.width(COPYRIGHT_TEXT);
        int j = this.width - copyrightWidth - 2;

        // this.addRenderableWidget(new Button(this.width / 2 - 100, l + 24, buttonWidth, buttonHeight,
        //         Component.literal("AUTISBORN"), button -> this.minecraftInstance.execute(() -> {
        //             // Conexión directa al servidor al pulsar el botón
        //             ServerData serverData = new ServerData("AUTISBORN", SERVER_ADDRESS + ":" + SERVER_PORT, false);
        //             join(serverData);
        //             Minecraft.getInstance().options.setSoundCategoryVolume(SoundSource.MUSIC, 0.0F);
        //         })){
        //     @Override
        //     public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        //         // Dibujar la textura personalizada del botón
        //         AUTISBORN_MOD.LOGGER.info("seteando textura de boton");
        //         Minecraft.getInstance().getTextureManager().bindForSetup(BUTTON_TEXTURE);  // Asegúrate de que la textura esté cargada
        //         RenderSystem.enableBlend();  // Habilitar el modo de mezcla para que los colores se vean bien
        //         // Dibuja la textura completa para el botón en las coordenadas correspondientes
        //         blit(matrices, this.x, this.y, 0, 0, this.width, this.height);  // 'this.x' y 'this.y' son las coordenadas del botón
        //         RenderSystem.disableBlend();  // Deshabilitar el modo de mezcla
//
        //         // Dibujar el texto del botón sobre la textura
        //         drawCenteredString(matrices, Minecraft.getInstance().font, this.getMessage(), this.x + this.width / 2, this.y + this.height / 2 - 4, 0xFFFFFF);
        //     }
        // });

        this.addRenderableWidget(new ImageButton(this.width / 2 - 100, l + 24, buttonWidth, buttonHeight, 0, 0, 200, BUTTON_TEXTURE, 200, 20, (p_96791_) -> {
            // Conexión directa al servidor al pulsar el botón
            ServerData serverData = new ServerData("AUTISBORN", SERVER_ADDRESS + ":" + SERVER_PORT, false);
            join(serverData);
            Minecraft.getInstance().options.setSoundCategoryVolume(SoundSource.MUSIC, 0.0F);
        },Component.literal("AUTISBORN")){
            @Override
            public void renderButton(PoseStack p_94282_, int p_94283_, int p_94284_, float p_94285_) {
                drawCenteredString(p_94282_, Minecraft.getInstance().font, this.getMessage(), this.x + this.width / 2, this.y + this.height / 2 - 4, 0xFFFFFF);
                // Establecer el color según si está en hover o no
                if (this.isHoveredOrFocused()) {
                    // Si el botón está en hover, dibujamos un contorno blanco
                    RenderSystem.setShaderColor(0.7f, 0.7f, 0.7f, 1.0f);
                } else {
                    // Si no está en hover, restauramos el color original (puedes cambiarlo si lo deseas)
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f); // Blanco (o cualquier color)
                }

                super.renderButton(p_94282_, p_94283_, p_94284_, p_94285_);
            }
        });


        this.addRenderableWidget(new Button(this.width / 2 - 100, l + 68, 98, 20,
                Component.translatable("menu.options"), (p_96788_) -> {
            this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
        }));

        this.addRenderableWidget(new Button(this.width / 2 + 2, l + 68, 98, 20,
                Component.translatable("menu.quit"), button -> executorService.submit(this.minecraftInstance::stop)));

        this.addRenderableWidget(new Button(this.width / 2 - 100, l + 23 * 2, buttonWidth, buttonHeight,
                Component.translatable("fml.menu.mods"), button -> {
            this.minecraft.setScreen(new net.minecraftforge.client.gui.ModListScreen(this));
        }));

        // Botón de Copyright del Menu
        this.addRenderableWidget(new PlainTextButton(j - 3, this.height - 20, copyrightWidth, 10, COPYRIGHT_TEXT, (button) -> executorService.submit(() -> {
            try {
                Util.getPlatform().openUrl(new URL("https://victorgponce.com"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }), this.font));
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft.getInstance().getMusicManager().stopPlaying();

        // Habilitamos las texturas y el sistema de render
        RenderSystem.enableTexture();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Seteamos el fondo
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        blit(poseStack, 0, 0, 0, 0, this.width, this.height, this.width, this.height);

        // Seteamos el título
        RenderSystem.setShaderTexture(0, TITLE_TEXTURE);
        int titleWidth = (int) Math.round(928 / 3.5);
        int titleHeight = (int) Math.round(253 / 3.5);
        int titleX = (this.width - titleWidth) / 2;

        blit(poseStack, titleX, TITLE_Y_POS, 0, 0, titleWidth, titleHeight, titleWidth, titleHeight);

        // Seteamos el Status con el subproceso de Ping iniciado anteriormente
        drawString(poseStack, this.font, status, 10, this.height - 20, 0xFFFFFF);

        // Renderiza los botones
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }


    private void startPingThread() {
        executorService.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                status = pingServer() ? "El servidor se encuentra ONLINE" : "El servidor se encuentra OFFLINE";
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    // Pingeamos al server
    private boolean pingServer() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(CustomMainScreen.SERVER_ADDRESS, CustomMainScreen.SERVER_PORT), 1000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void playBackgroundMusic() {
        if (first) {
            Minecraft.getInstance().options.setSoundCategoryVolume(SoundSource.MUSIC, 0.40F);
            Minecraft.getInstance().getMusicManager().stopPlaying();
            first = false;
        }

        ResourceLocation location = new ResourceLocation("victorgponce", "main_bg"); // Asegúrate de que esta ruta sea correcta
        SoundEvent event = new SoundEvent(location);
        SimpleSoundInstance sound = SimpleSoundInstance.forMusic(event);

        if (!sounding) {
            this.minecraftInstance.getSoundManager().play(sound);
            sounding = true;
        }
        if (!Minecraft.getInstance().getSoundManager().isActive(sound)) {
            sounding = false;
        }

    }


    private void join(ServerData p_99703_) {
        assert this.minecraft != null;
        ConnectScreen.startConnecting(this, this.minecraft, ServerAddress.parseString(p_99703_.ip), p_99703_);
    }

    // Al cerrar la pantalla detenemos el subproceso pues no se usa y cerramos la pantalla
    @Override
    public void onClose() {
        return;
        // this.minecraftInstance.getSoundManager().stop();
        // executorService.shutdownNow();
        // super.onClose();
    }

}
