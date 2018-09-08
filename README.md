# Location_tracker
Author: Yuanchen Lu  ylu36@ncsu.edu
## Introduction
An Android application to track user's outdoor activities. The application constantly sends the user’s location to a
server, which saves the location information in a database and tells the user how far he or she
has gone. To save battery on the client, the server also tells the client how long to wait before
sending the next update, based on the user’s speed.

## Usage
* Run the command `sbt run` inside `Location_server` directory. Wait until server starts. 
* Build the apk inside `Location_tracker` directory. Note this is the Android application. 
* Enter the host name and username as prompted, the host name should be the address for the `Location_server`.
* Upon moving, the app tracks the total distance since start. It also shows the user location update frequency. (Note: <i> update frequency may not show for the first few update requests</i>)

## Build Enironment
The server was built and tested within a Ubuntu 16.04 Virtual Machine. The Android app was built with Android Studio on Windows OS. 

## Screenshots
<img src="https://github.com/ylu36/Location_tracker_android/blob/master/Screenshot_1536436251.png" height="600" width="350"> <img src="https://github.com/ylu36/Location_tracker_android/blob/master/Screenshot_1536436404.png" height="600" width="350">
