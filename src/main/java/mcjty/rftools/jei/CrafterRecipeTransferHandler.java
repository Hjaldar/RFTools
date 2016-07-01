package mcjty.rftools.jei;

import mcjty.rftools.blocks.crafter.CrafterBaseTE;
import mcjty.rftools.blocks.crafter.CrafterContainer;
import mcjty.rftools.network.RFToolsMessages;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.gui.ingredients.IGuiIngredient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrafterRecipeTransferHandler implements IRecipeTransferHandler {

    @Override
    public Class<? extends Container> getContainerClass() {
        return CrafterContainer.class;
    }

    @Override
    public String getRecipeCategoryUid() {
        return VanillaRecipeCategoryUid.CRAFTING;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(@Nonnull Container container, @Nonnull IRecipeLayout recipeLayout, @Nonnull EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
        Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = recipeLayout.getItemStacks().getGuiIngredients();

        CrafterContainer containerWorktable = (CrafterContainer) container;
        IInventory inventory = containerWorktable.getCrafterTE();

        List<ItemStack> items = new ArrayList<>(10);
        for (int i = 0 ; i < 10 ; i++) {
            items.add(null);
        }
        for (Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> entry : guiIngredients.entrySet()) {
            int recipeSlot = entry.getKey();
            List<ItemStack> allIngredients = entry.getValue().getAllIngredients();
            if (!allIngredients.isEmpty()) {
                items.set(recipeSlot, allIngredients.get(0));
            }
        }

        if (doTransfer) {
            RFToolsMessages.INSTANCE.sendToServer(new PacketSendRecipe(items, ((CrafterBaseTE) inventory).getPos()));
        }

        return null;
    }
}
