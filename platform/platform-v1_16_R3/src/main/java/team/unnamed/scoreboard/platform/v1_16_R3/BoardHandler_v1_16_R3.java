package team.unnamed.scoreboard.platform.v1_16_R3;

import net.minecraft.server.v1_16_R3.ChatMessage;
import net.minecraft.server.v1_16_R3.EnumChatFormat;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.IScoreboardCriteria;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketDataSerializer;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.minecraft.server.v1_16_R3.ScoreboardServer;
import net.minecraft.server.v1_16_R3.ScoreboardTeamBase;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import team.unnamed.scoreboard.BoardHandler;

import javax.annotation.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Version-specific implementation of {@link BoardHandler}
 * for 1.16 minecraft servers
 */
public class BoardHandler_v1_16_R3
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
        Packet<?> packet = new PacketPlayOutScoreboardDisplayObjective();
        suppressWrite(packet, new PacketDataSerializer(null) {

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
        Packet<?> packet = new PacketPlayOutScoreboardObjective();
        suppressWrite(packet, new PacketDataSerializer(null) {

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
        Packet<?> packet = new PacketPlayOutScoreboardObjective();
        suppressWrite(packet, new PacketDataSerializer(null) {

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
            public IChatBaseComponent h() {
                // return the objective
                // display name
                return new ChatMessage(displayName);
            }

            @Override
            @SuppressWarnings("unchecked")
            public <T extends Enum<T>> T a(Class<T> enumType) {
                // return the scoreboard objective type
                return (T) IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER;
            }
        });
        return packet;
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
            ScoreboardServer.Action.REMOVE
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
            ScoreboardServer.Action.CHANGE
        ));
    }

    private Packet<?> newScorePacket(
        @Nullable String playerName,
        String objectiveName,
        int score,
        ScoreboardServer.Action action
    ) {
        Packet<?> packet = new PacketPlayOutScoreboardScore();
        suppressWrite(packet, new PacketDataSerializer(null) {

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
            public int i() {
                // method invoked to read the
                // score
                return score;
            }
        });
        return packet;
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
        Packet<?> packet = new PacketPlayOutScoreboardTeam();
        suppressWrite(packet, new PacketDataSerializer(null) {

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
        Packet<?> packet = new PacketPlayOutScoreboardTeam();
        suppressWrite(packet, new PacketDataSerializer(null) {

            private final Iterator<String> memberIterator = members.iterator();

            // flag determining if the
            // tag-visibility was already read
            private byte stringCursor;

            // counter for determining what
            // components were already read
            private byte componentCursor;

            // flag determining if the read
            // was already read
            private boolean readAction;

            @Override
            public String e(int length) {
                if (length == 16) {
                    // if length is 16, return the
                    // team name
                    return teamName;
                } else if (stringCursor == 0) {
                    // if length is 40 and the tag-visibility
                    // isn't read yet, return always enum constant
                    stringCursor++;
                    return ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS.e;
                } else if (stringCursor == 1) {
                    // if the length is 40 and the tag-visibility
                    // is already read, return always enum constant
                    stringCursor++;
                    return ScoreboardTeamBase.EnumTeamPush.ALWAYS.e;
                } else {
                    // the cursor size is 2,
                    // return the next member name
                    return memberIterator.next();
                }
            }

            @Override
            public byte readByte() {
                // method invoked to read the
                // packet action, the pack option
                // data and the color, we just care
                // about the action, other properties
                // are set to zero
                if (!readAction) {
                    readAction = true;
                    return action;
                } else {
                    return 0;
                }
            }

            @Override
            public IChatBaseComponent h() {
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
                return (T) EnumChatFormat.RESET;
            }

            @Override
            public int i() {
                // method invoked to read the
                // team members names count
                return members.size();
            }
        });
        return packet;
    }
    //#endregion

    /**
     * Helper method for writing the given
     * {@code serializer} data to the given
     * {@code packet} object suppressing the
     * {@link IOException}
     *
     * @param packet     The wrote packet
     * @param serializer The data container
     */
    private static void suppressWrite(
        Packet<?> packet,
        PacketDataSerializer serializer
    ) {
        try {
            packet.a(serializer);
        } catch (IOException e) {
            // should never happen with valid serializers, but
            // throw it anyway
            throw new IllegalStateException(
                "Cannot write packet data",
                e
            );
        }
    }

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
            .playerConnection;
        for (Packet<?> packet : packets) {
            connection.sendPacket(packet);
        }
    }
}