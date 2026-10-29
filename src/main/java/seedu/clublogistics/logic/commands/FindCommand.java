package seedu.clublogistics.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Locale;

import seedu.clublogistics.model.Model;
import seedu.clublogistics.model.equipment.BorrowerName;

/** Shows equipment whose current borrower contains the supplied name fragment. */
public class FindCommand extends Command {
    public static final String COMMAND_WORD = "find";
    public static final String MESSAGE_USAGE = "find <borrower-name>";
    private final BorrowerName query;

    /** Creates a search using a validated, normalized borrower name or fragment. */
    public FindCommand(BorrowerName query) {
        this.query = requireNonNull(query);
    }

    /** Applies a persistent, case-insensitive borrower substring search. */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        String fragment = query.toString().toLowerCase(Locale.ROOT);
        model.updateFilteredEquipmentList(item -> item.getIssuedTo()
                .map(name -> name.toString().toLowerCase(Locale.ROOT).contains(fragment)).orElse(false),
                "Showing borrower: " + query + " — use list to show all");
        int count = model.getFilteredEquipmentList().size();
        return new CommandResult(count == 0 ? "No equipment found for borrower: " + query + "."
                : "Found " + count + " equipment " + (count == 1 ? "item" : "items")
                        + " for borrower: " + query + ".");
    }
}
