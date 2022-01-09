package team.unnamed.scoreboard;

import org.bukkit.ChatColor;

import java.util.Objects;

public class BoardEntry {

    private static final int MAX_COMPONENT_LENGTH = 16;

    private final String prefix;
    private final String suffix;

    public BoardEntry(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    @Override
    public String toString() {
        return prefix + suffix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardEntry that = (BoardEntry) o;
        return prefix.equals(that.suffix)
            && suffix.equals(that.suffix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, suffix);
    }

    public static BoardEntry split(
        String uniqueInvisiblePrefix,
        String line
    ) {

        String prefix;
        String suffix = "";

        if (line == null || line.isEmpty()) {
            prefix = uniqueInvisiblePrefix + ChatColor.RESET;
        } else if (line.length() <= MAX_COMPONENT_LENGTH) {
            prefix = line;
        } else {

            int splitPoint = line.charAt(MAX_COMPONENT_LENGTH - 1) == ChatColor.COLOR_CHAR
                ? MAX_COMPONENT_LENGTH - 1
                : MAX_COMPONENT_LENGTH;

            prefix = line.substring(0, splitPoint);
            suffix = line.substring(splitPoint);

            String prefixLastColors = ChatColor.getLastColors(prefix);

            if (
                suffix.length() < 2
                    || suffix.charAt(0) != ChatColor.COLOR_CHAR
                    || ChatColor.getByChar(suffix.charAt(1)).isFormat()
            ) {
                // if the initial suffix characters aren't color codes,
                // some weird bugs may happen, so we need to fix it...
                if (prefixLastColors.isEmpty()) {
                    // if no prefix last colors, then use default
                    // since entry name may colorize the suffix
                    suffix = ChatColor.RESET + suffix;
                } else {
                    // if there's prefix last colors, just use them
                    suffix = prefixLastColors + suffix;
                }
            }

            if (suffix.length() > MAX_COMPONENT_LENGTH) {
                suffix = suffix.substring(0, MAX_COMPONENT_LENGTH);
            }
        }

        return new BoardEntry(prefix, suffix);
    }
}