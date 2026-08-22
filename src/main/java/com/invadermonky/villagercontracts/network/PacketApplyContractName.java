package com.invadermonky.villagercontracts.network;

import com.invadermonky.villagercontracts.handlers.ConfigHandler;
import com.invadermonky.villagercontracts.handlers.EventHandler;
import com.invadermonky.villagercontracts.init.RegistryVC;
import com.invadermonky.villagercontracts.util.VillagerInfo;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Locale;

/**
 * Packet sent from the client to the server to apply a contract name to the
 * item in hand
 * This is necessary because setStackDisplayName() only works on the client side
 * (As far as I know)
 */
public class PacketApplyContractName implements IMessage {
    private String contractName;

    public PacketApplyContractName() {
    }

    public PacketApplyContractName(String contractName) {
        this.contractName = contractName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.contractName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.contractName);
    }

    public static class Handler implements IMessageHandler<PacketApplyContractName, IMessage> {
        @Override
        public IMessage onMessage(PacketApplyContractName message, MessageContext ctx) {
            // Execute the logic in the main thread of the server
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                ItemStack held = player.getHeldItemMainhand();

                // Verify that the player is holding a Villager Contract
                if (held.isEmpty() || held.getItem() != RegistryVC.villagerContract) {
                    player.sendMessage(
                            new TextComponentString(TextFormatting.RED + "You are not holding a Villager Contract"));
                    return;
                }

                // Verify that the name is a valid contract
                String lowerName = message.contractName.toLowerCase(Locale.ROOT);
                VillagerInfo info = EventHandler.contractMap.get(lowerName);
                if (info == null) {
                    player.sendMessage(new TextComponentString(
                            TextFormatting.RED + "Invalid contract name: " + message.contractName));
                    return;
                }

                // Apply the name to the item in hand
                held.setStackDisplayName(message.contractName);

                // Confirmation message to the player (TODO: Not sure whether to keep it or
                // remove it)
                player.sendMessage(new TextComponentString(
                        TextFormatting.GREEN + "Contract renamed to: " + TextFormatting.WHITE + message.contractName));

                // If the configuration says that the contract is consumed upon renaming, it
                // consumes it
                // (This is optional and depends on the configuration added by the user)
            });

            return null;
        }
    }
}