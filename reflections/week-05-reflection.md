# Week-05- Reflection

**Name:** Fuchee Young
**Date:** 6/18/2026

---

## Commits This Week

<!-- Paste a link to your commits for this week. The easiest way: go to your repo on GitHub,
     click "commits", and copy the URL after filtering by your name or branch. -->

**Link:**
https://github.com/fucheeyoung-blip/media-tracker-android/pull/6/changes/0cb6bf8fdaffb6442500786fd796f28ce034ecd3#diff-697f70cdd88ba88fe77eebda60c7e143f6ad1286bca75017421e93ad84fb87df
---

## Code Review

<!-- Every week you leave a review on a pod mate's pull request. Fill in both parts below.
     Part 1 is the link — I will verify the review exists on GitHub.
     Part 2 is your written assessment — what you actually looked at and what you found. -->

**Reviewed:** *(pod mate's name)* 
Dustin Marsh

**Link to my review:**
https://github.com/dmarsh31/media-tracker-android/pull/7


### What I Looked At

I reviewed Dustin's PR focusing on AuthViewModel.kt, ApiConstants.kt, and DefaultUserRepository.kt. 
The PR was wiring up the real login API call using Retrofit and TokenRequest.

<!-- Walk through the code you reviewed. What was the PR trying to do? Which files or
     functions did you focus on? -->

### What I Noticed

The ApiConstants.kt file correctly reads CLIENT_ID and CLIENT_SECRET from BuildConfig, which is the right way to keep credentials out of source code. However, in AuthViewModel.kt
I noticed a bug on the grantType field — it's being set to password.value (the user's actual password string) instead of the string literal "password".
The grantType field tells the API which flow to use, so passing the user's password there would send a malformed request that the server would reject. It should be grantType = "password" as a hardcoded string.

<!-- Be specific. Did you spot a potential bug? A pattern that could cause problems? Something
     done well that you want to call out? "I looked at the ViewModel and everything seemed fine"
     is not specific enough. Name the thing you noticed and explain why it matters. -->

### Comments I Left

I flagged the grantType = password.value bug and explained that grantType needs to be the literal string "password" to tell the API which grant flow to use. 
I also pointed out the duplicate class declaration and asked if there was a merge conflict that didn't get cleaned up.

<!-- Briefly summarize the comments you left on the PR. If you left a positive comment,
     say what it was. If you left a suggestion, say what you suggested and why. -->

---

## One Thing I Understood More Deeply

This week I understood why UserRepository is an interface rather than a class. At first it seemed like extra work and why not just call the API directly from the ViewModel? But when DefaultUserRepository implements it, 
the ViewModel only depends on the interface, not the concrete implementation. That means you could swap in a fake repository for testing without changing any ViewModel code. It also changed how errors are handled instead of throwing exceptions and catching them in the ViewModel, 
the repository returns a typed RegisterResult (Success, Conflict, NetworkError) and the ViewModel handles each case with a when expression, which is much cleaner than a generic try/catch.

<!-- Be specific. Don't write "I learned about ViewModels." Write what specifically clicked —
     what was confusing before, what made it make sense, and how you'd explain it to someone else.
     There are no wrong answers here. -->

---

## One Thing I'm Still Confused About

I don't fully understand how RetrofitInstance connects to UserApiService. I can see that DefaultUserRepository gets RetrofitInstance.userApiService and uses it to make API calls, 
but I don't understand what's happening inside RetrofitInstance specifically how Retrofit takes the UserApiService interface with @POST annotations and turns it into actual HTTP calls at runtime. 
I know it works, but I couldn't explain the mechanism yet.

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
