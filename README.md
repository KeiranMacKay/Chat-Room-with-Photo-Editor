# Final Project
This project is brought to you by..: <br>
Sam Mitchell-McAdoo <br>
Keiran Mackay <br>
Faraj Mustafa <br>

### General Information
This project implements a chat room website with a built in image editor. Upon loading the website, you have 3 tabs you can go to: Chat, Draw, About. <br> <br> Chat is the chat room where you are prompted to enter a username. After which you may chat freely in the current chatroom, create a new chatroom, or refresh the current ones in case of any issues. <br> <br> Draw takes you to an image editor. The user is prompted to select an image from their devices local storage, after which they may choose a colour of their choice to draw on the image, and are able to save it to their device afterwords. <br> <br> Finally About displays the infromation of the creators of the website <br>

### Improvements
Improvments made to this project includes the ability to use a refresh button to refresh the rooms in case of any issues. We can also have multiple chat rooms open in the same tab rather then having to have multiple tabs open for multiple rooms. We have added an in house photo editor that allows the user to input a photo and draw on it, with a clear button to remove changes and a save button to save the changes to a new file.

![alt text](https://cdn.discordapp.com/attachments/220395098272170004/1096630874306531329/image.png)
![alt text](https://cdn.discordapp.com/attachments/220395098272170004/1096631006188028065/image.png)

### How to Run
To run this project, you will need to access a server that is running the project. To do this, you will either require a <br> local test-type server, such as Glassfish, which this project was developed on, or any of the variety of other <br> equipment you may choose to run a server. For the purposes of brevity, 
I will explain here how one might start the project using a Glassfish server. Once you have started the Glassfish server, access the IP of the server. If you need <br> help with getting a local glassfish instance going on your machine, we recommend using IntelliJ Idea <br> in conjunction with Glassfish for ease of use. For an explanation on how to install IntelliJ, look here..: <br>https://www.youtube.com/watch?v=-5kIt83ldk8&ab_channel=GeekyScript <br> For help with installing and running Glassfish, look here..: <br>https://www.youtube.com/watch?v=AJxBg90HM4s&ab_channel=IntelliJIDEAbyJetBrains  <br> After running the project using glassfish, the local address on your machine will be something like..: <br> http://localhost:8080/WSChatServerDemo-1.0-SNAPSHOT/index.html. <br>
Once you have accessed the server, you can use the UI to do a few different things. You can press about to learn <br> a bit more about this project's creators, or alternatively use the main function:
chatting with... yourself. <br>
Since you've run this locally, unfortunately you can only open new instances in your own browser, which is a little <br> anticlimatctic. However, you will notice that each tab is its' own Chatroom instance, and for each tab you can <br> input any chatroom code you like. for two tabs with the same code, they will see activities in that room. <br>
Each time you enter a room code in a new tab, you will be prompted by the system to input a username. <br>
this will be your username for the duration of your time in the room, and, when you enter, all will be notified of <br> your grand entrance, assuming they where aleady in the channel.
Similarly, when you leave, all the other users will also be notified. That's it! You now have the project fully running.

### Other Resources
Libraries used for this assignment include:
org.json.JSONObject<br> 
java.IO.IOException<br> 
java.util.Hashmap<br> 
javs.util.Map<br> 
