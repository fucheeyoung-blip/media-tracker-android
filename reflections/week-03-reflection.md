# Week {{N}} Reflection

**Name:** Fuchee Young
**Date:** 6/4/2026

---

## Commits This Week

<!-- Paste a link to your commits for this week. The easiest way: go to your repo on GitHub,
     click "commits", and copy the URL after filtering by your name or branch. -->

**Link:** 
https://github.com/fucheeyoung-blip/media-tracker-android/pull/4/changes/84eba65aa75b4ecb1fc571e2201f4be6bd133e41

---

## Code Review

<!-- Every week you leave a review on a pod mate's pull request. Fill in both parts below.
     Part 1 is the link — I will verify the review exists on GitHub.
     Part 2 is your written assessment — what you actually looked at and what you found. -->

**Reviewed:** *(pod mate's name)*
Fasika Yifru

**Link to my review:**
https://github.com/FasikaYifru/fy-media-tracker-android/pull/4/changes/19261bd36426aee4ebfca999cc6b775646422709

### What I Looked At
I reviewed Fasika's PR for week 3, focusing on AuthModels.kt in the data/model package. The PR was implementing the data classes 
needed for the authentication API CreateUserRequest, CreateUserResponse, TokenRequest, and TokenResponse.

<!-- Walk through the code you reviewed. What was the PR trying to do? Which files or
     functions did you focus on? -->

### What I Noticed

All four data classes are properly annotated with @Serializable, which is clean and consistent. However, CreateUserResponse is missing several fields from the API spec — bio, avatarUrl, followerCount, followingCount, trackedCount, isFollowing, and createdAt are all absent. 
Retrofit will silently drop those fields when deserializing the response, which won't cause a crash now but will be a problem later when any screen tries to display profile information.

<!-- Be specific. Did you spot a potential bug? A pattern that could cause problems? Something
     done well that you want to call out? "I looked at the ViewModel and everything seemed fine"
     is not specific enough. Name the thing you noticed and explain why it matters. -->

### Comments I Left

I praised the consistent use of @Serializable across all four classes, then flagged the incomplete CreateUserResponse and suggested adding the missing fields 
with nullable defaults (e.g. val bio: String? = null) so the model matches the full API response.

<!-- Briefly summarize the comments you left on the PR. If you left a positive comment,
     say what it was. If you left a suggestion, say what you suggested and why. -->

---

## One Thing I Understood More Deeply

This week I implemented RegisterScreen from scratch. While building it, my build failed with an "Unresolved reference: WindowCompat" error in Theme.kt at line 54 — the line uses WindowCompat.getInsetsController() inside a SideEffect block to sync the status bar color with the light/dark theme. I checked build.gradle.kts first thinking it was a 
missing dependency, but the library was already on the classpath. The actual problem was a missing import adding import androidx.core.view.WindowCompat resolved it immediately. This taught me that in Kotlin, every external class needs an explicit import even when its library is already included as a dependency. 
Building the screen itself also helped me understand how state hoisting works in Compose  the form fields each have their own remember { mutableStateOf("") } and validation logic runs off those values, so the UI always reflects the current state without any manual update calls.

<!-- Be specific. Don't write "I learned about ViewModels." Write what specifically clicked —
     what was confusing before, what made it make sense, and how you'd explain it to someone else.
     There are no wrong answers here. -->

---

## One Thing I'm Still Confused About

<!-- Be honest. This is the most useful part of the reflection for me — it tells me where to
     spend more time in class. You will not lose points for being confused. -->

--- 
I'm still not fully clear on the role of UserRepository and why it exists as a separate layer between the ViewModel and ApiService. I understand that AuthViewModel calls repository.createAccount() 
and UserRepository calls api.createUser(), but I'm not sure why we don't just call the API directly from the ViewModel. 
I know it has something to do with separation of concerns, but I don't yet understand when that separation actually matters in practice.

## Anything Else *(optional)*
I implemented the RegisterScreen this week, including form validation, password mismatch error handling, 
and wiring up a real API call through UserRepository and AuthViewModel using Retrofit.

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
