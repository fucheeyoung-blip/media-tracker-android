# Week-04- Reflection

**Name:** Fuchee Young
**Date:** 6/11/2026

---

## Commits This Week

<!-- Paste a link to your commits for this week. The easiest way: go to your repo on GitHub,
     click "commits", and copy the URL after filtering by your name or branch. -->

**Link:**

https://github.com/fucheeyoung-blip/media-tracker-android/pull/5/changes/964a298cdb8c42cfe251fadae274f0dea5a7f1e7
---

## Code Review

<!-- Every week you leave a review on a pod mate's pull request. Fill in both parts below.
     Part 1 is the link — I will verify the review exists on GitHub.
     Part 2 is your written assessment — what you actually looked at and what you found. -->

**Reviewed:** *(pod mate's name)*

Fasika Yifru

**Link to my review:**

https://github.com/FasikaYifru/fy-media-tracker-android/pull/5
### What I Looked At

I reviewed Fasika's PR, focusing on RegisterScreen.kt, RegisterViewModel.kt, and strings.xml. 
The PR touched the sign-up button behavior and a few string resources.

<!-- Walk through the code you reviewed. What was the PR trying to do? Which files or
     functions did you focus on? -->

### What I Noticed


The strings.xml change fixing sign_up_button from "Sign In" to "Sign Up" was a real bug fix that label would have confused users on the registration screen. However, I noticed a bigger issue in RegisterViewModel.kt: the onSignupClicked() 
function previously called userRepository.createAccount(...) inside viewModelScope.launch on successful validation, but this PR removes that call entirely and replaces it with a hardcoded _errorMessage.value = "Sign up functionality isn't implemented yet." 
A matching Toast was also added in RegisterScreen.kt that fires on every button click regardless of validation. This looks like it could be reverting working registration functionality rather than adding to it.

<!-- Be specific. Did you spot a potential bug? A pattern that could cause problems? Something
     done well that you want to call out? "I looked at the ViewModel and everything seemed fine"
     is not specific enough. Name the thing you noticed and explain why it matters. -->

### Comments I Left


I praised the strings.xml fix as a genuinely good catch. Then I flagged the removal of the userRepository.createAccount(...) call and asked whether this was intentional for example,
temporarily disabling registration while the backend isn't ready or whether it accidentally overwrote working code during a merge.

<!-- Briefly summarize the comments you left on the PR. If you left a positive comment,
     say what it was. If you left a suggestion, say what you suggested and why. -->

---

## One Thing I Understood More Deeply

This week I restyled RegisterScreen.kt to match LoginScreen.kt's layout. Going through it line by line, I realized "matching the style" wasn't about copying colors by eye — both screens share the same structure: a centered Column, 
a header Text using MaterialTheme.typography.headlineMedium and colorScheme.primary, then a subtitle in bodyMedium. Using those theme tokens instead of hardcoded values is what keeps screens consistent even if the theme changes later.

<!-- Be specific. Don't write "I learned about ViewModels." Write what specifically clicked —
     what was confusing before, what made it make sense, and how you'd explain it to someone else.
     There are no wrong answers here. -->

---

## One Thing I'm Still Confused About

In RegisterScreen.kt, I used LaunchedEffect(registerState) to call onRegisterSuccess() when registerState becomes Success. I copied this pattern from LoginScreen.kt and it works, but I don't fully understand why we need LaunchedEffect 
here instead of just calling onRegisterSuccess() directly inside the button's onClick. I think it has to do with registerState being a StateFlow that updates asynchronously after the API call finishes, but I'd like to understand 
more precisely when LaunchedEffect is necessary versus when a direct call would work.

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
