#!/usr/bin/env python3
"""Reject stale product pages in a built site or a live Pages deployment."""

import argparse
from html.parser import HTMLParser
import json
from pathlib import Path
import sys
from urllib.request import Request, urlopen


class Page(HTMLParser):
    """Extract visible text and links without depending on rendered markup."""

    def __init__(self, html):
        super().__init__()
        self.words = []
        self.links = []
        self.feed(html)
        self.text = " ".join(" ".join(self.words).split())

    def handle_data(self, data):
        """Collect rendered document text."""
        self.words.append(data)

    def handle_starttag(self, tag, attrs):
        """Collect navigation destinations for homepage checks."""
        if tag == "a":
            self.links.extend(value for key, value in attrs if key == "href")


def main():
    """Check product identity, equipment instructions and deployment provenance."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("site", help="Built-site directory or public site URL")
    parser.add_argument("--commit", help="Expected deployment source revision")
    args = parser.parse_args()

    def read(name):
        if args.site.startswith(("https://", "http://")):
            request = Request(args.site.rstrip("/") + "/" + name,
                              headers={"Cache-Control": "no-cache"})
            with urlopen(request, timeout=30) as response:
                return response.read().decode("utf-8")
        return (Path(args.site) / name).read_text(encoding="utf-8")

    requirements = {
        "index.html": ["ClubLogistics", "equipment", "logistics coordinators"],
        "UserGuide.html": ["Returning equipment", "Removing equipment", "Filtering equipment",
                           "data/clublogistics.json", "java -jar clublogistics.jar"],
        "DeveloperGuide.html": ["EquipmentListPanel", "UniqueEquipmentList",
                                "data/clublogistics.json"],
    }
    obsolete = {
        "index.html": ["AddressBook Level 3"],
        "UserGuide.html": ["alongside contacts", "java -jar addressbook.jar"],
        "DeveloperGuide.html": ["PersonListPanel"],
    }
    failures = []
    for name, phrases in requirements.items():
        page = Page(read(name))
        for phrase in phrases:
            if phrase not in page.text:
                failures.append(f"{name}: missing {phrase!r}")
        for stale in obsolete[name]:
            if stale in page.text:
                failures.append(f"{name}: stale text {stale!r}")
        if name == "index.html":
            for guide in ["UserGuide.html", "DeveloperGuide.html"]:
                if not any(link.split("#")[0].endswith(guide) for link in page.links):
                    failures.append(f"{name}: missing link to {guide}")
    if args.commit and json.loads(read("deployment.json")).get("commit") != args.commit:
        failures.append("deployment.json: source revision differs from expected commit")
    if failures:
        print("\n".join(failures), file=sys.stderr)
        return 1
    print("PASS: ClubLogistics homepage, guides and source revision checks")
    return 0


if __name__ == "__main__":
    sys.exit(main())
