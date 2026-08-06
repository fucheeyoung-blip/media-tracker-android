# Week 11 Reflection — Bonus Feature Sprint (Week 1 of 2)

*This week's reflection is different from the standard template. We're not doing Profile this week — instead, this is the first of two weeks building your assigned bonus feature (Write Review, Quotes, or Priorities). See `reflection-instructions.md` for naming/submission rules, which are unchanged; only the content below differs.*

**Name:**

Fuchee Young

**Date:**

7/30/2026

**My assigned bonus feature:** *(Write Review / Quotes / Priorities)*

Priorities

---

## Commits This Week

<!-- Paste a link to your commits for this week. -->

**Link:**

https://github.com/fucheeyoung-blip/media-tracker-android/pull/11/changes

---

## Code Review

<!-- Code review continues as normal — same pod rotation, regardless of which bonus feature you or your pod mate are building. -->

**Reviewed:** *(pod mate's name)*

Fasika Yifru

**Link to my review:**

https://github.com/FasikaYifru/fy-media-tracker-android/pull/13/changes#r3732389940

### What I Looked At

Fasika's PR for their assigned bonus feature (Write Review), specifically `Review.kt` and the changes to `DefaultReviewRepository.kt`.

### What I Noticed

`Review.kt` had `@Serializable` added to the data class, which was good to see given I'd just spent time debugging a crash in my own code caused by a *missing* `@Serializable` annotation. But I also noticed `createdAt` changed from a required `String` to an optional field defaulting to `""`. Since `createdAt` is presumably meant to be a real server-set timestamp, defaulting it to an empty string means a missing or malformed field from the API would fail silently instead of throwing a clear error — which could cause confusing bugs later (e.g. blank dates on review cards, or broken sort-by-date logic) instead of surfacing the problem immediately.


### Comments I Left

Noticed `createdAt` changed from a required field to defaulting to `""` — was that intentional, or a side effect of adding `@Serializable`? If the server is always expected to return this, keeping it required would make a missing/malformed response fail loudly (so it's easy to catch) instead of silently rendering an empty date somewhere in the UI later. Just flagging it in case it wasn't a deliberate choice — I hit something similar in my own PR this week where a missing `@Serializable` annotation caused a silent crash, so I've been extra wary of default values masking real data problems.

---

## Bonus Feature Progress

<!-- This is the most important section this week. Be concrete: which endpoint(s) did you wire?
     What's actually showing on screen with real data? What's still stubbed or fake?
     "I worked on my bonus feature" is not an answer. "I got POST /quotes working from Media Detail
     and quotes show up in a list on my profile, but I haven't wired edit or delete yet" is. -->

**What's working:**

Both endpoints for Priorities are wired end-to-end: `GET /priorities` and `PUT /priorities` are hooked up through `DefaultPriorityRepository` and `PriorityViewModel`, following the same repository/ViewModel pattern as the existing Library feature. There's a real `PriorityScreen` with filter chips (All/High/Medium/Low), loading/error/empty states, and cards showing title, priority badge, estimated hours, and notes. Navigation is wired — tapping a star icon on the Library screen takes you to Priorities and back.

**What's still stubbed, fake, or not started:**

There's no UI yet to actually *add* a "Want To" item to the priorities list — `PriorityViewModel.addOrUpdatePriority()` exists but nothing calls it, so I haven't been able to test the full loop with real data yet. Drag-to-reorder isn't built (correctly deferred to Week 2 per the handout). The 5-item cap logic exists in the ViewModel (`canAddMore()`) but isn't enforced anywhere in the UI yet since there's no add flow to enforce it on.

**What I'm blocked on, if anything:**

Not blocked currently, but I spent most of this session fixing bugs unrelated to Priorities that were blocking me from testing it at all: `SearchResultsViewModel` was returning hardcoded fake data instead of calling the real search API (so tapping any search result 404'd), and `AddToLibraryRequest` and friends in `MediaApiService.kt` were missing the `@Serializable` annotation, which crashed the "+ Want To" button with a Retrofit converter error. Both are fixed now, so I can finally get a real "Want To" item into my library — next step is building the "mark as priority" flow so I can test Priorities end-to-end.

---

## One Thing I Understood More Deeply

<!-- Be specific. What clicked this week, building your own feature instead of following a handout step-by-step? -->

Debugging a silent failure taught me more than the wiring did. My "+ Want To" button was failing with no visible error because the ViewModel's catch block was swallowing the real exception message and always showing a generic "try again" string. Once I actually surfaced `e.message` in the UI, the real error appeared immediately (a missing `@Serializable` annotation) and the fix took one line. It made me realize how much time I lose when errors are hidden versus shown — I'm going to leave real error messages visible (at least during development) instead of generic ones going forward.


## One Thing I'm Still Confused About

<!-- Be honest. This tells me where to spend time in class next week. -->

I'm not fully sure why some of my request data classes had `@Serializable` and others didn't — I think they were written at different points and the annotation got missed inconsistently, but I'd like to understand kotlinx.serialization's failure mode better (it crashes at runtime on the specific request rather than at compile time), so I can catch this kind of thing earlier next time.

## Anything Else *(optional)*

---

## Rubric

*You don't need to self-assess — this is here so you know what I'm looking at.*

| Section | Points | Full Credit | Half Credit | No Credit |
|:---|:---:|:---|:---|:---|
| **Reflection** | 10 | Concrete progress report (what's wired, what's not) plus specific, honest "Understood More Deeply" and "Still Confused" sections. | Present but vague — "I worked on my feature" with no specifics on what's actually working. | Missing or one-word answers. |
| **Code Review** | 10 | Specific observation about the code with explanation of why it matters (or a substantive positive comment). Link to review present and verified. | A question or comment that shows you read the code, but lacks explanation. | "Looks good!" or equivalent. Missing link. Review not found on GitHub. |
| **Total** | **20** | | | |

**A note on the code review score:** I check that the review actually exists on GitHub before grading. The written summary here and the GitHub comment should match.