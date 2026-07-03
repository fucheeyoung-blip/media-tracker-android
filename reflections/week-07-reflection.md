# Week-07- Reflection

**Name:** Fuchee Young
**Date:** 7/2/2026

---

## Commits This Week

<!-- Paste a link to your commits for this week. The easiest way: go to your repo on GitHub,
     click "commits", and copy the URL after filtering by your name or branch. -->

**Link:** 

https://github.com/fucheeyoung-blip/media-tracker-android/pull/8


---

## Code Review

<!-- Every week you leave a review on a pod mate's pull request. Fill in both parts below.
     Part 1 is the link — I will verify the review exists on GitHub.
     Part 2 is your written assessment — what you actually looked at and what you found. -->

**Reviewed:** *(pod mate's name)* 

Fasika Yifru

**Link to my review:**

https://github.com/FasikaYifru/fy-media-tracker-android/pull/8


### What I Looked At

I reviewed Fasika's MediaDetailScreen.kt, which builds out the Week 7 media detail screen cover image, title/credit, star rating, action buttons, an "About" section, a stat grid (Year/Pages/Runtime/Genre), 
and a list of review cards. I focused mainly on the stat grid logic and the star-rating rendering, since those were the parts handling conditional/nullable data.

<!-- Walk through the code you reviewed. What was the PR trying to do? Which files or
     functions did you focus on? -->

### What I Noticed

I noticed both star-rating displays (the header rating and the rating inside each review card) are hardcoded as the literal string "★★★★★", 
regardless of the actual rating value. That means a 2-star review and a 5-star review would render identically, which defeats the purpose of showing a rating at all.


<!-- Be specific. Did you spot a potential bug? A pattern that could cause problems? Something
     done well that you want to call out? "I looked at the ViewModel and everything seemed fine"
     is not specific enough. Name the thing you noticed and explain why it matters. -->

### Comments I Left

Reviewed MediaDetailScreen.kt — solid layout overall. Two things worth fixing: m.pageCount.let { } should be m.pageCount?.let { }, since it's nullable and currently prints "null" in the Pages card for movies/shows.
Also, both star ratings are hardcoded to "★★★★★" regardless of actual score worth using the conditional star pattern used elsewhere so ratings actually differ visually.



<!-- Briefly summarize the comments you left on the PR. If you left a positive comment,
     say what it was. If you left a suggestion, say what you suggested and why. -->

---

## One Thing I Understood More Deeply

I understood StateFlow a lot more deeply this week after fixing a bug where my filter chips ("All," "Books," "Movies," "Shows") would visually highlight correctly when tapped, but the list underneath never actually changed. 
At first I assumed the chips just weren't wired to the click handler at all, but they were — onTypeSelect was correctly updating a _selectedType StateFlow every time. The actual problem was that nothing was ever reading _selectedType to filter anything. 
The popular list was only being set once, in init, using the full unfiltered results, and it just sat there afterward. Updating _selectedType had nowhere to flow to.

<!-- Be specific. Don't write "I learned about ViewModels." Write what specifically clicked —
     what was confusing before, what made it make sense, and how you'd explain it to someone else.
     There are no wrong answers here. -->

---

## One Thing I'm Still Confused About

I'm still not fully confident about when Compose components like FilterChip and NavigationBarItem use primary/secondary from the theme by default versus when I need to explicitly pass a colors parameter.
I ran into this a few times this week — components were rendering with the wrong theme color (amber instead of indigo) even though my Theme.kt was set up correctly, 
because the components themselves default to secondary unless overridden.


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
