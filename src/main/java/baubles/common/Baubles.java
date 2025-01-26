package baubles.common;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaubleItem;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.BaublesCapabilities.CapabilityBaubles;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.event.CommandBaubles;
import baubles.common.init.BaubleTypes;
import baubles.common.init.SlotDefinitions;
import baubles.common.integration.ModCompatibility;
import baubles.common.network.PacketHandler;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Objects;

@Mod(modid = Baubles.MODID, name = "Baubles", version = Tags.MOD_VERSION, guiFactory = "baubles.client.gui.BaublesGuiFactory", dependencies = "after:mousetweaks@[3.0,)")
@SuppressWarnings("unused") // mods instance class
public class Baubles {

    public static final String MODID = "baubles";
    public static final Logger log = LogManager.getLogger(MODID.toUpperCase());
    public static final int GUI = 0;
    @SidedProxy(clientSide = "baubles.client.ClientProxy", serverSide = "baubles.common.CommonProxy")
    public static CommonProxy proxy;
    @Instance(value = Baubles.MODID)
    public static Baubles instance;
    public File modDir;

    public static final Item MAX_VERSTAPPEN;

    public static final SoundEvent TU_TU_TU_TU;

    @EventHandler
    public void construction(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        modDir = event.getModConfigurationDirectory();
        Config.initialize(event.getSuggestedConfigurationFile());

        CapabilityManager.INSTANCE.register(IBaublesItemHandler.class, new CapabilityBaubles<>(), BaublesContainer::new);
        CapabilityManager.INSTANCE.register(IBauble.class, new BaublesCapabilities.CapabilityItemBaubleStorage(), () -> new BaubleItem(BaubleType.TRINKET));

        proxy.registerEventHandlers();
        PacketHandler.init();
        ModCompatibility.applyEvents();
        Config.save();

        BaubleTypes.registerDefaults();
        MinecraftForge.EVENT_BUS.register(new BaubleTypes.Register());
        SlotDefinitions.registerDefaults();
        MinecraftForge.EVENT_BUS.register(new SlotDefinitions.Register());
    }

    @EventHandler
    public void init(FMLInitializationEvent evt) {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
        proxy.init();
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandBaubles());
    }

    @SubscribeEvent
    public void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(MAX_VERSTAPPEN);
    }

    @SubscribeEvent
    public void registerSound(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(TU_TU_TU_TU);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(MAX_VERSTAPPEN, 0, new ModelResourceLocation(Objects.requireNonNull(MAX_VERSTAPPEN.getRegistryName()), "inventory"));
    }

    static {
        ResourceLocation MAX_VERSTAPPEN_LOCATION = new ResourceLocation(MODID, "max_verstappen");

        MAX_VERSTAPPEN = new BaubleItem(BaubleType.RING) {

            @Override
            public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
                World world = player.world;
                if (!world.isRemote) {
                    world.playSound(null, player.posX, player.posY, player.posZ, TU_TU_TU_TU, SoundCategory.PLAYERS, 1F, 1F);
                }
            }

        }.setRegistryName(MAX_VERSTAPPEN_LOCATION).setTranslationKey(MODID + ".max_verstappen").setCreativeTab(CreativeTabs.MISC);

        TU_TU_TU_TU = new SoundEvent(MAX_VERSTAPPEN_LOCATION).setRegistryName(MAX_VERSTAPPEN_LOCATION);
    }
}
