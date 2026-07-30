# Week-010- Reflection

**Name:** Fuchee Young
**Date:** 7/23/2026

---

## Commits This Week

**Link:**

https://github.com/fucheeyoung-blip/media-tracker-android/pull/10/changes

---

## Code Review

**Reviewed:** *(pod mate's name)*

Fasika Yifru

**Link to my review:**

https://github.com/FasikaYifru/fy-media-tracker-android/pull/12/changes#r3686316433

### What I Looked At

Fasika's PR was adding the media detail API requests and library data model — wiring up things like `ErrorResponse`, `Favorite`, and `LibraryItem`, plus the network layer (`AddToLibraryRequest`, `DefaultMediaRepository`, `MediaApiService`). I focused mostly on `LibraryItem.kt`, since that's the file with the custom serialization logic, and skimmed the network/repository files to see how the requests were structured.

### What I Noticed

`LibraryItem.kt` includes a custom `KSerializer` using `PrimitiveSerialDescriptor` and manual `encodeString`/`decodeString` for the status field. That's the same problem I ran into myself — kotlinx.serialization expects an enum's raw constant name (`WANT_TO`) by default, but the API sends snake_case strings like `want_to`, so without a custom serializer the deserialization silently fails or throws. It's a subtle gotcha that's easy to miss until you actually test against the real API, so I wanted to flag it and compare how each of us solved it.

### Comments I Left

I left a comment on the `KSerializer` section in `LibraryItem.kt` asking whether they ran into the same enum-vs-snake_case mismatch I hit, and explained how I solved it on my end (custom serializer with `PrimitiveSerialDescriptor` + manual `encodeString`/`decodeString`). I framed it as a question to invite comparison rather than a correction, since their approach already looked functionally correct — I just wanted to understand their reasoning and share mine.

---

## One Thing I Understood More Deeply

Optimistic updates finally clicked for me this week. Before, I thought of it as vaguely "assume it'll work," but the actual pattern is precise: update local state first so the UI reacts instantly, fire the network call second, and only roll back if the call genuinely fails. What made it click was realizing a 409 (duplicate add/favorite) is *not* a failure — the end state is already correct, so rolling back there would actually be wrong. That distinction between "the request failed" and "the request told me something I already assumed" was the piece I was missing. I'd explain it to someone else as: update the UI like you're sure it'll work, keep a backup of the old state in memory, and only restore that backup if the network genuinely says no.

---

## One Thing I'm Still Confused About

I ran into a lot of friction with Gradle test dependencies and the `test` vs `main` source set structure this week — `mockk` and `kotlinx-coroutines-test` weren't resolving even after I created my test file, because I hadn't actually added them to `build.gradle`'s `dependencies` block yet. I understand *that* was the problem in hindsight, but the "Unresolved reference" error looked identical to a typo or wrong package name, so it took a while to realize it was a missing dependency rather than a code mistake. I'd like to understand Gradle's dependency resolution better so I can diagnose that faster next time.

---

## Anything Else *(optional)*

Spent a good chunk of this week debugging a build failure caused by a duplicate `AddToLibraryRequest` data class living in two different files — one in `MediaApiService.kt` and a leftover standalone file with a slightly different (arguably cleaner) implementation using the typed `LibraryStatus` enum directly instead of a raw `String`. Good reminder to check for existing declarations before adding new request/model classes, especially in a codebase that's been through several weeks of iteration.

---

## Rubric

*You don't need to self-assess — this is here so you know what I'm looking at.*

| Section | Points | Full Credit | Half Credit | No Credit |
|:---|:---:|:---|:---|:---|
| **Reflection** | 10 | Specific, honest responses to "More Deeply" and "Still Confused" sections. Shows genuine thinking — not just "I learned X." | Responses are present but vague or generic ("I got better at Compose"). | Missing or one-word answers. |
| **Code Review** | 10 | Specific observation about the code with explanation of why it matters (or a substantive positive comment). Link to review present and verified. | A question or comment that shows you read the code, but lacks explanation. | "Looks good!" or equivalent. Missing link. Review not found on GitHub. |
| **Total** | **20** | | | |

**A note on the code review score:** I check that the review actually exists on GitHub before grading. The written summary here and the GitHub comment should match. If the review isn't there, the written summary can't earn credit.