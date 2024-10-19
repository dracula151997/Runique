# Learning Path for Run Tracker Project

This document outlines the learning path and key concepts covered while working on the Run Tracker
project. The project is an Android application that tracks running activities, including distance,
speed, and elevation gain.

## Project Overview

Run Tracker is built using Kotlin and various Android libraries. The main features include:

- Real-time tracking of running activities
- Calculation of total distance, maximum speed, and total elevation gain
- Saving and viewing past runs
- Displaying run data on a map

# New Concepts I Learned

## General Concepts

#### 1. Latitude, Longitude, and Altitude

It seems like you might be using "idiot" in a playful or mistaken way here. Latitude, longitude, and
altitude are geographical coordinates that define a location on Earth. Here's a breakdown:

* **Latitude**: Measures how far north or south you are from the equator (in degrees). It ranges
  from -90° (South Pole) to +90° (North Pole).
* **Longitude**: Measures how far east or west you are from the Prime Meridian (in degrees). It
  ranges from -180° to +180°.
* **Altitude**: The height above sea level (usually in meters or feet).
  Here’s a **recap on polylines** in a way that’s perfect for taking notes:

---

### 2. Polyline Overview

- A **polyline** is a series of straight lines that connect multiple points (like connecting dots on
  a map).
- It is used to represent **paths, routes, or lines** on maps, drawings, or diagrams.

### **Why Use Polylines?**

- To show **routes** between multiple locations (like a GPS path).
- To display **connected segments** in drawings, maps, or navigation systems.

---

### **Basic Concepts:**

1. **Points**: Each point is a specific location (usually with latitude, longitude).
2. **Lines**: Polylines connect those points with straight lines.

---

### **List of Locations (for one path)**

- **One list of locations** represents a **single, continuous line** or path.
- Example: Connecting several cities on a map.

```text
Example: [Point A] → [Point B] → [Point C]
```

---

### **List of Lists of Locations (for multiple paths)**

- **Multiple lists of locations** represent **different, separate routes** or segments.
- Each list is a different polyline.
- Useful when you have multiple paths, not just one continuous path.

```text
Example:
List 1: [Point A] → [Point B]
List 2: [Point C] → [Point D]
```

---

### **Why Use a List of Lists?**

- When you need to draw **multiple polylines**, each representing a separate segment or route.
- Helps to organize and manage different sets of paths on the same map or diagram.

---

### **Practical Uses of Polylines:**

1. **Mapping**: Show routes for driving, biking, or walking.
2. **Graphics**: Create connected lines in computer graphics.
3. **Navigation**: Display travel paths with multiple stops.


---
## New Functions
### What is `zipWithNext {}`?

`zipWithNext {}` works just like `zipWithNext()`, but with the added bonus of allowing you to **do something** with each pair of consecutive items **inside the curly braces `{}`**. In other words, you get to process or compute something for each pair directly.

### How does it work?

Let’s break it down with an example.

Imagine you have a list of numbers:

```kotlin
val numbers = listOf(1, 3, 5, 7)
```

When you use `zipWithNext {}` like this:

```kotlin
numbers.zipWithNext { a, b ->
    b - a
}
```

Here’s what happens:
- The `zipWithNext {}` goes through the list and pairs the items like this:
  - First pair: `(1, 3)`
  - Second pair: `(3, 5)`
  - Third pair: `(5, 7)`

- Inside the curly braces `{}`, you get to **do something** with each pair. In this case, we're subtracting the first number (`a`) from the second number (`b`).

The result will be:
```kotlin
// Output: [2, 2, 2]
```
Because:
- `3 - 1 = 2`
- `5 - 3 = 2`
- `7 - 5 = 2`

### So what’s happening here?

- **`zipWithNext { a, b -> }`**:
  - **`a`** is the first item in each pair.
  - **`b`** is the second item in each pair.
  - You can perform any calculation or action with `a` and `b`.

### Example with locations:

Let’s say you have a list of locations (just like in your original example with `getMaxSpeedKmh`):

```kotlin
val locations = listOf(100, 200, 300, 400)
```

You can calculate the **difference** (distance) between each consecutive location like this:

```kotlin
locations.zipWithNext { loc1, loc2 ->
    loc2 - loc1 // Calculate the difference between the two locations
}
```

This will give you:
```kotlin
// Output: [100, 100, 100]
```
Because:
- `200 - 100 = 100`
- `300 - 200 = 100`
- `400 - 300 = 100`

### Summary:

- **`zipWithNext {}`** pairs consecutive items in a list, like regular `zipWithNext()`.
- But the difference is, inside the `{}`, you can **do something** with each pair (like adding, subtracting, or comparing them).
- It’s useful when you need to process pairs of consecutive items and return a result for each pair.

### What is `flatMapLatest` in Kotlin Flow?
Okay, let's simplify it. Imagine you are at an ice cream shop where you can only eat one scoop at a time, but you like trying different flavors. Every time you pick a new flavor, the shop takes away the previous one before letting you taste the new one.

In Kotlin Flow, `flatMapLatest` works just like this. It allows you to switch to a new "flavor" (or new data) and cancel the old one immediately.

Here's how it works in plain words:

1. **Flow of requests**: Imagine you're sending requests for data continuously. Each request triggers a new data flow.
2. **Cancel the old flow**: If a new request comes in before the previous one finishes, `flatMapLatest` cancels the ongoing flow and starts the new one.
3. **Latest only**: It always gives you the result of the latest request and ignores the previous ones.

### Example:

```kotlin
val flowOfRequests = flowOf("Request A", "Request B", "Request C")

flowOfRequests.flatMapLatest { request ->
    // Imagine we're fetching data based on each request
    fetchDataBasedOnRequest(request)
}.collect { data ->
    println(data) // You only get the result of the latest request
}
```

If "Request C" comes in while "Request A" or "Request B" is still in progress, they will be canceled, and only the result of "Request C" will be processed.

### `stateIn` in Kotlin Flow
Let's break down `stateIn` in Kotlin Flow with a simple analogy and its use cases.

### What is `stateIn`?

Think of `stateIn` as a **live scoreboard** for an ongoing game. Imagine you're watching a football game and want to know the current score at any time. The scoreboard always shows the latest score, and if you miss any goals, you can still see the updated score when you look at the board.

In Kotlin Flow:
- **Flow** is like the game that's constantly producing events (data updates).
- **stateIn** is like the scoreboard. It gives you the most recent data (the current state) at any moment, even if you weren't watching the updates from the beginning.

When you convert a `Flow` into a `StateFlow` using `stateIn`, it starts **holding the latest value** so that if anyone asks, it can instantly provide the current state without waiting for the Flow to emit.

### How does it work?

- `stateIn` turns your cold Flow (which doesn't start until someone listens) into a **hot Flow** (always active and holding the latest value).
- It will keep emitting updates while holding onto the latest value that can be accessed anytime.

### Use Cases for `stateIn`

1. **UI State Management (e.g., ViewModel to UI)**:
   When building UIs, you often need to display and update state (like form inputs, loading indicators, etc.). `stateIn` ensures that the UI always has the most up-to-date data without needing to wait for the next emission.

   Example:
   ```kotlin
   val userStateFlow = userRepository.getUserData()
       .stateIn(viewModelScope, SharingStarted.Eagerly, initialUserState)
   ```

   Here, the UI can access `userStateFlow` anytime and get the current user data immediately, instead of waiting for the Flow to emit the latest value.

2. **Shared Data Between Multiple Collectors**:
   Suppose you have multiple parts of your app (like different fragments) that need the same data (e.g., user settings). By using `stateIn`, you ensure that all these parts of the app have access to the current settings, and when the settings change, all collectors are updated automatically.

3. **Preventing Re-triggering Expensive Operations**:
   If your Flow is triggering expensive operations (like network requests) every time it's collected, you can use `stateIn` to keep the latest result and avoid running the same operation repeatedly.

4. **Converting Cold Flows to Hot Flows**:
   Cold flows start from scratch every time they're collected, but if you want to share a Flow that emits updates to multiple subscribers (e.g., app-wide data like user authentication state), `stateIn` makes sure that subscribers all get the latest value and new subscribers don't re-trigger the entire Flow.

### Summary

- **`stateIn` = Live scoreboard:** Always shows the latest data (state) from the Flow.
- **Use it** when you need to hold onto the latest value and provide it immediately to new collectors, especially for UI state management, sharing data across collectors, and preventing redundant operations.