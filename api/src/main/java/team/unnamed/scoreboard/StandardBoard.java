package team.unnamed.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import team.unnamed.validate.Validate;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Default implementation of {@link Board} that
 * just wraps {@link BoardHandler} and gives an
 * object-based API to handle scoreboards
 */
public class StandardBoard
    implements Board {

    /**
     * Integer for generating unique objective names
     */
    private static final AtomicInteger COUNTER
        = new AtomicInteger();

    /**
     * Array containing strings not visible in game
     * we can use as fake player names for the scores
     */
    private static final String[] FAKE_PLAYER_NAMES;

    static {
        // we can use all the codes from ChatColor enum
        ChatColor[] colors = ChatColor.values();
        FAKE_PLAYER_NAMES = new String[colors.length];
        for (int i = 0; i < colors.length; i++) {
            FAKE_PLAYER_NAMES[i] = colors[i].toString();
        }
    }

    /**
     * The objective name for this scoreboard
     */
    private final String name;

    /**
     * The objective display name
     */
    private String title;

    /**
     * The content of this scoreboard
     */
    private final List<BoardEntry> entries = new ArrayList<>();

    /**
     * Weak reference to the viewer
     */
    private final WeakReference<Player> player;

    /**
     * Delegated handler for this scoreboard
     */
    private final BoardHandler handler;

    /**
     * State determining if the scoreboard
     * was deleted, no operations can be
     * executed when this variable is true
     */
    private boolean deleted;

    public StandardBoard(
        BoardHandler handler,
        Player player,
        String title
    ) {
        Validate.isNotNull(player, "player");
        Validate.isNotNull(title, "title");

        this.handler = handler;
        this.name = Integer.toHexString(COUNTER.getAndIncrement());
        this.player = new WeakReference<>(player);
        this.title = title;

        handler.createObjective(player, name, title);
        handler.updateObjectiveDisplay(player, name);
    }

    private Player getPlayer() {
        Player player = this.player.get();
        if (player == null) {
            throw new IllegalStateException(
                "Player reference was discarded, cannot"
                    + " modify scoreboard"
            );
        }
        return player;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        Validate.isState(!deleted, "Cannot set title to deleted board");
        Validate.isNotNull(title, "title");
        if (!this.title.equals(title)) {
            this.title = title;
            handler.updateObjective(
                getPlayer(),
                name,
                title
            );
        }
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public List<String> getLines() {
        List<String> lines = new ArrayList<>();
        for (BoardEntry entry : entries) {
            lines.add(entry.toString());
        }
        return lines;
    }

    @Override
    public void setLines(List<String> lines) {
        checkNotDeleted();
        Validate.isTrue(lines.size() <= FAKE_PLAYER_NAMES.length,
            "Too many lines");

        int currentSize = entries.size();
        int newSize = lines.size();
        int toUpdate;

        Player target = getPlayer();

        if (currentSize > newSize) {
            // remove unnecessary entries
            for (int i = this.entries.size(); i > lines.size(); i--) {
                removeLine(i - 1);
            }
            // it must update 'newSize' entries
            toUpdate = newSize;
        } else if (currentSize < newSize) {
            // create required entries
            for (int i = this.entries.size(); i < lines.size(); i++) {

                // cached player name to avoid some array accesses
                String entryName = FAKE_PLAYER_NAMES[i];
                BoardEntry entry = BoardEntry.split(entryName, lines.get(lines.size() - i - 1));

                // create the score
                handler.updateScore(target, name, i, entryName);
                // create the team
                handler.createTeam(
                    target,
                    entryName,
                    entry.getPrefix(),
                    entry.getSuffix(),
                    Collections.singleton(entryName)
                );

                this.entries.add(entry);
            }
            // it must update 'currentSize' entries
            toUpdate = currentSize;
        } else {
            // newSize and currentSize are equal, so
            // everything must be updated (nothing to
            // delete and nothing to create)
            toUpdate = newSize;
        }

        for (int i = 0; i < toUpdate; i++) {
            setLine(i, lines.get(lines.size() - i - 1));
        }
    }

    @Override
    public String getLine(int index) {
        return entries.get(index).toString();
    }

    @Override
    public void setLine(int index, String line) {
        checkNotDeleted();
        checkInRange(index);

        BoardEntry old = entries.get(index);
        if (!old.toString().equals(line)) {
            String entryName = FAKE_PLAYER_NAMES[index];
            BoardEntry newEntry = BoardEntry.split(entryName, line);
            //just update the text of the team wrapping
            //the scoreboard score
            handler.updateTeam(
                getPlayer(),
                entryName,
                newEntry.getPrefix(),
                newEntry.getSuffix()
            );
        }
    }

    @Override
    public String removeLine(int index) {
        checkNotDeleted();
        checkInRange(index);
        Player target = getPlayer();
        String entryName = FAKE_PLAYER_NAMES[index];
        // remove score
        handler.removeScore(target, name, entryName);
        // remove team
        handler.deleteTeam(target, entryName);
        return entries.remove(index).toString();
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void delete() {
        checkNotDeleted();
        Player target = getPlayer();
        for (int i = 0; i < entries.size(); i++) {
            handler.deleteTeam(target, name + ':' + i);
        }
        handler.removeObjective(target, name);
        this.deleted = true;
    }

    private void checkNotDeleted() {
        Validate.isState(
            !deleted,
            "Cannot modify deleted scoreboard"
        );
    }

    private void checkInRange(int index) {
        Validate.isTrue(
            index >= 0 && index < entries.size(),
            "Index cannot be negative or greater than line count"
        );
    }
}