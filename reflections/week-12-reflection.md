# Week 12 Reflection — Bonus Feature Sprint (Week 2 of 2)

*This week's reflection is different from the standard template. We're not doing Profile this week — instead, this is the first of two weeks building your assigned bonus feature (Write Review, Quotes, or Priorities). See `reflection-instructions.md` for naming/submission rules, which are unchanged; only the content below differs.*

**Name:**

Fuchee Young

**Date:**

8/6/2026

**My assigned bonus feature:** *(Write Review / Quotes / Priorities)*

Priorities

---

## Commits This Week

<!-- Paste a link to your commits for this week. -->

**Link:**

https://github.com/fucheeyoung-blip/media-tracker-android/pull/12/changes

---

## Code Review

<!-- Code review continues as normal — same pod rotation, regardless of which bonus feature you or your pod mate are building. -->

**Reviewed:** *(pod mate's name)*

Fasika Yifru


**Link to my review:**

https://github.com/FasikaYifru/fy-media-tracker-android/commit/7560c9840fbe14e2501aa3e2e11d6fd685bdad77#r196192777


### What I Looked At

ReviewApiService.kt and ReviewRequest.kt — the Retrofit interface and request model backing Fasika's Write Review bonus feature (create/update/delete review endpoints).

### What I Noticed

updateReview takes @Path("id") id: Int to target a specific review, but the ReviewRequest body it sends also includes mediaId. Since the review is already uniquely identified by id in the path, it's not clear whether the server actually uses mediaId on update or just ignores it — and if it does use it, a bug elsewhere could end up reassigning a review to the wrong media item without anything in the client catching it. This felt similar to the PUT /priorities issue I ran into this week, where the body shape I assumed from REST conventions didn't match what the server actually expected, and I only found the real behavior by reading the raw request/response bytes in Logcat.


### Comments I Left

Noticed updateReview sends mediaId in the request body even though the review's already targeted by id in the path. Is the server expected to ignore that field on update, or does it actually use it? Might be worth confirming — similar to the priorities endpoint I ran into this week, where the body shape didn't match what I assumed from REST conventions and I only figured it out by reading the raw request/response bytes. Probably worth a quick Logcat check before this gets built on top of.
---

## Bonus Feature Progress

<!-- This is the most important section this week. Be concrete: which endpoint(s) did you wire?
     What's actually showing on screen with real data? What's still stubbed or fake?
     "I worked on my bonus feature" is not an answer. "I got POST /quotes working from Media Detail
     and quotes show up in a list on my profile, but I haven't wired edit or delete yet" is. -->

**What's working:**

The full add-to-priorities loop is now working end-to-end with real data: a "Want To" item in the Library can be marked as a priority (High/Medium/Low) with optional estimated hours and notes via a new dialog on `LibraryScreen`, which calls `PriorityViewModel.addOrUpdatePriority()`. That correctly PUTs to the priorities endpoint and the item shows up on `PriorityScreen` with the right badge, hours, and notes. Filter chips (All/High/Medium/Low) work correctly against real data now, not just the empty state.

**What's still stubbed, fake, or not started:**

Drag-to-reorder isn't built yet — cards still show a static drag-handle icon with no actual gesture wired up. The 5-item cap logic exists in the ViewModel (`canAddMore()`) but I haven't confirmed it triggers correctly in the UI with 5 real items yet. I also haven't written the required Week 2 test (either reordering state or the 5-item cap). No remove-from-priorities UI exists yet either, even though `PriorityViewModel.removePriority()` is already written.


**What I'm blocked on, if anything:**

Not blocked, but this session ended up being almost entirely bug-fixing rather than new feature work. I discovered `PUT /priorities` doesn't behave like a typical bulk-replace endpoint — it actually accepts and returns **one priority entry at a time**, not a list, despite my `Priority`/`UpdatePrioritiesRequest` models originally assuming a list-based body and response. I only found this by reading the raw request/response bytes in Logcat, since the API kept returning a generic `"Missing required field: mediaId"` error that was actually caused by sending an array when it wanted a single object. I had to rewrite `DefaultPriorityRepository.updatePriorities()` to loop over items and call the endpoint once per item, and change the endpoint's Retrofit signature from `List<PriorityWriteItem>` to a single `PriorityWriteItem`, with the response type changed from `List<Priority>` to a single `Priority`. Since that's now sorted out, I should be able to move faster on drag-to-reorder and the cap enforcement.
---

## One Thing I Understood More Deeply

Trial-and-error on request shape doesn't scale — reading the actual bytes does. I spent a while guessing at what shape the API wanted (wrapped object vs. bare array, full `Priority` vs. a slimmed request type) and kept getting the same generic error back, which made it feel like nothing I changed was working. It wasn't until I actually looked at the raw OkHttp request/response logs in Logcat that I saw the real signal: the server was returning the exact same "missing mediaId" message whether I sent a wrapped object or a bare array, which only makes sense if it's expecting neither — a single flat object. That one piece of evidence (not another guess) is what actually solved it. I'm going to reach for request/response logging much earlier next time instead of guessing at shapes based on typical REST conventions, since this API didn't follow them.

<!-- Be specific. What clicked this week, building your own feature instead of following a handout step-by-step? -->

Debugging a silent failure taught me more than the wiring did. My "+ Want To" button was failing with no visible error because the ViewModel's catch block was swallowing the real exception message and always showing a generic "try again" string. Once I actually surfaced `e.message` in the UI, the real error appeared immediately (a missing `@Serializable` annotation) and the fix took one line. It made me realize how much time I lose when errors are hidden versus shown — I'm going to leave real error messages visible (at least during development) instead of generic ones going forward.


## One Thing I'm Still Confused About

I don't fully understand why the API is designed this way — a "bulk update" endpoint that only accepts one item per call feels unusual for a `PUT` on a collection resource, and I'm not sure if that's intentional API design, an inconsistency in how it was built, or something I'm still misunderstanding about the intended usage. I'd like to understand the reasoning (or confirm it really is just quirky) before I build drag-to-reorder on top of it, since reordering will need multiple items updated together and I want to make sure I'm not about to hit the same wall again with a rewritten loop.

<!-- Be honest. This tells me where to spend time in class next week. -->


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