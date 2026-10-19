# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Coding Instructions
- Use Red/Green TDD by default.
- Adhere to the Single Layer of Abstraction Principle (SLAP) for all new code and modified functions.
- Include JavaDoc comment headers for all public methods.

## Testing

Maintain JUnit test coverage for approximately the top 90% highest-value methods. Prioritize complex methods, core behavior, and critical business logic rather than selecting methods solely to increase the raw coverage count.

After every code change, review the affected behavior and update the JUnit tests as needed to continue meeting this target. Run the relevant Gradle tests with Java 25 to verify the changes.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Git commit message style:

Follow the CS2103 Standards/Conventions
(https://nus-cs2103-ay2627-s1.github.io/website/admin/standardsAndConventions.html),
which defer to the SE-EDU Git conventions
(https://se-education.org/guides/conventions/git.html).

### Subject

Every commit must have a well-written subject line.
- Try to limit the subject line to 50 characters (hard limit: 72 characters).
- Use the imperative mood, e.g. `Add README.md`, not `Added README.md` or `Adding README.md`.
- Capitalize the first letter, e.g. `Move index.html file to root`, not `move index.html file to root`.
- Do not end the subject line with a period, e.g. `Update sample data`, not `Update sample data.`
- You may add a `<scope>:` or `<category>:` in front, when applicable. The subject after the colon should still start with an uppercase letter, e.g.
  `Person class: Remove static imports`, `Main.java: Remove blank lines`, `bug fix: Add space after name`, `chore: Update release date`.
- Use DG and UG as abbreviations for Developer Guide and User Guide only in the subject to reduce sentence length.

### Body

Writing a body is optional for trivial commits, but non-trivial commits should have one, and any body must follow these conventions:
- Separate subject from body with a blank line.
- Wrap the body at 72 characters.
- Use blank lines to separate paragraphs.
- Use bullet points as necessary, when they help more than paragraphs of text.
- Explain WHAT the commit is about and WHY it was done that way, not HOW. The reader can refer to the diff for how. The explanation should be detailed enough for the reader to judge whether the change is a good thing to do without reading the diff.
- If the description gets too long, the commit probably needs to be split into finer-grained commits.
- Minimize repeating information that is already given in code comments of the same commit.

Use the following structure for the body:

{current situation} -- use present tense

{why it needs to change}

{what is being done about it} -- use imperative mood

{why it is done that way}

{any other relevant info}

Avoid terms such as "currently" and "originally" when describing the current situation, as they are implied.
The word Let's can be used to indicate the beginning of the section that describes the change done in the commit.

### Branch names

Use a meaningful name consisting of relevant keywords in kebab case, e.g. `refactor-ui-tests`.
If the branch is related to an issue, use `issueNumber-some-keywords-from-issue-title`, e.g. `1234-ui-freeze-error`.

### Examples

Code quality refactoring:

Person attributes classes: extract a parent class PersonAttribute

Person attribute classes (e.g. Name, Address, Age etc.) have some common
behaviors (e.g. isValid()).

The common behaviors across person attribute classes cause code duplication.

Extracting the common behavior into a super class allows us to use
polymorphism when dealing with person attributes. For example, validity
checking can be done for all attributes of a person in one loop.

Let's pull up behaviors common to all person attribute classes into a new
parent class named PersonAttribute.

Using inheritance is preferable over composition in this situation
because the common behaviors are not composable.

Bug fix with a bullet list:

Find command: make matching case-insensitive

Find command is case-sensitive.

A case-insensitive find is more user-friendly because users cannot be
expected to remember the exact case of the keywords.

Let's,
* update the search algorithm to use case-insensitive matching
* add a script to migrate stress tests to the new format
