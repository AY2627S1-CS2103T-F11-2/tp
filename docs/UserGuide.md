---
layout: page
title: User Guide
---

ClubLogistics is a **command-line application for student club logistics coordinators** to track the club's reusable
equipment and see who each item is currently issued to. Every action is performed by typing a command, so if you type
quickly, ClubLogistics lets you record loans and returns faster than a spreadsheet or a paper log.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103T-F11-2/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for ClubLogistics.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar clublogistics.jar`.<br>
   The application window opens in a few seconds. On the first run, it contains three sample equipment items:
   `CAM001`, `MIC001`, and `PROJ001`.

1. Type a command in the command box and press Enter to execute it. Some example commands you can try:

   * `list` : Lists all equipment and shows whether each item is available or issued.

   * `add TRI001 Manfrotto Tripod` : Adds a tripod with the ID `TRI001`.

   * `issue CAM001 John Tan` : Records that `CAM001` is now held by John Tan.

   * `return CAM001` : Records that `CAM001` has been returned.

   * `remove TRI001` : Removes the tripod from the equipment register.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Equipment records

Each equipment record contains:

* **Equipment ID**: the unique identifier of the item, such as `CAM001`.
* **Equipment name**: a human-readable name, such as `Sony Camera`.
* **Issued to**: the person currently holding the item. This is empty when the item is available.

Every item has one of two statuses:

* **Available**: the item is not issued to anyone. Newly added items start as available.
* **Issued**: the item is held by the person recorded in its _Issued to_ field.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in angle brackets are parameters to be supplied by you.<br>
  For example, in `return <equipment-id>`, replace `<equipment-id>` with a value such as `CAM001`.

* Command words are case-sensitive and must be typed in lowercase, such as `filter`, `list`, and `issue`.
  The status value for `filter` is case-insensitive.

* Parameters must be given in the order shown in the command format.

* Every parameter is required. A missing or extra parameter is rejected with an error showing the correct usage,
  such as `Error: Invalid command format. Usage: return <equipment-id>`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

<div markdown="block" class="alert alert-info">

**:information_source: Equipment ID rules (shared by all commands):**<br>

* An equipment ID has 1–20 characters, consisting only of letters (A–Z, a–z) and digits (0–9).
  Spaces and special characters are not allowed.
* Leading and trailing spaces are ignored.
* IDs are matched without regard to capitalization, so `CAM001` and `cam001` refer to the same item.
* An invalid ID is rejected with
  `Error: Equipment ID must contain only letters and numbers and cannot contain spaces.`
</div>

### Adding equipment: `add`

Adds a new item to the equipment register. The new item is available and is not issued to anyone.

Format: `add <equipment-id> <equipment-name>`

* The equipment ID must follow the [equipment ID rules](#features) and must not already be in use.
* The equipment name has 1–50 characters. Letters, digits, spaces, and common punctuation such as `-`, `/`, and `'`
  are allowed. All text after the ID is treated as the name.
* Leading and trailing spaces are removed from the name, and consecutive spaces are combined into one.
  The name is displayed with the capitalization you entered.
* Several items may share a name as long as their IDs differ, since the club may own several identical items.

Examples:
* `add CAM001 Sony Camera`
* `add MIC001 Wireless Microphone`
* `add PROJ001 Epson Projector`

Successful output:

```text
Equipment CAM001 added successfully.
```

Possible errors:

Situation | Error message
----------|--------------
The ID is already in use (ignoring capitalization) | `Error: Equipment with ID CAM001 already exists.`
The ID is invalid | `Error: Equipment ID must contain only letters and numbers and cannot contain spaces.`
The name is empty or too long | `Error: Equipment name cannot be empty and must be 1–50 characters long.`
A parameter is missing | `Error: Invalid command format. Usage: add <equipment-id> <equipment-name>`

### Removing equipment: `remove`

Removes an item from the equipment register. Use this to correct an item that was added by mistake, or to stop
tracking an item the club no longer owns.

Format: `remove <equipment-id>`

* The equipment ID must follow the [equipment ID rules](#features).
* The item must exist and be available. An issued item cannot be removed, so that its current holder is not lost.
  [Return](#returning-equipment-return) the item first.

Example: `remove CAM001`

Successful output:

```text
Equipment CAM001 removed successfully.
```

Possible errors:

Situation | Error message
----------|--------------
No item has this ID | `Error: Equipment with ID CAM001 does not exist.`
The item is currently issued | `Error: Equipment CAM001 is issued to John Tan and cannot be removed.`
A parameter is missing or extra | `Error: Invalid command format. Usage: remove <equipment-id>`

### Listing all equipment: `list`

Shows every item in the equipment register and whether it is available or issued. For an issued item, the person
currently holding it is shown.

The equipment panel in the application window always shows each item as `Available` or `Issued to: <person-name>`,
and updates immediately after an item is added, removed, issued, or returned.

Format: `list`

Example output:

ID | Name | Status
---|------|-------
CAM001 | Sony Camera | Available
MIC001 | Wireless Microphone | Issued to: John Tan
PROJ001 | Epson Projector | Available

Possible errors:

Situation | Message
----------|--------
The register is empty | `No equipment found.`
A parameter is supplied | `Error: Invalid command format. Usage: list`

### Issuing equipment: `issue`

Records that an item has been issued to a person. The item is no longer available, and the person is recorded as
its current holder.

Format: `issue <equipment-id> <person-name>`

* The equipment ID must follow the [equipment ID rules](#features).
* All text after the ID is treated as the person's name, including spaces.
* The person's name has 1–50 characters and contains only the English letters `A–Z` and `a–z` and spaces. Digits,
  punctuation, and accented or non-English letters such as `é` are rejected.
  Leading and trailing spaces are removed, and consecutive spaces are combined into one.
* The item must exist and be available. Each item can have only one holder at a time, so issuing an item that is
  already issued is rejected and its current holder is kept.
* Different items can be issued to the same person.

Examples:
* `issue CAM001 John Tan`
* `issue PROJ001 Alice Lim`

Successful output:

```text
Equipment CAM001 issued to John Tan.
CAM001 | Sony Camera | Issued to: John Tan
```

Possible errors:

Situation | Error message
----------|--------------
No item has this ID | `Error: Equipment with ID CAM001 does not exist.`
The item is already issued | `Error: Equipment CAM001 is already issued to John Tan.`
The person's name is invalid | `Error: Person name must contain only letters and spaces and be 1–50 characters long.`
A parameter is missing | `Error: Invalid command format. Usage: issue <equipment-id> <person-name>`

### Returning equipment: `return`

Records that an issued item has been returned. The item becomes available and is no longer associated with any
person.

Format: `return <equipment-id>`

* The equipment ID must follow the [equipment ID rules](#features).
* The item must exist and be currently issued.

Example: `return CAM001`

Successful output:

```text
Equipment CAM001 returned successfully.
CAM001 | Sony Camera | Available
```

Possible errors:

Situation | Error message
----------|--------------
No item has this ID | `Error: Equipment with ID CAM001 does not exist.`
The item is already available | `Error: Equipment CAM001 is not currently issued.`
A parameter is missing or extra | `Error: Invalid command format. Usage: return <equipment-id>`

### Filtering equipment: `filter`

Shows only equipment with the selected status. Type the command word `filter` in lowercase; `FILTER AVAILABLE`
returns the error `Unknown command.` The status argument, `available` or `issued`, is case-insensitive.

Format: `filter <available|issued>`

Examples:
* `filter available` shows equipment that can be issued.
* `filter issued` shows equipment currently held by borrowers.
* `filter AVAILABLE` has the same effect as `filter available`.

The command reports one of the following results:

Selected status | Matching equipment exists | No equipment matches
----------------|---------------------------|---------------------
`available` | `Showing available equipment.` | `No available equipment found.`
`issued` | `Showing issued equipment.` | `No issued equipment found.`

An empty result means no equipment matches the selected status; it does not necessarily mean
that the whole register is empty. Filtering changes only the displayed list, not the equipment records.

The selected filter remains active as equipment is added, issued, returned, or removed, and the
visible list updates to reflect each item's current status. Run `list` to clear the filter and
show all equipment again. The filter is not saved between application sessions.

Possible errors:

Situation | Error message
----------|--------------
Status is missing, e.g., `filter` | `Error: Invalid command format. Usage: filter <available|issued>`
Extra arguments, e.g., `filter available extra` | `Error: Invalid command format. Usage: filter <available|issued>`
Unsupported status, e.g., `filter damaged` | `Error: Invalid command format. Usage: filter <available|issued>`

### Clearing the register: `clear`

Removes all equipment records and saves the empty register.

Format: `clear [force]`

* If any item is still issued, `clear` is refused and lists each issued item with its holder, so that loan
  records are not lost by accident. Return those items first, or use `clear force` to delete all records anyway.
* `clear force` removes every record, including issued items and their borrowers. This cannot be undone.
* `force` is not case-sensitive. Any other text after `clear` is rejected.

Examples:
* `clear` when nothing is issued: removes all records.
* `clear` when `CAM001` is issued to John Tan: refused with
  `Error: Some equipment is still issued: CAM001 (John Tan).`
* `clear force`: removes all records, including `CAM001`.

### Viewing help: `help`

Opens a window containing the ClubLogistics User Guide link. F1 opens the same help window.

Format: `help`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

Only one ClubLogistics instance can use the same home folder at a time. If startup reports that ClubLogistics
is already running, use the existing window or close it before launching again. The app keeps a small
`.clublogistics.lock` file in its home folder; do not delete it while the app is running.

ClubLogistics saves the equipment register automatically after successful commands, except `help` and `exit`,
which do not change or save equipment data. You do not need to save manually, and saved data is retained when
you restart the application. If saving fails, the command reports an error and the previous equipment register
is restored in memory. Resolve the reported file or folder problem before retrying; Help and exit remain available.

Saving writes a complete replacement before replacing the previous file. If your folder cannot support this
safe replacement, saving reports an error and keeps the previous file. Use a writable local folder for the app.
Keep separate backups for recovery from device failures; the app does not keep backup versions automatically.

### Editing the data file

The equipment register is saved as `data/clublogistics.json` inside the folder containing the JAR file, even if
you launch it from another directory. Window preferences and log files also stay inside that home folder.
Advanced users are welcome to update the equipment data directly by editing that file. If an older version
saved your data in a different launch folder, close the app, back up that file, and copy it into the new home
folder's `data` directory before starting the updated version.

Equipment is stored in the top-level `equipment` array. Each item has an `id`, a `name`, a `status`, and an
`issuedTo` value:

```json
{
  "equipment": [
    { "id": "CAM001", "name": "Sony Camera", "status": "AVAILABLE", "issuedTo": null },
    { "id": "MIC001", "name": "Wireless Microphone", "status": "ISSUED", "issuedTo": "John Tan" }
  ]
}
```

The `status` and `issuedTo` values must agree with each other. The only valid combinations are:

* `"status": "AVAILABLE"` with a null or empty `issuedTo` — the item is available.
* `"status": "ISSUED"` with a valid person's name in `issuedTo` — the item is issued to that person.

When you change whether an item is available, update **both** fields together. For example, to mark `MIC001` as
returned, change `"status": "ISSUED"` to `"status": "AVAILABLE"` **and** `"issuedTo": "John Tan"` to
`"issuedTo": null`. Changing only one of them makes the whole data file invalid.

If a record has no `status` field, ClubLogistics works out the status from `issuedTo`. This is only for
compatibility with older data files; ClubLogistics always writes `status` when it saves.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, ClubLogistics starts with an empty equipment register at the next run. Help and exit leave the equipment file untouched. Other successful commands replace it with the in-memory register. Back up the file before editing it or running other commands.<br>
Furthermore, certain edits can cause ClubLogistics to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

ClubLogistics reads the data file only when it starts, and does not notice changes made while it is running. If you
edit the file while ClubLogistics is open, the next successful command other than `help` or `exit` saves the register in memory over your
changes, and your edits are lost. To edit the data file safely:

1. Back up `data/clublogistics.json` by copying it somewhere else.
2. Close every running ClubLogistics window.
3. Edit the data file, then save it.
4. Start ClubLogistics again to load your changes.

#### Recovering from an invalid data file

ClubLogistics does not repair an invalid data file automatically. It starts with an empty register. Help and exit
leave the equipment file untouched; other successful commands replace it with the in-memory register. To recover
your records:

1. Close ClubLogistics, then copy the invalid `data/clublogistics.json` somewhere safe before changing anything.
2. Choose one of the following:
   * **Restore a backup:** replace `data/clublogistics.json` with a valid backup copy.
   * **Repair the file:** if there is no backup, edit the file so it follows the format described above. Each item needs
     a valid `id` and `name`, and a `status` that is `AVAILABLE` with an empty `issuedTo`, or `ISSUED` with a valid
     borrower name in `issuedTo`. Equipment IDs must be unique.
3. Start ClubLogistics and check that your records are listed. Do not run any command until you have confirmed that they
   loaded, because a successful command other than `help` or `exit` overwrites the file.

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and run it and enter `list`. The data file is created only when a command other than `help` or `exit` succeeds and
the register is saved, not when the app starts. Then:

1. Back up the data files on both computers.
2. Close ClubLogistics on both computers.
3. Overwrite the data file on the new computer with `data/clublogistics.json` from your previous ClubLogistics home folder.
4. Start ClubLogistics on the new computer to load the transferred records.

If ClubLogistics is still running when you replace the file, the next saving command replaces the transferred file with its old in-memory data.

**Q**: Do I need to register a person before issuing equipment to them?<br>
**A**: No. ClubLogistics only records the name of the person currently holding each item, so you can issue equipment to anyone by name.

--------------------------------------------------------------------------------------------------------------------

## Window settings

ClubLogistics restores saved window settings when they fit the current displays. Invalid sizes use defaults,
and positions outside all current displays are ignored. If you need to reset the window settings manually,
close ClubLogistics and delete `preferences.json` before starting it again; equipment data is stored separately.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add equipment** | `add <equipment-id> <equipment-name>`<br> e.g., `add CAM001 Sony Camera`
**Remove equipment** | `remove <equipment-id>`<br> e.g., `remove CAM001`
**Issue equipment** | `issue <equipment-id> <person-name>`<br> e.g., `issue CAM001 John Tan`
**Return equipment** | `return <equipment-id>`<br> e.g., `return CAM001`
**List all equipment** | `list`
**Filter by status** | `filter available` or `filter issued`
**Clear the register** | `clear [force]`<br> e.g., `clear`, `clear force`
**Help** | `help`
**Exit** | `exit`
