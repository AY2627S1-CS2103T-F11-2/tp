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

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
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
The item is currently issued | `Error: Equipment CAM001 is currently issued to John Tan and cannot be removed.`
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
* The person's name has 1–50 characters and contains only letters and spaces. Digits and punctuation are rejected.
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

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

ClubLogistics saves the equipment register automatically after every successful command. You do not need to
save manually, and the data is retained when you restart the application.

### Editing the data file

The equipment register is saved as a JSON file at `[JAR file location]/data/addressbook.json`. Advanced users are
welcome to update the data directly by editing that file.

Equipment is stored in the top-level `equipment` array. Each item has an `id`, a `name`, and an `issuedTo` value:

```json
"equipment": [
  { "id": "CAM001", "name": "Sony Camera", "issuedTo": null },
  { "id": "MIC001", "name": "Wireless Microphone", "issuedTo": "John Tan" }
]
```

An empty or null `issuedTo` value means the item is available. A valid person's name means it is issued to that person.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, ClubLogistics starts with an empty equipment register at the next run. The invalid file remains on disk until you run a command (ClubLogistics saves after every command). We recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause ClubLogistics to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous ClubLogistics home folder.

**Q**: Do I need to register a person before issuing equipment to them?<br>
**A**: No. ClubLogistics only records the name of the person currently holding each item, so you can issue equipment to anyone by name.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add <equipment-id> <equipment-name>`<br> e.g., `add CAM001 Sony Camera`
**Remove** | `remove <equipment-id>`<br> e.g., `remove CAM001`
**List** | `list`
**Issue** | `issue <equipment-id> <person-name>`<br> e.g., `issue CAM001 John Tan`
**Return** | `return <equipment-id>`<br> e.g., `return CAM001`
**Exit** | `exit`
