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

                // Verifica que el jugador tenga un contrato de aldeano
                if (held.isEmpty() || held.getItem() != RegistryVC.villagerContract) {
                    return;
                }

                // Verifica que el nombre sea un contrato valido
                String lowerName = message.contractName.toLowerCase(Locale.ROOT);
                VillagerInfo info = EventHandler.contractMap.get(lowerName);
                if (info == null) {
                    return;
                }

                // Aplica el nombre al objeto que tienes en la mano// Aplica el nombre al objeto que tienes en la mano
                held.setStackDisplayName(message.contractName);
            });

            return null;
        }
    }
}
