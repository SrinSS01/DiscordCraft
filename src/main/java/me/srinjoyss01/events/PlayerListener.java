package me.srinjoyss01.events;

import me.srinjoyss01.Main;
import me.srinjoyss01.Webhook;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.simple.JSONObject;

public class PlayerListener implements Listener {
    public PlayerListener(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(Main.getJsonObject().get(player.getName()) == null)
            player.sendMessage(ChatColor.GOLD+""+ChatColor.BOLD+"You need to do /set in the discord server in order to send message here");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        try {
            JSONObject object = (JSONObject) Main.getJsonObject().get(player.getName());
            String userid = object.get("id").toString();
            User user = Main.getJda().getUserById(userid);

            Main.webhook.forEach(web ->{
                assert user != null;
                new Webhook(web).sendWebhook(
                        user.getName(),(object.get("avatar").equals(true))?
                                "https://minotar.net/helm/"+player.getName()+".png":user.getEffectiveAvatarUrl(), event.getMessage());
            });
        }catch (NullPointerException e){
            player.sendMessage(ChatColor.ITALIC + "" + ChatColor.BOLD + "" + ChatColor.RED + "USER NOT SET");
            event.setCancelled(true);
        }
    }
}
