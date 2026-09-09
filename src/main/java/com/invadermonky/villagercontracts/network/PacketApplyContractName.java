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
 * Esto es un paquete enviado desde el cliente al servidor para asignar un nombre de contrato
 * Esto es necesario porque setStackDisplayName() solo funciona del lado del cliente
 * (Esto posiblemente no funciona como yo lo planteo)
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
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                ItemStack held = player.getHeldItemMainhand();

                if (held.isEmpty() || held.getItem() != RegistryVC.villagerContract) {
                    player.sendMessage(
                            new TextComponentString(TextFormatting.RED + "You are not holding a Villager Contract"));
                    return;
                }

                String lowerName = message.contractName.toLowerCase(Locale.ROOT);
                VillagerInfo info = EventHandler.contractMap.get(lowerName);
                if (info == null) {
                    player.sendMessage(new TextComponentString(
                            TextFormatting.RED + "Invalid contract name: " + message.contractName));
                    return;
                }

                held.setStackDisplayName(message.contractName);
            });

            return null;
        }
    }
}