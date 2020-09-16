package me.srinjoyss01;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import javax.annotation.Nonnull;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class set extends ListenerAdapter {
    public static String messageid;
    public boolean change;
    public User user;
    public String IGN;
    public static boolean avatar;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        event.getJDA().getGuilds().forEach(guild -> guild.getTextChannels().forEach(textChannel ->
                textChannel.retrieveWebhooks().queue(webhooks -> {
                    if (!webhooks.isEmpty()) textChannel.sendMessage("```Server is Ready !!!```").queue();
                })
        )
        );
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        String[] s = event.getMessage().getContentRaw().split(" ");

        if (s[0].equalsIgnoreCase("/set")){
            if (s.length == 2){
                if (Main.jsonObject.get(s[1]) != null) {
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " already set").queue();
                    return;
                }
                    event.getChannel().sendMessage("Do you wish to use:\n1️⃣ your discord avatar\n**Or**\n2️⃣ your Minecraft avatar\nfor the pfp of the webhook")
                    .queue(message -> {
                        messageid = message.getId();
                        IGN = s[1];
                        user = event.getAuthor();
                        message.addReaction("\u0031\uFE0F\u20E3").queue();
                        message.addReaction("\u0032\uFE0F\u20E3").queue();
                    });


            }else event.getChannel().sendMessage("usage: `/set <minecraft IGN>`").queue();
        }
        if (s[0].equalsIgnoreCase("/change")){
            if (s.length == 2) {
                event.getChannel().sendMessage("Do you wish to use:\n your discord avatar\n**Or**\n your Minecraft avatar\nfor the pfp of the webhook")
                        .queue(message -> {
                            messageid = message.getId();
                            IGN = s[1];
                            user = event.getAuthor();
                            change = true;
                            message.addReaction("\u0031\uFE0F\u20E3").queue();
                            message.addReaction("\u0032\uFE0F\u20E3").queue();
                        });
            }else event.getChannel().sendMessage("usage: `/change <minecraft IGN>`").queue();
        }

        event.getTextChannel().retrieveWebhooks().queue(webhooks -> {
            if(!webhooks.isEmpty()){
                String message = "";
                for (String words : s) {
                    if (words.charAt(0) == '<' && words.charAt(1) == '@' && words.lastIndexOf('>') != -1)
                        message += "@"+ Objects.requireNonNull(event.getGuild().getMemberById(words.substring(2, words.lastIndexOf('>')))).getEffectiveName() + " ";
                    else message += words + " ";
                }
                if(!event.isWebhookMessage())
                    Bukkit.broadcastMessage(ChatColor.ITALIC+""+ChatColor.GOLD+"["+event.getAuthor().getName()+"] "+
                        ChatColor.AQUA+""+message);
            }
        });

    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (Objects.requireNonNull(event.getUser()).isBot())return;
        if (event.getMessageId().equals(messageid) ){
            if (event.getUser().equals(user)) {
                if (event.getReactionEmote().getEmoji().equals("\u0032\uFE0F\u20E3")) avatar = true;
                else if (event.getReactionEmote().getEmoji().equals("\u0031\uFE0F\u20E3")) avatar = false;
                if (change) changing(IGN);
                else setUser(IGN, user);
                event.getChannel().editMessageById(messageid, "Done!").queue(message ->
                        event.getTextChannel().clearReactionsById(message.getId()).queue()
                );
            }else event.retrieveMessage().queue(message -> {
                message.removeReaction("\u0032\uFE0F\u20E3", event.getUser()).queue();
                message.removeReaction("\u0031\uFE0F\u20E3", event.getUser()).queue();
            });
        }
    }

    @SuppressWarnings("unchecked")
    public void changing(String IGN){
        JSONObject object = (JSONObject) Main.jsonObject.get(IGN);
        object.replace("avatar",avatar);
        Main.jsonObject.replace(IGN,object);
        try {
            FileWriter file = new FileWriter("players.json");
            file.write(Main.jsonObject.toString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @SuppressWarnings("unchecked")
    public void setUser(String s, User user){
        try {
            Main.jsonObject.put(s,property.generate(user.getId(),avatar));
            FileWriter file = new FileWriter("players.json");
            file.write(Main.jsonObject.toString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
