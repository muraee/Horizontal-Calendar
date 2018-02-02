# Horizontal Calendar

[![Download](https://api.bintray.com/packages/mulham-raee/maven/horizontal-calendar/images/download.svg) ](https://bintray.com/mulham-raee/maven/horizontal-calendar/_latestVersion)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A material horizontal calendar view for Android based on `RecyclerView`.

![showcase](/art/showCase.png)

## Installation

The library is hosted on jcenter, add this to your **build.gradle**:

```gradle
repositories {
      jcenter()
    }
    
dependencies {
      compile 'devs.mulham.horizontalcalendar:horizontalcalendar:1.3.4'
    }
```

## Prerequisites

The minimum API level supported by this library is **API 14 (ICE_CREAM_SANDWICH)**.

## Usage

- Add `HorizontalCalendarView` to your layout file, for example:

```xml
<android.support.design.widget.AppBarLayout>
		............ 
		
        <devs.mulham.horizontalcalendar.HorizontalCalendarView
            android:id="@+id/calendarView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@color/colorPrimary"
            app:textColorSelected="#FFFF"/>
            
</android.support.design.widget.AppBarLayout>
```

- In your Activity or Fragment, define your **start** and **end** dates to set the range of the calendar:

```java
/* starts before 1 month from now */
Calendar startDate = Calendar.getInstance();
startDate.add(Calendar.MONTH, -1);

/* ends after 1 month from now */
Calendar endDate = Calendar.getInstance();
endDate.add(Calendar.MONTH, 1);
```

- Then setup `HorizontalCalendar` in your **Activity** through its Builder: 

```java
HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .build();
```

- Or if you are using a **Fragment**:

```java
HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(rootView, R.id.calendarView)
	...................
```

- To listen to date change events you need to set a listener:

```java
horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                //do something
            }
        });
```

- You can also listen to **scroll** and **long press** events by overriding each perspective method within **HorizontalCalendarListener**:

```java
horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

            }

            @Override
            public void onCalendarScroll(HorizontalCalendarView calendarView, 
            int dx, int dy) {
                
            }

            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                return true;
            }
        });
```

## Customization

- You can customize it directly inside your **layout**:

```xml
<devs.mulham.horizontalcalendar.HorizontalCalendarView
            android:id="@+id/calendarView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:textColorNormal="#bababa"
            app:textColorSelected="#FFFF"
            app:selectorColor="#c62828"  //default to colorAccent
            app:selectedDateBackground="@drawable/myDrawable"/>
```

- Or you can do it programmatically in your **Activity** or **Fragment** using `HorizontalCalendar.Builder`:

```java
HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(Calendar startDate, Calendar endDate)
                .datesNumberOnScreen(int number)   // Number of Dates cells shown on screen (default to 5).
                .configure()    // starts configuration.
                    .formatTopText(String dateFormat)       // default to "MMM".
                    .formatMiddleText(String dateFormat)    // default to "dd".
                    .formatBottomText(String dateFormat)    // default to "EEE".
                    .showTopText(boolean show)              // show or hide TopText (default to true).
                    .showBottomText(boolean show)           // show or hide BottomText (default to true).
                    .textColor(int normalColor, int selectedColor)    // default to (Color.LTGRAY, Color.WHITE).
                    .selectedDateBackground(Drawable background)      // set selected date cell background.
                    .selectorColor(int color)               // set selection indicator bar's color (default to colorAccent).
                .end()          // ends configuration.
                .defaultSelectedDate(Calendar date)    // Date to be selected at start (default to current day `Calendar.getInstance()`).
                .build();
```

#### More Customizations

```java
builder.configure()
           .textSize(float topTextSize, float middleTextSize, float bottomTextSize)
           .sizeTopText(float size)
           .sizeMiddleText(float size)
           .sizeBottomText(float size)
           .colorTextTop(int normalColor, int selectedColor)
           .colorTextMiddle(int normalColor, int selectedColor)
           .colorTextBottom(int normalColor, int selectedColor)
       .end()
```

## Months Mode
HorizontalCalendar can display only **Months** instead of Dates by adding `mode(HorizontalCalendar.Mode.MONTHS)` to the builder, for example:

```java
horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(Calendar startDate, Calendar endDate)
                .datesNumberOnScreen(int number)
                .mode(HorizontalCalendar.Mode.MONTHS)
                .configure()
                    .formatMiddleText("MMM")
                    .formatBottomText("yyyy")
                    .showTopText(false)
                    .showBottomText(true)
                    .textColor(Color.LTGRAY, Color.WHITE)
                .end()
                .defaultSelectedDate(defaultSelectedDate)
```

## Events
A list of Events can be provided for each Date which will be represented as circle indicators under the Date with:

```java
builder.addEvents(new CalendarEventsPredicate() {

                    @Override
                    public List<CalendarEvent> events(Calendar date) {
                        // test the date and return a list of CalendarEvent to assosiate with this Date.
                    }
                })
```

## Reconfiguration
HorizontalCalendar configurations can be changed after initialization:
 
- Change calendar dates range:
```java
horizontalCalendar.setRange(Calendar startDate, Calendar endDate);
```
 
- Change default(not selected) items style:
```java
horizontalCalendar.getDefaultStyle()
        .setColorTopText(int color)
        .setColorMiddleText(int color)
        .setColorBottomText(int color)
        .setBackground(Drawable background);      
```

- Change selected item style:
```java
horizontalCalendar.getSelectedItemStyle()
        .setColorTopText(int color)
        ..............
```

- Change formats, text sizes and selector color:
```java
horizontalCalendar.getConfig()
        .setSelectorColor(int color)
        .setFormatTopText(String format)
        .setSizeTopText(float size)
        ..............
```

### Important
**Make sure to call `horizontalCalendar.refresh();` when you finish your changes**

## Features

- Disable specific dates with `HorizontalCalendarPredicate`, a unique style for disabled dates can be specified as well with `CalendarItemStyle`:
```java
builder.disableDates(new HorizontalCalendarPredicate() {
                           @Override
                           public boolean test(Calendar date) {
                               return false;    // return true if this date should be disabled, false otherwise.
                           }
       
                           @Override
                           public CalendarItemStyle style() {
                               return null;     // create and return a new Style for disabled dates, or null if no styling needed.
                           }
                       })
```

- Select a specific **Date** programmatically with the option whether to play the animation or not:
```java
horizontalCalendar.selectDate(Calendar date, boolean immediate); // set immediate to false to ignore animation.
	// or simply
horizontalCalendar.goToday(boolean immediate);
```

- Check if a date is contained in the Calendar:
```java
horizontalCalendar.contains(Calendar date);
```

- Check if two dates are equal (year, month, day of month):
```java
Utils.isSameDate(Calendar date1, Calendar date2);
```

- Get number of **days** between two dates:
```java
Utils.daysBetween(Calendar startInclusive, Calendar endExclusive);
```

## Contributing

Contributions are welcome, feel free to submit a pull request.

## License

> Copyright 2017  Mulham Raee
> 
> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0

> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the [License](/LICENSE) for the specific language governing
> permissions and limitations under the License.
