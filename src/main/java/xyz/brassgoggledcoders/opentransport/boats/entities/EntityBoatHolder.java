package xyz.brassgoggledcoders.opentransport.boats.entities;

import com.google.common.base.Optional;
import com.teamacronymcoders.base.client.gui.IHasGui;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import xyz.brassgoggledcoders.opentransport.api.blockwrappers.IBlockWrapper;
import xyz.brassgoggledcoders.opentransport.api.entities.IHolderEntity;
import xyz.brassgoggledcoders.opentransport.boats.items.ItemBoatHolder;
import xyz.brassgoggledcoders.opentransport.registries.BlockWrapperRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityBoatHolder extends EntityBoatBase implements IHolderEntity<EntityBoatHolder>, IHasGui {
    private static final DataParameter<String> BLOCK_CONTAINER_NAME =
            EntityDataManager.createKey(EntityBoat.class, DataSerializers.STRING);
    private static final DataParameter<Optional<ItemStack>> ITEM_BOAT =
            EntityDataManager.createKey(EntityBoat.class, DataSerializers.OPTIONAL_ITEM_STACK);
    IBlockWrapper blockWrapper;

    public EntityBoatHolder(World world) {
        super(world);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(BLOCK_CONTAINER_NAME, "");
        this.dataManager.register(ITEM_BOAT, Optional.<ItemStack>absent());
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.getBlockWrapper().tick();
    }

    @Override
    @Nonnull
    public Item getItemBoat() {
        Optional<ItemStack> itemStackBoat = this.dataManager.get(ITEM_BOAT);
        if (itemStackBoat.isPresent()) {
            return itemStackBoat.get().getItem();
        }
        return Items.BOAT;
    }

    public void setItemBoat(@Nonnull ItemStack itemBoatStack) {
        if (itemBoatStack.getItem() instanceof ItemBoatHolder) {
            this.dataManager.set(ITEM_BOAT, Optional.of(itemBoatStack));
        }
    }

    @Override
    public EntityBoatHolder getEntity() {
        return this;
    }

    @Override
    public IBlockWrapper getBlockWrapper() {
        if (this.blockWrapper == null) {
            String containerName = this.dataManager.get(BLOCK_CONTAINER_NAME);
            this.blockWrapper = BlockWrapperRegistry.getBlockWrapper(containerName);
            if (this.blockWrapper != null) {
                this.blockWrapper.setHolder(this);
            }
        }
        return this.blockWrapper;
    }

    @Override
    public void setBlockWrapper(IBlockWrapper blockWrapper) {
        this.dataManager.set(BLOCK_CONTAINER_NAME, blockWrapper.getUnlocalizedName());
        this.blockWrapper = blockWrapper;
        this.blockWrapper.setHolder(this);
    }

    @Override
    public Entity getEmptyEntity() {
        EntityBoat entityBoat = new EntityBoat(this.getEntityWorld());
        entityBoat.setBoatType(this.getBoatType());
        entityBoat.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        return entityBoat;
    }

    @Override
    public boolean processInitialInteract(@Nonnull EntityPlayer entityPlayer, @Nullable ItemStack itemStack,
                                          EnumHand hand) {
        return this.getBlockWrapper() != null && this.getBlockWrapper().onInteract(entityPlayer, hand, itemStack);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);

        Optional<ItemStack> itemStackBoat = this.dataManager.get(ITEM_BOAT);
        if (itemStackBoat.isPresent()) {
            NBTTagCompound itemBoat = new NBTTagCompound();
            nbtTagCompound.setTag("ITEM_BOAT", itemStackBoat.get().writeToNBT(itemBoat));
        }
        nbtTagCompound.setString("CONTAINER_NAME", this.dataManager.get(BLOCK_CONTAINER_NAME));
        if (blockWrapper != null) {
            NBTTagCompound containerTag = new NBTTagCompound();
            containerTag = blockWrapper.writeToNBT(containerTag);
            nbtTagCompound.setTag("CONTAINER", containerTag);
        }
        return nbtTagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        this.dataManager.set(BLOCK_CONTAINER_NAME, nbtTagCompound.getString("CONTAINER_NAME"));
        this.setBlockWrapper(BlockWrapperRegistry.getBlockWrapper(nbtTagCompound.getString("CONTAINER_NAME")));
        this.setItemBoat(ItemStack.loadItemStackFromNBT(nbtTagCompound.getCompoundTag("ITEM_BOAT")));
        blockWrapper.setHolder(this);
        blockWrapper.readFromNBT(nbtTagCompound.getCompoundTag("CONTAINER"));
    }

    @Override
    public Gui getClientGuiElement(int ID, EntityPlayer player, World world, BlockPos blockPos) {
        return this.getBlockWrapper().getInterface().getGUI(player, this, this.getBlockWrapper());
    }

    @Override
    public Container getServerGuiElement(int ID, EntityPlayer player, World world, BlockPos blockPos) {
        return this.getBlockWrapper().getInterface().getContainer(player, this, this.getBlockWrapper());
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return this.getBlockWrapper().hasCapability(capability, facing) || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return this.getBlockWrapper().hasCapability(capability, facing) ?
                this.getBlockWrapper().getCapability(capability, facing) : super.getCapability(capability, facing);
    }
}

