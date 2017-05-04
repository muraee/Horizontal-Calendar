# Horizontal Calendar

[ ![Download](https://api.bintray.com/packages/mulham-raee/maven/horizontal-calendar/images/download.svg) ](https://bintray.com/mulham-raee/maven/horizontal-calendar/_latestVersion)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A material horizontal calendar view for Android based on `RecyclerView`.

![demo](/art/demo.gif)

## Installation
The library is hosted on jcenter, add this to your **build.gradle**:

```gradle
repositories {
      jcenter()
    }
    
    dependencies {
      compile 'devs.mulham.horizontalcalendar:horizontalcalendar:1.1.5'
    }
```

##Prerequisites
The minimum API level supported by this library is **API 9 (GINGERBREAD)**.

## Usage
- Add `HorizontalCalendarView` to your layout file, for example:

```xml
<android.support.design.widget.AppBarLayout
		............ >
		
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
/** end after 1 month from now */
Calendar endDate = Calendar.getInstance();
endDate.add(Calendar.MONTH, 1);

/** start before 1 month from now */
Calendar startDate = Calendar.getInstance();
startDate.add(Calendar.MONTH, -1);
```

- Then setup `HorizontalCalendar` in your **Activity** through its Builder: 

```java
HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .startDate(startDate.getTime())
                .endDate(endDate.getTime())
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
            public void onDateSelected(Date date, int position) {
                //do something
            }
        });
```

- You can also listen to **scroll** and **long press** events by overriding each prespective method within **HorizontalCalendarListener**:

```java
horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Date date, int position) {

            }

            @Override
            public void onCalendarScroll(HorizontalCalendarView calendarView, 
            int dx, int dy) {
                
            }

            @Override
            public boolean onDateLongClicked(Date date, int position) {
                return true;
            }
        });
```

## Customization
You can customize it directly inside your layout:

```xml
<devs.mulham.horizontalcalendar.HorizontalCalendarView
            android:id="@+id/calendarView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@color/colorPrimary"
            app:textColorNormal="#bababa"
            app:textColorSelected="#FFFF"
            app:selectorColor="#c62828"   // default to colorAccent.
            app:selectedDateBackground="#00ffffff"/>
```

Or you can do it programmatically in your Activity using the **Builder**:

```java
HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .startDate(startDate.getTime())
                .endDate(endDate.getTime())
                .datesNumberOnScreen(5)   // Number of Dates cells shown on screen (Recommended 5)
                .dayNameFormat("EEE")	  // WeekDay text format
                .dayNumberFormat("dd")    // Date format
				.monthFormat("MMM") 	  // Month format
				.showDayName(true)	  // Show or Hide dayName text
				.showMonthName(true)	  // Show or Hide month text
                .textColor(Color.LTGRAY, Color.WHITE)    // Text color for none selected Dates, Text color for selected Date.
                .selectedDateBackground(Color.TRANSPARENT)  // Background color of the selected date cell.
                .selectorColor(Color.RED)   // Color of the selection indicator bar (default to colorAccent).
		.defaultSelectedDate(date)  // Date to be seleceted at start (default to Today)
                .build();
```

## Features
- You can select a specific **Date** programmatically with the option whether to play the animation or not, with:
```java
horizontalCalendar.selectDate(Date date, boolean immediate); // set immediate to false to ignore animation.
	// or simply
horizontalCalendar.goToday(boolean immediate);
```

- Check if two dates' **days** are equal:
```java
horizontalCalendar.isDatesDaysEquals(Date date1, Date date2);
```

- Check if a date is contained in the Calendar:
```java
horizontalCalendar.contains(Date date);
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
