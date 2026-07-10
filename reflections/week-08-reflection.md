# Week-08- Reflection

**Name:** Fuchee Young
**Date:** 7/9/2026

---

## Commits This Week

<!-- Paste a link to your commits for this week. The easiest way: go to your repo on GitHub,
     click "commits", and copy the URL after filtering by your name or branch. -->

**Link:** 

https://github.com/fucheeyoung-blip/media-tracker-android/pull/9/changes

---

## Code Review

<!-- Every week you leave a review on a pod mate's pull request. Fill in both parts below.
     Part 1 is the link — I will verify the review exists on GitHub.
     Part 2 is your written assessment — what you actually looked at and what you found. -->

**Reviewed:** *(pod mate's name)* 

Fasika Yifru

**Link to my review:**

https://github.com/FasikaYifru/fy-media-tracker-android/pull/10



### What I Looked At

I reviewed Fasika's PR that adds a new getMediaById API endpoint. I focused on three files: MediaApiService.kt (the new Retrofit endpoint definition), 
DefaultMediaRepository.kt (the repository function that calls it), and RetrofitInstance.kt, which had a larger set of changes involving BuildConfig.


<!-- Walk through the code you reviewed. What was the PR trying to do? Which files or
     functions did you focus on? -->

### What I Noticed

In DefaultMediaRepository.getMediaById, the function returns Media? null if the response isn't successful. This means a 404 (media not found) 
and a network failure both collapse into the same null result, so the caller can't tell them apart or show a different message for each case. 
I ran into this exact issue in my own code this week my DefaultMediaDetailRepository originally had the same problem, and I fixed it by using a sealed MediaDetailResult 
class with Success and Error(message) cases instead of returning a nullable type. I'd suggest Fasika consider a similar pattern here so the UI layer can distinguish 'not found' from 'network error' and show the right message to the user.



<!-- Be specific. Did you spot a potential bug? A pattern that could cause problems? Something
     done well that you want to call out? "I looked at the ViewModel and everything seemed fine"
     is not specific enough. Name the thing you noticed and explain why it matters. -->

### Comments I Left

I left a comment on DefaultMediaRepository.kt suggesting a sealed result type (Success/Error) instead of a nullable return, so 404s and network failures aren't indistinguishable to the caller. 
I also left a positive comment on the RetrofitInstance.kt changes using BuildConfig to adjust configuration (e.g., logging level) 
based on build type is a good practice for keeping verbose logs out of release builds.



<!-- Briefly summarize the comments you left on the PR. If you left a positive comment,
     say what it was. If you left a suggestion, say what you suggested and why. -->

---

## One Thing I Understood More Deeply

I finally understood why my login was returning a 401 even though my AuthInterceptor looked correct. The interceptor was fine the real problem was that my AuthViewModel.onLoginClick() 
was never actually calling the login API or saving the session token; it was a stubbed placeholder that just checked if the fields were non-blank. 
This clicked for me: an interceptor can only attach a token if something upstream actually saved one first.


<!-- Be specific. Don't write "I learned about ViewModels." Write what specifically clicked —
     what was confusing before, what made it make sense, and how you'd explain it to someone else.
     There are no wrong answers here. -->

---

## One Thing I'm Still Confused About

I'm still trying to figure out why my search screen freezes the whole app when I type 
I know my SearchViewModel is currently using fake hardcoded data instead of the real API, 
but I haven't pinpointed what's actually blocking the main thread yet.


<!-- Be honest. This is the most useful part of the reflection for me — it tells me where to
     spend more time in class. You will not lose points for being confused. -->

---

## Anything Else *(optional)*



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
