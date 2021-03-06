package xyz.brassgoggledcoders.opentransport.interfaces;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import xyz.brassgoggledcoders.opentransport.api.blockwrappers.IBlockWrapper;
import xyz.brassgoggledcoders.opentransport.api.blockwrappers.IGuiInterface;
import xyz.brassgoggledcoders.opentransport.api.entities.IHolderEntity;

public class BaseInterface implements IGuiInterface {
    @Override
    public Gui getGUI(EntityPlayer entityPlayer, IHolderEntity holderEntity, IBlockWrapper blockWrapper) {
        return null;
    }

    @Override
    public Container getContainer(EntityPlayer entityPlayer, IHolderEntity holderEntity,
                                  IBlockWrapper blockWrapper) {
        return null;
    }
}
