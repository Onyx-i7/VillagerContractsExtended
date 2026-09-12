package com.invadermonky.villagercontracts.client.gui;

import com.invadermonky.villagercontracts.VillagerContracts;
import com.invadermonky.villagercontracts.compat.GameStageIntegration;
import com.invadermonky.villagercontracts.handlers.ConfigHandler;
import com.invadermonky.villagercontracts.handlers.ConfigHandler.ContractCostType;
import com.invadermonky.villagercontracts.handlers.EventHandler;
import com.invadermonky.villagercontracts.network.PacketApplyContractName;
import com.invadermonky.villagercontracts.util.VillagerDataHelper;
import com.invadermonky.villagercontracts.util.VillagerInfo;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.*;

/**
 * No hace falta mencionar para que sirve esto ¿verdad?
 */
public class GuiVillagerContracts extends GuiScreen {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(
            VillagerContracts.MOD_ID, "textures/gui/contracts.png");
    private static final int TEXTURE_WIDTH = 280;
    private static final int TEXTURE_HEIGHT = 213;
    private final List<ProfessionEntry> professionList = new ArrayList<>();
    private final int maxVisibleEntries = 8;
    private GuiTextField searchField;
    private final List<CareerEntry> careerList = new ArrayList<>();
    private int selectedProfessionIndex = -1;
    private int selectedCareerIndex = -1;
    private int professionScrollOffset = 0;
    private int careerScrollOffset = 0;
    private long lastCareerClickTime = 0;
    private int lastCareerClickIndex = -1;
    private final int listHeight = 120;
    private final int scrollBarWidth = 6;
    private final int scrollTrackHeight = listHeight;
    private boolean isDraggingProfessionScrollBar = false;
    private boolean isDraggingCareerScrollBar = false;

    public GuiVillagerContracts() {
        ConfigHandler.ConfigChangeListener.checkLanguageChange();
        // rebuildLists(); Esta mierda explota en este lugar al intentarle meterle la compatibilidad con Gamestage
    }

    // Bugeado pero mas o menos funcional
    private void rebuildLists() {
        professionList.clear();
        Map<String, List<VillagerInfo>> grouped = new TreeMap<>();
        EntityPlayer player = this.mc.player;
        boolean isGameStagesEnabled = ConfigHandler.enableGameStages && com.invadermonky.villagercontracts.compat.GameStageIntegration.isAvailable();

        for (VillagerInfo info : EventHandler.contractMap.values()) {
            if (isGameStagesEnabled && player != null) {
                String requiredStage = ConfigHandler.getRequiredStageForProfession(info.profession);

                if (requiredStage != null && !requiredStage.isEmpty()) {
                    if (!GameStageIntegration.hasRequiredStage(player, requiredStage)) {
                        continue;
                    }
                }
            }

            String modId = extractModId(info);
            grouped.computeIfAbsent(modId, k -> new ArrayList<>()).add(info);
        }

        for (Map.Entry<String, List<VillagerInfo>> entry : grouped.entrySet()) {
            entry.getValue().sort(Comparator.comparing(i -> i.identifier));
            professionList.add(new ProfessionEntry(entry.getKey(), entry.getValue()));
        }
    }

    private String extractModId(VillagerInfo info) {
        if (info.profession.getRegistryName() == null)
            return "minecraft";
        String regName = info.profession.getRegistryName().toString();
        return regName.contains(":") ? regName.split(":")[0] : "minecraft";
    }

    @Override
    public void initGui() {
        super.initGui();
        rebuildLists();
        Keyboard.enableRepeatEvents(true);

        int xCenter = this.width / 2;
        int yCenter = this.height / 2;

        searchField = new GuiTextField(0, this.fontRenderer, xCenter - 120, yCenter - 95, 240, 16);
        searchField.setMaxStringLength(50);
        searchField.setEnableBackgroundDrawing(true);
        searchField.setFocused(true);
        searchField.setCanLoseFocus(true);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int xCenter = this.width / 2;
        int yCenter = this.height / 2;

        int guiLeft = xCenter - TEXTURE_WIDTH / 2;
        int guiTop = yCenter - 100;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
        drawModalRectWithCustomSizedTexture(
                guiLeft,
                guiTop,
                0,
                0,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT);

        String title = TextFormatting.GOLD + I18n.format("gui.villagercontracts.title");
        drawCenteredString(this.fontRenderer, title, xCenter, yCenter - 110, 0xFFFFFF);

        drawString(this.fontRenderer, TextFormatting.YELLOW + I18n.format("gui.villagercontracts.professions"),
                xCenter - 135, yCenter - 75, 0xFFFFFF);

        drawString(this.fontRenderer, TextFormatting.YELLOW + I18n.format("gui.villagercontracts.careers"), xCenter + 5,
                yCenter - 75, 0xFFFFFF);

        searchField.drawTextBox();

        drawProfessionList(xCenter - 135, yCenter - 60, mouseX, mouseY);
        drawCareerList(xCenter + 5, yCenter - 60, mouseX, mouseY);
        drawSelectedInfo(xCenter, yCenter + 65);
        drawCostInfo(xCenter, yCenter + 90);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawProfessionList(int x, int y, int mouseX, int mouseY) {
        List<ProfessionEntry> filtered = getFilteredProfessions();
        int visibleCount = Math.min(maxVisibleEntries, filtered.size() - professionScrollOffset);

        for (int i = 0; i < visibleCount; i++) {
            int dataIndex = i + professionScrollOffset;
            if (dataIndex >= filtered.size())
                break;

            ProfessionEntry entry = filtered.get(dataIndex);
            int entryY = y + i * 16;

            if (dataIndex == selectedProfessionIndex) {
                drawRect(x - 2, entryY - 1, x + 130, entryY + 11, 0xFF505050);
            }

            if (mouseX >= x && mouseX <= x + 130 && mouseY >= entryY - 1 && mouseY <= entryY + 11) {
                drawRect(x - 2, entryY - 1, x + 130, entryY + 11, 0x40FFFFFF);
            }

            String display = entry.modId + " (" + entry.careers.size() + ")";
            drawString(this.fontRenderer, display, x, entryY, 0xFFFFFF);
        }

        // Dibuja la barra de scroll de las profesiones
        int scrollTrackX = x + 132;
        drawScrollBar(scrollTrackX, y, filtered.size(), professionScrollOffset, isDraggingProfessionScrollBar);
    }

    private void drawCareerList(int x, int y, int mouseX, int mouseY) {
        if (selectedProfessionIndex < 0 || careerList.isEmpty()) {
            drawString(this.fontRenderer, TextFormatting.GRAY + I18n.format("gui.villagercontracts.select_profession"),
                    x, y, 0x808080);
            return;
        }

        int visibleCount = Math.min(maxVisibleEntries, careerList.size() - careerScrollOffset);
        for (int i = 0; i < visibleCount; i++) {
            int dataIndex = i + careerScrollOffset;
            if (dataIndex >= careerList.size())
                break;

            CareerEntry entry = careerList.get(dataIndex);
            int entryY = y + i * 16;

            if (dataIndex == selectedCareerIndex) {
                drawRect(x - 2, entryY - 1, x + 130, entryY + 11, 0xFF505050);
            }

            if (mouseX >= x && mouseX <= x + 130 && mouseY >= entryY - 1 && mouseY <= entryY + 11) {
                drawRect(x - 2, entryY - 1, x + 130, entryY + 11, 0x40FFFFFF);
            }

            drawString(this.fontRenderer, entry.displayName, x, entryY, 0xFFFFFF);
        }

        // Dibuja la barra de desplazamiento de carreras
        int scrollTrackX = x + 132;
        drawScrollBar(scrollTrackX, y, careerList.size(), careerScrollOffset, isDraggingCareerScrollBar);
    }

    // Dibuja el carril y la barra de desplazamiento (thumb) de una lista
    private void drawScrollBar(int x, int y, int totalItems, int currentOffset, boolean isDragging) {
        // Solo dibuja la barra de desplazamiento si la lista supera la cantidad maxima visible
        if (totalItems > maxVisibleEntries) {
            // Dibuja el fondo del carril (track) en gris oscuro ya que estoy perezoso para hacerle una textura
            drawRect(x, y, x + scrollBarWidth, y + scrollTrackHeight, 0xFF000000);

            // Calcula la altura de la barra respecto a la cantidad de elementos
            int thumbHeight = Math.max(10, (maxVisibleEntries * scrollTrackHeight) / totalItems);
            // Calcula cuantas entradas se pueden scrollear en total
            int maxOffset = totalItems - maxVisibleEntries;
            // Calcula la posicion Y de la barra basada en el offset actual
            int thumbY = y + (currentOffset * (scrollTrackHeight - thumbHeight)) / maxOffset;

            // El color cambia si el jugador esta arrastrando la barra o pasando el mouse por encima
            int thumbColor = isDragging ? 0xFFFFFFFF : 0xFF808080;
            drawRect(x, thumbY, x + scrollBarWidth, thumbY + thumbHeight, thumbColor);
        }
    }

    private void drawSelectedInfo(int xCenter, int y) {
        if (selectedCareerIndex < 0 || selectedCareerIndex >= careerList.size()) {
            drawCenteredString(this.fontRenderer,
                    TextFormatting.GRAY + I18n.format("gui.villagercontracts.select_career"), xCenter, y, 0x808080);
            return;
        }

        CareerEntry entry = careerList.get(selectedCareerIndex);
        drawCenteredString(this.fontRenderer,
                TextFormatting.GREEN + I18n.format("gui.villagercontracts.contract_name") + ":", xCenter, y, 0xFFFFFF);
        drawCenteredString(this.fontRenderer, TextFormatting.WHITE + entry.contractName, xCenter, y + 12, 0xFFFFFF);
    }

    private void drawCostInfo(int xCenter, int y) {
        VillagerRegistry.VillagerProfession selectedProfession = getSelectedProfession();
        ConfigHandler.ProfessionCost cost = ConfigHandler.getCostForProfession(selectedProfession);

        if (cost.type == ContractCostType.NONE || cost.amount <= 0) {
            return;
        }

        boolean canAfford = false;
        String costText = "";

        if (cost.type == ContractCostType.EXPERIENCE) {
            int playerXP = this.mc.player.experienceLevel;
            canAfford = playerXP >= cost.amount;
            String colorCode = canAfford ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString();
            costText = colorCode + I18n.format("gui.villagercontracts.cost_experience", cost.amount, playerXP);
        } else if (cost.type == ContractCostType.ITEM) {
            Item costItem = ConfigHandler.getItemFromId(cost.itemId);
            if (costItem != null) {
                int playerItemCount = countItem(this.mc.player, costItem);
                canAfford = playerItemCount >= cost.amount;
                String colorCode = canAfford ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString();

                String itemName = new ItemStack(costItem).getDisplayName();
                costText = colorCode
                        + I18n.format("gui.villagercontracts.cost_item", cost.amount, itemName, playerItemCount);
            } else {
                costText = TextFormatting.RED + I18n.format("gui.villagercontracts.cost_item_not_found");
            }
        }

        drawCenteredString(this.fontRenderer, costText, xCenter, y, 0xFFFFFF);

        if (!canAfford) {
            drawCenteredString(this.fontRenderer,
                    TextFormatting.RED + I18n.format("gui.villagercontracts.insufficient_funds"), xCenter, y + 12,
                    0xFFFFFF);
        }
    }

    private int countItem(EntityPlayer player, Item item) {
        int count = 0;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private List<ProfessionEntry> getFilteredProfessions() {
        String filter = searchField.getText().trim().toLowerCase(Locale.ROOT);
        if (filter.isEmpty())
            return professionList;

        List<ProfessionEntry> filtered = new ArrayList<>();
        for (ProfessionEntry entry : professionList) {
            if (entry.modId.toLowerCase(Locale.ROOT).contains(filter)) {
                filtered.add(entry);
                continue;
            }

            boolean hasMatchingCareer = false;
            for (VillagerInfo info : entry.careers) {
                if (info.identifier.toLowerCase(Locale.ROOT).contains(filter)) {
                    hasMatchingCareer = true;
                    break;
                }

                String professionName = VillagerDataHelper.getProfessionName(info.profession);
                if (professionName.toLowerCase(Locale.ROOT).contains(filter)) {
                    hasMatchingCareer = true;
                    break;
                }

                String careerName = VillagerDataHelper.getCareerName(info.career);
                if (careerName.toLowerCase(Locale.ROOT).contains(filter)) {
                    hasMatchingCareer = true;
                    break;
                }
            }

            if (hasMatchingCareer) {
                filtered.add(entry);
            }
        }

        return filtered;
    }

    private void updateCareerList() {
        careerList.clear();
        if (selectedProfessionIndex < 0 || selectedProfessionIndex >= professionList.size())
            return;

        ProfessionEntry prof = professionList.get(selectedProfessionIndex);
        String filter = searchField.getText().trim().toLowerCase(Locale.ROOT);

        for (VillagerInfo info : prof.careers) {
            if (!filter.isEmpty()) {
                boolean matches = info.identifier.toLowerCase(Locale.ROOT).contains(filter)
                        || VillagerDataHelper.getProfessionName(info.profession).toLowerCase(Locale.ROOT)
                        .contains(filter)
                        || VillagerDataHelper.getCareerName(info.career).toLowerCase(Locale.ROOT).contains(filter);

                if (!matches)
                    continue;
            }

            careerList.add(new CareerEntry(info.identifier, info.identifier));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        searchField.mouseClicked(mouseX, mouseY, mouseButton);

        int xCenter = this.width / 2;
        int yCenter = this.height / 2;
        int listStartY = yCenter - 60;

        if (mouseButton == 0) {
            List<ProfessionEntry> filtered = getFilteredProfessions();

            // Calcula las posiciones X de las barras de desplazamiento
            int profScrollBarX = xCenter - 135 + 132;
            int careerScrollBarX = xCenter + 5 + 132;

            // Verifica si el clic fue en la barra de profesiones (solo si hay suficientes elementos para mostrarla)
            if (filtered.size() > maxVisibleEntries &&
                    mouseX >= profScrollBarX && mouseX <= profScrollBarX + scrollBarWidth &&
                    mouseY >= listStartY && mouseY <= listStartY + scrollTrackHeight) {
                isDraggingProfessionScrollBar = true;
                updateScrollFromMouse(mouseY, filtered.size(), professionScrollOffset, true);
                return;
            }

            // Verifica si el clic fue en la barra de carreras (solo si hay suficientes elementos para mostrarla)
            if (selectedProfessionIndex >= 0 && careerList.size() > maxVisibleEntries &&
                    mouseX >= careerScrollBarX && mouseX <= careerScrollBarX + scrollBarWidth &&
                    mouseY >= listStartY && mouseY <= listStartY + scrollTrackHeight) {
                isDraggingCareerScrollBar = true;
                updateScrollFromMouse(mouseY, careerList.size(), careerScrollOffset, false);
                return;
            }

            // Logica original de clics en las listas
            if (mouseX >= xCenter - 135 && mouseX <= xCenter - 5 && mouseY >= listStartY && mouseY <= yCenter + 60) {
                int relativeY = mouseY - listStartY;
                int clickedIndex = professionScrollOffset + relativeY / 16;

                if (clickedIndex >= 0 && clickedIndex < filtered.size()) {
                    selectedProfessionIndex = professionList.indexOf(filtered.get(clickedIndex));
                    updateCareerList();
                    selectedCareerIndex = -1;
                    careerScrollOffset = 0;
                    lastCareerClickTime = 0;
                    lastCareerClickIndex = -1;
                }
            }

            if (mouseX >= xCenter + 5 && mouseX <= xCenter + 135 && mouseY >= listStartY && mouseY <= yCenter + 60) {
                int relativeY = mouseY - listStartY;

                int clickedIndex = careerScrollOffset + relativeY / 16;
                if (clickedIndex >= 0 && clickedIndex < careerList.size()) {
                    long currentTime = System.currentTimeMillis();

                    if (lastCareerClickIndex == clickedIndex && currentTime - lastCareerClickTime < 500) {
                        applyContractName(careerList.get(clickedIndex).contractName);
                    } else {
                        selectedCareerIndex = clickedIndex;
                        lastCareerClickTime = currentTime;
                        lastCareerClickIndex = clickedIndex;
                    }
                }
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        // Libera el estado de arrastre al soltar el boton del raton
        if (state == 0) {
            isDraggingProfessionScrollBar = false;
            isDraggingCareerScrollBar = false;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        // Actualiza el scroll mientras el jugador arrastra el mouse sobre la barra de scroll
        if (clickedMouseButton == 0) {
            if (isDraggingProfessionScrollBar) {
                updateScrollFromMouse(mouseY, getFilteredProfessions().size(), professionScrollOffset, true);
            } else if (isDraggingCareerScrollBar) {
                updateScrollFromMouse(mouseY, careerList.size(), careerScrollOffset, false);
            }
        }
    }

    // Calcula y actualiza el offset de scroll basandose en la posicion Y del mouse
    private void updateScrollFromMouse(int mouseY, int totalItems, int currentOffset, boolean isProfessionList) {
        int yCenter = this.height / 2;
        int listStartY = yCenter - 60;
        // Calcula la posicion relativa del mouse dentro del carril
        int relativeY = mouseY - listStartY;
        // Altura maxima utilizable (restando la altura minima del thumb)
        int usableHeight = scrollTrackHeight - Math.max(10, (maxVisibleEntries * scrollTrackHeight) / totalItems);
        // Offset maximo permitido
        int maxOffset = Math.max(0, totalItems - maxVisibleEntries);

        // Calcula el nuevo offset y lo restringe a los limites validos
        int newOffset = (relativeY * maxOffset) / usableHeight;
        newOffset = Math.max(0, Math.min(maxOffset, newOffset));

        if (isProfessionList) {
            professionScrollOffset = newOffset;
        } else {
            careerScrollOffset = newOffset;
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        if (scroll != 0) {
            int xCenter = this.width / 2;
            int yCenter = this.height / 2;
            int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

            List<ProfessionEntry> filtered = getFilteredProfessions();

            if (mouseX >= xCenter - 135 && mouseX <= xCenter - 5 && mouseY >= yCenter - 60 && mouseY <= yCenter + 60) {
                int maxScroll = Math.max(0, filtered.size() - maxVisibleEntries);
                professionScrollOffset = Math.max(0,
                        Math.min(maxScroll, professionScrollOffset + (scroll > 0 ? -1 : 1)));
            } else if (mouseX >= xCenter + 5 && mouseX <= xCenter + 135 && mouseY >= yCenter - 60
                    && mouseY <= yCenter + 60) {
                int maxScroll = Math.max(0, careerList.size() - maxVisibleEntries);
                careerScrollOffset = Math.max(0, Math.min(maxScroll, careerScrollOffset + (scroll > 0 ? -1 : 1)));
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE || (keyCode == Keyboard.KEY_E && !searchField.isFocused())) {
            this.mc.player.closeScreen();
            return;
        }

        if (searchField.textboxKeyTyped(typedChar, keyCode)) {
            professionScrollOffset = 0;
            selectedProfessionIndex = -1;
            selectedCareerIndex = -1;
            careerList.clear();

            String filter = searchField.getText().trim();
            if (!filter.isEmpty()) {
                List<ProfessionEntry> filtered = getFilteredProfessions();
                if (!filtered.isEmpty()) {
                    selectedProfessionIndex = professionList.indexOf(filtered.get(0));
                    updateCareerList();

                    if (!careerList.isEmpty()) {
                        selectedCareerIndex = 0;
                    }
                }
            }
            return;
        }

        if (keyCode == Keyboard.KEY_RETURN && selectedCareerIndex >= 0 && selectedCareerIndex < careerList.size()) {
            applyContractName(careerList.get(selectedCareerIndex).contractName);
        }

        super.keyTyped(typedChar, keyCode);
    }

    private void applyContractName(String name) {
        EntityPlayer player = this.mc.player;
        ItemStack held = player.getHeldItemMainhand();

        if (held.isEmpty() || held.getItem() != com.invadermonky.villagercontracts.init.RegistryVC.villagerContract) {
            player.sendMessage(new net.minecraft.util.text.TextComponentString(
                    TextFormatting.RED + I18n.format("gui.villagercontracts.no_contract")));
            return;
        }

        VillagerContracts.NETWORK.sendToServer(new PacketApplyContractName(name));

        this.mc.player.closeScreen();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private static class ProfessionEntry {
        final String modId;
        final List<VillagerInfo> careers;

        ProfessionEntry(String modId, List<VillagerInfo> careers) {
            this.modId = modId;
            this.careers = careers;
        }
    }

    private static class CareerEntry {
        final String displayName;
        final String contractName;

        CareerEntry(String displayName, String contractName) {
            this.displayName = displayName;
            this.contractName = contractName;
        }
    }

    private VillagerRegistry.VillagerProfession getSelectedProfession() {
        if (selectedProfessionIndex < 0 || selectedProfessionIndex >= professionList.size()) {
            return null;
        }

        ProfessionEntry entry = professionList.get(selectedProfessionIndex);
        if (entry.careers.isEmpty()) {
            return null;
        }

        if (selectedCareerIndex >= 0 && selectedCareerIndex < careerList.size()) {
            String selectedCareerName = careerList.get(selectedCareerIndex).contractName;
            for (VillagerInfo info : entry.careers) {
                if (info.identifier.equals(selectedCareerName)) {
                    return info.profession;
                }
            }
        }

        return entry.careers.get(0).profession;
    }
}
