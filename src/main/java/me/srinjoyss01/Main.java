package me.srinjoyss01;

import me.srinjoyss01.events.PlayerListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public final class Main extends JavaPlugin {

    public static String token;
    public static JDA jda;
    public static CommandSender sender;
    public static JSONObject jsonObject;
    public static List<String> webhook;
    private boolean enable = true;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        sender = Bukkit.getConsoleSender();
        new PlayerListener(this);
        try {
            token = this.getConfig().getString("token");
            webhook = this.getConfig().getStringList("webhook");
            if (token.equalsIgnoreCase("bot token here")) throw new Exception("TokenNotSet");
            if (webhook.contains("path")) throw new Exception("Webhook Path Not set");
        }catch (Exception e) {
            e.printStackTrace();
            enable = false;
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
            try {
                if (enable) activate();
                JSONParser parser = new JSONParser();
                jsonObject =  (JSONObject) parser.parse(new FileReader("players.json"));
            }
            catch (LoginException e) {
                e.printStackTrace();
                Bukkit.broadcastMessage(ChatColor.RED +""+ChatColor.BOLD + " ERROR: login !!! ");
                Bukkit.getServer().getPluginManager().disablePlugin(this);
            }
            catch (FileNotFoundException e){
                try {
                    FileWriter fileWriter = new FileWriter("players.json");
                    fileWriter.write((jsonObject = new JSONObject()).toJSONString());
                    fileWriter.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    Bukkit.getServer().getPluginManager().disablePlugin(this);
                }
            }
            catch (ParseException | IOException e) {
                e.printStackTrace();
                Bukkit.getServer().getPluginManager().disablePlugin(this);
            }
    }

    @Override
    public void onDisable() {
        if (enable) jda.shutdownNow();
    }


    public static JDA getJda() {
        return jda;
    }

    public static JSONObject getJsonObject() {
        return jsonObject;
    }

    public void activate() throws LoginException {
        jda = JDABuilder.createDefault(token).enableIntents(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MEMBERS
        ).disableCache(
                CacheFlag.VOICE_STATE,
                CacheFlag.EMOTE
        ).setMemberCachePolicy(MemberCachePolicy.ALL)
        .build();
        jda.addEventListener(new set());
    }
}
