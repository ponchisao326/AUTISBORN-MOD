package victorgponce.com.autisbornmod.Screens;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.minecraft.network.Connection.connectToServer;

public class CustomMainScreen extends Screen {

    private Minecraft minecraftInstance = Minecraft.getInstance();
    private static final int TITLE_Y_POS = 30; // Y position of the title on screen
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("victorgponce:autisbornmod/textures/background.png");
    private static final ResourceLocation TITLE_TEXTURE = new ResourceLocation("victorgponce:autisbornmod/textures/title.png");
    private String status = "El servidor se encuentra OFFLINE"; // Server status
    private final ExecutorService executorService = Executors.newFixedThreadPool(3); // Thread pool for multiple threads
    private static final String SERVER_ADDRESS = "node-marb.ponchisaohosting.xyz";
    private static final int SERVER_PORT = 25566;
    private static final Component COPYRIGHT_TEXT = Component.literal("Menu creado por PonchisaoHosting");

    public CustomMainScreen() {
        super(Component.literal("Custom Main Menu"));
        startPingThread();
    }

    @Override
    protected void init() {
        this.minecraftInstance = Minecraft.getInstance();
        int buttonWidth = 200;
        int buttonHeight = 20;
        int l = this.height / 4 + 48;
        int copyrightWidth = this.font.width(COPYRIGHT_TEXT);
        int j = this.width - copyrightWidth - 2;

        // Button "AUTISBORN" - Direct connection to the server
        this.addRenderableWidget(new Button(this.width / 2 - 100, l + 24, buttonWidth, buttonHeight,
                Component.literal("AUTISBORN"), button -> {
            this.minecraftInstance.execute(() -> {
                // Crea un objeto ServerData con la dirección y el puerto del servidor
                ServerData serverData = new ServerData("AUTISBORN", SERVER_ADDRESS + ":" + SERVER_PORT, false);
                // Llama al método join para conectarse al servidor
                join(serverData);
            });
        }));


        // Button "Opciones" - Opens options menu
        this.addRenderableWidget(new Button(this.width / 2 - 100, l + 68, 98, 20,
                Component.translatable("menu.options"), button -> executorService.submit(() ->
                this.minecraftInstance.setScreen(new OptionsScreen(this, this.minecraftInstance.options)))));

        // Button "Quit Game" - Exit the game
        this.addRenderableWidget(new Button(this.width / 2 + 2, l + 68, 98, 20,
                Component.translatable("menu.quit"), button -> executorService.submit(this.minecraftInstance::stop)));

        // Button "MODS" - Opens the Forge mods menu
        this.addRenderableWidget(new Button(this.width / 2 - 100, l + 23 * 2, buttonWidth, buttonHeight,
                Component.translatable("fml.menu.mods"), button -> executorService.submit(() -> {
            assert this.minecraftInstance != null;
            this.minecraftInstance.setScreen(new net.minecraftforge.client.gui.ModListScreen(this));
        })));

        // Button for opening URL
        this.addRenderableWidget(new PlainTextButton(j - 3, this.height - 20, copyrightWidth, 10, COPYRIGHT_TEXT, (button) -> {
            executorService.submit(() -> {
                try {
                    Util.getPlatform().openUrl(new URL("https://victorgponce.com"));
                } catch (IOException e) {
                    e.printStackTrace(); // Handle the exception, e.g., log it
                }
            });
        }, this.font));
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.enableTexture();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Draw background
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        blit(poseStack, 0, 0, 0, 0, this.width, this.height, this.width, this.height);

        // Draw custom title
        RenderSystem.setShaderTexture(0, TITLE_TEXTURE);
        int titleWidth = (int) Math.round(928 / 3.5);
        int titleHeight = (int) Math.round(253 / 3.5);
        int titleX = (this.width - titleWidth) / 2; // Center title

        // Draw title without repeating the texture
        blit(poseStack, titleX, TITLE_Y_POS, 0, 0, titleWidth, titleHeight, titleWidth, titleHeight);

        // Render server status
        drawString(poseStack, this.font, status, 10, this.height - 20, 0xFFFFFF);

        // Render buttons and other elements
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    private void startPingThread() {
        executorService.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                status = pingServer(SERVER_ADDRESS, SERVER_PORT) ? "El servidor se encuentra ONLINE" : "El servidor se encuentra OFFLINE";
                try {
                    Thread.sleep(4000); // Check every 4 seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupt status
                    break; // Exit loop if interrupted
                }
            }
        });
    }

    private boolean pingServer(String address, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(address, port), 1000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void join(ServerData p_99703_) {
        ConnectScreen.startConnecting(this, this.minecraft, ServerAddress.parseString(p_99703_.ip), p_99703_);
    }


    // public void connectToServer(String ip, int port) {
    //     ServerData serverData = new ServerData("Servidor", ip + ":" + port, false);
    //     assert Minecraft.getInstance().screen != null;
    //     ConnectScreen.startConnecting(
    //             Minecraft.getInstance().screen,
    //             Minecraft.getInstance(),
    //             ServerAddress.parseString(serverData.ip),
    //             serverData
    //     );
    // }

    @Override
    public void onClose() {
        executorService.shutdownNow(); // Stop the ping thread when closing the screen
        super.onClose();
    }
}
