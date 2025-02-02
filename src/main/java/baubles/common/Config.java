package baubles.common;

import baubles.api.inv.SlotDefinition;
import baubles.common.init.SlotDefinitions;
import baubles.common.integration.ModCompatibility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.*;

public class Config {

    public static Configuration config;
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private static final String
      NORMAL = "[\n \"amulet\",\n \"ring\",\n \"ring\",\n \"belt\",\n \"head\",\n \"body\",\n \"charm\"\n]",
      EXPANDED = "[\n \"amulet\",\n \"amulet\",\n \"ring\",\n \"ring\",\n \"ring\",\n \"ring\",\n \"belt\",\n \"belt\",\n \"head\",\n \"head\",\n \"body\",\n \"body\",\n \"charm\",\n \"charm\"\n]";

    public static File JSON_DIR;

    // Configuration Options
    public static boolean renderBaubles = true;
    public static boolean expandedMode = false;
    public static int slotMaxNum = 32;

    public static void initialize(File configFile) {
        initConfig(configFile);
        JSON_DIR = new File(configFile.getParentFile(), "baubles/slots.json");
        initJsonConfig();
    }

    private static void initJsonConfig() {
        JSON_DIR.getParentFile().mkdirs();
        if (!JSON_DIR.exists()) {
            writeSlotsJson(JSON_DIR);
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
        slotMaxNum = config.getInt("slotMaxNum", Configuration.CATEGORY_GENERAL, slotMaxNum, 0, 99999, "Used to set the maximum number of slots");
        expandedMode = config.getBoolean("baubleExpanded.enabled", Configuration.CATEGORY_GENERAL, expandedMode, "Set this to true to have more slots than normal.");
        renderBaubles = config.getBoolean("baubleRender.enabled", Configuration.CATEGORY_CLIENT, renderBaubles, "Set this to false to disable rendering of baubles in the player.");
        if (config.hasChanged()) config.save();
    }

    public static void save() {
        config.save();
    }

    public static SlotDefinition[] getJsonSlots() {
        JsonArray slots;

        String fOut = readFile(JSON_DIR);
        if ((fOut.equals(NORMAL) && expandedMode) || (fOut.equals(EXPANDED) && !expandedMode)) {
            writeSlotsJson(JSON_DIR);
            fOut = readFile(JSON_DIR);
        }

        try {
            slots = GSON.fromJson(fOut, JsonArray.class);
        } catch (Exception e) {
            Baubles.log.error("Exception while reading slots.json");
            throw new RuntimeException(e);
        }

        SlotDefinition[] definitions = new SlotDefinition[slots.size()];
        for (int i = 0; i < slots.size(); i++) {
            String slot = slots.get(i).getAsString();
            ResourceLocation location;
            if (!slot.contains(":")) location = new ResourceLocation(Baubles.MODID, slot);
            else location = new ResourceLocation(slot);
            SlotDefinition definition = SlotDefinitions.get(location);
            if (definition == null) {
                Baubles.log.error("Could not find slot definition from {}", location);
                continue;
            }
            definitions[i] = definition;
        }

        return definitions;
    }

    public static SlotDefinition[] getSlots() {
        JsonArray slots;

        String fOut = readFile(JSON_DIR);
        if ((fOut.equals(NORMAL) && expandedMode) || (fOut.equals(EXPANDED) && !expandedMode)) {
            writeSlotsJson(JSON_DIR);
            fOut = readFile(JSON_DIR);
        }

        try {
            slots = GSON.fromJson(fOut, JsonArray.class);
        } catch (Exception e) {
            Baubles.log.error("Exception while reading slots.json");
            throw new RuntimeException(e);
        }

        SlotDefinition[] definitions = new SlotDefinition[Config.slotMaxNum];
        for (int i = 0; i < Config.slotMaxNum; i++) {
            if (i >= slots.size()) {
                definitions[i] = null;
            } else {
                String slot = slots.get(i).getAsString();
                ResourceLocation location;
                if (!slot.contains(":")) location = new ResourceLocation(Baubles.MODID, slot);
                else location = new ResourceLocation(slot);
                SlotDefinition definition = SlotDefinitions.get(location);
                if (definition == null) {
                    Baubles.log.error("Could not find slot definition from {}", location);
                    continue;
                }
                definitions[i] = definition;
            }
        }

        return definitions;
    }

    public static class ConfigChangeListener {
        @SuppressWarnings("unused")
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
            String modId = eventArgs.getModID();
            if (modId.equals(Baubles.MODID)) loadConfigs();
        }

        @SubscribeEvent
        public static void postConfigChange(ConfigChangedEvent.PostConfigChangedEvent event) {
            String modId = event.getModID();
            if (modId.equals(ModCompatibility.ME) && ModCompatibility.ME$checkMiniEffectIsLegacy()) ModCompatibility.ME$applyOffset();
        }
    }

    public static void writeSlotsJson(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(getJson());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private static String getJson() {
        return expandedMode ? EXPANDED : NORMAL;
    }

    public static String readFile(File file) {
        StringBuilder builder = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int c;
            while((c = reader.read()) != -1) {
                builder.append((char) c);
            }
            return builder.toString();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
