package cn.tesseract.worleycaves;


import cn.tesseract.worleycaves.world.WorleyCaveGenerator;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "worleycaves", name = "Worley Caves", version = Tags.VERSION, acceptableRemoteVersions = "*", dependencies = "required-after:mycelium@[2.4.3,)")
public class Main {
    public static final Logger LOGGER = LogManager.getLogger("worleycaves");
    public static float noiseCutoffValue = -0.14f;
    public static float warpAmplifier = 8.0f;
    public static float verticalCompressionMultiplier = 2.0f;
    public static float horizonalCompressionMultiplier = 1.0f;
    public static int[] blackListedDims = {-1};
    public static int maxCaveHeight = 128;
    public static int minCaveHeight = 1;
    public static String lavaBlock = "minecraft:lava";
    public static int lavaDepth = 6;
    public static boolean allowReplaceMoreBlocks = true;

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.TERRAIN_GEN_BUS.register(this);
        Configuration cfg = new Configuration(e.getSuggestedConfigurationFile());
        noiseCutoffValue = cfg.getFloat("noiseCutoffValue", "cave", noiseCutoffValue, -1f, 1f, "Controls size of caves. Smaller values = larger caves. Between -1.0 and 1.0");
        warpAmplifier = cfg.getFloat("warpAmplifier", "cave", warpAmplifier, 0f, Float.MAX_VALUE, "Controls how much to warp caves. Lower values = straighter caves");
        verticalCompressionMultiplier = cfg.getFloat("verticalCompressionMultiplier", "cave", verticalCompressionMultiplier, 0, Float.MAX_VALUE, "Squishes caves on the Y axis. Lower values = taller caves and more steep drops");
        horizonalCompressionMultiplier = cfg.getFloat("horizonalCompressionMultiplier", "cave", horizonalCompressionMultiplier, 0, Float.MAX_VALUE, "Streches (when < 1.0) or compresses (when > 1.0) cave generation along X and Z axis");
        blackListedDims = cfg.get("cave", "blackListedDims", blackListedDims, "Dimension IDs that will use Vanilla cave generation rather than Worley's Caves").getIntList();
        maxCaveHeight = cfg.getInt("maxCaveHeight", "cave", maxCaveHeight, 1, 256, "Caves will not attempt to generate above this y level. Range 1-256");
        minCaveHeight = cfg.getInt("minCaveHeight", "cave", minCaveHeight, 1, 256, "Caves will not attempt to generate below this y level. Range 1-256");
        lavaBlock = cfg.getString("lavaBlock", "cave", lavaBlock, "Block to use when generating large lava lakes below lavaDepth (usually y=10)");
        lavaDepth = cfg.getInt("lavaDepth", "cave", lavaDepth, 1, 256, "Air blocks at or below this y level will generate as lavaBlock");
        allowReplaceMoreBlocks = cfg.getBoolean("allowReplaceMoreBlocks", "cave", allowReplaceMoreBlocks, "Allow replacing more blocks with caves (useful for mods which completely overwrite world gen)");
        if (cfg.hasChanged()) cfg.save();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onCaveEvent(InitMapGenEvent event) {
        //only replace cave gen if the original gen passed isn't a worley cave
        if (event.type == InitMapGenEvent.EventType.CAVE && !event.originalGen.getClass().equals(WorleyCaveGenerator.class)) {
            //Main.LOGGER.info("Replacing cave generation with Worley Caves");
            event.newGen = new WorleyCaveGenerator();
        }
    }
}
