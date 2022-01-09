package team.unnamed.scoreboard;

import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import team.unnamed.scoreboard.animated.AnimatedBoard;
import team.unnamed.scoreboard.animated.AnimatedBoardEntry;
import team.unnamed.validate.Validate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Class holding per-player {@link Board} instances,
 * its main purpose is to encapsulate the access to
 * {@link Board} instances
 */
public class BoardRegistry
    implements Iterable<Board> {

    private final Map<UUID, Board> registry = new HashMap<>();
    private final BoardHandler handler;

    public BoardRegistry(BoardHandler handler) {
        this.handler = handler;
    }

    /**
     * Creates a {@link Board} for the given {@code player}
     * with the specified {@code title}.
     */
    public synchronized Board create(Player player, String title) {
        Validate.isNotNull(player, "player");
        Validate.isNotNull(title, "title");
        Validate.isState(
            !registry.containsKey(player.getUniqueId()),
            "Cannot create multiple scoreboards for the given player"
        );
        Board board = new StandardBoard(handler, player, title);
        registry.put(player.getUniqueId(), board);
        return board;
    }

    public synchronized Board createAnimated(Player player, AnimatedBoardEntry title) {
        Validate.isNotNull(player, "player");
        Validate.isNotNull(title, "title");
        Validate.isState(
            !registry.containsKey(player.getUniqueId()),
            "Cannot create multiple scoreboards for the given player"
        );
        Board board = new AnimatedBoard(handler, player, title);
        registry.put(player.getUniqueId(), board);
        return board;
    }

    /**
     * Gets the {@link Board} registered for the
     * given {@code playerId}. Returns {@link Optional#empty()}
     * if no scoreboard registered.
     */
    public synchronized Optional<Board> get(UUID playerId) {
        Validate.isNotNull(playerId, "playerId");
        return Optional.ofNullable(registry.get(playerId));
    }

    /**
     * Same as to {@link BoardRegistry#get(UUID)} but
     * takes a {@link Player} as parameter and gets
     * its {@link UUID}
     */
    public synchronized Optional<Board> get(Player player) {
        Validate.isNotNull(player, "player");
        return get(player.getUniqueId());
    }

    /**
     * Removes the {@link Board} registered for the
     * given {@code playerId} if present.
     */
    public synchronized void remove(UUID playerId) {
        Validate.isNotNull(playerId, "playerId");
        registry.remove(playerId);
    }

    /**
     * Same as to {@link BoardRegistry#remove(UUID)} but
     * takes a {@link Player} as parameter and gets
     * its {@link UUID}
     */
    public synchronized void remove(Player player) {
        Validate.isNotNull(player, "player");
        remove(player.getUniqueId());
    }

    @NotNull
    @Override
    public synchronized Iterator<Board> iterator() {
        return registry.values().iterator();
    }
}