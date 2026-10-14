# Issue-equipment v1.3 verification

This follow-up is based on team master commit `d001563d`. The issue command
is already merged; this work verifies its integration with the current
equipment model, register display, and storage.

## Automated checks

Run with Java 25 from the project directory:

```sh
sdk use java 25.0.3.fx-zulu
./gradlew clean check coverage shadowJar
./.github/run-checks.sh
```

The verified run passes 336 JUnit tests, Checkstyle, the repository-wide
checks, and the JAR build. JaCoCo reports no missing or partial executable
lines in `Equipment.java`, `JsonAdaptedEquipment.java`, or `Model.java`.
Real JavaFX card tests also execute the equipment panel's card-building
code and verify live updates and cell recycling. Linux CI uses Xvfb to
provide the virtual display required to initialize JavaFX.

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
java -jar "$test_project_dir/build/libs/addressbook.jar"
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

## Return dependency

On the checked team master, `return` still throws a not-supported error.
The full MVP lifecycle cannot be verified until Jason's implementation
is integrated. After that merge, verify:

1. `return qa001` makes the card Available and clears the holder.
2. `issue QA001 Alice Lim` succeeds and saves Alice as the new holder.
3. `return QA001`, then `remove qa001`, removes the available item.
4. Restarting retains the final register state.

Do not mark return/reissue or the complete MVP lifecycle as verified yet.
