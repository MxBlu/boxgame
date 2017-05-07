# COMP2911 - Assignment 3 Code Repository #

A version control system for the code of the warehouse game assignment. 

### Team Members: ###

* Brandon 
* Ines
* Jumail
* Stewart
* Emily

### BitBucket User Guide ###

* https://confluence.atlassian.com/bitbucketserver/commit-and-push-changes-to-bitbucket-server-776798246.html

### How do I upload changes? ###

1. git status
2. git add * (* means all files)
3. git commit -m "COMMENT_HERE"
4. git push -u origin master 

### How do I download files? ###

1. cd ~/TARGET_DIRECTORY 
2. git clone https://bitbucket.org/brandw888/comp2911a3

or click clone in the left side bar

### Game Master ###

This class contains the main method of the game. The main method constructs an instance of a Game Master object. When this object is instantiated, the game’s window is setup, a state manager object is instantiated, and a run() function is called. This function has an infinite loop in it, otherwise called the ‘game loop’. This loops indefinitely until the user quits the game. In this loop, two functions are constantly called: update() and draw().

The update() function is used to update events and refresh the game. This is, so to speak, how the game keeps progressing.

The draw() function is in charge of all the graphics, or what is seen and printed to the game window.

Each of these two functions calls the sub functions of the state manager class.

###State Manager###

The next class in our game system is the State Manager. In our game, we need multiple screens, or sets of things to be displayed at a time – we need a screen for booting up, a screen for our menu, and a screen for the actual warehouse game. The state manager is used as the context to an interface – Game State.

###Game State###

The Game State interface has its own set of functions that mirrors those in the Game Master. Init(), update(), draw(), and now handleInput().
Each class that implements Game State has its own set of these functions. Depending on which game state class is selected in manager, our Game Master will send and receive from that class’s particular set of functions.

###Boot State###

To give an example, look at the update() and draw() functions in the Boot State class. If you play the game, our screen fades in from black, to display our splash screen. The update function contains two local variables, x and ticks. Every update these are incremented. There is a series of ‘if’ clauses. Depending on how much x and ticks have been incremented, set the alpha/color of the screen.

This screen will display the our logo, as previous stated. This is configured in the draw() function, which takes in a Graphics2D object sent from Game Master -> State Manager -> BootState. It sets the colour to white. Fills a rectangle, and then renders our image over this.