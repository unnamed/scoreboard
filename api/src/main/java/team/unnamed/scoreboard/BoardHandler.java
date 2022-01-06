package team.unnamed.scoreboard;

import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Version-specific {@link StandardBoard} internal
 * handler, contains methods for handling objectives,
 * scores and teams
 * <p>
 * The unique difference between implementations
 * are imports and data-serializers
 */
public interface BoardHandler {

    //#region Objective handling

    /**
     * Creates a scoreboard objective with 'integer'
     * as default type
     *
     * @param viewer      The objective viewer
     * @param name        The objective name
     * @param displayName The objective display name
     */
    void createObjective(
        Player viewer,
        String name,
        String displayName
    );

    /**
     * Updates a scoreboard objective
     *
     * @param viewer      The objective viewer
     * @param name        The objective name
     * @param displayName The objective display name
     */
    void updateObjective(
        Player viewer,
        String name,
        String displayName
    );

    /**
     * Updates the objective display to SIDEBAR
     *
     * @param viewer The objective viewer
     * @param name   The objective name
     */
    void updateObjectiveDisplay(
        Player viewer,
        String name
    );

    /**
     * Removes a previously created objective
     *
     * @param viewer The objective viewer
     * @param name   The objective name
     */
    void removeObjective(
        Player viewer,
        String name
    );
    //#endregion

    //#region Score handling

    /**
     * Deletes a scoreboard score
     *
     * @param viewer        The scoreboard viewer
     * @param objectiveName The objective name
     * @param name          The name in the score entry
     */
    void removeScore(
        Player viewer,
        String objectiveName,
        String name
    );

    /**
     * Updates a scoreboard score properties
     *
     * @param viewer        The scoreboard viewer
     * @param objectiveName The objective name
     * @param score         The score index
     * @param name          The name in the score entry
     */
    void updateScore(
        Player viewer,
        String objectiveName,
        int score,
        String name
    );
    //#endregion

    //#region Team handling

    /**
     * Creates a scoreboard team
     *
     * @param viewer  The scoreboard viewer
     * @param name    The team name
     * @param prefix  The team prefix
     * @param suffix  The team suffix
     * @param members The team members names
     */
    void createTeam(
        Player viewer,
        String name,
        String prefix,
        String suffix,
        Collection<String> members
    );

    /**
     * Updates a scoreboard team
     *
     * @param viewer The scoreboard viewer
     * @param name   The team name
     * @param prefix The team prefix
     * @param suffix The team suffix
     */
    void updateTeam(
        Player viewer,
        String name,
        String prefix,
        String suffix
    );

    /**
     * Deletes a scoreboard team
     *
     * @param viewer The scoreboard viewer
     * @param name   The scoreboard team name
     */
    void deleteTeam(
        Player viewer,
        String name
    );
    //#endregion
}