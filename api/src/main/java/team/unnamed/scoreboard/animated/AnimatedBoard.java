package team.unnamed.scoreboard.animated;

import org.bukkit.entity.Player;

import team.unnamed.scoreboard.Board;
import team.unnamed.scoreboard.BoardHandler;
import team.unnamed.scoreboard.StandardBoard;
import team.unnamed.validate.Validate;

import java.util.ArrayList;
import java.util.List;

public class AnimatedBoard
    extends StandardBoard
    implements Board, Runnable {

    /**
     * The objective animated display name
     */
    private final AnimatedBoardEntry title;

    /**
     * The animated content of this scoreboard
     */
    private List<AnimatedBoardEntry> entries = new ArrayList<>();

    public AnimatedBoard(
        BoardHandler handler,
        Player player,
        AnimatedBoardEntry title
    ) {
        super(handler, player, title.getLast().toString());
        this.title = title;
    }

    public void setAnimatedLines(List<AnimatedBoardEntry> lines) {
        checkNotDeleted();
        Validate.isTrue(lines.size() <= Board.MAX_ENTRIES_SIZE,
            "Too many lines");
        this.entries = lines;
    }

    public void setAnimatedLine(int index, AnimatedBoardEntry line) {
        checkNotDeleted();
        checkInRange(index);
        this.entries.set(index, line);
    }

    @Override
    public void run() {
        if (title.tick()) {
            setTitle(title.getNext().toString());
        }

        List<String> lines = new ArrayList<>();
        for (AnimatedBoardEntry entry : entries) {
            if (entry.tick()) {
                lines.add(entry.getNext().toString());
            } else {
                lines.add(entry.getLast().toString());
            }
        }

        setLines(lines);
    }
}