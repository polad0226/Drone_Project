# AgriBot

### Agricultural Drone Term Project for CS420

#### Developers:
* Austin Anderson
* Carlos Rivas
* Polad Yunisov
* Vraj Patel
* Daniel Oguhebe

# About

This JavaFX desktop app was made to simulate a drone management
system for a farm. This app displays all items in the farm alongside
options to move the drone around to different locations. You can:

- Create new items and item containers
- Set attributes for item and item containers
  - This includes name, location, price, and dimensions
- Move the drone to an item
  - Will stay at that item until a new location is specified
- Have the drone scan the entire farm area
  - Will return to the command center when complete
- View the entire farm and all of its elements

### Other things to know:
- The Root, Command Center, and Drone are preset
  - You will not be able to delete any of these items
- The farm dimensions are 600 x 800 (width x height)
- Adding a new item or item container will create one with default values
  - You can then change its location, dimensions, and other attributes in the tree view

# How to Run
#### The JavaFX project was built with JDK 16 (azul community 16) through the IntelliJ IDE.

1) Clone the repository (follow the Clone instructions below)


2) Open the project directory with IntelliJ (not tested on other IDEs)


3) Click *Run* on your IDE

*\* All team members ran the project with success.*

# Clone the Project
#### Make sure you are connected to the UAB VPN if you are not on campus.
1) With Terminal, move to the folder you want to clone the project
```
cd ~/Documents
```
2) Use the git clone command to clone the project
```
git clone https://gitlab.cs.uab.edu/CS420_Group7/AgriBot.git
```

# Make New Changes
#### We will use the Merge Request git workflow.
1) Create a branch to write all of your code under
```
git branch your-branch-name

git checkout your-branch-name
```
2) Write all of your new code


3) Commit all of your new code
```
git add .

git commit -m "your commit message"
```
4) Push your new branch to GitLab
```
git push origin your-branch-name
```
5) In GitLab, go to the "Merge Request" tab


6) At the top, there should be a popup to "Create merge request"

![merge-request-ss](https://gitlab.cs.uab.edu/CS420_Group7/AgriBot/uploads/bba630771e9692130fe52e4b63621d20/merge-request-ss.png)


7) You can add a "Title" and "Description," and then at the bottom click "Submit merge request"


8) Let the team know about the new Merge Request to do a code review, then you can merge to **master**
