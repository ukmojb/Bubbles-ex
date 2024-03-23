package baubles.common;

import baubles.api.BaubleType;
import baubles.api.inv.SlotTypeDefinition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {

    public static Configuration config;
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    // Configuration Options
    public static boolean renderBaubles = true;

    public static void initialize(File configFile) {
        initConfig(configFile);
        initJsonConfig();
    }

    private static void initJsonConfig() {
        File dir = new File(Minecraft.getMinecraft().gameDir, "config/baubles");
        dir.mkdirs();

        File slotsJson = new File(dir, "slots.json");
        if (!slotsJson.exists()) {
            writeSlotsJson(slotsJson);
        }
    }

    private static void initConfig(File file) {
        config = new Configuration(file);
        config.load();
        loadConfigs();
        MinecraftForge.EVENT_BUS.register(ConfigChangeListener.class);
        config.save();
    }

    public static void loadConfigs() {
        String desc = "Set this to false to disable rendering of baubles in the player.";
        renderBaubles = config.getBoolean("baubleRender.enabled",
                Configuration.CATEGORY_CLIENT, renderBaubles, desc);

        if (config.hasChanged()) config.save();
    }

    public static void save() {
        config.save();
    }

    public static SlotTypeDefinition[] getSlots() {
        File file = new File(Minecraft.getMinecraft().gameDir, "config/" + Baubles.MODID + "/slots.json");
        JsonArray slots;

        try {
            slots = GSON.fromJson(readFile(file), JsonArray.class);
        } catch (Exception e) {
            Baubles.log.error("Exception while reading slots.json! Using default slots");
            file.delete();
            writeSlotsJson(file);
            slots = GSON.fromJson(readFile(file), JsonArray.class);
        }

        SlotTypeDefinition[] definitions = new SlotTypeDefinition[slots.size()];
        for (int i = 0; i < slots.size(); i++) {
            String slot = slots.get(i).getAsString();
            definitions[i] = new SlotTypeDefinition(BaubleType.getType(slot));
        }

        return definitions;
    }

    public static class ConfigChangeListener {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
            if (eventArgs.getModID().equals(Baubles.MODID))
                loadConfigs();
        }
    }

    private static FileReader readFile(File file) {
        try {
            return new FileReader(file);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private static void writeSlotsJson(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("[\n \"amulet\",\n \"ring\",\n \"ring\",\n \"belt\",\n \"head\",\n \"body\",\n \"charm\"\n]");
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
