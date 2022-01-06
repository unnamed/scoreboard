package team.unnamed.scoreabord.platform.v1_17_R1;

import net.minecraft.EnumChatFormat;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.scores.ScoreboardTeamBase;
import net.minecraft.world.scores.criteria.IScoreboardCriteria;

import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import team.unnamed.scoreboard.BoardHandler;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Version-specific implementation of {@link BoardHandler}
 * for 1.17 minecraft servers
 */
public class BoardHandler_v1_17_R1
    implements BoardHandler {

    //#region Objective handling
    // See https://wiki.vg/Protocol#Scoreboard_Objective

    @Override
    public void createObjective(
        Player viewer,
        String name,
        String displayName
    ) {
        // sends an objective packet with 'create' (0x0) as action
        sendPackets(viewer, newUpdateObjectivePacket(
            (byte) 0x0,
            name,
            displayName
        ));
    }

    @Override
    public void updateObjective(
        Player viewer,
        String name,
        String displayName
    ) {
        // sends an objective packet with 'update' (0x2) as action
        sendPackets(viewer, newUpdateObjectivePacket(
            (byte) 0x2,
            name,
            displayName
        ));
    }

    @Override
    public void updateObjectiveDisplay(
        Player viewer,
        String name
    ) {
        Packet<?> packet = new PacketPlayOutScoreboardDisplayObjective(
            new PacketDataSerializer(null) {

                @Override
                public byte readByte() {
                    // method invoked to read the
                    // objective display
                    // 0x1: sidebar
                    return 0x1;
                }

                @Override
                public String e(int length) {
                    // method invoked to read the
                    // objective name, length is ignorable
                    return name;
                }

            });
        sendPackets(viewer, packet);
    }

    @Override
    public void removeObjective(
        Player viewer,
        String name
    ) {
        Packet<?> packet = new PacketPlayOutScoreboardObjective(
            new PacketDataSerializer(null) {

                @Override
                public byte readByte() {
                    // method invoked to read the
                    // packet action
                    // 0x1: delete
                    return 0x1;
                }

                @Override
                public String e(int length) {
                    // method invoked to read the
                    // objective name, length is ignorable
                    return name;
                }

            });
        sendPackets(viewer, packet);
    }

    private Packet<?> newUpdateObjectivePacket(
        byte action,
        String name,
        String displayName
    ) {
        return new PacketPlayOutScoreboardObjective(
            new PacketDataSerializer(null) {

                @Override
                public String e(int length) {
                    // return the objective name
                    return name;
                }

                @Override
                public byte readByte() {
                    // method invoked to read the
                    // packet action
                    // 0x0: create
                    // 0x1: delete
                    // 0x2: update
                    return action;
                }

                @Override
                public IChatBaseComponent i() {
                    // return the objective
                    // display name
                    return new ChatMessage(displayName);
                }

                @Override
                @SuppressWarnings("unchecked")
                public <T extends Enum<T>> T a(Class<T> enumType) {
                    // return the scoreboard objective type
                    return (T) IScoreboardCriteria.EnumScoreboardHealthDisplay.a;
                }
            });
    }
    //#endregion

    //#region Score handling
    // See https://wiki.vg/Protocol#Update_Score

    @Override
    public void removeScore(
        Player viewer,
        String objectiveName,
        String name
    ) {
        // removes the score by using the 'REMOVE' action
        sendPackets(viewer, newScorePacket(
            name,
            objectiveName,
            -1, // score isn't required on remove
            ScoreboardServer.Action.b
        ));
    }

    @Override
    public void updateScore(
        Player viewer,
        String objectiveName,
        int score,
        String name
    ) {
        // creates/updates the score by using the 'CHANGE' action
        sendPackets(viewer, newScorePacket(
            name,
            objectiveName,
            score,
            ScoreboardServer.Action.a
        ));
    }

    private Packet<?> newScorePacket(
        @Nullable String playerName,
        String objectiveName,
        int score,
        ScoreboardServer.Action action
    ) {
        return new PacketPlayOutScoreboardScore(
            new PacketDataSerializer(null) {

                @Override
                public String e(int length) {
                    // method invoked to read the
                    // player name and the objective
                    // name, its call order is like:
                    //   1. playerName = c(40)
                    //   2. objectiveName = c(16)
                    if (length == 40) {
                        return playerName;
                    } else {
                        return objectiveName;
                    }
                }

                @Override
                @SuppressWarnings("unchecked")
                public <T extends Enum<T>> T a(Class<T> enumType) {
                    // method invoked to read the
                    // packet action, just return
                    // the given action
                    return (T) action;
                }

                @Override
                public int j() {
                    // method invoked to read the
                    // score
                    return score;
                }
            });
    }
    //#endregion

    //#region Team handling
    // See https://wiki.vg/Protocol#Teams

    @Override
    public void createTeam(
        Player viewer,
        String name,
        String prefix,
        String suffix,
        Collection<String> members
    ) {
        // creates the team using the CREATE
        // action (0x0)
        sendPackets(viewer, newUpdateTeamPacket(
            (byte) 0x0, // create
            name,
            prefix,
            suffix,
            members
        ));
    }

    @Override
    public void updateTeam(
        Player viewer,
        String name,
        String prefix,
        String suffix
    ) {
        // updates the team information using the
        // UPDATE action (0x2)
        sendPackets(viewer, newUpdateTeamPacket(
            (byte) 0x2, // update
            name,
            prefix,
            suffix,
            Collections.emptySet()
        ));
    }

    @Override
    public void deleteTeam(
        Player viewer,
        String name
    ) {
        Packet<?> packet = new PacketPlayOutScoreboardTeam(
            new PacketDataSerializer(null) {

                @Override
                public String e(int length) {
                    // method invoked to read
                    // the team name, length is
                    // ignorable
                    return name;
                }

                @Override
                public byte readByte() {
                    // method invoked to read the
                    // packet action
                    // 0x1: delete
                    return 0x1;
                }
            });
        sendPackets(viewer, packet);
    }

    private Packet<?> newUpdateTeamPacket(
        byte action,
        String teamName,
        String prefix,
        String suffix,
        Collection<String> members
    ) {
        return new PacketPlayOutScoreboardTeam(
            new PacketDataSerializer(null) {

                // counter for determining what
                // components were already read
                private byte componentCursor;

                private boolean readVisibility;
                // flag determining if the read
                // was already read
                private boolean readAction;

                @Override
                public String e(int length) {
                    if (length == 16) {
                        return teamName;
                    } else if (length == 40 && !readVisibility) {
                        readVisibility = true;
                        return ScoreboardTeamBase.EnumNameTagVisibility.a.e;
                    } else {
                        return ScoreboardTeamBase.EnumTeamPush.a.e;
                    }
                }

                @Override
                public byte readByte() {
                    // method invoked to read the
                    // packet action and the pack option
                    // data
                    if (!readAction) {
                        readAction = true;
                        return action;
                    } else {
                        return 0;
                    }
                }

                @Override
                @SuppressWarnings("unchecked")
                public <T> List<T> a(Function<PacketDataSerializer, T> function) {
                    // return members as array-list
                    return (List<T>) new ArrayList<>(members);
                }

                @Override
                public IChatBaseComponent i() {
                    // method invoked to read the
                    // team display name, the prefix
                    // and suffix, it's call order is like:
                    // 1. displayName
                    // 2. prefix
                    // 3. suffix
                    if (componentCursor == 0) {
                        componentCursor++;
                        return new ChatMessage(teamName);
                    } else if (componentCursor == 1) {
                        componentCursor++;
                        return new ChatMessage(prefix);
                    } else {
                        return new ChatMessage(suffix);
                    }
                }

                @Override
                @SuppressWarnings("unchecked")
                public <T extends Enum<T>> T a(Class<T> enumType) {
                    // method invoked to read the
                    // packet chat format
                    return (T) EnumChatFormat.v;
                }
            });
    }
    //#endregion

    /**
     * Sends the given {@code packets} to the
     * specified {@code receiver} player
     *
     * @param receiver The packet receiver
     * @param packets  The sent packets
     */
    private static void sendPackets(
        Player receiver,
        Packet<?>... packets
    ) {
        PlayerConnection connection = ((CraftPlayer) receiver)
            .getHandle()
            .b;
        for (Packet<?> packet : packets) {
            connection.sendPacket(packet);
        }
    }
}