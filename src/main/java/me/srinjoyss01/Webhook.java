package me.srinjoyss01;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;

public class Webhook {
    private final WebhookClient client;

    public Webhook(String webhook) {
        WebhookClientBuilder builder = new WebhookClientBuilder(webhook);
        builder.setThreadFactory((job) -> {
            Thread thread = new Thread(job);
            thread.setName("WbhookThread");
            thread.setDaemon(true);
            return thread;
        });
        builder.setWait(true);
        this.client = builder.build();
    }

    public void sendWebhook(String username, String avatarUrl, String content){
        WebhookMessageBuilder builder = new WebhookMessageBuilder()
                .setUsername(username)
                .setAvatarUrl(avatarUrl)
                .setContent(content);
        client.send(builder.build());
    }
}
