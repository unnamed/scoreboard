package team.unnamed.scoreboard;

import org.jetbrains.annotations.Contract;

import java.util.List;

/**
 * Represents a handleable per-player
 * scoreboard, implementations should be
 * thread-safe
 */
public interface Board {

    /**
     * Returns the current title
     * for this scoreboard
     */
    String getTitle();

    /**
     * Updates the current title
     * for this scoreboard
     */
    @Contract("null -> fail")
    void setTitle(String title);

    /**
     * Returns the count of current
     * lines in this scoreboard
     */
    int size();

    /**
     * Returns an unmodifiable list
     * of lines of this scoreboard
     */
    List<String> getLines();

    /**
     * Updates all the lines in this scoreboard
     * to the specified {@code lines}
     */
    @Contract("null -> fail")
    void setLines(List<String> lines);

    /**
     * Returns the line at the specified
     * {@code index} in this scoreboard
     * than {@link Board#size()}
     */
    String getLine(int index);

    /**
     * Sets the text at the specified {@code index}
     * for this scoreboard
     * {@code line} is null
     */
    @Contract("_, null -> fail")
    void setLine(int index, String line);

    /**
     * Removes the line at the specified {@code index}
     * from this scoreboard
     *
     * @return The removed line
     */
    String removeLine(int index);

    /**
     * Determines if this scoreboard was
     * deleted or not. Deleted boards can't
     * be modified and should be discarded
     */
    boolean isDeleted();

    /**
     * Deletes this scoreboard and clears it
     * from the player
     */
    void delete();

}
