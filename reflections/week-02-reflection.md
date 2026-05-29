# Week-02- Reflection

**Name:** Fuchee Young
**Date:** 5/28/2026

---

## Commits This Week

<!-- Paste a link to your commits for this week. The easiest way: go to your repo on GitHub,
     click "commits", and copy the URL after filtering by your name or branch. -->

**Link:**
https://github.com/fucheeyoung-blip/media-tracker-android/pull/3
---

## Code Review

<!-- Every week you leave a review on a pod mate's pull request. Fill in both parts below.
     Part 1 is the link — I will verify the review exists on GitHub.
     Part 2 is your written assessment — what you actually looked at and what you found. -->

**Reviewed:** *(pod mate's name)*
Abdullahi Hassan
**Link to my review:**
https://github.com/ahassan5557-jpg/media-tracker-android/pull/2

### What I Looked At
This PR touched five files across the navigation, library, and profile layers of the app. The main goal was to fix how the bottom 
nav bar tracks the selected destination, parameterize the MEDIA_DETAIL route, and clean up state management in the library screen and its ViewModel.
I focused most closely on LibraryViewModel.kt and LibraryScreen.kt since those had the most substantive changes.

<!-- Walk through the code you reviewed. What was the PR trying to do? Which files or
     functions did you focus on? -->

### What I Noticed
I also noticed the BottomNavBar.kt change from a simple route equality check to hierarchy?.any { it.route == item.route }, which is the 
correct way to highlight the active tab when nested navigation graphs are involved.

<!-- Be specific. Did you spot a potential bug? A pattern that could cause problems? Something
     done well that you want to call out? "I looked at the ViewModel and everything seemed fine"
     is not specific enough. Name the thing you noticed and explain why it matters. -->

### Comments I Left
I left a positive comment on the GlobalScope → viewModelScope change, noting it was the right fix 
and why it matters for lifecycle safety.
<!-- Briefly summarize the comments you left on the PR. If you left a positive comment,
     say what it was. If you left a suggestion, say what you suggested and why. -->

---

## One Thing I Understood More Deeply
I learned more how to look at errors and learned more debugging and what the correct
code would be to fix it.

<!-- Be specific. Don't write "I learned about ViewModels." Write what specifically clicked —
     what was confusing before, what made it make sense, and how you'd explain it to someone else.
     There are no wrong answers here. -->

---

## One Thing I'm Still Confused About
I think im just kinda confuse how the submitting process but other than that im 
slowly learning how this class works. 

<!-- Be honest. This is the most useful part of the reflection for me — it tells me where to
     spend more time in class. You will not lose points for being confused. -->

---

## Anything Else *(optional)*
I helped guide my classmates with some of the missing things he forgot to do but they did 
the same for me and helped me out also.

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
