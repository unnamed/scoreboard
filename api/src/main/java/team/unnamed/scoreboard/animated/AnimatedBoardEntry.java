package team.unnamed.scoreboard.animated;

import org.bukkit.ChatColor;

import team.unnamed.scoreboard.BoardEntry;
import team.unnamed.validate.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnimatedBoardEntry {

    private final List<BoardEntry> frames;

    private final int interval;
    private int ticked;

    private int index = 1;

    public AnimatedBoardEntry(List<BoardEntry> frames,
                              int interval) {
        Validate.isState(
            frames != null && frames.size() > 0, "Entry frames cannot be null or empty."
        );
        this.frames = frames;
        this.interval = interval;
    }

    public BoardEntry getLast() {
        return frames.get(index % frames.size());
    }

    public BoardEntry getNext() {
        return frames.get(index++ % frames.size());
    }

    public boolean tick() {
        if (++ticked >= interval) {
            ticked = 0;
            return true;
        }

        return false;
    }

    public static AnimatedBoardEntry of(int interval, BoardEntry frame) {
        return new AnimatedBoardEntry(Collections.singletonList(frame), interval);
    }

    public static AnimatedBoardEntry of(int interval, String frame) {
        return of(interval, BoardEntry.split(ChatColor.RESET.toString(), frame));
    }

    public static AnimatedBoardEntry of(int interval, String... frames) {
        List<BoardEntry> entries = new ArrayList<>();
        for (String frame : frames) {
            entries.add(BoardEntry.split("", frame));
        }
        return new AnimatedBoardEntry(entries, interval);
    }
}