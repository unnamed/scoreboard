package team.unnamed.scoreboard;

import org.bukkit.entity.Player;
import team.unnamed.validate.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Class holding per-player {@link Board} instances,
 * its main purpose is to encapsulate the access to
 * {@link Board} instances
 */
public class BoardRegistry {

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

}
