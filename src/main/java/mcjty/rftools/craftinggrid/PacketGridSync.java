package mcjty.rftools.craftinggrid;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.rftools.blocks.crafter.CraftingRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PacketGridSync {

    private BlockPos pos;
    private List<ItemStack[]> recipes;

    public void convertFromBytes(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
        int s = buf.readInt();
        recipes = new ArrayList<>(s);
        for (int i = 0 ; i < s ; i++) {
            int ss = buf.readInt();
            ItemStack[] stacks = new ItemStack[ss];
            for (int j = 0 ; j < ss ; j++) {
                if (buf.readBoolean()) {
                    stacks[j] = NetworkTools.readItemStack(buf);
                } else {
                    stacks[j] = null;
                }
            }
            recipes.add(stacks);
        }
    }

    public void convertToBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);
        buf.writeInt(recipes.size());
        for (ItemStack[] recipe : recipes) {
            buf.writeInt(recipe.length);
            for (ItemStack stack : recipe) {
                if (stack != null) {
                    buf.writeBoolean(true);
                    NetworkTools.writeItemStack(buf, stack);
                } else {
                    buf.writeBoolean(false);
                }
            }
        }
    }

    protected void init(BlockPos pos, CraftingGrid grid) {
        this.pos = pos;
        recipes = new ArrayList<>();
        for (int i = 0 ; i < 6 ; i++) {
            CraftingRecipe recipe = grid.getRecipe(i);
            InventoryCrafting inventory = recipe.getInventory();
            ItemStack[] stacks = new ItemStack[10];
            stacks[0] = recipe.getResult();
            for (int j = 0 ; j < 9 ; j++) {
                stacks[j+1] = inventory.getStackInSlot(j);
            }
            recipes.add(stacks);
        }
    }

    protected CraftingGridProvider handleMessage(World world) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof CraftingGridProvider) {
            for (int i = 0 ; i < recipes.size() ; i++) {
                ((CraftingGridProvider) te).setRecipe(i, recipes.get(i));
            }
            return (CraftingGridProvider) te;
        }
        return null;
    }
}
