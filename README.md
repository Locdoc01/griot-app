# Griot - Digital Archive Of A Families
</br></br>
![Griot Logo](https://i.imgur.com/kPgZaTV.png)
</br></br></br>
*Read this in [German](README.de.md) / Lesen Sie dies auf [Deutsch](README.de.md)*

## Table Of Contents
- [About Griot](#about-griot)
- [Installation](#installation)
- [Screenshots](#screenshots)
- [User Guide](#user-guide)
- [Stage Of Development](#stage-of-development)
- [Technical Environment](#technical-environment)
- [Contributors](#contributers)


## About Griot

The App *Griot - Digital Archive Of A Family* primarily serves to provide a facility to interview a Person biographically and to record and archive the interview as Video or Audio.

The central idea is about the way of conducting the interview. The interviewer should ask well-directed questions to the narrator, which bring him to *indulge in reminiscences* and thus lapse into a lengthy flow of speech.  
For the achievement of this goal the app provides a guideline to the interviewer in form of a *topic catalogue*, from which he can put together the questions for his interview beforehand. Then during the interview the selected questions are shown in a scrollable *question carousel* and will be associated with the records afterwards.  
Like that the app helps the interviewer to conduct structured and biographic substantial interviews.

In the course of this there are different usage scenarious provided. The user can either record himself, for example to keep a spoken diary, or he can interview and record another person about his life events and stories.

Furthermore the app provides some social media features, which allows the user to make friends and groups with other users, invite guests, share contents und comment on shared contents. Moreover there is a section called *questionmail*, where interview questions can be sent to friends, which can be answered by them.

Other features are i.a.:
- Overview of all own and shared interviews of the user
- Interview detail views with playback function
- Personal page of the user
- Profile views
- Notification function for important events
- Userregistration and login

## Installation

The app can only be installed by Android Studio for now. Therefor the repository has to be imported as new project in Android Studio. To install the app a smartphone or tablet with Android 4.1 or above, unlocked developer options and enabled USB-debugging is required.


## Screenshots

<img src="https://i.imgur.com/rz8jcc1.png" width="250" height="" title="Registration">&emsp;<img src="https://i.imgur.com/1lI4oWw.jpg" width="250" height="" title="Overview">&emsp;<img src="https://i.imgur.com/jLCKG6a.jpg" width="250" height="" title="Interview details">
</br></br>
<img src="https://i.imgur.com/nLsHpLS.jpg" width="800" height="" title="Video recording">
</br></br>
<img src="https://i.imgur.com/dBAxLvZ.jpg" width="250" height="" title="Audio recording">&emsp;<img src="https://i.imgur.com/RSKzSip.png" width="250" height="" title="Contact managment">&emsp;<img src="https://i.imgur.com/ufcyVQJ.png" width="250" height="" title="Topic catalogue">
</br></br>
<img src="https://i.imgur.com/b2U9ewv.jpg" width="250" height="" title="Record review">&emsp;<img src="https://i.imgur.com/pUYJpAW.jpg" width="250" height="" title="Personal page">&emsp;<img src="https://i.imgur.com/mmcei7Y.png" width="250" height="" title="Profil">

<!--- <img src="https://i.imgur.com/iOUv4NB.png" width="250" height="" title="Narrator selection"> --->
<!--- <img src="https://i.imgur.com/g6GZWxs.png" width="250" height="" title="Topic selection"> --->
<!--- <img src="https://i.imgur.com/KO0It6n.png" width="250" height="" title="Medium selection"> --->

## User Guide

### Start The App
When the app gets started for the first time or if there is no user logged in, you get to the **Login** page first with the sections **Create Account** and **Sign In**.

#### Create Account
If you don't have an account yet, you can register yourself here. To do that, you have to fill out die fields *first name*, *last name*, *date of birth* and *email adress* and choose a password. This must contain numbers and letters and be at least 6 characters long.  
By the *plus* sign you can choose a profile image. If you don't want that, you will be assigned an anonymised avatar.  
Press on *Create Account* to perform the registration.

#### Sign In
If you are already a registered user, you can enter your email adress and password here. Press *Sign In* to get access to your account.

If you haven't signed out since the last time, you used the app, you will automatically be signed in, when you start the app again.

### Main functions
If you are signed in, you have full access to all app functions.  
At the bottom edge of the screen you see the main navigation bar, which gives you access to the most important functions of the app. From left to right, these are:
1. **Overview**
2. **Personal Page**
3. **Record an Interview**
4. **Notifications**\*
5. **Topic Catalogue**

By the *floatig* button to the left over the main navigation you get to the **Questionmail**\*.

### Overview
The **overview** shows all interviews, which you have access to. This includes your own interviews, in which you played a part, as well as those, which were shared to you.
If you choose an interview, you get to a **detail view**, which shows you all relevant information about the interview. At the top you can play the interview\*. Further down you can see a list of the belonging interviewquestions. If you choose one, you get to a further detail view for that question, where you also can play that single question\*.


### Record an Interview
By the middle button in the main navigation you can initiate a new interview. In that process you pass through the following steps:
- Selection of the narrator
- Selection of a topic
- Selection of the medium
- Record of the interview
- Review of the interview
- Save the interview

#### Selection of the narrator
At first you choose a person as narrator. You have the following options:  
- You can choose yourself for a self-interview.
- You can choose a friend.
- You can choose an existing guest.
- You can create a new guest profil by *add guest* and choose this guest afterwards.
- You can search for other users and add them as a friend. (They will receive a friendship request, which has to be accepted first, befor they can be chosen for the interview!)

Press *Next*, after you made your choice.

#### Selection of a topic
Next you choose a topic. You can expand the topics to see the associated questions. They can be switched on or off at will. Only switched on questions will be shown during the interview. By the appropriate *plus* signs you also can add new topics and questions\*.  
(Furthermore you can cancel you previously made choice of narrator, which automatically brings you back to the previous selection step.)  
Press *Next*, after you have chosen a topic. 

#### Selection ot the medium
Finally you choose a recording medium. You have the choice between **video recording** and **audio recording**.  
(As in the last step you also can cancel your previously made choices of narrator and topic here, which brings you back to the appropriate selection step.)  
Press *Next*, after you have chosen the medium.

#### Recording the interview
For using the **recording function**, you have to grant permissions for using the internal memory, the microphone and in case of video recording the camera es well.

In the bottom area of the recording function you can see the **question carousel**, in which you can browse through the questions by vertical wipe gestures.  
If you press the red *record* button, the recording starts for the middle question in the question carousel. Thus the question gets marked with a record sign.
During the recording you still can browse through the question carousel, which has no effect on the recording though.  
If you press the *stop* button, the recording stops and the recorded question gets marked blue.  
Already recorded questions can be recorded again at any time. If a question gets recorded more than once, the belonging single records will be concatenated to a complete record afterwards\*.  

If you record a video, you can switch between front and back camera (if applicable). Furthermore you can turn on or off the lightning (also if applicable).  
Press the *Finish* button, if you want to end the interview.

#### Review of the interview
In the review you see an overview of the recorded questions. For every question you can define *tags* by pressing the particular *plus* sign left under the covers.  
Through the *plus* sign at the top you also can add further questionsto your interview\*.  
Pressing the *back* button brings you back to the recording function, where you can record newly added questions as well as the previous questions again.  
Press *Next*, if you want to finish reviewing your interview.

#### Save the interview
In the last step you have to give a title to your interview. You also have the opportunity once more to change the chosen narrator.  
Press *Save*, if title and narrator are definite, to complete the interview. Then the interview will be uploaded and archived online. It will now be permanently available to you through the app, even if you remove the original recording from your device. (But bear in mind, that the quality of the uploaded records can differ from the original quality!)  
After saving the interview you will be redirected to the Overview automatically.

### Personal Page
On your **Personal Page** all your own interviews are shown. Furthermore you have the following options:  
- Press on your profile image or your name to get to your **Profile**. Here you can edit your personal information or delete your account\*.
- Press on the symbol in the top right corner to add a new guest.
- Press on *Friends and Groups* to get to the **Contact Managment**.
- Press on *Questionmail* to get to your **Questionmail**.

### Contact Managment
The **Contact Managment** contains a list of your personal contacts as well as a list of the groups\*, which you are a member of in two seperate tabs.  
The list of personal contacts shows your guests and friends. Press on a person to show the belonging profile. Profiles of your guests can also be edited.  
Press on *Add guest* to create a new guest profile. By the search field you can search for other users and add them as friends\*.

##### Difference between friends and guests

Friends are other registered users, which made friends with. Friends have unlimited access to all functions of the app.

Guests are persons for whom you created a guest profile, but who aren't users of the app by themselves. Guests can be chosen as narrator for an interview. In that case after saving the interview they receive an automated email with a link, that gives them access to the interview. 
If you want to share an interview, where a guest was narrator, the guest has to agree to that. In that case he also receives an email with links to approve or decline.

### Topic Catalogue
In the **Topic Catalogue** you can, as described in [Selection of a topic](#selection-of-a-topic), see your topics and questions as well as switch on or off the questions for your interviews. Furthermore you can add new topics and questions\*.

\* *These features are not available yet in the product core*


## Stage Of Development

This repository is a runnable product core, which provides the most important features of the app.  
The code is not free of errors, not fully tested and is still under development.  
Following app features are still missing:
- Playback function for medias
- support for different recording qualities
- Questionmail
- Notification function
- Expandability of the Topic Catalogue
- Group features
- search functions


##  Technical Environment

The App uses an architecture consistent of clients and a cloud-based backend.

For the backend the app uses i.a. the following services of the cloud service provider Google Firebase:
- Authentication
- Realtime Database
- Storage

As clients serve smartphones and tablets with Android 4.1 (API level 16) or above.
Following device features are necessary:
- Camera
- Mikrophone
- Internal or external storage
- WLAN or mobile data connection

For the recording of an interview following periphery is recommended:
- Tripod with mounting for smartphone/tablet
- external Mikrophone
- remote control for the camera


## Contributors

### Modelling, Software Design & Implementation

Marcel Schoob

### Idea, Conception & Interface Design

Muriel Balzer
