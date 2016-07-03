package xyz.brassgoggledcoders.opentransport.modules.boats.navigation;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import xyz.brassgoggledcoders.boilerplate.module.Module;
import xyz.brassgoggledcoders.boilerplate.module.ModuleBase;
import xyz.brassgoggledcoders.opentransport.OpenTransport;
import xyz.brassgoggledcoders.opentransport.api.navigation.navpoint.CapabilityNavPoint;
import xyz.brassgoggledcoders.opentransport.modules.boats.navigation.blocks.BlockBuoy;
import xyz.brassgoggledcoders.opentransport.modules.boats.navigation.blocks.BlockDock;

@Module(mod = OpenTransport.MODID)
public class NavigationModule extends ModuleBase {
	public static BlockBuoy BLOCK_BUOY = new BlockBuoy();
	public static BlockDock BLOCK_DOCK = new BlockDock();

	@Override
	public String getName() {
		return "Navigation";
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		CapabilityNavPoint.register();
		this.getBlockRegistry().registerBlock(BLOCK_DOCK);
		this.getBlockRegistry().registerBlock(BLOCK_BUOY);
	}
}