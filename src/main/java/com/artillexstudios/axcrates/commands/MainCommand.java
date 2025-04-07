package com.artillexstudios.axcrates.commands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.commands.subcommands.SubCommandDrop;
import com.artillexstudios.axcrates.commands.subcommands.SubCommandGive;
import com.artillexstudios.axcrates.commands.subcommands.SubCommandKeys;
import com.artillexstudios.axcrates.commands.subcommands.SubCommandOpen;
import com.artillexstudios.axcrates.commands.subcommands.SubCommandReload;
import com.artillexstudios.axcrates.commands.subcommands.SubCommandShow;
import com.artillexstudios.axcrates.commands.subcommands.SubCommandTake;
import com.artillexstudios.axcrates.commands.subcommands.SubCommandTransfer;
import com.artillexstudios.axcrates.commands.suggestions.LocationXSuggestion;
import com.artillexstudios.axcrates.commands.suggestions.LocationYSuggestion;
import com.artillexstudios.axcrates.commands.suggestions.LocationZSuggestion;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.editor.impl.MainEditor;
import com.artillexstudios.axcrates.keys.Key;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Range;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.annotation.Switch;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.bukkit.parameters.EntitySelector;

import static com.artillexstudios.axcrates.AxCrates.LANG;

@Command({"axcrate", "axcrates", "crate", "crates"})
public class MainCommand {

    @CommandPlaceholder
    @CommandPermission("axcrates.help")
    public void help(@NotNull CommandSender sender) {
        for (String m : LANG.getStringList("help")) {
            sender.sendMessage(StringUtils.formatToString(m));
        }
    }

    @Subcommand("give")
    @CommandPermission("axcrates.give")
    public void give(@NotNull CommandSender sender, EntitySelector<Player> player, Key key, @Optional @Range(min = 1) Integer amount, @Switch(value = "silent", shorthand = 's') boolean silent, @Switch(value = "virtual", shorthand = 'v') boolean virtual) {
        SubCommandGive.INSTANCE.execute(sender, player, key, virtual, silent, amount);
    }

    @Subcommand("take")
    @CommandPermission("axcrates.take")
    public void take(@NotNull CommandSender sender, EntitySelector<Player> player, Key key, @Optional @Range(min = 1) Integer amount, @Switch(value = "physical", shorthand = 'p') boolean physical, @Switch(value = "silent", shorthand = 's') boolean silent) {
        SubCommandTake.INSTANCE.execute(sender, player, key, amount, silent, physical);
    }

    @Subcommand("transfer")
    @CommandPermission("axcrates.transfer")
    public void transfer(@NotNull Player sender, OfflinePlayer player, Key key, @Optional @Range(min = 1) Integer amount) {
        SubCommandTransfer.INSTANCE.execute(sender, player, key, amount);
    }

    @Subcommand("keys")
    @CommandPermission("axcrates.keys")
    public void keys(@NotNull CommandSender sender, @CommandPermission("axcrates.keys.others") @Optional OfflinePlayer player) {
        SubCommandKeys.INSTANCE.execute(sender, player);
    }

    @Subcommand("drop location")
    @CommandPermission("axcrates.drop")
    public void dropAtLocation(@NotNull CommandSender sender, Key key, World world, @SuggestWith(LocationXSuggestion.class) Float x, @SuggestWith(LocationYSuggestion.class) Float y, @SuggestWith(LocationZSuggestion.class) Float z, @Optional @Range(min = 1) Integer amount, @Switch(value = "withVelocity", shorthand = 'w') boolean withVelocity) {
        SubCommandDrop.INSTANCE.execute(sender, key, new Location(world, x, y, z), amount, withVelocity);
    }

    @Subcommand("drop entity")
    @CommandPermission("axcrates.drop")
    public void dropAtEntity(@NotNull CommandSender sender, Key key, EntitySelector<Entity> entity, @Optional @Range(min = 1) Integer amount, @Switch(value = "withVelocity", shorthand = 'w') boolean withVelocity) {
        for (Entity entity1 : entity) {
            SubCommandDrop.INSTANCE.execute(sender, key, entity1.getLocation(), amount, withVelocity);
        }
    }

    @Subcommand("show")
    @CommandPermission("axcrates.show")
    public void show(@NotNull CommandSender sender, Crate crate, @CommandPermission("axcrates.show.others") @Optional Player player) {
        SubCommandShow.INSTANCE.execute(sender, crate, player);
    }

    @Subcommand("open")
    @CommandPermission("axcrates.open")
    public void open(@NotNull CommandSender sender, Crate crate, Player player, @Optional @Range(min = 1) Integer amount, @Switch(value = "force", shorthand = 'f') boolean force, @Switch(value = "silent", shorthand = 's') boolean silent) {
        SubCommandOpen.INSTANCE.execute(sender, crate, player, amount, force, silent);
    }

    @Subcommand("reload")
    @CommandPermission("axcrates.reload")
    public void reload(@NotNull CommandSender sender) {
        SubCommandReload.INSTANCE.execute(sender);
    }

    @Subcommand("editor")
    @CommandPermission("axcrates.editor")
    public void editor(@NotNull Player sender) {
        new MainEditor(sender).open();
    }

//    @Subcommand("convert")
//    @CommandPermission("axcrates.convert")
//    public void convert(@NotNull CommandSender sender, String plugin) {
//    }
}
