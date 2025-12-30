package net.azisaba.afnw.afnwcore2.util.item;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * AfnwTicketを生成します。
 *
 * @author m2en
 */
public record AfnwTicket() {

  public static ItemStack afnwTicket = new ItemStack(Material.PAPER, 1);

  static {
    ItemMeta metaData = afnwTicket.getItemMeta();
    metaData.displayName(Component.text("AfnwTicket").color(NamedTextColor.LIGHT_PURPLE));
    List<String> lore = new ArrayList<>();
    lore.add("Afnw内で使用できるチケットです。");
    lore.add("/afnw でチケットとアイテムを交換できます。また、このアイテムは交換可能です。");
    metaData.setLore(lore);
    afnwTicket.setItemMeta(metaData);
  }

}
