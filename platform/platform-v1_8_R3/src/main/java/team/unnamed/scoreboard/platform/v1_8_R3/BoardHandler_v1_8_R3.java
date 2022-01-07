package team.unnamed.scoreboard.platform.v1_8_R3;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.PlayerConnection;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import team.unnamed.scoreboard.BoardHandler;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Version-specific implementation of {@link BoardHandler}
 * for 1.8 minecraft servers
 */
public class BoardHandler_v1_8_R3
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
            public String c(int length) {
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
            public String c(int length) {
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

            // flag determining if the name
            // was already read
            private boolean readName;

            @Override
            public String c(int length) {
                // method invoked to read the
                // objective name, type and display
                // name, calls are like:
                //   1. name = c(16)
                //   2. display name = c(32)
                //   3. type = c(16)
                if (length == 16) {
                    if (readName) {
                        // after reading the name
                        // it asks for the objective
                        // type, return always "integer"
                        return "integer";
                    } else {
                        readName = true;
                        // name wasn't read yet, so
                        // return the name
                        return name;
                    }
                } else {
                    // return the objective
                    // display name
                    return displayName;
                }
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
            PacketPlayOutScoreboardScore.EnumScoreboardAction.REMOVE
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
            PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE
        ));
    }

    private Packet<?> newScorePacket(
        @Nullable String playerName,
        String objectiveName,
        int score,
        PacketPlayOutScoreboardScore.EnumScoreboardAction action
    ) {
        Packet<?> packet = new PacketPlayOutScoreboardScore();
        suppressWrite(packet, new PacketDataSerializer(null) {

            @Override
            public String c(int length) {
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
            public int e() {
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
            public String c(int length) {
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

            // counter for determining what
            // properties were already read
            private byte propertyCursor;

            // flag determining if the read
            // was already read
            private boolean readAction;

            @Override
            public String c(int length) {
                if (length == 40) {
                    // if length is 40, return the
                    // next read member name
                    return memberIterator.next();
                } else if (length == 32) {
                    if (propertyCursor == 1) {
                        // after the team name read
                        // the packet reads the display name
                        propertyCursor++;
                        return teamName;
                    } else {
                        // after most reads, it will
                        // read the member visibility,
                        // just return always
                        return "always";
                    }
                } else {
                    // if length is 16
                    if (propertyCursor == 0) {
                        // the first thing the packet
                        // will read is the team name
                        propertyCursor++;
                        return teamName;
                    } else if (propertyCursor == 2) {
                        // after the display name read,
                        // it reads the prefix
                        propertyCursor++;
                        return prefix;
                    } else {
                        // and then the suffix
                        propertyCursor++;
                        return suffix;
                    }
                }
            }

            @Override
            public int e() {
                // method invoked to read the
                // team members names count
                return members.size();
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