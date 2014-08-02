![Cymorth logo (TODO: someone make me a prettier one pls?)](https://raw.github.com/erinaceous/cymorth/master/app/src/main/ic_launcher-web.png)

Cymorth
=======
Android Timetable Viewer and reminderer for Aber Uni students

If you just want to download the APK (it's not on the play store yet; checking with IS first to make sure putting it on there is okay!),<br />
**[Download it here.](https://raw.github.com/erinaceous/cymorth/master/app/app-release.apk)**<br />
![QR code](http://i.imgur.com/f3afLWa.png "Scan this with your phone!")<br />
(This is the latest development build)


Installing to your phone
========================
If you have a QR code reader app on your phone, scan the above code, and open the URL it gives you. You can download the Cymorth application's .APK directly. When the download is complete you can click the notification and install it. You will probably have to go into System Settings -> Security and enable an option something like "Allow installation of apps from sources other than the Play Store" before installing it.

In future if it comes in helpful to enough for me to warrant maintaining it I'll put it on the Play Store.

Notes (Caveats)
---------------
- When I started this, I hadn't touched Android (or Java) in a while. Last time I used it was using a different IDE too, Android Studio is neat but it is pretty different from Eclipse. (I would be using good old Vim if Android Studio wasn't so awesome)
- So bearing in mind above point, I kinda just stumbled through this without any proper planning. Code has no comments. Some classes are defined in the wrong files. Logic probably doesn't flow right in some places. I will have done dumb things.
- I haven't tested this with anything other than my phone, Galaxy S4 mini. No tablets or older phones. Not entirely sure what it's compatible with.
- [Hopefully I'll improve it over time :)](https://github.com/doomcat/cymorth/issues)

About
=====
Cymorth is an app for Aberystwyth University students (Well, for me, but by extension any other students). I am super disorganized and can't even remember to put my lectures on Google Calendar or set any alarms or reminders for them. So I wrote StudentHelper/Abersistant/Cymorth (its current name, which *according to Google Translate* means 'help' in Welsh).

Cymorth provides a really simple and quick mobile interface for:
- Checking your lecture timetable for the week
- Reminding you, a little while before it, of your next lecture, via notification
- Setting alarms a configurable duration of time before your first lecture every day
- Above two will use your default system notification and alarm settings (e.g. your ringtones for them, whether to vibrate, etc.) - and won't make noise on silent/vibrate mode.
- Importing timetable HTML files from Dropbox, Drive, local storage (SD card) - depending on what apps you have installed for browsing files.
- Exporting of lectures to calendar (via Google Calendar, S Planner, etc)

There are two ways of importing your timetable for the week into Cymorth:
- Downloading directly from the Student Record website. This uses a normal web browser frame to log you in using Information Service's own login page for the Student Record website, and uses the resulting session cookies in HTTP requests to timetable.php. This method does **NOT** store your login details on your phone. As a result, you'll have to login every time, but if someone steals/exploits your phone, they can't get your uni login details.
- Importing from file -- this can be a file on Drive/Dropbox, local storage etc. -- so you can save Student Record timetable pages using a trusted web browser and copy them to your phone.

Cymorth scrapes the HTML (Using the awesome JSoup libraries) from student record timetable pages to extract information from

[Screenshots](http://imgur.com/a/18gMF)
=============


Clearing Cymorth's database
===========================
If something has gone wrong with the database, simplest way to clean it is to uninstall and then reinstall the app. You can also "Clear data" using the Application Info system settings screen.

Alarms
======
I haven't tested alarms and notifications much, so Your Mileage May Vary. If you force-close the app, it might forget to notify you! When Android boots up it runs some code to set any alarms/future notifications for future lectures, and also does so whenever you leave the app's settings page, but if you kill/force-close the app, from experience it forgets your notifications. Bear that in mind if you use any task killers.

Older Android Phones / Other devices
====================================
I have nooooooo idea whether this works on Android 2 (Gingerbread, Froyo etc) or Android 3.x (Honeycomb). The only phone and emulated devices I've had access to so far have all been on Android 4.2+. I've used compatability libraries and haven't done anything particularly complicated, but I would like this to be accessible to lots of people so if you have an older phone and it doesn't work, please submit an issue and prove any output from `adb logcat`, like stack traces from the app etc.

Licenses
========
All of my own code is under the permissive MIT license.
This app makes use of the JSoup library as well as the Android AsyncHTTPClient library:
http://loopj.com/android-async-http/
Which is under the Apache 2.0 license.