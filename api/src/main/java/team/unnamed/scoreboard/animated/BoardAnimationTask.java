package team.unnamed.scoreboard.animated;

import org.bukkit.scheduler.BukkitRunnable;

import team.unnamed.scoreboard.Board;
import team.unnamed.scoreboard.BoardRegistry;

public class BoardAnimationTask extends BukkitRunnable {

    private final BoardRegistry registry;

    public BoardAnimationTask(BoardRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void run() {
        for (Board board : registry) {
            if (board instanceof Runnable) {
                ((Runnable) board).run();
            }
        }
    }
}