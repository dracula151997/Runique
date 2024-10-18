# Learning Path for Run Tracker Project

This document outlines the learning path and key concepts covered while working on the Run Tracker project. The project is an Android application that tracks running activities, including distance, speed, and elevation gain.

## Project Overview

Run Tracker is built using Kotlin and various Android libraries. The main features include:

- Real-time tracking of running activities
- Calculation of total distance, maximum speed, and total elevation gain
- Saving and viewing past runs
- Displaying run data on a map

## New Concepts I Learned

### General Concepts
#### 1. Latitude, Longitude, and Altitude

It seems like you might be using "idiot" in a playful or mistaken way here. Latitude, longitude, and altitude are geographical coordinates that define a location on Earth. Here's a breakdown:

* **Latitude**: Measures how far north or south you are from the equator (in degrees). It ranges from -90° (South Pole) to +90° (North Pole).
* **Longitude**: Measures how far east or west you are from the Prime Meridian (in degrees). It ranges from -180° to +180°.
* **Altitude**: The height above sea level (usually in meters or feet).
  Here’s a **recap on polylines** in a way that’s perfect for taking notes:

---

### **Polyline Overview**
- A **polyline** is a series of straight lines that connect multiple points (like connecting dots on a map).
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

That's the core of **polylines** in a simplified, easy-to-note way. If you'd like to add anything else or want more examples, let me know!


