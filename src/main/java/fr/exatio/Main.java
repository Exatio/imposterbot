package fr.exatio;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

public class Main extends ListenerAdapter {

    public static JDA jda;

    public static void main(String[] args) throws IOException {

        // read from config.properties

        InputStream inputStream = Objects.requireNonNull(JDA.class.getClassLoader().getResourceAsStream("config.properties"));
        Properties properties = new Properties();
        properties.load(inputStream);

        jda = JDABuilder.createLight(properties.getProperty("token"), GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new Main())
                .setActivity(Activity.playing("Qui est l'imposteur ... ?"))
                .build();
    }

    @Override
    public void onReady(ReadyEvent event) {

        event.getJDA().updateCommands().addCommands(
                Commands.slash("start", "Starts an imposter game")
                        .addOption(OptionType.STRING, "players", "5 mentions to users in the game", true),
                Commands.slash("vote", "Vote during an imposter game")
        ).queue(commands -> commands.forEach(System.out::println));

        System.out.println("Bot is ready!");
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        switch (event.getName()) {
            case "start":
                if(Game.gameActive) {
                    event.reply("Une partie est déjà en cours!").queue();
                } else {
                    new Game(event.getOption("players", OptionMapping::getAsString), event);
                }
                break;

            case "vote":
                if(!Game.gameActive) {
                    event.reply("Il n'y a pas de partie en cours...").queue();
                } else {
                    Game.instance.vote(event);
                }

                break;
            default:
                System.out.printf("Unknown command %s used by %#s%n", event.getName(), event.getUser());
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {

        if (event.getComponentId().startsWith("game-")) {
            String gameId = event.getComponentId().substring(5);

            if(!Game.gameActive) {
                event.reply("Aucune partie en cours!").queue();
            } else if(!Objects.equals(Game.instance.uuid, UUID.fromString(gameId))) {
                event.reply("Cette partie est déjà terminée...").queue();
            } else {
                Game.instance.voteSelect(event);
            }
        } else {
            event.reply("wth ?").queue();
        }
    }

}