package baubles.common;

import baubles.api.BaubleType;
import baubles.api.inv.SlotTypeDefinition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import net.minecraft.client.Minecraft;
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


    // Configuration Options
    public static boolean renderBaubles = true;
    public static boolean expandedMode = false;

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
        expandedMode = config.getBoolean("baubleExpanded.enabled", Configuration.CATEGORY_GENERAL, expandedMode, "Set this to true to have more slots than normal.");
        renderBaubles = config.getBoolean("baubleRender.enabled", Configuration.CATEGORY_CLIENT, renderBaubles, "Set this to false to disable rendering of baubles in the player.");
        if (config.hasChanged()) config.save();
    }

    public static void save() {
        config.save();
    }

    public static SlotTypeDefinition[] getSlots() {
        File file = new File(Minecraft.getMinecraft().gameDir, "config/" + Baubles.MODID + "/slots.json");
        JsonArray slots;

        String fOut = readFile(file);
        if ((fOut.equals(NORMAL) && expandedMode) || (fOut.equals(EXPANDED) && !expandedMode)) {
            writeSlotsJson(file);
            fOut = readFile(file);
        }

        try {
            slots = GSON.fromJson(fOut, JsonArray.class);
        } catch (Exception e) {
            Baubles.log.error("Exception while reading slots.json");
            throw new RuntimeException(e);
        }

        SlotTypeDefinition[] definitions = new SlotTypeDefinition[slots.size()];
        for (int i = 0; i < slots.size(); i++) {
            String slot = slots.get(i).getAsString();
            definitions[i] = new SlotTypeDefinition(i, BaubleType.getType(slot));
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

    private static void writeSlotsJson(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(getJson());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private static String getJson() {
        return expandedMode ? EXPANDED : NORMAL;
    }

    private static String readFile(File file) {
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
