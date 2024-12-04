package fr.exatio;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Game {

    public static Game instance;

    public static boolean gameActive = false;
    public UUID uuid;

    User impostor;
    Map<User, User> playersVotes;
    TextChannel gameChannel;

    public void vote(SlashCommandInteractionEvent event) {
        StringSelectMenu.Builder builder = StringSelectMenu.create("game-" + Game.instance.uuid.toString());

        for(User user : playersVotes.keySet()) {
            builder.addOption(user.getEffectiveName(), user.getId());
        }

        builder.setRequiredRange(1, 1);
        StringSelectMenu ar = builder.build();

        for(User user : playersVotes.keySet()) {
            user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Choisissez un joueur à voter comme imposter").addActionRow(ar).queue());
        }

        event.reply("Les fiches de votes ont été envoyées").queue();
    }

    public void voteSelect(StringSelectInteractionEvent event) {
        String voted = event.getInteraction().getSelectedOptions().get(0).getValue();
        String votedBy = event.getUser().getId();

        User votedUser = event.getJDA().retrieveUserById(voted).complete();
        playersVotes.replace(event.getUser(), votedUser);

        if(playersVotes.get(event.getUser()) == null) {
            event.reply("Votre vote a été enregistré pour " + votedUser.getEffectiveName()).queue();
            gameChannel.sendMessage("1 vote a été pris en compte : " + event.getUser().getEffectiveName() + " a voté pour " + votedUser.getEffectiveName()).queue();
        } else {
            event.reply("La modification de votre vote a été enregistrée. Nouveau vote : " + votedUser.getEffectiveName()).queue();
            gameChannel.sendMessage(event.getUser().getEffectiveName() + " a changé son vote. Il vote maintenant pour " + votedUser.getEffectiveName()).queue();
        }

        if(!playersVotes.containsValue(null)) {
            gameChannel.sendMessage("La partie est terminée! L'imposteur voté est " + getMostVoted() + ".\nLe réel imposteur était " + impostor.getEffectiveName()).complete();
            gameActive = false;
        }



    }
    public Game(String messagePlayers, SlashCommandInteractionEvent event) {

        instance = this;
        playersVotes = new HashMap<>();
        Pattern mentionPattern = Pattern.compile("<@!?(\\d+)>");
        Matcher matcher = mentionPattern.matcher(messagePlayers);

        while (matcher.find()) {
            String userId = matcher.group(1);
            User user = Main.jda.retrieveUserById(userId).complete();

            if (user != null) {
                playersVotes.put(user, null);
                System.out.println(user.getEffectiveName());
            }
        }

        if (playersVotes.size() != 5) {
            event.reply("You need to mention 5 players!").setEphemeral(true).queue();
            System.out.println(playersVotes.size());
            return;
        }

        gameChannel = event.getChannel().asTextChannel();
        impostor = (User) playersVotes.keySet().toArray()[new Random().nextInt(5) + 1];
        uuid = UUID.randomUUID();
        gameActive = true;

        for(User user : playersVotes.keySet()) {
            if(user.equals(impostor)) {
                user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Vous êtes l'imposteur !").queue());
            } else {
                user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Vous n'êtes pas l'imposteur").queue());
            }
        }

        event.reply("Partie lancée! Les rôles ont été envoyés!").queue();

    }

    public String getMostVoted() {

        Map<User, Integer> votes = new HashMap<>();

        for(User voter : playersVotes.keySet()) {
            User voted = playersVotes.get(voter);
            if(!votes.containsKey(voted)) {
                votes.put(voted, 1);
            } else {
                votes.replace(voted, votes.get(voted) + 1);
            }
        }

        return Collections.max(votes.entrySet(), Map.Entry.comparingByValue()).getKey().getEffectiveName();
    }

}
