package com.artillexstudios.axcrates.commands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.commands.annotations.CrateCompleter;
import com.artillexstudios.axcrates.commands.annotations.CrateKeys;
import com.artillexstudios.axcrates.commands.subcommands.SubCommandDrop;
import com.artillexstudios.axcrates.commands.subcommands.SubCommandGive;
import com.artillexstudios.axcrates.commands.subcommands.SubCommandOpen;
import com.artillexstudios.axcrates.commands.subcommands.SubCommandReload;
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
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Range;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.Switch;
import revxrsal.commands.bukkit.EntitySelector;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import static com.artillexstudios.axcrates.AxCrates.LANG;

@Command({"axcrate", "axcrates", "crate", "crates"})
public class MainCommand {

    @DefaultFor({"~", "~ help"})
    @CommandPermission("axcrates.help")
    public void help(@NotNull CommandSender sender) {
        for (String m : LANG.getStringList("help")) {
            sender.sendMessage(StringUtils.formatToString(m));
        }
    }

    @Subcommand("give")
    @CommandPermission("axcrates.give")
    public void give(@NotNull CommandSender sender, EntitySelector<Player> player, @CrateKeys Key key, @Switch("-virtual") boolean virtual, @Switch("-silent") boolean silent, @Optional @Range(min = 1) Integer amount) {
        new SubCommandGive().execute(sender, player, key, virtual, silent, amount);
    }

    @Subcommand("take")
    @CommandPermission("axcrates.take")
    public void take(@NotNull CommandSender sender, EntitySelector<Player> player, @CrateKeys Key key, @Switch("-physical") boolean physical, @Optional @Range(min = 1) Integer amount) {
    }

    @Subcommand("transfer")
    @CommandPermission("axcrates.transfer")
    public void transfer(@NotNull Player sender, OfflinePlayer player, @CrateKeys Key key, @Optional @Range(min = 1) Integer amount) {
    }

    @Subcommand("keys")
    @CommandPermission("axcrates.keys")
    public void keys(@NotNull Player sender, @CommandPermission("axcrates.keys.others") @Optional Player player) {
    }

    @Subcommand("drop")
    @CommandPermission("axcrates.drop")
    @AutoComplete("* * @x @y @z * *")
    public void drop(@NotNull CommandSender sender, @CrateKeys Key key, World world, float x, float y, float z, @Optional @Range(min = 1) Integer amount, @Switch("-withVelocity") boolean withVelocity) {
        new SubCommandDrop().execute(sender, key, new Location(world, x, y, z), amount, withVelocity);
    }

    @Subcommand("drop2")
    @CommandPermission("axcrates.drop")
    public void drop2(@NotNull CommandSender sender, @CrateKeys Key key, EntitySelector<Entity> entity, @Optional @Range(min = 1) Integer amount, @Switch("-withVelocity") boolean withVelocity) {
        for (Entity entity1 : entity) {
            new SubCommandDrop().execute(sender, key, entity1.getLocation(), amount, withVelocity);
        }
    }

    @Subcommand("show")
    @CommandPermission("axcrates.show")
    public void show(@NotNull CommandSender sender, @CrateCompleter Crate crate, @CommandPermission("axcrates.show.others") @Optional Player player) {
    }

    @Subcommand("open")
    @CommandPermission("axcrates.open")
    public void open(@NotNull CommandSender sender, @CrateCompleter Crate crate, Player player, @Optional @Range(min = 1) Integer amount, @Switch("-force") boolean force, @Switch("-silent") boolean silent) {
        new SubCommandOpen().execute(sender, crate, player, amount, force, silent);
    }

    @Subcommand("reload")
    @CommandPermission("axcrates.reload")
    public void reload(@NotNull CommandSender sender) {
        new SubCommandReload().execute(sender);
    }

    @Subcommand("editor")
    @CommandPermission("axcrates.editor")
    public void editor(@NotNull Player sender) {
        new MainEditor(sender).open();
    }

    @Subcommand("convert")
    @CommandPermission("axcrates.convert")
    public void convert(@NotNull CommandSender sender, String plugin) {
    }
}
