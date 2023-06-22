package org.affinitydev.blockify

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.plugin.java.JavaPlugin

class Blockify : JavaPlugin(), Listener {
    private var lastNotification: Long = 0

    override fun onEnable() {
        saveDefaultConfig()
        lastNotification = 0
        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
    }

    @EventHandler
    fun onBlockMine(e: BlockBreakEvent) {
        val material = e.block.type
        val player = e.player
        if (isBlockInConfig(material) && isCooldownOver()) {
            for (p in Bukkit.getOnlinePlayers()) {
                var message : String? = config.getString("notification", "§c§l[!] §b%player% §7just mined §e%block%§7!")
                if (message != null) {
                    message = message.replace("%player%", player.name)
                    message = message.replace("%block%", material.toString().replace("_", " "))
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', message))
                }
            }
            lastNotification = System.currentTimeMillis()
        }
    }

    private fun isBlockInConfig(block: Material): Boolean {
        return config.getStringList("blocks").contains(block.toString())
    }

    private fun isCooldownOver(): Boolean {
        return System.currentTimeMillis() - lastNotification > getCooldownFromConfig()
    }

    private fun getCooldownFromConfig(): Long {
        return config.getLong("cooldown-in-ms", 2000)
    }
}