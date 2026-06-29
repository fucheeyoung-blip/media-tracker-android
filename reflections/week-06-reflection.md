# Week-06- Reflection

**Name:** Fuchee Young
**Date:** 6/25/2026

---

## Commits This Week

<!-- Paste a link to your commits for this week. The easiest way: go to your repo on GitHub,
     click "commits", and copy the URL after filtering by your name or branch. -->

**Link:** 

https://github.com/fucheeyoung-blip/media-tracker-android/pull/7/changes/a2898231848beb1bd7e07e92654bac25a453a13b

---

## Code Review

<!-- Every week you leave a review on a pod mate's pull request. Fill in both parts below.
     Part 1 is the link — I will verify the review exists on GitHub.
     Part 2 is your written assessment — what you actually looked at and what you found. -->

**Reviewed:** *(pod mate's name)* 

Fasika Yifru

**Link to my review:**

https://github.com/FasikaYifru/fy-media-tracker-android/pull/7

### What I Looked At

Fasika's implemented the search screen for the media tracker app. I focused on AuthInterceptor.kt and DefaultMediaRepository.kt. 
The PR adds network authentication via an interceptor that attaches a Bearer token to requests, and a repository layer that fetches paginated 
media results from the API.



<!-- Walk through the code you reviewed. What was the PR trying to do? Which files or
     functions did you focus on? -->

### What I Noticed

In AuthInterceptor.kt on line 9, runBlocking is used inside the OkHttp interceptor to call sessionRepository.getAccessToken(). Using runBlocking inside an OkHttp interceptor runs a coroutine on the network thread, which can cause the thread to freeze while waiting for the token. Under load this could cause ANRs (App Not Responding).
In DefaultMediaRepository.kt, the MediaPage data class includes nextCursor and hasMore fields, which shows forward-thinking design for pagination it avoids a big refactor later when the media list grows.


<!-- Be specific. Did you spot a potential bug? A pattern that could cause problems? Something
     done well that you want to call out? "I looked at the ViewModel and everything seemed fine"
     is not specific enough. Name the thing you noticed and explain why it matters. -->

### Comments I Left

I left an inline comment on line 9 of AuthInterceptor.kt suggesting that a safer approach is to make getAccessToken() a regular 
blocking/synchronous function instead of a suspend function, so runBlocking isn't needed at all. I also left a positive comment on 
DefaultMediaRepository.kt noting that the pagination design was a good architectural decision.


<!-- Briefly summarize the comments you left on the PR. If you left a positive comment,
     say what it was. If you left a suggestion, say what you suggested and why. -->

---

## One Thing I Understood More Deeply

This week I understood why @Serializable needs to be on every class in a chain, not just the top-level response class. I had AuthResponse annotated but not UserProfile, and the build failed with "Serializer has not been found for type 'UserProfile'." 
It clicked that the Kotlin serialization compiler plugin generates serializers at compile time for each class individually if a nested class isn't annotated, there's simply no serializer for it, and the outer class can't serialize either.

<!-- Be specific. Don't write "I learned about ViewModels." Write what specifically clicked —
     what was confusing before, what made it make sense, and how you'd explain it to someone else.
     There are no wrong answers here. -->

---

## One Thing I'm Still Confused About

I'm still not fully clear on when to use StateFlow vs LiveData in a ViewModel. I used StateFlow this week because that's what the existing code used, 
but I don't fully understand the tradeoffs between the two and when one is preferred over the other in a Compose project.


<!-- Be honest. This is the most useful part of the reflection for me — it tells me where to
     spend more time in class. You will not lose points for being confused. -->

---

## Anything Else *(optional)*

This week I built out the Search screen from a stub that just said "Search is not implemented yet." Debugging the build errors one at a time serialization, 
missing string resources, missing color values helped me understand how all the layers of the app connect.

<!-- Did you help a pod mate work through something? Did you discover something cool or frustrating?
     Did something from a previous week finally click? This is a good place to put it. -->

---

## Rubric

*You don't need to self-assess — this is here so you know what I'm looking at.*

| Section | Points | Full Credit | Half Credit | No Credit |
|:---|:---:|:---|:---|:---|
| **Reflection** | 10 | Specific, honest responses to "More Deeply" and "Still Confused" sections. Shows genuine thinking — not just "I learned X." | Responses are present but vague or generic ("I got better at Compose"). | Missing or one-word answers. |
| **Code Review** | 10 | Specific observation about the code with explanation of why it matters (or a substantive positive comment). Link to review present and verified. | A question or comment that shows you read the code, but lacks explanation. | "Looks good!" or equivalent. Missing link. Review not found on GitHub. |
| **Total** | **20** | | | |

**A note on the code review score:** I check that the review actually exists on GitHub before grading. The written summary here and the GitHub comment should match. If the review isn't there, the written summary can't earn credit.
