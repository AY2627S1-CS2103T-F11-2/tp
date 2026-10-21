# Equipment lifecycle verification

This plan verifies equipment registration, issuing, returning, filtering, removal, and persistence.

## Automated checks

Run with Java 25 from the project directory:

```sh
sdk use java 25.0.3.fx-zulu
./gradlew clean check coverage shadowJar
./.github/run-checks.sh
```

The automated suite covers command validation, JSON storage, equipment model updates, and real JavaFX cards.
Linux CI uses Xvfb to provide the virtual display required to initialize JavaFX.

On a Linux machine without a desktop display, run the Gradle command as
`xvfb-run -a ./gradlew clean check coverage shadowJar` with Xvfb installed.

The new regression tests cover:

* Adding an item through the command parser, then issuing and listing it.
* Case-insensitive lookup and normalization of multiword borrower names.
* Observable equipment-list updates after issuing.
* Keeping the borrower after a reload and rejecting a second issue.
* Rejecting removal of issued equipment without altering saved JSON.
* Independent assignments of different equipment to the same borrower.
* Requiring consistent equipment status and borrower combinations.
* Rejecting invalid stored statuses and inconsistent stored records.
* Loading legacy status-only available records.
* Displaying the same availability/borrower text in cards and command output.

Existing command/parser tests also cover invalid IDs, missing arguments,
invalid names, length boundaries, unknown equipment, and repeat issuing.

## Manual release checks

Use a temporary working directory so these checks do not change your real
saved equipment. After building the JAR, run from the project directory:

```sh
test_project_dir="$PWD"
test_data_dir=$(mktemp -d)
cd "$test_data_dir"
java -jar "$test_project_dir/build/libs/clublogistics.jar"
```

In the application's command box:

| Command | Expected result |
| --- | --- |
| `add QA001 Sony Camera` | A new card shows the ID, name, and Available. |
| `issue qa001 John   Tan` | Success output and the card show Issued to: John Tan. |
| `list` | All equipment is visible with availability/current holder. |
| `issue QA001 Alice Lim` | Rejected; John Tan remains the holder. |
| `remove qa001` | Rejected; the issued item and holder remain intact. |
| `issue QA001 John123` | Invalid name; the existing holder remains intact. |
| `issue UNKNOWN John Tan` | Missing-equipment error. |

Close and reopen the JAR from the same temporary directory. Check that
the QA001 card still shows John Tan. Automated storage/reload tests pass;
the final visual checks should be completed by the release tester.

The packaged application has also been launched successfully with isolated
temporary data. This startup check does not replace the visual checks above.

## Return and reissue checks

1. `return qa001` makes the card Available and clears the holder.
2. `issue QA001 Alice Lim` succeeds and saves Alice as the new holder.
3. `filter issued` shows the item with Alice as its holder.
4. `return QA001`, then `remove qa001`, removes the available item.
5. Restarting retains the final register state in `data/clublogistics.json`.
